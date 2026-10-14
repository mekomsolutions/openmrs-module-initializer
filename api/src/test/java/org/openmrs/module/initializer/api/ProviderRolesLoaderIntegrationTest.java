package org.openmrs.module.initializer.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.providerroles.ProviderRolesLoader;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ProviderRolesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("providerManagementService")
	private ProviderManagementService service;
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private ProviderRolesLoader loader;
	
	private RelationshipType supervisor;
	
	private ProviderAttributeType households;
	
	private ProviderAttributeType dateHired;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
		supervisor = personService.getRelationshipTypeByUuid("53d8a8f3-0084-4a52-8666-c655f5bd2689");
		households = providerService.getProviderAttributeTypeByUuid("0c267ae8-f793-4cf8-9b27-93accaa45d86");
		dateHired = providerService.getProviderAttributeTypeByUuid("c8ef8a16-a8cd-4748-b0ea-e8a1ec503fbb");
	}
	
	@Test
	public void load_shouldCreateProviderRolesFromCSVLines() {
		loader.load();
		verifyExpectedState();
	}
	
	@Test
	public void load_shouldUpdateProviderRolesFromCSVLines() {
		ProviderRole chw = new ProviderRole();
		chw.setUuid("68624C4C-9E10-473B-A849-204820D16C45");
		chw.setName("Community Health Worker");
		service.saveProviderRole(chw);
		
		chw = service.getProviderRoleByUuid("68624C4C-9E10-473B-A849-204820D16C45");
		Assertions.assertEquals("Community Health Worker", chw.getName());
		
		loader.load();
		verifyExpectedState();
	}
	
	protected void verifyExpectedState() {
		ProviderRole chw = service.getProviderRoleByUuid("68624C4C-9E10-473B-A849-204820D16C45");
		ProviderRole nurseAccompagnateur = service.getProviderRoleByUuid("9a4b44b2-8a9f-11e8-9a94-a6cf71072f73");
		ProviderRole chwSupervisor = service.getProviderRoleByUuid("11C1A56D-82F7-4269-95E8-2B67B9A3D837");
		
		// Verify created
		Assertions.assertNotNull(chw);
		Assertions.assertEquals("CHW", chw.getName());
		Assertions.assertEquals(0, chw.getSuperviseeProviderRoles().size());
		Assertions.assertEquals(1, chw.getRelationshipTypes().size());
		Assertions.assertEquals(supervisor, chw.getRelationshipTypes().iterator().next());
		Assertions.assertEquals(2, chw.getProviderAttributeTypes().size());
		Assertions.assertTrue(chw.getProviderAttributeTypes().contains(households));
		Assertions.assertTrue(chw.getProviderAttributeTypes().contains(dateHired));
		
		Assertions.assertNotNull(nurseAccompagnateur);
		Assertions.assertEquals("Nurse Accompagnateur", nurseAccompagnateur.getName());
		Assertions.assertEquals(0, nurseAccompagnateur.getSuperviseeProviderRoles().size());
		Assertions.assertEquals(1, nurseAccompagnateur.getRelationshipTypes().size());
		Assertions.assertEquals(supervisor, nurseAccompagnateur.getRelationshipTypes().iterator().next());
		Assertions.assertEquals(2, nurseAccompagnateur.getProviderAttributeTypes().size());
		Assertions.assertTrue(nurseAccompagnateur.getProviderAttributeTypes().contains(households));
		Assertions.assertTrue(nurseAccompagnateur.getProviderAttributeTypes().contains(dateHired));
		
		Assertions.assertNotNull(chwSupervisor);
		Assertions.assertEquals("CHW Supervisor", chwSupervisor.getName());
		Assertions.assertEquals(2, chwSupervisor.getSuperviseeProviderRoles().size());
		Assertions.assertTrue(chwSupervisor.getSuperviseeProviderRoles().contains(chw));
		Assertions.assertTrue(chwSupervisor.getSuperviseeProviderRoles().contains(nurseAccompagnateur));
		Assertions.assertEquals(0, chwSupervisor.getRelationshipTypes().size());
		Assertions.assertEquals(1, chwSupervisor.getProviderAttributeTypes().size());
		Assertions.assertTrue(chwSupervisor.getProviderAttributeTypes().contains(dateHired));
	}
}
