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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentServiceDefinitionsLoader;
import org.openmrs.module.initializer.api.appt.servicetypes.AppointmentServiceTypesLoader;
import org.openmrs.module.initializer.api.appt.specialities.SpecialitiesLoader;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.openmrs.module.initializer.api.c.ConceptClassesLoader;
import org.openmrs.module.initializer.api.c.ConceptSourcesLoader;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingsLoader;
import org.openmrs.module.initializer.api.drugs.DrugsLoader;
import org.openmrs.module.initializer.api.er.EncounterRolesLoader;
import org.openmrs.module.initializer.api.et.EncounterTypesLoader;
import org.openmrs.module.initializer.api.freq.OrderFrequenciesLoader;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesLoader;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesLoader;
import org.openmrs.module.initializer.api.idgen.autogen.AutoGenerationOptionsLoader;
import org.openmrs.module.initializer.api.loc.LocationTagsLoader;
import org.openmrs.module.initializer.api.loc.LocationsLoader;
import org.openmrs.module.initializer.api.mdm.MetadataTermMappingsLoader;
import org.openmrs.module.initializer.api.mds.MetadataSetMembersLoader;
import org.openmrs.module.initializer.api.mds.MetadataSetsLoader;
import org.openmrs.module.initializer.api.ot.OrderTypesLoader;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypesLoader;
import org.openmrs.module.initializer.api.pit.PatientIdentifierTypesLoader;
import org.openmrs.module.initializer.api.privileges.PrivilegesLoader;
import org.openmrs.module.initializer.api.programs.ProgramsLoader;
import org.openmrs.module.initializer.api.programs.workflows.ProgramWorkflowsLoader;
import org.openmrs.module.initializer.api.programs.workflows.states.ProgramWorkflowStatesLoader;
import org.openmrs.module.initializer.api.roles.RolesLoader;
import org.openmrs.module.initializer.api.visittypes.VisitTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadersOrderTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private JsonKeyValuesLoader jsonKeyValuesLoader;
	
	@Autowired
	private MdsLoader mdsLoader;
	
	@Autowired
	private VisitTypesLoader visitTypesLoader;
	
	@Autowired
	private PatientIdentifierTypesLoader pitLoader;
	
	@Autowired
	private GlobalPropertiesLoader globalPropertiesLoader;
	
	@Autowired
	private AttributeTypesLoader attributeTypesLoader;
	
	@Autowired
	private LocationTagsLoader locationTagsLoader;
	
	@Autowired
	private LocationsLoader locationsLoader;
	
	@Autowired
	private ConceptClassesLoader conceptClassesLoader;
	
	@Autowired
	private ConceptSourcesLoader conceptSourcesLoader;
	
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
	private EncounterRolesLoader encounterRolesLoader;
	
	@Autowired
	private RolesLoader rolesLoader;
	
	@Autowired
	private MetadataTermMappingsLoader metadataTermMappingsLoader;
	
	@Autowired
	private SpecialitiesLoader appointmentsSpecialitiesLoader;
	
	@Autowired
	private AppointmentServiceDefinitionsLoader appointmentsServiceDefinitionsLoader;
	
	@Autowired
	private AppointmentServiceTypesLoader appointmentServiceTypesLoader;
	
	@Autowired
	private OrderTypesLoader orderTypesLoader;
	
	@Autowired
	private DataFilterMappingsLoader dataFilterMappingsLoader;
	
	@Autowired
	private BahmniFormsLoader bahmniFormsLoader;
	
	@Autowired
	private MetadataSetsLoader metadataSetLoader;
	
	@Autowired
	private MetadataSetMembersLoader metadataSetMemberLoader;
	
	@Autowired
	private AutoGenerationOptionsLoader autoGenerationOptionsLoader;
	
	@Autowired
	private HtmlFormsLoader htmlFormsLoader;
	
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
		loader = conceptClassesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = conceptSourcesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));

		previousLoader = loader;
		loader = mdsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = visitTypesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = pitLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = locationTagsLoader;
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
		loader = encounterRolesLoader;
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
		loader = bahmniFormsLoader;
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
		loader = autoGenerationOptionsLoader;
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
		loader = appointmentServiceTypesLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = dataFilterMappingsLoader;
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
		
		previousLoader = loader;
		loader = metadataTermMappingsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		previousLoader = loader;
		loader = htmlFormsLoader;
		count++;
		Assert.assertThat(loader.getOrder(), greaterThan(previousLoader.getOrder()));
		
		Assert.assertEquals(getService().getLoaders().size(), count);
		
	}
}
