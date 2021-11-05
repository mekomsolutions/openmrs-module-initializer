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
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.messagesource.impl.CachedMessageSource;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ResourceBundleMessageSource extends ReloadableResourceBundleMessageSource to provide the
 * additional features of a MutableMessageSource.
 * 
 * @see <a href=
 *      "https://talk.openmrs.org/t/address-hierarchy-support-for-i18n/10415/19?u=mksd">...</a>
 */
public class InitializerMessageSource extends ReloadableResourceBundleMessageSource implements MutableMessageSource, ApplicationContextAware {
	
	private static final Logger log = LoggerFactory.getLogger(MutableResourceBundleMessageSource.class);
	
	public static final String[] CLASSPATH_RESOURCES_TO_SCAN = { "classpath*:messages*.properties" };
	
	public static final String[] DOMAINS_TO_SCAN = { InitializerConstants.DOMAIN_ADDR, InitializerConstants.DOMAIN_MSGPROP };
	
	public static final String PROPERTIES_EXTENSION = "properties";
	
	@Autowired
	protected InitializerService iniz;
	
	@Autowired
	protected InitializerConfig cfg;
	
	protected Map<Locale, Map<String, Properties>> resourcesByLocale = new ConcurrentHashMap<>();
	
	protected CachedMessageSource presentationCache = new CachedMessageSource();
	
	/**
	 * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		MessageSourceService svc = (MessageSourceService) context.getBean("messageSourceServiceTarget");
		MessageSource parentSource = svc.getActiveMessageSource().getParentMessageSource();
		setParentMessageSource(parentSource);
		svc.setActiveMessageSource(this);
	}
	
	/**
	 * This method is called automatically by Spring when this Bean is wired in. See application context
	 * xml
	 */
	public void refreshCache() {
		setUseCodeAsDefaultMessage(true);
		setFallbackToSystemLocale(true);
		setDefaultEncoding("UTF-8");
		setBaseNamesAndAvailableLocales();
	}
	
	/**
	 * Locates each of the message resources, parses these into base names and locales for processing
	 */
	protected synchronized void setBaseNamesAndAvailableLocales() {
		Set<String> baseNames = new TreeSet<>();
		
		// Process resources
		Map<String, Properties> resources = new HashMap<>();
		resources.putAll(getMessagePropertyResourcesFromClasspath());
		resources.putAll(getMessagePropertyResourcesFromFilesystem());
		
		for (String resourceName : resources.keySet()) {
			Properties properties = resources.get(resourceName);
			String nameWithoutExtension = FilenameUtils.removeExtension(resourceName);
			Locale locale = getLocaleFromFileBaseName(nameWithoutExtension);
			String baseName = StringUtils.removeEnd(nameWithoutExtension, "_" + locale.toString());
			baseNames.add(baseName);
			resourcesByLocale.computeIfAbsent(locale, k -> new HashMap<>()).put(resourceName, properties);
			log.trace("Added message properties file: " + resourceName + " (" + baseName + " : " + locale + ")");
			for (Object property : properties.keySet()) {
				String key = property.toString();
				String value = properties.getProperty(key);
				addPresentation(new PresentationMessage(key, locale, value, ""));
			}
		}
		
		setBasenames(baseNames.toArray(new String[0]));
	}
	
	/**
	 * @return an array of message property resource file names found on the filesystem
	 */
	protected Map<String, Properties> getMessagePropertyResourcesFromFilesystem() {
		Map<String, Properties> ret = new HashMap<>();
		for (String domainToScan : DOMAINS_TO_SCAN) {
			ConfigDirUtil configDirUtil = new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(),
			        domainToScan, cfg.skipChecksums());
			List<File> propertiesFiles = configDirUtil.getFiles(PROPERTIES_EXTENSION);
			for (File file : propertiesFiles) {
				Properties properties = new Properties();
				OpenmrsUtil.loadProperties(properties, file);
				ret.put("file:" + file.getAbsolutePath(), properties);
			}
		}
		return ret;
	}
	
	/**
	 * @return an array of message property resource file names found on the classpath
	 */
	protected Map<String, Properties> getMessagePropertyResourcesFromClasspath() {
		Map<String, Properties> ret = new HashMap<>();
		for (String pattern : CLASSPATH_RESOURCES_TO_SCAN) {
			try {
				ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver(OpenmrsClassLoader.getInstance());
				Resource[] coreResources = rpr.getResources(pattern);
				for (Resource resource : coreResources) {
					Properties properties = new Properties();
					OpenmrsUtil.loadProperties(properties, resource.getInputStream());
					ret.put("jar:file:" + resource.getFilename(), properties);
				}
				for (ModuleClassLoader moduleClassLoader : ModuleFactory.getModuleClassLoaders()) {
					Module module = moduleClassLoader.getModule();
					String filePrefix = "jar:file:" + module.getFile().getAbsolutePath() + "!/";
					rpr = new PathMatchingResourcePatternResolver(moduleClassLoader);
					Resource[] moduleResources = rpr.getResources(pattern);
					for (Resource resource : moduleResources) {
						if (resource.getURI().toString().contains("/" + module.getModuleId() + "/")) {
							Properties properties = new Properties();
							OpenmrsUtil.loadProperties(properties, resource.getInputStream());
							ret.put(filePrefix + resource.getFilename(), properties);
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
		String[] parts = baseName.split("_");
		if (parts.length == 1) {
			return Locale.getDefault(); // If no locale is specified, assume the default locale is intended
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
		return resourcesByLocale.keySet();
	}
	
	/**
	 * @see MutableMessageSource#addPresentation(PresentationMessage) NOTE: This does not actually
	 *      affect the underlying message source, so this is really non-functional
	 */
	@Override
	public void addPresentation(PresentationMessage message) {
		presentationCache.addPresentation(message);
	}
	
	/**
	 * @see MutableMessageSource#removePresentation(PresentationMessage) NOTE: This does not actually
	 *      affect the underlying message source, so this is really non-functional
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
}
