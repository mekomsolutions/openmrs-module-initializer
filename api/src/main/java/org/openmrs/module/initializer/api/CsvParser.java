package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Privilege;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.initializer.InitializerConstants;

import com.opencsv.CSVReader;

public abstract class CsvParser<T extends BaseOpenmrsObject, S extends OpenmrsService, P extends BaseLineProcessor<T, S>> {
	
	protected static final String DEFAULT_RETIRE_REASON = "Retired by module " + InitializerConstants.MODULE_NAME;
	
	protected static final String DEFAULT_VOID_REASON = "Voided by module " + InitializerConstants.MODULE_NAME;
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	protected CSVReader reader;
	
	protected S service;
	
	protected List<P> lineProcessors = new ArrayList<P>();
	
	// The header line
	protected String[] headerLine = new String[0];
	
	// The current line
	protected String[] line = new String[0];
	
	public CsvParser(InputStream is, S service) throws IOException {
		this.service = service;
		this.reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		
		headerLine = reader.readNext();
		String version = P.getVersion(headerLine);
		setLineProcessors(version, headerLine);
	}
	
	/**
	 * @param is The input stream for the CSV data.
	 * @return The header line.
	 * @throws IOException
	 */
	public static String[] getHeaderLine(InputStream is) throws IOException {
		CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		String[] headerLine = reader.readNext();
		reader.close();
		return headerLine;
	}
	
	/*
	 * The implementation should be delegated to the line processor in the subclass
	 */
	protected T createInstance(String[] line) throws APIException {
		if (line == null) {
			return null;
		}
		
		// Boostrapping
		P bootstrapper = getAnyLineProcessor();
		if (bootstrapper == null) { // no processors available
			log.warn(
			    "No line processors have been set, you should either overload '" + getClass().getEnclosingMethod().getName()
			            + "' directly or provide lines processors to this class: " + getClass().getCanonicalName());
			return null;
		}
		
		T instance = bootstrapper.bootstrap(new CsvLine(bootstrapper, line));
		if (instance == null) {
			throw new APIException(
			        "An instance that could not be bootstrapped was not provided as an empty object either. Check the implementation of this parser: "
			                + getClass().getSuperclass().getCanonicalName());
		}
		if (isVoidedOrRetired(instance)) {
			if (instance instanceof BaseOpenmrsMetadata) {
				((BaseOpenmrsMetadata) instance).setRetireReason(DEFAULT_RETIRE_REASON);
			}
			if (instance instanceof BaseOpenmrsData) {
				((BaseOpenmrsData) instance).setVoidReason(DEFAULT_VOID_REASON);
			}
			return instance;
		}
		
		// Applying the lines processors in order
		for (P processor : getLineProcessors()) {
			instance = processor.fill(instance, new CsvLine(processor, line));
		}
		return instance;
	}
	
	/*
	 * The actual saving should be implemented in this method.
	 */
	abstract protected T save(T instance);
	
	/*
	 * Says if the CSV line is marked for voiding or retiring.
	 */
	abstract protected boolean isVoidedOrRetired(T instance);
	
	/*
	 * Parsers must set their line processor implementation based on the version
	 * indicated in the CSV metadata headers.
	 * 
	 * You should add the line processors in the order that you want them to follow.
	 */
	abstract protected void setLineProcessors(String version, String[] headerLine);
	
	protected void addLineProcessor(P lineProcessor) {
		lineProcessors.add(lineProcessor);
	}
	
	protected List<P> getLineProcessors() {
		return this.lineProcessors;
	}
	
	/*
	 * Returns null if there are no processors set.
	 */
	protected P getAnyLineProcessor() {
		return getLineProcessors().iterator().next();
	}
	
	/*
	 * Fetches the next CSV line
	 */
	protected String[] fetchNextLine() throws Exception {
		
		line = reader.readNext();
		if (line == null) {
			close();
			return null;
		}
		
		// Trim values and replace blank or empty values with nulls
		for (int col = 0; col < line.length; ++col) {
			String val = line[col].trim();
			
			line[col] = StringUtils.isNotEmpty(val) ? val : null;
		}
		
		return line;
	}
	
	protected void close() throws IOException {
		reader.close();
	}
	
	/**
	 * Main method to proceed to save all instances fetched through parsing the CSV data.
	 * 
	 * @return The list of saved OpenMRS objects instances.
	 */
	public List<T> saveAll() {
		
		List<T> instances = new ArrayList<T>();
		
		String[] line = null;
		do {
			try {
				line = fetchNextLine();
				T instance = createInstance(line);
				if (instance != null) {
					instance = save(instance);
					if (instance instanceof Privilege && ((Privilege) instance).getPrivilege() != null) {
						instances.add(instance);
					} else if (instance.getId() != null) {
						instances.add(instance);
					}
				}
			}
			catch (Exception e) {
				log.error("An OpenMRS object could not be constructed or saved from the following CSV line: "
				        + Arrays.toString(line),
				    e);
			}
		} while (line != null);
		
		return instances;
	}
}
