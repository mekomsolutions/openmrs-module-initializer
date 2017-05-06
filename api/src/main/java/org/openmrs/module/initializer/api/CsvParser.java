package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;

import au.com.bytecode.opencsv.CSVReader;

public abstract class CsvParser<T extends BaseOpenmrsObject, S extends OpenmrsService, P extends BaseLineProcessor<T, S>> {
	
	protected static final Log log = LogFactory.getLog(CsvParser.class);
	
	protected CSVReader reader;
	
	protected S service;
	
	protected List<P> lineProcessors = new ArrayList<P>();
	
	// The current line
	protected String[] line = new String[0];
	
	public CsvParser(InputStream is, S service) throws IOException {
		this.service = service;
		this.reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		
		String[] headerLine = reader.readNext();
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
	 * The implementation should be delegated
	 * to the line processor in the subclass 
	 */
	protected T fromCsvLine(String[] line) {
		
		if (CollectionUtils.isEmpty(getLineProcessors())) {
			log.warn("No line processors have been set, you should either overload '"
			        + getClass().getEnclosingMethod().getName() + "' directly or provide lines processors to this class: "
			        + getClass().getCanonicalName());
			return null;
		}
		
		P firstProcessor = getLineProcessors().get(0);
		
		T instance = firstProcessor.getByUuid(line); // any line processor is fine to fetch by uuid
		if (instance == null) {
			throw new APIException(
			        "An instance that could not be fetched by UUID was not provided as an empty object for further filling. Check the implementation of this line processor: "
			                + firstProcessor.getClass().getCanonicalName());
		}
		
		// Applying the lines processors in order
		for (P processor : getLineProcessors()) {
			instance = processor.fill(instance, line);
		}
		return instance;
	}
	
	/*
	 * The actual saving should be implemented in this method.
	 */
	abstract protected T save(T instance);
	
	/*
	 * Parsers must set their line processor implementation
	 * based on the version indicated in the CSV metadata headers.
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
	 * Fills the OpenMRS instance from the next CSV line.
	 */
	protected T fetchNext() throws Exception {
		
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
		
		return fromCsvLine(line); // that's the actual method that must be overloaded
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
		
		T instance = null;
		do {
			try {
				instance = fetchNext();
				if (instance != null) {
					instance = save(instance);
					if (instance != null && instance.getId() != null) {
						instances.add(instance);
					}
				}
			}
			catch (Exception e) {
				log.error(
				    "An OpenMRS object could not be constructed or saved from the following CSV line: "
				            + Arrays.toString(line), e);
			}
		} while (instance != null);
		
		return instances;
	}
}
