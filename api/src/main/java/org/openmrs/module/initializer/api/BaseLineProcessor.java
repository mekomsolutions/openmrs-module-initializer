package org.openmrs.module.initializer.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.InitializerLogFactory;

/**
 * Base class to any CSV line processor.
 */
abstract public class BaseLineProcessor<T extends BaseOpenmrsObject> {
	
	protected final static Log log = InitializerLogFactory.getLog(BaseLineProcessor.class);
	
	protected static final String DEFAULT_RETIRE_REASON = "Retired by module " + InitializerConstants.MODULE_NAME;
	
	protected static final String DEFAULT_VOID_REASON = "Voided by module " + InitializerConstants.MODULE_NAME;
	
	protected static String HEADER_UUID = "uuid";
	
	protected static String HEADER_VOID_RETIRE = "void/retire";
	
	protected static String HEADER_NAME = "name";
	
	protected static String HEADER_DESC = "description";
	
	protected static String HEADER_DURATION = "duration";
	
	protected static String HEADER_START_TIME = "start time";
	
	protected static String HEADER_END_TIME = "end time";
	
	protected static String HEADER_MAX_LOAD = "max load";
	
	public static String LIST_SEPARATOR = ";";
	
	public static String LOCALE_SEPARATOR = ":"; // for header only
	
	public static String METADATA_PREFIX = "_"; // for header only
	
	public static String VERSION_LHS = METADATA_PREFIX + "version:"; // for header only
	
	public static String ORDER_LHS = METADATA_PREFIX + "order:"; // for header only
	
	public static String UNDEFINED_METADATA_VALUE = "__metadata__value__undefined__"; // for header only
	
	protected static String PARENT = "parent";
	
	/**
	 * Bootstraps an instance from a CSV line. The bootstrapping consists in 1. Either fetching an
	 * existing instance ready to be overridden from the available identifying keys found on the CSV
	 * line (UUID, name(s), ... etc) ; 2. Or instantiating a new instance ready to be filled up.
	 * 
	 * @param line The CSV line.
	 * @return The bootstrapped instance.
	 */
	abstract protected T bootstrap(CsvLine line) throws IllegalArgumentException;
	
	/**
	 * @param csvVoidOrRetireFlag The boolean flag for 'void or retire' as coming out of the CSV line.
	 * @param instance
	 * @return True whether the instance has been marked as voided or retired, false otherwise.
	 */
	protected boolean voidOrRetire(boolean csvVoidOrRetireFlag, T instance) {
		if (instance instanceof Retireable) {
			Retireable metadataInstance = (Retireable) instance;
			metadataInstance.setRetired(csvVoidOrRetireFlag);
			metadataInstance.setRetireReason(DEFAULT_RETIRE_REASON);
			return metadataInstance.isRetired();
		} else if (instance instanceof BaseOpenmrsData) {
			BaseOpenmrsData dataInstance = (BaseOpenmrsData) instance;
			dataInstance.setVoided(csvVoidOrRetireFlag);
			dataInstance.setVoidReason(DEFAULT_VOID_REASON);
			return dataInstance.isVoided();
		} else {
			return false;
		}
	}
	
	/*
	 * This implements how to fill T instances from any CSV line, ignoring the
	 * processing of the uuid. This method can assume that the provided instance is
	 * never null as this is being taken care of upstream.
	 */
	abstract protected T fill(T instance, CsvLine line) throws IllegalArgumentException;
	
	public static Boolean getVoidOrRetire(CsvLine line) {
		String str = Boolean.FALSE.toString();
		try {
			str = line.getString(HEADER_VOID_RETIRE);
		}
		catch (IllegalArgumentException e) {}
		
		return BooleanUtils.toBoolean(str);
	}
	
	/*
	 * This is basically a map between a localized header and its found locales.
	 * From such headers | Name:en | Name:km_KH | this would be a map between 'name'
	 * and [en, km_KH].
	 */
	protected static class LocalizedHeader {
		
		private String header; // the localized header name, so just 'Name'
		
		private Set<Locale> locales = new HashSet<Locale>(); // the entries locale-i18n name for each locale, eg:
		                                                     // en-Name:en
		
		public LocalizedHeader(String header) {
			this.header = header;
		}
		
		/**
		 * Localizes a header name. Eg 'Name:en' becomes 'Name'.
		 * 
		 * @return The localized header name.
		 */
		public String getHeader() {
			return header;
		}
		
		/**
		 * |Name:en|Name:km_KH| would return 'Name:en' for the locale 'en'.
		 * 
		 * @param locale The locale for which the full header name is wanted.
		 * @return The internationalized header name, if available.
		 */
		public String getI18nHeader(Locale locale) {
			String i18nHeader = getHeader() + LOCALE_SEPARATOR + locale.toString();
			if (locales.contains(locale)) {
				return i18nHeader;
			} else {
				throw new IllegalArgumentException(
				        "The header requested was never decorated with the specified locale: '" + i18nHeader + "'");
			}
		}
		
		public Set<Locale> getLocales() {
			return locales;
		}
	}
	
	/**
	 * From a set of i18n headers, such as 'Name:en', 'Name:km_KH', 'Description:en',
	 * 'Description:km_KH' it returns a map with the localized name as keys, so here: 'Name' and
	 * 'Description' and {@link LocalizedHeader} instances as values. Each {@link LocalizedHeader}
	 * carries the link between a localized name and the possible locales that where found for that
	 * name.
	 * 
	 * @param headerLine
	 */
	protected static Map<String, LocalizedHeader> getLocalizedHeadersMap(String[] headerLine) {
		
		Map<String, LocalizedHeader> l10nHeaderMap = new HashMap<String, LocalizedHeader>();
		
		for (String i18nHeader : headerLine) {
			if (i18nHeader.startsWith(METADATA_PREFIX)) {
				continue;
			}
			String[] parts = StringUtils.split(i18nHeader, LOCALE_SEPARATOR);
			if (parts.length == 2) {
				String header = parts[0].trim().toLowerCase();
				if (!l10nHeaderMap.containsKey(header)) {
					l10nHeaderMap.put(header, new LocalizedHeader(header));
				}
				LocalizedHeader l10nHeader = l10nHeaderMap.get(header);
				Locale locale = LocaleUtils.toLocale(parts[1]);
				if (l10nHeader.getLocales().contains(locale)) {
					throw new IllegalArgumentException(
					        "The CSV header line cannot contains twice the same header: '" + i18nHeader + "'");
				}
				l10nHeader.getLocales().add(locale);
			}
		}
		return l10nHeaderMap;
	}
	
	/**
	 * @see #getLocalizedHeadersMap(String[])
	 */
	public LocalizedHeader getLocalizedHeader(String[] headerLine, String header) {
		Map<String, LocalizedHeader> l10nHeadersMap = getLocalizedHeadersMap(headerLine);
		if (l10nHeadersMap.containsKey(header.trim().toLowerCase())) {
			return l10nHeadersMap.get(header.trim().toLowerCase());
		} else {
			return new LocalizedHeader("");
		}
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
	 * @return The order, eg. "100" out of "_order:100", or null if the order could not be parsed as an
	 *         int.
	 * @throws IllegalArgumentException
	 */
	public static Integer getOrder(String[] headerLine) throws IllegalArgumentException {
		String str = getMetadataValue(headerLine, ORDER_LHS);
		if (UNDEFINED_METADATA_VALUE.equals(str)) { // no order means last in line
			return Integer.MAX_VALUE;
		}
		Integer order = null;
		try {
			order = Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			log.error("'" + str + "' could not be parsed as a valid integer in header line: " + Arrays.toString(headerLine),
			    e);
		}
		return order;
	}
}
