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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.DomainBaseModuleContextSensitive_2_4_test;
import org.openmrs.module.medicationsideeffects.model.MedicationSideEffect;
import org.openmrs.module.medicationsideeffects.model.SideEffectClassification;
import org.openmrs.module.medicationsideeffects.service.MedicationSideEffectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MedicationSideEffectsLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {
	
	private static final String DRUG_UUID = "a1a1a1a1-0000-0000-0000-000000000001";
	
	private static final String CONCEPT_UUID = "b1b1b1b1-0000-0000-0000-000000000001";
	
	private static final String UUID_TO_CREATE = "c1c1c1c1-0000-0000-0000-000000000011";
	
	private static final String UUID_TO_EDIT = "c1c1c1c1-0000-0000-0000-000000000012";
	
	private static final String UUID_TO_RETIRE = "c1c1c1c1-0000-0000-0000-000000000013";
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private MedicationSideEffectsLoader loader;
	
	private MedicationSideEffectService service;
	
	@Before
	public void setup() {
		service = Context.getService(MedicationSideEffectService.class);
		
		Concept effect = new Concept();
		effect.setUuid(CONCEPT_UUID);
		effect.setShortName(new ConceptName("Test Nausea", Locale.ENGLISH));
		effect.setConceptClass(cs.getConceptClassByName("Misc"));
		effect.setDatatype(cs.getConceptDatatypeByName("Text"));
		cs.saveConcept(effect);
		
		Concept drugConcept = new Concept();
		drugConcept.setShortName(new ConceptName("Test Drug Concept", Locale.ENGLISH));
		drugConcept.setConceptClass(cs.getConceptClassByName("Drug"));
		drugConcept.setDatatype(cs.getConceptDatatypeByName("Text"));
		drugConcept = cs.saveConcept(drugConcept);
		
		Drug drug = new Drug();
		drug.setUuid(DRUG_UUID);
		drug.setName("Test Drug 100mg");
		drug.setConcept(drugConcept);
		cs.saveDrug(drug);
		
		MedicationSideEffect toEdit = new MedicationSideEffect();
		toEdit.setUuid(UUID_TO_EDIT);
		toEdit.setDrug(drug);
		toEdit.setClassification(SideEffectClassification.COMMON);
		toEdit.setSideEffectText("Old text");
		service.saveMedicationSideEffect(toEdit);
		
		MedicationSideEffect toRetire = new MedicationSideEffect();
		toRetire.setUuid(UUID_TO_RETIRE);
		toRetire.setDrug(drug);
		toRetire.setClassification(SideEffectClassification.SERIOUS);
		toRetire.setSideEffectText("To be voided");
		service.saveMedicationSideEffect(toRetire);
	}
	
	@Test
	public void load_shouldLoadMedicationSideEffectsAccordingToCsvFiles() {
		loader.load();
		
		MedicationSideEffect created = service.getByUuid(UUID_TO_CREATE);
		assertNotNull(created);
		assertEquals(SideEffectClassification.COMMON, created.getClassification());
		assertEquals(DRUG_UUID, created.getDrug().getUuid());
		assertNotNull(created.getSideEffectConcept());
		assertEquals(CONCEPT_UUID, created.getSideEffectConcept().getUuid());
		
		MedicationSideEffect edited = service.getByUuid(UUID_TO_EDIT);
		assertNotNull(edited);
		assertEquals(SideEffectClassification.SERIOUS, edited.getClassification());
		assertNull(edited.getSideEffectConcept());
		assertEquals("Updated serious text", edited.getSideEffectText());
		assertEquals("Stop the medication", edited.getRecommendedAction());
		
		MedicationSideEffect voided = service.getByUuid(UUID_TO_RETIRE);
		assertNotNull(voided);
		assertTrue(voided.getVoided());
	}
}
