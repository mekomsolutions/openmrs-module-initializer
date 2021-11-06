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
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.openmrs.module.initializer.InitializerConstants.DOMAIN_ADDR;
import static org.openmrs.module.initializer.InitializerConstants.DOMAIN_MSGPROP;

/**
 * ResourceBundleMessageSource extends ReloadableResourceBundleMessageSource to provide the
 * additional features of a MutableMessageSource.
 * 
 * @see <a href=
 *      "https://talk.openmrs.org/t/address-hierarchy-support-for-i18n/10415/19?u=mksd">...</a>
 */
public class InitializerMessageSource extends AbstractMessageSource implements MutableMessageSource, ApplicationContextAware {
	
	private static final Logger log = LoggerFactory.getLogger(MutableResourceBundleMessageSource.class);
	
	private final List<String> classpathPatternsToScan = Arrays.asList("classpath*:messages*.properties");
	
	private final List<String> domainsToScan = Arrays.asList(DOMAIN_ADDR, DOMAIN_MSGPROP);
	
	public static final String PROPERTIES_EXTENSION = "properties";
	
	@Autowired
	protected InitializerService iniz;
	
	@Autowired
	protected InitializerConfig cfg;
	
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
	public void initialize() {
		setUseCodeAsDefaultMessage(true);
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
	
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		if (locale == null) {
			return resolveCodeWithoutArguments(code, Locale.getDefault());
		}
		// If an exact match is found in the requested locale, return it
		PresentationMessage pm = getPresentation(code, locale);
		if (pm != null) {
			return pm.getMessage();
		}
		// Otherwise, try to find the best matching locale with a message
		if (StringUtils.isNotEmpty(locale.getVariant())) {
			return resolveCodeWithoutArguments(code, new Locale(locale.getLanguage(), locale.getCountry()));
		} else if (StringUtils.isNotEmpty(locale.getCountry())) {
			return resolveCodeWithoutArguments(code, new Locale(locale.getLanguage()));
		} else {
			pm = getPresentation(code, Locale.getDefault());
			if (pm != null) {
				return pm.getMessage();
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
	protected synchronized void refreshCache() {
		Map<String, Properties> resources = new LinkedHashMap<>();
		resources.putAll(getMessagePropertyResourcesFromClasspath());
		resources.putAll(getMessagePropertyResourcesFromFilesystem()); // Filesystem takes precedence
		
		for (String resourceName : resources.keySet()) {
			Properties properties = resources.get(resourceName);
			String nameWithoutExtension = FilenameUtils.removeExtension(resourceName);
			Locale locale = getLocaleFromFileBaseName(nameWithoutExtension);
			String baseName = StringUtils.removeEnd(nameWithoutExtension, "_" + locale.toString());
			log.trace("Added message properties file: " + resourceName + " (" + baseName + " : " + locale + ")");
			for (Object property : properties.keySet()) {
				String key = property.toString();
				String value = properties.getProperty(key);
				addPresentation(new PresentationMessage(key, locale, value, ""));
			}
		}
	}
	
	/**
	 * @return an array of message property resource file names found on the filesystem
	 */
	protected Map<String, Properties> getMessagePropertyResourcesFromFilesystem() {
		Map<String, Properties> ret = new LinkedHashMap<>();
		for (String domain : getDomainsToScan()) {
			ConfigDirUtil dirUtil = new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), domain, true);
			List<File> propFiles = dirUtil.getFiles(PROPERTIES_EXTENSION);
			for (File file : propFiles) {
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
		Map<String, Properties> ret = new LinkedHashMap<>();
		for (String pattern : getClasspathPatternsToScan()) {
			try {
				ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver(OpenmrsClassLoader.getInstance());
				Resource[] coreResources = rpr.getResources(pattern);
				
				// Load core first as the initial set of message codes
				log.trace("Adding openmrs core message properties");
				for (Resource resource : coreResources) {
					Properties properties = new Properties();
					OpenmrsUtil.loadProperties(properties, resource.getInputStream());
					ret.put(resource.getURI().toString(), properties);
					log.trace("Added " + properties.size() + " from " + resource.getURI().toString());
				}
				
				// Load modules in their startup order, so these can overwrite each other where appropriate
				for (Module module : ModuleFactory.getStartedModulesInOrder()) {
					ModuleClassLoader moduleClassLoader = ModuleFactory.getModuleClassLoader(module);
					log.trace("Adding module message properties: " + module.getModuleId());
					rpr = new PathMatchingResourcePatternResolver(moduleClassLoader);
					Resource[] moduleResources = rpr.getResources(pattern);
					for (Resource resource : moduleResources) {
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
	
	public void addClasspathPatternToScan(String classpathPatternToScan) {
		classpathPatternsToScan.add(classpathPatternToScan);
	}
	
	public List<String> getDomainsToScan() {
		return domainsToScan;
	}
	
	public void addDomainToScan(String domainToScan) {
		domainsToScan.add(domainToScan);
	}
}
