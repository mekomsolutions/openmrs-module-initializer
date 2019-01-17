/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.initializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.messagesource.PresentationMessageMap;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;

/**
 * Registers the custom message source service
 * 
 * @see https
 *      ://github.com/openmrs/openmrs-module-reporting/blob/037c74949f0e01f5a5cb04c5467912654d808765
 *      /api-tests/src/test/java/org/openmrs/module/reporting/test/CustomMessageSource.java
 * @see https://talk.openmrs.org/t/address-hierarchy-support-for-i18n/10415/19?u=mksd
 */
public class InitializerMessageSource extends AbstractMessageSource implements MutableMessageSource, ApplicationContextAware {
	
	protected static final Log log = LogFactory.getLog(InitializerMessageSource.class);
	
	private Map<Locale, PresentationMessageMap> cache = null;
	
	private boolean showMessageCode = false;
	
	@Autowired
	protected InitializerService iniz;
	
	protected Map<File, Locale> messagePropertiesMap;
	
	public Map<File, Locale> getMessagePropertiesMap() {
		return messagePropertiesMap;
	}
	
	/**
	 * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		MessageSourceService svc = (MessageSourceService) context.getBean("messageSourceServiceTarget");
		MessageSource activeSource = svc.getActiveMessageSource();
		setParentMessageSource(activeSource);
		svc.setActiveMessageSource(this);
	}
	
	/**
	 * @return the cached messages, merged from the custom source and the parent source
	 */
	public synchronized Map<Locale, PresentationMessageMap> getCachedMessages() {
		if (cache == null) {
			refreshCache();
		}
		return cache;
	}
	
	/**
	 * @return all message codes defined in the system
	 */
	public Set<String> getAllMessageCodes() {
		return getAllMessagesByCode().keySet();
	}
	
	/**
	 * @return a Map from code to Map of Locale string to message
	 */
	public Map<String, Map<Locale, PresentationMessage>> getAllMessagesByCode() {
		Map<String, Map<Locale, PresentationMessage>> ret = new TreeMap<String, Map<Locale, PresentationMessage>>();
		Map<Locale, PresentationMessageMap> m = getCachedMessages();
		for (Locale locale : m.keySet()) {
			PresentationMessageMap pmm = m.get(locale);
			for (String code : pmm.keySet()) {
				Map<Locale, PresentationMessage> messagesForCode = ret.get(code);
				if (messagesForCode == null) {
					messagesForCode = new LinkedHashMap<Locale, PresentationMessage>();
					ret.put(code, messagesForCode);
				}
				messagesForCode.put(locale, pmm.get(code));
			}
		}
		return ret;
	}
	
	/**
	 * @param pm the presentation message to add to the cache
	 * @param override if true, should override any existing message
	 */
	public void addPresentationMessageToCache(PresentationMessage pm, boolean override) {
		PresentationMessageMap pmm = getCachedMessages().get(pm.getLocale());
		if (pmm == null) {
			pmm = new PresentationMessageMap(pm.getLocale());
			getCachedMessages().put(pm.getLocale(), pmm);
		}
		if (pmm.get(pm.getCode()) == null || override) {
			pmm.put(pm.getCode(), pm);
		}
	}
	
	/**
	 * Refreshes the cache, merged from the custom source and the parent source
	 */
	public synchronized void refreshCache() {
		
		cache = new HashMap<Locale, PresentationMessageMap>();
		setUseCodeAsDefaultMessage(true);
		
		ConfigDirUtil ahDir = (new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getConfigChecksumsDirPath(),
		        InitializerConstants.DOMAIN_ADDR));
		addMessageProperties(ahDir.getDomainDirPath());
		ConfigDirUtil msgDir = (new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getConfigChecksumsDirPath(),
		        InitializerConstants.DOMAIN_MSGPROP));
		addMessageProperties(msgDir.getDomainDirPath());
		
		if (MapUtils.isEmpty(messagePropertiesMap)) {
			return;
		}
		for (Map.Entry<File, Locale> entry : messagePropertiesMap.entrySet()) {
			
			Locale locale = entry.getValue();
			PresentationMessageMap pmm = new PresentationMessageMap(locale);
			if (cache.containsKey(locale)) {
				pmm = cache.get(locale);
			}
			Properties messages = loadPropertiesFromFile(entry.getKey());
			for (String code : messages.stringPropertyNames()) {
				String message = messages.getProperty(code);
				message = message.replace("{{", "'{{'");
				message = message.replace("}}", "'}}'");
				pmm.put(code, new PresentationMessage(code, locale, message, null));
			}
			cache.put(locale, pmm);
		}
	}
	
	/**
	 * Scans a directory for possible message properties files and adds it to the internal map.
	 * 
	 * @param dirPath The directory to scan.
	 */
	public void addMessageProperties(String dirPath) {
		
		final File[] propFiles = new File(dirPath).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String ext = FilenameUtils.getExtension(name);
				if (StringUtils.isEmpty(ext)) { // to be safe, ext can only be null if name is null
					return false;
				}
				if (ext.equals("properties")) {
					return true; // filtering only "*.properties" files
				}
				return false;
			}
		});
		
		if (propFiles != null) {
			if (MapUtils.isEmpty(messagePropertiesMap)) {
				messagePropertiesMap = new LinkedHashMap<File, Locale>();
			}
			for (File file : propFiles) {
				// Now reading the locale info out of the base name
				String baseName = FilenameUtils.getBaseName(file.getName()); // "messages_en_GB"
				String localeStr = baseName.substring(baseName.indexOf("_") + 1); // "en_GB"
				try {
					messagePropertiesMap.put(file, LocaleUtils.toLocale(localeStr));
				}
				catch (IllegalArgumentException e) {
					log.error("The locale could not be implied from the message properties file provided: " + file.getPath(),
					    e);
				}
			}
		}
	}
	
	/**
	 * @see MutableMessageSource#getLocales()
	 */
	@Override
	public Collection<Locale> getLocales() {
		MutableMessageSource m = getMutableParentSource();
		Set<Locale> s = new HashSet<Locale>(m.getLocales());
		s.addAll(cache.keySet());
		return s;
	}
	
	/**
	 * @see MutableMessageSource#publishProperties(Properties, String, String, String, String)
	 */
	@SuppressWarnings("deprecation")
	public void publishProperties(Properties props, String locale, String namespace, String name, String version) {
		try {
			Class c = getMutableParentSource().getClass();
			Method m = c.getMethod("publishProperties", Properties.class, String.class, String.class, String.class,
			    String.class);
			m.invoke(getMutableParentSource(), props, locale, namespace, name, version);
		}
		catch (Exception e) {
			// DO NOTHING
		}
	}
	
	/**
	 * @see MutableMessageSource#getPresentations()
	 */
	@Override
	public Collection<PresentationMessage> getPresentations() {
		Collection<PresentationMessage> ret = new ArrayList<PresentationMessage>();
		for (PresentationMessageMap pmm : getCachedMessages().values()) {
			ret.addAll(pmm.values());
		}
		return ret;
	}
	
	/**
	 * @see MutableMessageSource#getPresentationsInLocale(Locale)
	 */
	@Override
	public Collection<PresentationMessage> getPresentationsInLocale(Locale locale) {
		PresentationMessageMap pmm = getCachedMessages().get(locale);
		if (pmm == null) {
			return new HashSet<PresentationMessage>();
		}
		return pmm.values();
	}
	
	/**
	 * @see MutableMessageSource#addPresentation(PresentationMessage)
	 */
	@Override
	public void addPresentation(PresentationMessage message) {
		addPresentationMessageToCache(message, true);
	}
	
	/**
	 * @see MutableMessageSource#getPresentation(String, Locale)
	 */
	@Override
	public PresentationMessage getPresentation(String code, Locale locale) {
		PresentationMessageMap pmm = getCachedMessages().get(locale);
		if (pmm == null) {
			return null;
		}
		return pmm.get(code);
	}
	
	/**
	 * @see MutableMessageSource#removePresentation(PresentationMessage)
	 */
	@Override
	public void removePresentation(PresentationMessage message) {
		PresentationMessageMap pmm = getCachedMessages().get(message.getLocale());
		if (pmm != null) {
			pmm.remove(message.getCode());
		}
		getMutableParentSource().removePresentation(message);
	}
	
	/**
	 * @see MutableMessageSource#merge(MutableMessageSource, boolean)
	 */
	@Override
	public void merge(MutableMessageSource fromSource, boolean overwrite) {
		getMutableParentSource().merge(fromSource, overwrite);
	}
	
	/**
	 * @see AbstractMessageSource#resolveCode(String, Locale)
	 */
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		if (showMessageCode) {
			return new MessageFormat(code);
		}
		PresentationMessage pm = getPresentation(code, locale); // Check exact match
		if (pm == null) {
			if (locale.getVariant() != null) {
				pm = getPresentation(code, new Locale(locale.getLanguage(), locale.getCountry())); // Try to match
				                                                                                   // language and
				                                                                                   // country
				if (pm == null) {
					pm = getPresentation(code, new Locale(locale.getLanguage())); // Try to match language only
				}
			}
		}
		if (pm != null) {
			return new MessageFormat(pm.getMessage());
		}
		return null;
	}
	
	/**
	 * For some reason, this is needed to get the default text option in message tags working properly
	 * 
	 * @see AbstractMessageSource#getMessageInternal(String, Object[], Locale)
	 */
	@Override
	protected String getMessageInternal(String code, Object[] args, Locale locale) {
		String s = super.getMessageInternal(code, args, locale);
		if (s == null || s.equals(code)) {
			return null;
		}
		return s;
	}
	
	/**
	 * Convenience method to get the parent message source as a MutableMessageSource
	 */
	public MutableMessageSource getMutableParentSource() {
		return (MutableMessageSource) getParentMessageSource();
	}
	
	public static Properties loadPropertiesFromFile(File propFile) {
		Properties ret = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(propFile);
			ret.load(new InputStreamReader(is, StandardCharsets.UTF_8));
		}
		catch (Exception e) {
			log.error("There was an error while attempting to read properties file at : " + propFile.getPath(), e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		return ret;
	}
}
