/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.loaders;

import static org.hamcrest.Matchers.lessThan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadersIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private JsonKeyValuesLoader jkvLoader;
	
	@Autowired
	private MdsLoader mdsLoader;
	
	@Autowired
	private GlobalPropertiesLoader gpLoader;
	
	@Autowired
	private LocationsLoader locationsLoader;
	
	@Autowired
	private ConceptsLoader conceptsLoader;
	
	@Autowired
	private ProgramsLoader programsLoader;
	
	@Autowired
	private ProgramWorkflowsLoader workflowsLoader;
	
	@Autowired
	private ProgramWorkflowStatesLoader statesLoader;
	
	@Autowired
	private PersonAttributeTypesLoader patLoader;
	
	@Autowired
	private IdentifierSourcesLoader idSourcesLoader;
	
	@Autowired
	private DrugsLoader drugsLoader;
	
	@Autowired
	private OrderFrequenciesLoader freqLoader;
	
	@Autowired
	private PrivilegesLoader privilegesLoader;
	
	@Test
	public void getLoaders_shouldBeUnivoquelyOrdered() {
		
		int count = 1;
		
		Assert.assertThat(jkvLoader.getOrder(), lessThan(mdsLoader.getOrder()));
		count++;
		Assert.assertThat(mdsLoader.getOrder(), lessThan(privilegesLoader.getOrder()));
		count++;
		Assert.assertThat(privilegesLoader.getOrder(), lessThan(gpLoader.getOrder()));
		count++;
		Assert.assertThat(gpLoader.getOrder(), lessThan(locationsLoader.getOrder()));
		count++;
		Assert.assertThat(locationsLoader.getOrder(), lessThan(conceptsLoader.getOrder()));
		count++;
		Assert.assertThat(conceptsLoader.getOrder(), lessThan(programsLoader.getOrder()));
		count++;
		Assert.assertThat(programsLoader.getOrder(), lessThan(workflowsLoader.getOrder()));
		count++;
		Assert.assertThat(workflowsLoader.getOrder(), lessThan(statesLoader.getOrder()));
		count++;
		Assert.assertThat(statesLoader.getOrder(), lessThan(patLoader.getOrder()));
		count++;
		Assert.assertThat(patLoader.getOrder(), lessThan(idSourcesLoader.getOrder()));
		count++;
		Assert.assertThat(idSourcesLoader.getOrder(), lessThan(drugsLoader.getOrder()));
		count++;
		Assert.assertThat(drugsLoader.getOrder(), lessThan(freqLoader.getOrder()));
		count++;
		
		Assert.assertEquals(getService().getLoaders().size(), count);
		
		// System.out.println("Here is the list of loaders in order:");
		// for (Loader l : loaders) {
		// System.out.format("%4d%100s%n", l.getOrder(), l.toString());
		// }
		
	}
}
