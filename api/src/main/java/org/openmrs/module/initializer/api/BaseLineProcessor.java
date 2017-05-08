package org.openmrs.module.initializer.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.OpenmrsService;

/**
 * Base class to any CSV line processor.
 */
abstract public class BaseLineProcessor<T extends BaseOpenmrsObject, S extends OpenmrsService> {
	
	protected final static Log log = LogFactory.getLog(BaseLineProcessor.class);
	
	protected static String HEADER_UUID = "uuid";
	
	public static String LIST_SEPARATOR = ";";
	
	public static String LOCALE_SEPARATOR = ":"; // for header only
	
	public static String METADATA_PREFIX = "_"; // for header only
	
	public static String VERSION_LHS = METADATA_PREFIX + "version:"; // for header only
	
	public static String ORDER_LHS = METADATA_PREFIX + "order:"; // for header only
	
	public static String UNDEFINED_METADATA_VALUE = "__metadata__value__undefined__"; // for header only
	
	protected S service;
	
	protected String[] headerLine;
	
	protected Map<String, Integer> indexMap = new HashMap<String, Integer>();
	
	protected Map<String, LocalizedHeader> l10nHeadersMap = new HashMap<String, LocalizedHeader>();
	
	/*
	 * This implements how to fill T instances from any CSV line, ignoring the processing of the uuid.
	 * This method can assume that the provided instance is never null as this is being taken care of upstream.
	 */
	abstract protected T fill(T instance, String[] line) throws IllegalArgumentException;
	
	/**
	 * @param headerLine The CSV file header line
	 * @param line A line of the CSV file
	 * @return The UUID
	 * @throws IllegalArgumentException
	 */
	public static String getUuid(String[] headerLine, String[] line) throws IllegalArgumentException {
		String str = line[getColumn(headerLine, HEADER_UUID)];
		if (!StringUtils.isEmpty(str)) {
			str = UUID.fromString(str).toString();
		}
		return str;
	}
	
	protected String getUuid(String[] line) throws IllegalArgumentException {
		return getUuid(headerLine, line);
	}
	
	/*
	 * This is basically a map between a localized header and its found locales.
	 * From |Name:en|Name:km_KH| this would be a map between 'name' and [en, km_KH].
	 */
	protected static class LocalizedHeader {
		
		private String header; // the localized header name, so just 'Name'
		
		private Set<Locale> locales = new HashSet<Locale>(); // the entries locale-i18n name for each locale, eg: en-Name:en
		
		public LocalizedHeader(String header) {
			this.header = header;
		}
		
		public String getHeader() {
			return header;
		}
		
		public String getI18nHeader(Locale locale) {
			String i18nHeader = getHeader() + LOCALE_SEPARATOR + locale.toString();
			if (locales.contains(locale)) {
				return i18nHeader;
			} else {
				throw new IllegalArgumentException("The header requested was never decorated with the specified locale: '"
				        + i18nHeader + "'");
			}
		}
		
		public Set<Locale> getLocales() {
			return locales;
		}
	}
	
	/**
	 * From a set of i18n headers, such as Name:en, Name:km_KH, Description:en, Description:km_KH it
	 * returns a map with the localized name as keys, so here: 'Name' and 'Description' and
	 * {@link LocalizedHeader} instances as values. Each {@link LocalizedHeader} carries the link
	 * between a localized name and the possible locales that where found for that name.
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
					throw new IllegalArgumentException("The CSV header line cannot contains twice the same header: '"
					        + i18nHeader + "'");
				}
				l10nHeader.getLocales().add(locale);
			}
		}
		return l10nHeaderMap;
	}
	
	public LocalizedHeader getLocalizedHeader(String header) {
		if (l10nHeadersMap.containsKey(header.trim().toLowerCase())) {
			return l10nHeadersMap.get(header.trim().toLowerCase());
		} else {
			throw new IllegalArgumentException("The CSV header line does not contain the requested localized header '"
			        + header + "': " + Arrays.toString(headerLine));
		}
	}
	
	/**
	 * @param headerLine The header line the processor will refer to.
	 */
	public BaseLineProcessor(String[] headerLine, S service) {
		this.service = service;
		this.headerLine = headerLine;
		this.indexMap = createIndexMap(headerLine);
		this.l10nHeadersMap = getLocalizedHeadersMap(headerLine);
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
				throw new IllegalArgumentException("The CSV header line cannot contains twice the same header: '" + header
				        + "'");
			}
			indexMap.put(header.trim().toLowerCase(), col);
			col++;
		}
		return indexMap;
	}
	
	public int getColumn(String header) throws IllegalArgumentException {
		return getColumn(indexMap, header.trim().toLowerCase());
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
			throw new IllegalArgumentException("The CSV header line contains multiple fields '" + fieldLhs + "': "
			        + headerLine.toString());
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
	 * @return The order, eg. "100" out of "_order:100", or null if the order could not be parsed as
	 *         an int.
	 * @throws IllegalArgumentException
	 */
	public static Integer getOrder(String[] headerLine) throws IllegalArgumentException {
		String str = getMetadataValue(headerLine, ORDER_LHS);
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
