package org.openmrs.module.initializer.api.conceptreferencerange;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptReferenceRangeIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	@Autowired
	private ConceptReferenceRangeService conceptReferenceRangeService;
	
	@Autowired
	private ConceptReferenceRangeLoader conceptReferenceRangeLoader;
	
	@BeforeEach
	public void setUp() {
		if (conceptReferenceRangeLoader == null) {
			conceptReferenceRangeLoader = new ConceptReferenceRangeLoader();
		}
		if (conceptReferenceRangeService == null) {
			conceptReferenceRangeService = new ConceptReferenceRangeService(conceptService);
		}
	}
	
	@Test
	public void load_shouldLoadConceptReferenceRangeFromCsvFiles() {
		conceptReferenceRangeLoader.load();
		
		ConceptReferenceRange referenceRange = conceptReferenceRangeService
		        .getConceptReferenceRangeByUuid("239c1904-15ff-45e1-ac9d-d83afb637926");
		Assert.assertNotNull(referenceRange);
		Assert.assertEquals(Double.valueOf("70"), referenceRange.getLowAbsolute());
	}
}
