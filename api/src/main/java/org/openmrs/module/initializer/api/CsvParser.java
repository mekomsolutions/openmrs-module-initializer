package org.openmrs.module.initializer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConstants;

import com.opencsv.CSVReader;

public abstract class CsvParser<T extends BaseOpenmrsObject, P extends BaseLineProcessor<T>> {
	
	protected final Log log = LogFactory.getLog(CsvParser.class);
	
	protected static final String DEFAULT_RETIRE_REASON = "Retired by module " + InitializerConstants.MODULE_NAME;
	
	protected static final String DEFAULT_VOID_REASON = "Voided by module " + InitializerConstants.MODULE_NAME;
	
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
	
	/**
	 * Most CSV parsers are built on a single line processor. This superclass constructor should be used
	 * to initialize such parsers.
	 * 
	 * @param lineProcessor The single line processor for the CSV parser.
	 */
	protected CsvParser(BaseLineProcessor<T> lineProcessor) {
		this.singleLineProcessor = lineProcessor;
	}
	
	public BaseLineProcessor<T> getSingleLineProcessor() {
		return singleLineProcessor;
	}
	
	/**
	 * Each parser must know how to bootstrap an instance from a CSV line. Bootstrapping means
	 * attempting to load an instance based on its identifiers provided through the CSV line (such as
	 * UUID or name), and if no instance can be loaded a new instance must be provided.
	 * 
	 * @param line The CSV line.
	 * @return The bootstrapped instance.
	 */
	public abstract T bootstrap(CsvLine line) throws IllegalArgumentException;
	
	/**
	 * Each parser must know how to save an instance.
	 * 
	 * @param instance The domain instance to save.
	 * @return The domain instance that has been saved.
	 */
	public abstract T save(T instance);
	
	/**
	 * Each parser must know how to setRetire of an instance.
	 * 
	 * @return true if the type of instance is supported, false otherwise
	 */
	public boolean setRetired(T instance, boolean retired) {
		if (instance instanceof Retireable) {
			Retireable metadataInstance = (Retireable) instance;
			metadataInstance.setRetired(retired);
			if (retired) {
				metadataInstance.setRetireReason(DEFAULT_RETIRE_REASON);
			} else {
				metadataInstance.setRetireReason("");
			}
			return retired;
		} else if (instance instanceof BaseOpenmrsData) {
			BaseOpenmrsData dataInstance = (BaseOpenmrsData) instance;
			dataInstance.setVoided(retired);
			if (retired) {
				dataInstance.setVoidReason(DEFAULT_VOID_REASON);
			} else {
				dataInstance.setVoidReason("");
			}
			return retired;
		} else {
			return false;
		}
	}
	
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
		lineProcessors.clear();
		lineProcessors.add(getSingleLineProcessor());
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
	public String[] getHeaderLine() {
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
				save(initialize(line));
				
				saved++;
				if (saved > 250) { // TODO make this configurable
					Context.flushSession();
					Context.clearSession();
				}
			}
			catch (Exception e) {
				failedLines.add(line);
				log.error("An OpenMRS object could not be constructed or saved from the following CSV line:"
				        + new CsvLine(getHeaderLine(), line).prettyPrint(),
				    e);
			}
		}
		
		return failedLines;
	}
	
	/**
	 * "Initializes" an instance from a CSV line. It bootstraps the instance, then retires early or
	 * fills it. It returns an instance ready to be saved through the API service layer.
	 */
	private T initialize(String[] line) throws APIException {
		if (line == null) {
			return null;
		}
		final CsvLine csvLine = new CsvLine(headerLine, line);
		
		//
		// 1. Boostrapping
		//
		T instance = bootstrap(csvLine);
		
		if (instance == null) {
			throw new APIException(
			        "An instance that could not be bootstrapped was not provided as an empty object either. Check the implementation of this parser: "
			                + getClass().getSuperclass().getCanonicalName());
		}
		
		//
		// 2. Retiring & Unretiring
		//
		boolean isRetired = BaseLineProcessor.getVoidOrRetire(csvLine);
		if (setRetired(instance, isRetired)) {
			return instance;
		}
		
		//
		// 3. Filling the instance using all line processors in their specified order
		//
		for (BaseLineProcessor<T> processor : lineProcessors) {
			instance = processor.fill(instance, csvLine);
			if (instance == null) {
				throw new APIException(
				        "An instance came null out of a line processor. Check the implementation of this line processor: "
				                + processor.getClass().getCanonicalName());
			}
		}
		
		return instance;
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
