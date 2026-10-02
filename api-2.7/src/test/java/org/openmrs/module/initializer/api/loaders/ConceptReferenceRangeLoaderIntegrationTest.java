package org.openmrs.module.initializer.api.loaders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_7_Test;
import org.openmrs.module.initializer.api.conceptreferencerange.ConceptReferenceRangeLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptReferenceRangeLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_7_Test {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	@Autowired
	private ConceptReferenceRangeLoader loader;
	
	public void setup() {
		executeDataSet("testdata/test-concepts.xml");
		executeDataSet("testdata/test-concepts-numeric.xml");
	}
	
	@Test
	public void load_shouldLoadConceptReferenceRangesAccordingToCsvFiles() {
		loader.load();
		
		{
			ConceptReferenceRange conceptReferenceRange = conceptService
			        .getConceptReferenceRangeByUuid("bc059100-4ace-4af5-afbf-2da7f3a34acf");
			Assertions.assertNotNull(conceptReferenceRange);
			Assertions.assertNotNull(conceptReferenceRange.getConceptNumeric());
			Assertions.assertEquals("a09ab2c5-878e-4905-b25d-5784167d0216", conceptReferenceRange.getConceptNumeric()
			        .getUuid());
			Assertions.assertEquals(-100.5, conceptReferenceRange.getLowAbsolute(), 0.01);
			Assertions.assertEquals(-85.7, conceptReferenceRange.getLowCritical(), 0.01);
			Assertions.assertEquals(-50.3, conceptReferenceRange.getLowNormal(), 0.01);
			Assertions.assertEquals(45.1, conceptReferenceRange.getHiNormal(), 0.01);
			Assertions.assertEquals(78.0, conceptReferenceRange.getHiCritical(), 0.01);
			Assertions.assertEquals(98.8, conceptReferenceRange.getHiAbsolute(), 0.01);
			Assertions.assertEquals("$patient.getAge() > 3", conceptReferenceRange.getCriteria());
		}
		
		{
			ConceptReferenceRange conceptReferenceRange = conceptService
			        .getConceptReferenceRangeByUuid("930e1fb4-490d-45fe-a137-0cd941c76124");
			Assertions.assertNotNull(conceptReferenceRange);
			Assertions.assertNotNull(conceptReferenceRange.getConceptNumeric());
			Assertions.assertEquals("a09ab2c5-878e-4905-b25d-5784167d0216", conceptReferenceRange.getConceptNumeric()
			        .getUuid());
			Assertions.assertEquals(-100.5, conceptReferenceRange.getLowAbsolute(), 0.01);
			Assertions.assertEquals(-85.7, conceptReferenceRange.getLowCritical(), 0.01);
			Assertions.assertEquals(-50.3, conceptReferenceRange.getLowNormal(), 0.01);
			Assertions.assertEquals(45.1, conceptReferenceRange.getHiNormal(), 0.01);
			Assertions.assertEquals(78.0, conceptReferenceRange.getHiCritical(), 0.01);
			Assertions.assertEquals(98.8, conceptReferenceRange.getHiAbsolute(), 0.01);
			Assertions.assertEquals("$patient.getAge() < 10", conceptReferenceRange.getCriteria());
		}
		
		{
			ConceptReferenceRange conceptReferenceRange = conceptService
			        .getConceptReferenceRangeByUuid("b5a7b296-e500-4a2c-ab2e-eb012ed9ae1e");
			Assertions.assertNotNull(conceptReferenceRange);
			Assertions.assertNotNull(conceptReferenceRange.getConceptNumeric());
			Assertions.assertEquals("a09ab2c5-878e-4905-b25d-5784167d0216", conceptReferenceRange.getConceptNumeric()
			        .getUuid());
			Assertions.assertEquals(60.0, conceptReferenceRange.getLowAbsolute(), 0.01);
			Assertions.assertEquals(70.0, conceptReferenceRange.getLowCritical(), 0.01);
			Assertions.assertEquals(80.0, conceptReferenceRange.getLowNormal(), 0.01);
			Assertions.assertEquals(120.0, conceptReferenceRange.getHiNormal(), 0.01);
			Assertions.assertEquals(130.0, conceptReferenceRange.getHiCritical(), 0.01);
			Assertions.assertEquals(150.0, conceptReferenceRange.getHiAbsolute(), 0.01);
			Assertions.assertEquals("$fn.getCurrentHour() > 2", conceptReferenceRange.getCriteria());
		}
	}
	
}
