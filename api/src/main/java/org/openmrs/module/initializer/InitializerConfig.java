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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openmrs.module.initializer.InitializerConstants.PROPS_DOMAINS;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_EXCLUDE;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_SKIPCHECKSUMS;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR;
import static org.openmrs.module.initializer.api.utils.Utils.getPropertyValue;

/**
 * Contains module's config.
 */
@Component
public class InitializerConfig implements InitializingBean {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean isInclusionList = true;
	
	private Set<String> filteredDomains = new HashSet<>();
	
	private Map<String, List<String>> allWildCardExclusions = new HashMap<>(); // mapped per domain
	
	private Boolean skipChecksums = false;
	
	private String startupLoadingMode = "";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	/**
	 * Initializes the configuration from system properties and runtime properties.
	 */
	public void init() {
		// Inclusion or exclusion list of domains
		String domainsCsv = Optional.ofNullable(getPropertyValue(PROPS_DOMAINS)).orElse("");
		if (StringUtils.startsWith(domainsCsv, "!")) {
			isInclusionList = false;
			domainsCsv = StringUtils.removeStart(domainsCsv, "!");
		}
		filteredDomains.addAll(Arrays.asList(StringUtils.split(domainsCsv, ",")));
		
		@SuppressWarnings("unchecked")
		Collection<String> unsupportedDomains = CollectionUtils.subtract(filteredDomains,
		    Stream.of(Domain.values()).map(d -> d.getName()).collect(Collectors.toSet()));
		if (CollectionUtils.isNotEmpty(unsupportedDomains)) {
			log.warn("Those domains are unknown and are not supported, however they are mentioned in the "
			        + (isInclusionList ? "inclusion" : "exclusion") + " list of domains: " + unsupportedDomains);
		}
		
		// Per-domain wildcard exclusion patterns
		Stream.of(Domain.values()).forEach(d -> {
			String exclusionsCsv = getPropertyValue(PROPS_EXCLUDE + "." + d.getName());
			if (!StringUtils.isEmpty(exclusionsCsv)) {
				allWildCardExclusions.put(d.getName(), Arrays.asList(StringUtils.split(exclusionsCsv, ",")));
			}
		});
		
		// checksums
		skipChecksums = BooleanUtils.toBoolean(Optional.ofNullable(getPropertyValue(PROPS_SKIPCHECKSUMS)).orElse(""));
		
		// Startup Loading Configuration
		startupLoadingMode = getPropertyValue(PROPS_STARTUP_LOAD);
	}
	
	/**
	 * Gets the list of wildcard exclusion patterns for the given domain.
	 * 
	 * @param domain The domain.
	 * @return The list of wildcard exclusion patterns.
	 */
	public List<String> getWidlcardExclusions(Domain domain) {
		return getWidlcardExclusions(domain.getName());
	}
	
	/**
	 * @see #getWidlcardExclusions(Domain)
	 */
	public List<String> getWidlcardExclusions(String domainName) {
		if (allWildCardExclusions.containsKey(domainName)) {
			return allWildCardExclusions.get(domainName);
		} else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * @return The list of domain names being in or ex filtered.
	 */
	public Set<String> getFilteredDomains() {
		return filteredDomains;
	}
	
	/**
	 * @return true if the filtered domains represent an inclusion list, false if the filtered domains
	 *         represent an exclusion list.
	 */
	public boolean isInclusionList() {
		return isInclusionList;
	}
	
	/**
	 * @return true to skip writing checksums, false otherwise
	 */
	public boolean skipChecksums() {
		return skipChecksums;
	}
	
	/**
	 * @return how configuration should be loaded in at startup in the module activator
	 */
	public String getStartupLoadingMode() {
		return StringUtils.isBlank(startupLoadingMode) ? PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR : startupLoadingMode;
	}
}
