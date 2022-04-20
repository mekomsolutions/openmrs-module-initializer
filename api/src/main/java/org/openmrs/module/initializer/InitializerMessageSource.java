/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.messagesource.impl.CachedMessageSource;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.OrderedPropertiesFile;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Message Source that is intended to replace the OpenMRS core message source and provide
 * enhanced capabilities. This message source loads in messages from OpenMRS core, followed by those
 * defined in each module in the order in which each is started, and finally by those defined in
 * configured Initializer domains. Messages are loaded in this order to enable predictable and
 * expected overrides. Thus, messages defined with the same code and Locale in Initializer domains
 * will take first precedence, followed by those in modules, and finally by those in OpenMRS core.
 * Ultimately, a Map backs this source, with Locale and message code as keys to the Map, and thus
 * values loaded later will replace those loaded earlier in the process. The requested Locale always
 * takes precedence over the order of sources. For example, if a message is requested for Locale
 * 'fr_FR', all sources are first searched for a match to 'fr_FR' prior to then searching for 'fr'
 * across all sources. If no match is found in a given Locale, then the fallback is to search for
 * the best match in the System Locale. If no match is found in the System Locale, then the message
 * code itself is returned as a final fallback. This source allows for supporting additional
 * fallback languages, as well as defining additional classpath patterns and domains to search for
 * message property files. See method javadoc for addClasspathPatternToScan, addDomainToScan, and
 * addFallbackLanguage for further details.
 * 
 * @see <a href=
 *      "https://talk.openmrs.org/t/address-hierarchy-support-for-i18n/10415/19?u=mksd">...</a>
 */
@Component("initializer.InitializerMessageSource")
public class InitializerMessageSource extends AbstractMessageSource implements MutableMessageSource {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String PROPERTIES_EXTENSION = "properties";
	
	private final List<String> classpathPatternsToScan = new ArrayList<>();
	
	private final List<String> domainsToScan = new ArrayList<>();
	
	private final Map<String, String> fallbackLanguages = new ConcurrentHashMap<>();
	
	@Autowired
	protected InitializerService iniz;
	
	protected CachedMessageSource presentationCache = new CachedMessageSource();
	
	@PostConstruct
	public void initialize() {
		setUseCodeAsDefaultMessage(true);
		addClasspathPatternToScan("classpath*:messages*.properties");
		addDomainToScan(InitializerConstants.DOMAIN_ADDR);
		addDomainToScan(InitializerConstants.DOMAIN_MSGPROP);
		refreshCache();
	}
	
	/**
	 * @see AbstractMessageSource#resolveCode(String, Locale)
	 */
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String message = resolveCodeWithoutArguments(code, locale);
		if (message != null) {
			return new MessageFormat(message);
		}
		return null;
	}
	
	/**
	 * @see AbstractMessageSource#resolveCodeWithoutArguments(String, Locale)
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		Set<Locale> localesAttempted = new HashSet<>();
		String message = resolveCodeWithoutArguments(code, locale, localesAttempted);
		if (message != null) {
			return message;
		}
		message = resolveCodeWithoutArguments(code, LocaleUtility.getDefaultLocale(), localesAttempted);
		if (message != null) {
			return message;
		}
		return resolveCodeWithoutArguments(code, Locale.getDefault(), localesAttempted);
	}
	
	/**
	 * Implementation of the resolveCodeWithoutArguments method that allows recursion into less specific
	 * variants of the given locale, while not checking any locales that have already been attempted
	 */
	protected String resolveCodeWithoutArguments(String code, Locale locale, Set<Locale> localesAttempted) {
		if (locale != null && !localesAttempted.contains(locale)) {
			localesAttempted.add(locale);
			// If an exact match is found in the requested locale, return it
			PresentationMessage pm = getPresentation(code, locale);
			if (pm != null) {
				return pm.getMessage();
			}
			// Otherwise, try to find the best matching locale with a message
			if (StringUtils.isNotEmpty(locale.getVariant())) {
				Locale countryLocale = new Locale(locale.getLanguage(), locale.getCountry());
				return resolveCodeWithoutArguments(code, countryLocale, localesAttempted);
			} else if (StringUtils.isNotEmpty(locale.getCountry())) {
				Locale languageLocale = new Locale(locale.getLanguage());
				return resolveCodeWithoutArguments(code, languageLocale, localesAttempted);
			} else if (getFallbackLanguages().containsKey(locale.getLanguage())) {
				Locale fallbackLanguage = new Locale(getFallbackLanguages().get(locale.getLanguage()));
				return resolveCodeWithoutArguments(code, fallbackLanguage, localesAttempted);
			}
		}
		return null;
	}
	
	/**
	 * Locates each of the message resources, and loads them into a cache. The strategy uses a Map,
	 * keyed off of Locale and message code, such that messages detected later in the process will
	 * override those defined earlier in the process if the Locale and code match. The approach is to
	 * load resources in this order, those at the bottom have higher precedene than those above them:
	 * Messages from OpenMRS core Messages from OpenMRS modules, added in the order in which the modules
	 * started Messages defined in Initializer domains
	 */
	public synchronized void refreshCache() {
		log.info("Refreshing message cache");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Map<String, Properties> resources = new LinkedHashMap<>();
		resources.putAll(getMessagePropertyResourcesFromClasspath());
		resources.putAll(getMessagePropertyResourcesFromFilesystem()); // Filesystem takes precedence
		
		for (String resourceName : resources.keySet()) {
			Properties properties = resources.get(resourceName);
			String nameWithoutExtension = FilenameUtils.removeExtension(resourceName);
			Locale locale = getLocaleFromFileBaseName(nameWithoutExtension);
			log.trace("Adding " + properties.size() + " messages from " + resourceName + " in locale: " + locale);
			for (Object property : properties.keySet()) {
				String key = property.toString();
				String value = properties.getProperty(key);
				addPresentation(new PresentationMessage(key, locale, value, ""));
			}
		}
		stopWatch.stop();
		log.info("Refreshing message cache completed. " + presentationCache.getPresentations().size() + " messages in"
		        + +presentationCache.getLocales().size() + " locales in " + stopWatch);
	}
	
	/**
	 * @return an array of message property resource file names found on the filesystem
	 */
	protected Map<String, Properties> getMessagePropertyResourcesFromFilesystem() {
		List<OrderedPropertiesFile> messagePropertyFiles = new ArrayList<>();
		for (String domain : getDomainsToScan()) {
			ConfigDirUtil dirUtil = new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), domain, true);
			List<File> propFiles = dirUtil.getFiles(PROPERTIES_EXTENSION);
			for (File file : propFiles) {
				messagePropertyFiles.add(new OrderedPropertiesFile(file));
			}
		}
		Collections.sort(messagePropertyFiles);
		Map<String, Properties> ret = new LinkedHashMap<>();
		for (OrderedPropertiesFile opf : messagePropertyFiles) {
			ret.put("file:" + opf.getAbsolutePath(), opf.getProperties());
		}
		return ret;
	}
	
	/**
	 * @return an array of message property resource file names found on the classpath
	 */
	protected Map<String, Properties> getMessagePropertyResourcesFromClasspath() {
		Map<String, Properties> ret = new LinkedHashMap<>();
		for (String pattern : getClasspathPatternsToScan()) {
			try {
				ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver(OpenmrsClassLoader.getInstance());
				Resource[] coreResources = rpr.getResources(pattern);
				
				// Load core first as the initial set of message codes
				log.debug("Adding openmrs core message properties");
				for (Resource resource : coreResources) {
					Properties properties = new Properties();
					OpenmrsUtil.loadProperties(properties, resource.getInputStream());
					ret.put(resource.getURI().toString(), properties);
					log.trace("Added " + properties.size() + " from " + resource.getURI().toString());
				}
				
				// Load modules in their startup order, so these can overwrite each other where appropriate
				for (Module module : ModuleFactory.getStartedModulesInOrder()) {
					ModuleClassLoader moduleClassLoader = ModuleFactory.getModuleClassLoader(module);
					log.debug("Adding module message properties: " + module.getModuleId());
					rpr = new PathMatchingResourcePatternResolver(moduleClassLoader);
					Resource[] moduleResources = rpr.getResources(pattern);
					for (Resource resource : moduleResources) {
						// Module classpath entries contain other modules and core version libraries.
						// We need to limit the resources processed to just those defined by this specific module
						if (resource.getURI().toString().contains("/" + module.getModuleId() + "/")) {
							Properties properties = new Properties();
							OpenmrsUtil.loadProperties(properties, resource.getInputStream());
							ret.put("classpath:" + resource.getURI().toString(), properties);
							log.trace("Added " + properties.size() + " from " + resource.getURI().toString());
						}
					}
				}
			}
			catch (IOException e) {
				throw new RuntimeException("Unable to load message property resources from " + pattern, e);
			}
		}
		return ret;
	}
	
	/**
	 * Infers the locale from a message properties file base name.
	 * 
	 * @param baseName A message properties file base name, eg. "my_properties_file_en_GB"
	 * @return The locale, eg. "en_GB"
	 * @throws IllegalArgumentException when no locale could be inferred
	 */
	protected Locale getLocaleFromFileBaseName(String baseName) throws IllegalArgumentException {
		String[] parts = FilenameUtils.getName(baseName).split("_");
		if (parts.length == 1) {
			// If no locale is specified, assume the default locale is intended, at only the language level
			return LocaleUtils.toLocale(Locale.getDefault().getLanguage());
		}
		String candidate = null;
		for (int i = parts.length - 1; i > 0; i--) {
			candidate = parts[i] + (candidate == null ? "" : "_" + candidate);
			try {
				return LocaleUtils.toLocale(candidate);
			}
			catch (IllegalArgumentException e) {
				log.trace(candidate + " is not a valid locale");
			}
		}
		String msg = "No valid locale could be inferred from the following file base name: '" + baseName + "'.";
		throw new IllegalArgumentException(msg);
	}
	
	/**
	 * @see MutableMessageSource#getLocales()
	 */
	@Override
	public Collection<Locale> getLocales() {
		return presentationCache.getLocales();
	}
	
	/**
	 * @see MutableMessageSource#addPresentation(PresentationMessage)
	 */
	@Override
	public void addPresentation(PresentationMessage message) {
		presentationCache.addPresentation(message);
	}
	
	/**
	 * @see MutableMessageSource#removePresentation(PresentationMessage)
	 */
	@Override
	public void removePresentation(PresentationMessage message) {
		presentationCache.removePresentation(message);
	}
	
	/**
	 * @see MutableMessageSource#getPresentations()
	 */
	@Override
	public Collection<PresentationMessage> getPresentations() {
		return presentationCache.getPresentations();
	}
	
	/**
	 * @see MutableMessageSource#getPresentation(String, Locale)
	 */
	@Override
	public PresentationMessage getPresentation(String key, Locale locale) {
		return presentationCache.getPresentation(key, locale);
	}
	
	/**
	 * @see MutableMessageSource#getPresentationsInLocale(Locale)
	 */
	@Override
	public Collection<PresentationMessage> getPresentationsInLocale(Locale locale) {
		return presentationCache.getPresentationsInLocale(locale);
	}
	
	/**
	 * @see MutableMessageSource#merge(MutableMessageSource, boolean)
	 */
	@Override
	public void merge(MutableMessageSource fromSource, boolean overwrite) {
		presentationCache.merge(fromSource, overwrite);
	}
	
	public List<String> getClasspathPatternsToScan() {
		return classpathPatternsToScan;
	}
	
	/**
	 * One can use this method to add additional classpath resource patterns to scan, if necessary Note,
	 * that one will need to ensure that the refreshCache() method is invoked after adding additional
	 * patterns
	 */
	public void addClasspathPatternToScan(String classpathPatternToScan) {
		classpathPatternsToScan.add(classpathPatternToScan);
	}
	
	public List<String> getDomainsToScan() {
		return domainsToScan;
	}
	
	/**
	 * One can use this method to add additional classpath initializer domains to scan, if necessary
	 * Note, that one will need to ensure that the refreshCache() method is invoked after adding
	 * additional domains
	 */
	public void addDomainToScan(String domainToScan) {
		domainsToScan.add(domainToScan);
	}
	
	public Map<String, String> getFallbackLanguages() {
		return fallbackLanguages;
	}
	
	/**
	 * One can use this method to add a fallback language for a particular language For example, to
	 * indicae that Haitian Kreyol (ht) should fallback to French prior to falling back to the System
	 * locale, you would use this method to indicate that. This does not require refreshCache() to be
	 * called, but will take immediate effect.
	 */
	public void addFallbackLanguage(String language, String fallbackLanguage) {
		fallbackLanguages.put(language, fallbackLanguage);
	}
}
