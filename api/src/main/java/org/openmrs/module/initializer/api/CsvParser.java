package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;

import au.com.bytecode.opencsv.CSVReader;

public abstract class CsvParser<T extends BaseOpenmrsObject, P extends BaseLineProcessor<T>> {
	
	protected static final Log log = LogFactory.getLog(CsvParser.class);
	
	protected CSVReader reader;
	
	protected List<P> lineProcessors = new ArrayList<P>();
	
	// The current line
	protected String[] line = new String[0];
	
	public CsvParser(InputStream is) throws IOException {
		this.reader = new CSVReader(new InputStreamReader(is));
		
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
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		String[] headerLine = reader.readNext();
		reader.close();
		return headerLine;
	}
	
	/*
	 * The implementation should be delegated
	 * to the line processor in the subclass 
	 */
	abstract protected T fromCsvLine(String[] line);
	
	/*
	 * The actual saving should be implemented in this method.
	 */
	abstract protected T save(T instance);
	
	/*
	 * Parsers must set their line processor implementation
	 * based on the version indicated in the CSV metadata headers.
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
