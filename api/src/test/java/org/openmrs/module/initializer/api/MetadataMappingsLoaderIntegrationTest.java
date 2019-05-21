package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.mdm.MetadataMappingsLoader;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

public class MetadataMappingsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataMappingService service;
	
	@Autowired
	private MetadataMappingsLoader loader;
	
	private MetadataSource ms;
	
	@Before
	public void setup() {
		
		ms = new MetadataSource();
		ms.setName("org.openmrs.module.emrapi");
		ms = service.saveMetadataSource(ms);
		
		// Metadata term mapping to edit using source and code
		{
			MetadataTermMapping mtm = new MetadataTermMapping();
			mtm.setUuid("21e24b36-f9e3-4b0e-986d-9899665597f7");
			mtm.setMetadataSource(ms);
			mtm.setCode("emr.primaryIdentifierType");
			PatientIdentifierType patientId = new PatientIdentifierType();
			patientId.setUuid("29f9405a-c0ed-11e2-94be-69e3348c13b9");
			mtm.setMappedObject(patientId);
			service.saveMetadataTermMapping(mtm);
		}
		// Metadata term mapping to edit using metadata UUID
		{
			MetadataTermMapping mtm = new MetadataTermMapping();
			mtm.setUuid("dbfd899d-e9e1-4059-8992-73737c924f84");
			mtm.setMetadataSource(ms);
			mtm.setCode("emr.encounterType");
			EncounterType encType = new EncounterType();
			encType.setUuid("e22e39fd-7db2-45e7-80f1-60fa0d5a4378");
			mtm.setMappedObject(encType);
			service.saveMetadataTermMapping(mtm);
		}
		// Metadata term mapping to retire
		{
			MetadataTermMapping mtm = new MetadataTermMapping();
			mtm.setUuid("5f84b986-232d-475b-aad2-2094306bd655");
			mtm.setMetadataSource(ms);
			mtm.setCode("emr.extraPatientIdentifierTypes");
			MetadataSet mset = new MetadataSet();
			mset.setUuid("05a29f94-c0ed-11e2-94be-8c13b969e334");
			mtm.setMappedObject(mset);
			service.saveMetadataTermMapping(mtm);
		}
	}
	
	@Test
	public void load_shouldLoadMetadataTermMappingsFromCsvFiles() {
		
		// Setup
		MetadataTermMapping mtm;
		
		// Replay
		loader.load();
		
		// Verify created
		mtm = service.getMetadataTermMapping(ms, "emr.atFacilityVisitType");
		Assert.assertNotNull(mtm);
		Assert.assertNotNull(mtm.getUuid());
		Assert.assertEquals("org.openmrs.VisitType", mtm.getMetadataClass());
		Assert.assertEquals("7b0f5697-27e3-40c4-8bae-f4049abfb4ed", mtm.getMetadataUuid());
		
		// Verify edited using source and code
		mtm = service.getMetadataTermMappingByUuid("21e24b36-f9e3-4b0e-986d-9899665597f7");
		Assert.assertNotNull(mtm);
		Assert.assertEquals("org.openmrs.module.emrapi", mtm.getMetadataSource().getName());
		Assert.assertEquals("emr.primaryIdentifierType", mtm.getCode());
		Assert.assertEquals("org.openmrs.PatientIdentifierType", mtm.getMetadataClass());
		Assert.assertEquals("264c9e75-77da-486a-8361-31558e051930", mtm.getMetadataUuid());
		
		// Verify edited using metadataUuid
		mtm = service.getMetadataTermMappingByUuid("dbfd899d-e9e1-4059-8992-73737c924f84");
		Assert.assertNotNull(mtm);
		Assert.assertEquals("org.openmrs.module.emrapi", mtm.getMetadataSource().getName());
		Assert.assertEquals("emr.admissionEncounterType", mtm.getCode());
		Assert.assertEquals("org.openmrs.EncounterType", mtm.getMetadataClass());
		Assert.assertEquals("e22e39fd-7db2-45e7-80f1-60fa0d5a4378", mtm.getMetadataUuid());
		
		// Verify retired
		mtm = service.getMetadataTermMappingByUuid("5f84b986-232d-475b-aad2-2094306bd655");
		Assert.assertNotNull(mtm);
		Assert.assertTrue(mtm.isRetired());
		Assert.assertEquals("org.openmrs.module.emrapi", mtm.getMetadataSource().getName());
		Assert.assertEquals("emr.extraPatientIdentifierTypes", mtm.getCode());
		Assert.assertEquals("org.openmrs.module.metadatamapping.MetadataSet", mtm.getMetadataClass());
		Assert.assertEquals("05a29f94-c0ed-11e2-94be-8c13b969e334", mtm.getMetadataUuid());
	}
}
