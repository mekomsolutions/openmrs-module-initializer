/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.medicationsideeffects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.medicationsideeffects.model.MedicationSideEffect;
import org.openmrs.module.medicationsideeffects.model.SideEffectClassification;

public class MedicationSideEffectLineProcessorTest {
	
	private static final String[] HEADERS = new String[] { "Uuid", "Void/Retire", "Drug Uuid", "Classification",
	        "Side Effect Concept Uuid", "Side Effect Text", "Recommended Action", "Notes" };
	
	private static final String DRUG_UUID = "a1a1a1a1-0000-0000-0000-000000000001";
	
	private static final String CONCEPT_UUID = "b1b1b1b1-0000-0000-0000-000000000001";
	
	@Mock
	private ConceptService conceptService;
	
	private MedicationSideEffectLineProcessor processor;
	
	private Drug drug;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		processor = new MedicationSideEffectLineProcessor(conceptService);
		drug = new Drug();
		drug.setUuid(DRUG_UUID);
		when(conceptService.getDrugByUuid(DRUG_UUID)).thenReturn(drug);
	}
	
	private CsvLine line(String... values) {
		return new CsvLine(HEADERS, values);
	}
	
	@Test
	public void fill_shouldMapCodedCommonEffect() {
		Concept concept = new Concept();
		concept.setUuid(CONCEPT_UUID);
		when(conceptService.getConceptByUuid(CONCEPT_UUID)).thenReturn(concept);
		
		MedicationSideEffect result = processor.fill(new MedicationSideEffect(),
		    line(null, null, DRUG_UUID, "common", CONCEPT_UUID, null, null, "Nausea note"));
		
		assertEquals(drug, result.getDrug());
		assertEquals(SideEffectClassification.COMMON, result.getClassification());
		assertEquals(concept, result.getSideEffectConcept());
		assertNull(result.getSideEffectText());
		assertEquals("Nausea note", result.getNotes());
	}
	
	@Test
	public void fill_shouldMapFreeTextSeriousEffect() {
		MedicationSideEffect result = processor.fill(new MedicationSideEffect(),
		    line(null, null, DRUG_UUID, "serious", null, "Signs of liver injury", "Seek urgent care", null));
		
		assertEquals(SideEffectClassification.SERIOUS, result.getClassification());
		assertNull(result.getSideEffectConcept());
		assertEquals("Signs of liver injury", result.getSideEffectText());
		assertEquals("Seek urgent care", result.getRecommendedAction());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldThrowWhenDrugNotFound() {
		processor.fill(new MedicationSideEffect(), line(null, null, "missing-drug-uuid", "common", null, "x", null, null));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldThrowWhenClassificationInvalid() {
		processor.fill(new MedicationSideEffect(), line(null, null, DRUG_UUID, "moderate", null, "x", null, null));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldThrowWhenNeitherConceptNorTextProvided() {
		processor.fill(new MedicationSideEffect(), line(null, null, DRUG_UUID, "common", null, null, null, null));
	}
}
