package org.openmrs.module.initializer.api.conceptreferencerange;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConceptReferenceRangeIntegrationTest {
	
	private ConceptService conceptService;
	
	private ConceptReferenceRangeService conceptReferenceRangeService;
	
	@Autowired
	private ConceptReferenceRangeLoader conceptReferenceRangeLoader;
	
	@BeforeEach
	public void setUp() {
		if (conceptReferenceRangeLoader == null) {
			conceptReferenceRangeLoader = new ConceptReferenceRangeLoader();
		}
		conceptService = mock(ConceptService.class);
		
		if (conceptReferenceRangeService == null) {
			conceptReferenceRangeService = new ConceptReferenceRangeService(conceptService);
		}
	}
	
	@Test
	public void testLoadConceptReferenceRange() {
		conceptReferenceRangeLoader.load();
		
		ConceptReferenceRange range = new ConceptReferenceRange();
		range.setHiAbsolute(130.0);
		range.setLowAbsolute(70.0);
		
		when(conceptService.getConceptReferenceRangeByUuid("239c1904-15ff-45e1-ac9d-d83afb637926")).thenReturn(range);
		
		ConceptReferenceRange referenceRange = conceptService
		        .getConceptReferenceRangeByUuid("239c1904-15ff-45e1-ac9d-d83afb637926");
		Assert.assertNotNull(referenceRange);
		Assert.assertEquals(Double.valueOf("70"), referenceRange.getLowAbsolute());
	}
}
