package org.openmrs.module.initializer.api.c;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.initializer.api.BaseLineProcessor;

public class LocalizedHeader {
	
	final public static String LOCALE_SEPARATOR = ":";
	
	private String header; // the localized header name, eg. 'Name'
	
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
	
	/**
	 * @see #getLocalizedHeadersMap(String[])
	 */
	public static LocalizedHeader getLocalizedHeader(String[] headerLine, String header) {
		Map<String, LocalizedHeader> l10nHeadersMap = getLocalizedHeadersMap(headerLine);
		if (l10nHeadersMap.containsKey(header.trim().toLowerCase())) {
			return l10nHeadersMap.get(header.trim().toLowerCase());
		} else {
			return new LocalizedHeader("");
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
	public static Map<String, LocalizedHeader> getLocalizedHeadersMap(String[] headerLine) {
		
		Map<String, LocalizedHeader> l10nHeaderMap = new HashMap<String, LocalizedHeader>();
		
		for (String i18nHeader : headerLine) {
			if (i18nHeader.startsWith(BaseLineProcessor.METADATA_PREFIX)) {
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
	
}
