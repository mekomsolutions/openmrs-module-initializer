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
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerLogFactory;

import com.opencsv.CSVReader;

public abstract class CsvParser<T extends BaseOpenmrsObject, P extends BaseLineProcessor<T>> {
	
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
	protected void setLineProcessors(String version) {
		if (singleLineProcessor == null) {
			throw new IllegalStateException(
			        "It was not possible to set the default single processor for the CSV parser, there was none to be found. Make sure to initialize your CSV parser with at least one line processor.");
		}
		lineProcessors.clear();
		lineProcessors.add(singleLineProcessor);
	}
	
	public void setInputStream(InputStream is) throws IOException {
		reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		headerLine = reader.readNext();
		
		String version = P.getVersion(headerLine);
		
		setLineProcessors(version);
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
	
	/**
	 * @return The header line of the file that this parser is set on.
	 */
	public String[] getHeaderLine() throws IOException {
		return headerLine;
	}
	
	/**
	 * Returns the CSV lines out of the CSV data, excluding the header line.
	 * 
	 * @return The list of CSV lines below the header line.
	 */
	public List<String[]> getLines() {
		
		final List<String[]> lines = new ArrayList<String[]>();
		
		String[] line = null;
		for (;;) {
			try {
				line = fetchNextLine();
			}
			catch (IOException e) {
				lines.add(new String[0]);
				log.error(
				    "There was an I/O exception while reading one of the CSV lines. That line will produce an error when it will be processed by the parser.",
				    e);
			}
			
			if (line == null) {
				break;
			}
			
			lines.add(line);
		}
		
		return lines;
	}
	
	/**
	 * Saves the instances created out of a list of CSV lines.
	 * 
	 * @param lines The CSV lines to process
	 * @return The failed CSV lines
	 */
	public List<String[]> process(List<String[]> lines) {
		
		final List<String[]> failedLines = new ArrayList<String[]>();
		
		int saved = 0;
		for (String[] line : lines) {
			try {
				T instance = createInstance(line);
				
				saved++;
				if (saved > 250) { // TODO make this configurable
					Context.flushSession();
					Context.clearSession();
				}
			}
			catch (Exception e) {
				failedLines.add(line);
				log.error("An OpenMRS object could not be constructed or saved from the following CSV line: \n"
				        + Arrays.toString(line),
				    e);
			}
		}
		
		return failedLines;
	}
	
	/**
	 * This fills and instance out of the information processed from a CSV line and attempts to save it.
	 */
	private T createInstance(String[] line) throws APIException {
		if (line == null) {
			return null;
		}
		final CsvLine csvLine = new CsvLine(headerLine, line);
		
		//
		// 1. Boostrapping with the bootstrapper line processor
		//
		BaseLineProcessor<T> bootstrapper = getAnyLineProcessor();
		if (bootstrapper == null) { // no processors available
			log.error(
			    "No line processors have been set, you should either overload '" + getClass().getEnclosingMethod().getName()
			            + "' directly or provide lines processors to this class: " + getClass().getCanonicalName());
			return null;
		}
		
		T instance = bootstrapper.bootstrap(csvLine);
		
		if (instance == null) {
			throw new APIException(
			        "An instance that could not be bootstrapped was not provided as an empty object either. Check the implementation of this parser: "
			                + getClass().getSuperclass().getCanonicalName());
		}
		
		//
		// 2. Voiding/retiring with the bootstrapper line processor
		//
		boolean isVoidedOrRetired = BaseLineProcessor.getVoidOrRetire(csvLine);
		if (bootstrapper.voidOrRetire(isVoidedOrRetired, instance)) {
			return instance;
		}
		
		//
		// 3. Filling the instance using all line processors
		//
		for (BaseLineProcessor<T> processor : lineProcessors) {
			instance = processor.fill(instance, csvLine);
		}
		
		//
		// 4. Saving the instance
		//
		if (instance != null) {
			instance = save(instance);
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
	protected String[] fetchNextLine() throws IOException {
		
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
