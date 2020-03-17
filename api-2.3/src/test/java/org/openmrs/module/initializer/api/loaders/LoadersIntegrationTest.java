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

import static org.hamcrest.Matchers.greaterThan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentsServicesDefinitionsLoader;
import org.openmrs.module.initializer.api.appt.specialities.AppointmentsSpecialitiesLoader;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.openmrs.module.initializer.api.c.ConceptClassesLoader;
import org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingsLoader;
import org.openmrs.module.initializer.api.drugs.DrugsLoader;
import org.openmrs.module.initializer.api.et.EncounterTypesLoader;
import org.openmrs.module.initializer.api.freq.OrderFrequenciesLoader;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesLoader;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesLoader;
import org.openmrs.module.initializer.api.loc.LocationsLoader;
import org.openmrs.module.initializer.api.mds.MetadataSetsLoader;
import org.openmrs.module.initializer.api.mds.MetadataSetMembersLoader;
import org.openmrs.module.initializer.api.mdm.MetadataTermMappingsLoader;
import org.openmrs.module.initializer.api.ot.OrderTypesLoader;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypesLoader;
import org.openmrs.module.initializer.api.pit.PatientIdentifierTypesLoader;
import org.openmrs.module.initializer.api.privileges.PrivilegesLoader;
import org.openmrs.module.initializer.api.programs.ProgramsLoader;
import org.openmrs.module.initializer.api.programs.workflows.ProgramWorkflowsLoader;
import org.openmrs.module.initializer.api.programs.workflows.states.ProgramWorkflowStatesLoader;
import org.openmrs.module.initializer.api.roles.RolesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadersIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private JsonKeyValuesLoader jsonKeyValuesLoader;
	
	@Autowired
	private MdsLoader mdsLoader;
	
	@Autowired
	private PatientIdentifierTypesLoader pitLoader;
	
	@Autowired
	private GlobalPropertiesLoader globalPropertiesLoader;
	
	@Autowired
	private AttributeTypesLoader attributeTypesLoader;
	
	@Autowired
	private LocationsLoader locationsLoader;
	
	@Autowired
	private ConceptClassesLoader conceptClassesLoader;
	
	@Autowired
	private ConceptsLoader conceptsLoader;
	
	@Autowired
	private ProgramsLoader programsLoader;
	
	@Autowired
	private ProgramWorkflowsLoader programWorkflowsLoader;
	
	@Autowired
	private ProgramWorkflowStatesLoader programWorkflowStatesLoader;
	
	@Autowired
	private PersonAttributeTypesLoader personAttributeTypesLoader;
	
	@Autowired
	private IdentifierSourcesLoader identifierSourcesLoader;
	
	@Autowired
	private DrugsLoader drugsLoader;
	
	@Autowired
	private OrderFrequenciesLoader orderFrequenciesLoader;
	
	@Autowired
	private PrivilegesLoader privilegesLoader;
	
	@Autowired
	private EncounterTypesLoader encounterTypesLoader;
	
	@Autowired
	private RolesLoader rolesLoader;
	
	@Autowired
	private MetadataTermMappingsLoader metadataTermMappingsLoader;
	
	@Autowired
	private AppointmentsSpecialitiesLoader appointmentsSpecialitiesLoader;
	
	@Autowired
	private AppointmentsServicesDefinitionsLoader appointmentsServiceDefinitionsLoader;
	
	@Autowired
	private OrderTypesLoader orderTypesLoader;
	
	@Autowired
	private DataFilterMappingsLoader dataFilterMappingsLoader;
	
	@Autowired
	private MetadataSetsLoader metadataSetLoader;
	
	@Autowired
	private MetadataSetMembersLoader metadataSetMemberLoader;
	
	@Override
	public void updateSearchIndex() {
		// to prevent Data Filter's 'Illegal Record Access'
	}
	
	@Test
	public void getLoaders_shouldBeUnivoquelyOrdered() {
		
		int count = 0;
		
		Loader previousLoader = null;
		Loader loader = new ZeroOrderLoader();
		
		previousLoader = loader;
		loader = jsonKeyValuesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = mdsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = pitLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = privilegesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = encounterTypesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = rolesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = globalPropertiesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = attributeTypesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = locationsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = conceptClassesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = conceptsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = programsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = programWorkflowsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = programWorkflowStatesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = personAttributeTypesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = identifierSourcesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = drugsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = orderFrequenciesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = orderTypesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = appointmentsSpecialitiesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = appointmentsServiceDefinitionsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = dataFilterMappingsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = metadataTermMappingsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = metadataSetLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = metadataSetMemberLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		Assert.assertEquals(getService().getLoaders().size(), count);
		
		// System.out.println("Here is the list of loaders in order:");
		// for (Loader l : loaders) {
		// System.out.format("%4d%100s%n", l.getOrder(), l.toString());
		// }
		
	}
}
