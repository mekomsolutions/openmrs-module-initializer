/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import static org.openmrs.module.initializer.InitializerConstants.DIR_NAME_CHECKSUM;
import static org.openmrs.module.initializer.InitializerConstants.DIR_NAME_CONFIG;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String LOCK_NAME = "initializer";
	
	private InitializerConfig cfg;
	
	private final Map<String, Object> keyValueCache = new HashMap<String, Object>();
	
	private InitializerDAO initializerDAO;
	
	private volatile Map<String, String> allChecksumsCache = null;
	
	private volatile Map<String, String> fileChecksumsCache = null;
	
	private AdministrationService adminService;
	
	private final Path configDirPath;
	
	private final Path basePath;
	
	private final Path checksumDirPath;
	
	public InitializerServiceImpl() {
		basePath = Paths.get(new File(OpenmrsUtil.getApplicationDataDirectory()).toURI());
		configDirPath = basePath.resolve(DIR_NAME_CONFIG);
		checksumDirPath = basePath.resolve(DIR_NAME_CHECKSUM);
	}
	
	public void setConfig(InitializerConfig cfg) {
		this.cfg = cfg;
	}
	
	public void setAdminService(AdministrationService adminService) {
		this.adminService = adminService;
	}
	
	/**
	 * Sets the data access object. The initializerDao is used for saving and getting entities to/from
	 * the database
	 * 
	 * @param initializerDAO The data access object to use
	 */
	public void setInitializerDAO(InitializerDAO initializerDAO) {
		this.initializerDAO = initializerDAO;
	}
	
	public Path getBasePath() {
		return basePath;
	}
	
	//Path based concatenation using Path/Paths resolve method
	//https://docs.oracle.com/javase/tutorial/essential/io/pathOps.html#resolve
	
	@Override
	public String getConfigDirPath() {
		return configDirPath.toString();
	}
	
	@Override
	public String getChecksumsDirPath() {
		return checksumDirPath.toString();
	}
	
	@Override
	public List<Loader> getLoaders() {
		return Context.getRegisteredComponents(Loader.class).stream().filter(l -> !l.isPreLoader()).sorted()
		        .collect(Collectors.toList());
	}
	
	@Override
	public void loadUnsafe(boolean applyFilters, boolean doThrow) throws Exception {
		boolean lockAcquired = false;
		
		try {
			log.info("Waiting for initializer lock...");
			getSelf().acquireLockOrWait(LOCK_NAME, 15 * 60 * 1000); // 15 minutes
			lockAcquired = true;
			
			// Check if config changed
			if (!getSelf().hasChecksumsChanged()) {
				log.info("No config changes... skipping initializer");
				return;
			}
			
			// Run Initializer
			log.info("OpenMRS config loading process started...");
			final Set<String> specifiedDomains = applyFilters ? cfg.getFilteredDomains() : Collections.emptySet();
			final boolean includeSpecifiedDomains = !applyFilters || cfg.isInclusionList();
			
			for (Loader loader : getLoaders()) {
				boolean domainSpecified = specifiedDomains.contains(loader.getDomainName());
				if (specifiedDomains.isEmpty() || ((includeSpecifiedDomains && domainSpecified)
				        || (!includeSpecifiedDomains && !domainSpecified))) {
					
					final List<String> wildcardExclusions = applyFilters ? cfg.getWidlcardExclusions(loader.getDomainName())
					        : Collections.emptyList();
					loader.loadUnsafe(wildcardExclusions, doThrow);
				}
			}
			
			log.info("OpenMRS config loading process completed.");
			getSelf().clearChecksumsCache(); // Free up memory
		}
		finally {
			if (lockAcquired) {
				getSelf().releaseLock(LOCK_NAME);
				log.info("Initializer lock released");
			}
		}
	}
	
	/*
	 * This is a mere wrapper of the unsafe version when it doesn't throw anyway.
	 */
	@Override
	public void load() {
		try {
			loadUnsafe(true, false);
		}
		catch (Exception e) {}
	}
	
	@Override
	public void addKeyValue(String key, String value) {
		keyValueCache.put(key, value);
	}
	
	@Override
	public void addKeyValues(InputStream is) throws Exception {
		keyValueCache.putAll((new ObjectMapper()).readValue(is, Map.class));
	}
	
	@Override
	public String getValueFromKey(String key) {
		Object value = keyValueCache.get(key);
		try {
			return Utils.asString(value);
		}
		catch (Exception e) {
			log.error(null, e);
		}
		return "";
	}
	
	@Override
	public Concept getConceptFromKey(String key, Concept defaultInstance) {
		String val = getValueFromKey(key);
		if (StringUtils.isEmpty(val)) {
			return defaultInstance;
		}
		Concept instance = Utils.fetchConcept(val, Context.getConceptService());
		if (instance != null) {
			return instance;
		} else {
			return defaultInstance;
		}
	}
	
	@Override
	public Concept getConceptFromKey(String key) {
		return getConceptFromKey(key, null);
	}
	
	@Override
	public List<Concept> getConceptsFromKey(String key) {
		List<String> ids;
		try {
			ids = Utils.asStringList(getValueFromKey(key));
		}
		catch (Exception e) {
			log.error("The JSON value for key '" + key + "' could not be parsed as a list of concept identifiers.", e);
			return Collections.emptyList();
		}
		List<Concept> concepts = new ArrayList<Concept>();
		for (String id : ids) {
			concepts.add(Utils.fetchConcept(id, Context.getConceptService()));
		}
		return concepts;
	}
	
	@Override
	public PersonAttributeType getPersonAttributeTypeFromKey(String key, PersonAttributeType defaultInstance) {
		String val = getValueFromKey(key);
		if (StringUtils.isEmpty(val)) {
			return defaultInstance;
		}
		PersonAttributeType instance = Utils.fetchPersonAttributeType(val, Context.getPersonService());
		if (instance != null) {
			return instance;
		} else {
			return defaultInstance;
		}
	}
	
	@Override
	public PersonAttributeType getPersonAttributeTypeFromKey(String key) {
		return getPersonAttributeTypeFromKey(key, null);
	}
	
	@Override
	public Boolean getBooleanFromKey(String key, Boolean defaultInstance) {
		String val = getValueFromKey(key);
		if (StringUtils.isEmpty(val)) {
			return defaultInstance;
		}
		try {
			return BooleanUtils.toBoolean(val, "1", "0");
		}
		catch (IllegalArgumentException e) {
			return BooleanUtils.toBooleanObject(val);
		}
	}
	
	@Override
	public Boolean getBooleanFromKey(String key) {
		return getBooleanFromKey(key, null);
	}
	
	@Override
	public InitializerConfig getInitializerConfig() {
		return cfg;
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#getUnretiredConceptsByFullySpecifiedName(String)
	 */
	@Override
	public List<Concept> getUnretiredConceptsByFullySpecifiedName(String name) {
		return initializerDAO.getUnretiredConceptsByFullySpecifiedName(name);
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#hasChecksumsChanged()
	 */
	@Transactional(readOnly = true)
	@Override
	public boolean hasChecksumsChanged() {
		Map<String, String> db = getSavedChecksums();
		Map<String, String> fs = getFileChecksums();
		
		return !db.equals(fs);
	}
	
	@Override
	public void clearChecksumsCache() {
		allChecksumsCache = null;
		fileChecksumsCache = null;
	}
	
	@Override
	public void clearChecksums() {
		clearChecksumsCache();
		initializerDAO.clearChecksums();
	}
	
	@Override
	public void deleteChecksum(Path path) {
		Path base = Paths.get(getConfigDirPath());
		Path rel = base.relativize(path);
		String checksumPath = rel.toString().replace(File.separator, "/");
		initializerDAO.deleteChecksum(checksumPath);
	}
	
	@Override
	public void deleteChecksums(String domain) {
		initializerDAO.deleteChecksumsStartingWith(domain + "/");
	}
	
	@Transactional(readOnly = true)
	@Override
	public Map<String, String> getSavedChecksums() {
		if (allChecksumsCache != null) {
			return allChecksumsCache;
		}
		
		Map<String, String> map = new HashMap<>();
		initializerDAO.getAllChecksums().forEach(c -> {
			map.put(c.getFilePath(), c.getChecksum());
		});
		
		allChecksumsCache = map;
		return map;
	}
	
	@Override
	public Map<String, String> getFileChecksums() {
		if (fileChecksumsCache != null) {
			return fileChecksumsCache;
		}
		
		Path base = Paths.get(getConfigDirPath());
		
		try (Stream<Path> stream = Files.walk(base)) {
			Map<String, String> fileChecksums = stream.filter(Files::isRegularFile).collect(Collectors.toMap(path -> {
				Path rel = base.relativize(path);
				return rel.toString().replace(File.separator, "/");
			}, this::getFileChecksum));
			fileChecksumsCache = fileChecksums;
			return fileChecksums;
		}
		catch (IOException | RuntimeException e) {
			throw new RuntimeException("Failed to scan configuration directory", e);
		}
	}
	
	@Override
	public String getFileChecksum(Path path) {
		try (InputStream is = Files.newInputStream(path)) {
			return DigestUtils.md5Hex(is);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to compute checksum for " + path, e);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public InitializerChecksum getChecksumIfChanged(Path path) {
		Path base = Paths.get(getConfigDirPath());
		Path rel = base.relativize(path);
		String checksumPath = rel.toString().replace(File.separator, "/");
		
		// Getting cached results
		String fileChecksum = getFileChecksums().get(checksumPath);
		if (fileChecksum == null) {
			fileChecksum = getFileChecksum(path);
		}
		String checksum = getSavedChecksums().get(checksumPath);
		if (checksum != null && checksum.equals(fileChecksum)) {
			return null;
		}
		return new InitializerChecksum(checksumPath, fileChecksum);
	}
	
	@Transactional
	@Override
	public void saveOrUpdateChecksum(InitializerChecksum checksum) {
		if (allChecksumsCache != null) {
			allChecksumsCache.put(checksum.getFilePath(), checksum.getChecksum());
		}
		initializerDAO.saveOrUpdateChecksum(checksum);
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#tryAcquireLock(String)
	 */
	@Override
	@Transactional
	public Boolean tryAcquireLock(String lockName) {
		initializerDAO.removeExpiredLock(lockName);
		Date lockUntil = Date.from(Instant.now().plus(15, ChronoUnit.MINUTES));
		
		return initializerDAO.tryAcquireLock(lockName, lockUntil, getHostname());
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#acquireLockOrWait(String, long
	 */
	@Override
	public void acquireLockOrWait(String lockName, long timeoutMillis) {
		long start = System.currentTimeMillis();
		
		while (true) {
			if (getSelf().tryAcquireLock(lockName)) {
				return;
			}
			
			if (System.currentTimeMillis() - start > timeoutMillis) {
				throw new RuntimeException("Timeout waiting for lock: " + lockName);
			}
			
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Interrupted while waiting for lock", e);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#releaseLock(String)
	 */
	@Override
	@Transactional
	public void releaseLock(String lockName) {
		initializerDAO.deleteLock(lockName);
	}
	
	/**
	 * Intended to be used to call this service methods from this service other methods.
	 * 
	 * @return this service with AOP
	 */
	public InitializerService getSelf() {
		// If the service wasn't declared in xml, I would just use @Lazy on setSelf
		return Context.getRegisteredComponent("initializer.InitializerService", InitializerService.class);
	}
	
	@Transactional
	@Override
	public void migrateChecksumsFromFilesToDB() {
		String globalProperty = adminService.getGlobalProperty("initializer.checksumsMigrated");
		if (!"true".equalsIgnoreCase(globalProperty)) {
			// Migrate file checksums, to be removed in later versions
			Path checksumsBase = Paths.get(getChecksumsDirPath());
			Path base = Paths.get(getConfigDirPath());
			try (Stream<Path> stream = Files.walk(base)) {
				stream.filter(Files::isRegularFile).forEach(path -> {
					String relConfigFilePath = base.relativize(path).toString();
					//Replace all '/' with '_' except for the first one separating domain
					relConfigFilePath = StringUtils.substringBefore(relConfigFilePath, File.separator) + File.separator
					        + StringUtils.substringAfter(relConfigFilePath, File.separator).replace(File.separator, "_");
					String checksumFilePath = FilenameUtils.removeExtension(relConfigFilePath) + "."
					        + ConfigDirUtil.CHECKSUM_FILE_EXT;
					File checksumFile = checksumsBase.resolve(checksumFilePath).toFile();
					if (checksumFile.exists()) {
						try {
							String checksum = FileUtils.readFileToString(checksumFile, "UTF-8");
							Path rel = base.relativize(path);
							String filePath = rel.toString().replace(File.separator, "/");
							
							saveOrUpdateChecksum(new InitializerChecksum(filePath, checksum));
							if (!checksumFile.delete()) {
								log.warn("Checksum file {} was not deleted", checksumFile);
							}
						}
						catch (IOException e) {
							log.warn("Could not migrate checksum file {}", checksumFile, e);
						}
					}
				});
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			adminService.saveGlobalProperty(new GlobalProperty("initializer.checksumsMigrated", "true"));
		}
	}
	
	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return "unknown";
		}
	}
}
