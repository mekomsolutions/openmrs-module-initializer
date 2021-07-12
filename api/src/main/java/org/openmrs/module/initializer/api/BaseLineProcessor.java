package org.openmrs.module.initializer.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class to any CSV line processor.
 */
public abstract class BaseLineProcessor<T extends BaseOpenmrsObject> {
	
	protected final static Logger log = LoggerFactory.getLogger(BaseLineProcessor.class);
	
	final public static String HEADER_UUID = "uuid";
	
	final public static String HEADER_VOID_RETIRE = "void/retire";
	
	final public static String HEADER_NAME = "name";
	
	final public static String HEADER_DESC = "description";
	
	final public static String HEADER_DISPLAY = "display";
	
	final public static String HEADER_DURATION = "duration";
	
	final public static String HEADER_START_TIME = "start time";
	
	final public static String HEADER_END_TIME = "end time";
	
	final public static String HEADER_MAX_LOAD = "max load";
	
	final public static String PARENT = "parent";
	
	final public static String LIST_SEPARATOR = ";";
	
	final public static String LOCALE_SEPARATOR = ":"; // for header only
	
	final public static String METADATA_PREFIX = "_"; // for header only
	
	final public static String VERSION_LHS = METADATA_PREFIX + "version:"; // for header only
	
	final public static String ORDER_LHS = METADATA_PREFIX + "order:"; // for header only
	
	final public static String UNDEFINED_METADATA_VALUE = "__metadata__value__undefined__"; // for header only
	
	/**
	 * Fills the instance with all or some of data from a CSV line.
	 * 
	 * @param instance A non-null instance that was already bootstrapped elsewhere.
	 * @param line The CSV line feeding the data to populate the instance.
	 * @return The modified instance, filled as per the line processor.
	 */
	public abstract T fill(T instance, CsvLine line) throws IllegalArgumentException;
	
	public static Boolean getVoidOrRetire(CsvLine line) {
		String str = Boolean.FALSE.toString();
		try {
			str = line.getString(HEADER_VOID_RETIRE);
		}
		catch (IllegalArgumentException e) {}
		
		return BooleanUtils.toBoolean(str);
	}
	
	/**
	 * @param headerLine
	 * @return A map of the headers column positions indexed by header name.
	 * @throws IllegalArgumentException
	 */
	public static Map<String, Integer> createIndexMap(String[] headerLine) throws IllegalArgumentException {
		
		if (headerLine == null) {
			throw new IllegalArgumentException("The CSV header line cannot be null.");
		}
		
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		int col = 0;
		for (String header : headerLine) {
			if (indexMap.containsKey(header.trim().toLowerCase())) {
				throw new IllegalArgumentException(
				        "The CSV header line cannot contains twice the same header: '" + header + "'");
			}
			indexMap.put(header.trim().toLowerCase(), col);
			col++;
		}
		return indexMap;
	}
	
	public static int getColumn(String[] headerLine, String header) throws IllegalArgumentException {
		return getColumn(createIndexMap(headerLine), header.trim().toLowerCase());
	}
	
	public static int getColumn(Map<String, Integer> indexMap, String header) throws IllegalArgumentException {
		if (!indexMap.containsKey(header.trim().toLowerCase())) {
			throw new IllegalArgumentException("'" + header + "' was not found in the index map: " + indexMap.toString());
		} else {
			return indexMap.get(header.trim().toLowerCase());
		}
	}
	
	/**
	 * Extracts a metadata value from its LHS prefix.
	 * 
	 * @param headerLine The header line as an array of values
	 * @param fieldLhs The LHS prefix, eg. "_version:"
	 * @return The RHS of the metadata header, eg. "1" out of "_version:1"
	 * @throws IllegalArgumentException
	 */
	public static String getMetadataValue(String[] headerLine, String fieldLhs) throws IllegalArgumentException {
		
		if (headerLine == null) {
			throw new IllegalArgumentException("The CSV header line cannot be null.");
		}
		
		String value = UNDEFINED_METADATA_VALUE;
		int count = 0;
		for (String header : headerLine) {
			if (StringUtils.isEmpty(header)) {
				continue;
			}
			if (header.startsWith(fieldLhs)) {
				value = header.replaceFirst(fieldLhs, "");
				count++;
			}
		}
		if (count > 1) {
			throw new IllegalArgumentException(
			        "The CSV header line contains multiple fields '" + fieldLhs + "': " + headerLine.toString());
		}
		return value;
	}
	
	/**
	 * Returns the CSV version from the header line
	 * 
	 * @param headerLine
	 * @return The version, eg. "1" out of "_version:1"
	 * @throws IllegalArgumentException
	 */
	public static String getVersion(String[] headerLine) throws IllegalArgumentException {
		return getMetadataValue(headerLine, VERSION_LHS);
	}
	
	/**
	 * Returns the CSV order from the header line
	 * 
	 * @param headerLine
	 * @return The order, eg. "100" out of "_order:100", or null
	 * @throws IllegalArgumentException if the order could not be parsed as an integer.
	 */
	public static Integer getOrder(String[] headerLine) throws IllegalArgumentException {
		String str = getMetadataValue(headerLine, ORDER_LHS);
		if (UNDEFINED_METADATA_VALUE.equals(str)) { // no order means last in line
			return Integer.MAX_VALUE;
		}
		
		Integer order = Integer.MAX_VALUE;
		try {
			order = Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("'" + str + "' could not be parsed as a valid integer order in header line: "
			        + Arrays.toString(headerLine), e);
		}
		return order;
	}
}
