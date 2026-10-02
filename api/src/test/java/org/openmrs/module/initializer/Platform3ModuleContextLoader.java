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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;
import org.springframework.util.ClassUtils;

/**
 * Loads the test application context like {@link GenericXmlContextLoader} but leaves out the Spring
 * beans of the optional modules that Initializer is still compiled against a Platform 2.x release
 * of: their moduleApplicationContext.xml is not loaded and the classes that core's component scan
 * of org.openmrs picks up from their API jars are dropped. Those modules have no Platform 3.0
 * compatible release yet and their beans cannot start on Platform 3.0 (Hibernate Criteria API,
 * removed *ServiceTarget beans, unresolvable placeholders...). Their jars are only kept on the test
 * classpath for the plain unit tests of the Initializer classes integrating with them.
 * <p>
 * It also registers the Platform 2.1 implementations that are profiled out on Platform 3.0 when the
 * module providing their replacement is not on the classpath (i.e. when running the tests of
 * initializer-api itself), so that those tests keep running against the same implementations as
 * when they were run on Platform 2.1.
 */
public class Platform3ModuleContextLoader extends GenericXmlContextLoader {
	
	private static final List<String> MODULES_WITHOUT_PLATFORM_3_RELEASE = Arrays.asList("addresshierarchy-api",
	    "appframework-api", "appointments-api", "bahmni.ie.apps-api", "bahmnicore-api", "billing-api", "calculation-api",
	    "datafilter-api", "emrapi-api", "episodes-api", "exti18n-api", "htmlformentry-api", "metadatasharing-api",
	    "openconceptlab-api", "patientflags-api", "providermanagement-api", "queue-api", "reporting-api",
	    "serialization.xstream-api", "tasks-api");
	
	private static final Map<String, String> PLATFORM_2_1_IMPLEMENTATIONS = new LinkedHashMap<>();
	
	static {
		PLATFORM_2_1_IMPLEMENTATIONS.put(
		    "org.openmrs.module.initializer.api.attributes.types.AttributeTypesProxyServiceImpl",
		    "org.openmrs.module.initializer.attributes.types.AttributeTypesProxyServiceImpl2_2");
		PLATFORM_2_1_IMPLEMENTATIONS.put(
		    "org.openmrs.module.initializer.api.attributes.types.AttributeTypeCsvLineHandlerImpl",
		    "org.openmrs.module.initializer.attributes.types.AttributeTypeCsvLineHandlerImpl2_2");
	}
	
	private static final Pattern JAR_NAME = Pattern.compile("/([^/!]+\\.jar)!/");
	
	@Override
	protected void loadBeanDefinitions(GenericApplicationContext context, MergedContextConfiguration mergedConfig) {
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		for (String location : mergedConfig.getLocations()) {
			try {
				for (Resource resource : context.getResources(location)) {
					if (!isFromModuleWithoutPlatform3Release(resource.getURL().toString())) {
						reader.loadBeanDefinitions(resource);
					}
				}
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		for (String beanName : context.getBeanDefinitionNames()) {
			if (isFromModuleWithoutPlatform3Release(context.getBeanDefinition(beanName).getResourceDescription())) {
				context.removeBeanDefinition(beanName);
			}
		}
		PLATFORM_2_1_IMPLEMENTATIONS.forEach((implementation, replacement) -> {
			if (!ClassUtils.isPresent(replacement, context.getClassLoader())) {
				context.registerBeanDefinition(implementation, new RootBeanDefinition(implementation));
			}
		});
	}
	
	private boolean isFromModuleWithoutPlatform3Release(String resource) {
		Matcher m = resource == null ? null : JAR_NAME.matcher(resource);
		if (m == null || !m.find()) {
			return false;
		}
		String jarName = m.group(1);
		return MODULES_WITHOUT_PLATFORM_3_RELEASE.stream().anyMatch(module -> jarName.startsWith(module + "-"));
	}
}
