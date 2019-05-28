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
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.api.APIException;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConstants;

import com.opencsv.CSVReader;
import org.openmrs.module.initializer.InitializerLogFactory;

public abstract class CsvParser<T extends BaseOpenmrsObject, P extends BaseLineProcessor<T>> {
	
	protected static final String DEFAULT_RETIRE_REASON = "Retired by module " + InitializerConstants.MODULE_NAME;
	
	protected static final String DEFAULT_VOID_REASON = "Voided by module " + InitializerConstants.MODULE_NAME;
	
	protected final Log log = InitializerLogFactory.getLog(CsvParser.class);
	
	protected CSVReader reader;
	
	/**
	 * In most cases parsers rely on only one line processor, that one can be set by using
	 * {@link #CsvParser(BaseLineProcessor)}. This processor still needs to be added to
	 * {@link #lineProcessors} and this process happens automatically with single line processors.
	 */
	private BaseLineProcessor<T> singleLineProcessor = null;
	
	protected List<BaseLineProcessor<T>> lineProcessors = new ArrayList<BaseLineProcessor<T>>();
	
	// The header line
	protected String[] headerLine = new String[0];
	
	// The current line
	protected String[] line = new String[0];
	
	protected CsvParser() {
	}
	
	/**
	 * Most CSV parsers are built on a single line processor. This superclass constructor should be used
	 * to initialize such parsers.
	 * 
	 * @param lineProcessor The single line processor for the CSV parser.
	 */
	protected CsvParser(P lineProcessor) {
		this.singleLineProcessor = lineProcessor;
	}
	
	/**
	 * Each parser implementation should specify how an instance is actually saved.
	 * 
	 * @param instance The domain instance to save.
	 * @return The domain instance that has been saved.
	 */
	protected abstract T save(T instance);
	
	/**
	 * Each parser implementation should on which domain it operates.
	 * 
	 * @return The {@link Domain} covered by the parser.
	 */
	public abstract Domain getDomain();
	
	/*
	 * Parsers must set their line processors implementations based on the version
	 * indicated in the CSV metadata headers.
	 * 
	 * The order of the line processors does greatly matter, make sure you add them
	 * in the right order.
	 */
	protected void setLineProcessors(String version, String[] headerLine) {
		if (singleLineProcessor == null) {
			throw new IllegalStateException(
			        "It was not possible to set the default single processor for the CSV parser, there was none to be found. Make sure to initialize your CSV parser with at least one line processor.");
		}
		lineProcessors.clear();
		lineProcessors.add(singleLineProcessor.setHeaderLine(headerLine));
	}
	
	public void setInputStream(InputStream is) throws IOException {
		reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
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
	
	protected T setVoidedOrRetired(boolean isVoidedOrRetired, T instance) {
		if (instance instanceof Retireable) {
			Retireable metadataInstance = (Retireable) instance;
			metadataInstance.setRetired(isVoidedOrRetired);
			metadataInstance.setRetireReason(DEFAULT_RETIRE_REASON);
		}
		if (instance instanceof BaseOpenmrsData) {
			BaseOpenmrsData dataInstance = (BaseOpenmrsData) instance;
			dataInstance.setVoided(isVoidedOrRetired);
			dataInstance.setVoidReason(DEFAULT_VOID_REASON);
		}
		return instance;
	}
	
	/**
	 * Main method to proceed to save all instances fetched through parsing the CSV data.
	 * 
	 * @return The instances that could not be saved.
	 */
	public List<T> saveAll() {
		
		final List<T> failures = new ArrayList<T>();
		
		String[] line = null;
		do {
			T instance = null;
			
			try {
				line = fetchNextLine();
				instance = createInstance(line);
				
				if (instance != null) {
					instance = save(instance);
				}
			}
			catch (Exception e) {
				failures.add(instance);
				log.error("An OpenMRS object could not be constructed or saved from the following CSV line: \n"
				        + Arrays.toString(line),
				    e);
			}
		} while (line != null);
		
		return failures;
	}
	
	/**
	 * Saves a list of instances that have already been filled up.
	 * 
	 * @param instances The instances to save.
	 * @return The instances that could not be saved.
	 */
	public List<T> save(List<T> instances) {
		
		final List<T> failures = new ArrayList<T>();
		
		for (T instance : instances) {
			try {
				if (instance != null) {
					instance = save(instance);
				}
			}
			catch (Exception e) {
				failures.add(instance);
				log.error("An OpenMRS object could not be saved from the following object: " + instance.toString(), e);
			}
		}
		
		return failures;
	}
	
	/**
	 * Return true if instance is actually saved in database.
	 */
	// protected boolean isSaved(T instance) {
	// return instance.getId() != null;
	// }
	
	private T createInstance(String[] line) throws APIException {
		if (line == null) {
			return null;
		}
		
		// Boostrapping
		BaseLineProcessor<T> bootstrapper = getAnyLineProcessor();
		if (bootstrapper == null) { // no processors available
			log.error(
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
		
		boolean voidedOrRetired = BaseLineProcessor.getVoidOrRetire(headerLine, line);
		instance = setVoidedOrRetired(voidedOrRetired, instance);
		if (voidedOrRetired) {
			return instance;
		}
		
		// Applying the lines processors in order
		for (BaseLineProcessor<T> processor : lineProcessors) {
			instance = processor.fill(instance, new CsvLine(processor, line));
		}
		return instance;
	}
	
	/*
	 * Returns null if there are no processors set.
	 */
	protected BaseLineProcessor<T> getAnyLineProcessor() {
		return lineProcessors.iterator().next();
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
}
