package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c.ConceptReferenceRangeLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class ConceptReferenceRangeIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private ConceptReferenceRangeLoader conceptReferenceRangeLoader;
	
	@Test
	public void load_shouldLoadConceptReferenceRangeFromCsvFiles() {
		conceptReferenceRangeLoader.load();
		
		{
			ConceptReferenceRange referenceRange = conceptService
			        .getConceptReferenceRangeByUuid("239c1904-15ff-45e1-ac9d-d83afb637926");
			Assert.assertNotNull(referenceRange);
			Assert.assertEquals(Double.valueOf("70"), referenceRange.getLowAbsolute());
		}
	}
}
