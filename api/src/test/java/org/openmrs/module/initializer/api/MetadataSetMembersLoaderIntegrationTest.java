package org.openmrs.module.initializer.api;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.mds.MetadataSetMembersLoader;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

public class MetadataSetMembersLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataMappingService service;
	
	@Autowired
	private MetadataSetMembersLoader loader;
	
	private static String MEMBER_TO_EDIT = "ee00777d-0cbe-41b7-4c67-8ff93de67b9e";
	
	private static String MEMBER_TO_RETIRE = "f0ebcb99-272d-41b7-4c67-078de9342492";
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadatasets.xml");
		
	}
	
	@Test
	public void load_shouldLoadMetadataSetMembersFromCSVLines() {
		// Setup
		MetadataSetMember member = null;
		
		// Replay
		loader.load();
		
		// Verify created
		member = service.getMetadataSetMemberByUuid("dbfd899d-e9e1-4059-8992-73737c924f88");
		Assertions.assertEquals("Outpatient Id", member.getName());
		Assertions.assertEquals("IdentifierType for OPD", member.getDescription());
		Assertions.assertEquals("7b0f5697-27e3-40c4-8bae-f4049abfb4ed", member.getMetadataUuid());
		MatcherAssert.assertThat(member.getSortWeight(), CoreMatchers.is(34.0));
		
		// Verify edited
		member = service.getMetadataSetMemberByUuid(MEMBER_TO_EDIT);
		Assertions.assertEquals("Legacy Id", member.getName());
		Assertions.assertEquals("n0ebcb90-m618-n1b1-b0bf-kff93de97b9j", member.getMetadataUuid());
		
		// Verify retired
		member = service.getMetadataSetMemberByUuid(MEMBER_TO_RETIRE);
		Assertions.assertTrue(member.isRetired());
	}
}
