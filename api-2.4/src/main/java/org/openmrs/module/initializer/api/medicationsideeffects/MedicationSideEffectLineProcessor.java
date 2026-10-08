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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.medicationsideeffects.model.MedicationSideEffect;
import org.openmrs.module.medicationsideeffects.model.SideEffectClassification;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "medicationsideeffects:1.0.0 - 9.*" })
public class MedicationSideEffectLineProcessor extends BaseLineProcessor<MedicationSideEffect> {
	
	protected static final String HEADER_DRUG_UUID = "Drug Uuid";
	
	protected static final String HEADER_CLASSIFICATION = "Classification";
	
	protected static final String HEADER_CONCEPT_UUID = "Side Effect Concept Uuid";
	
	protected static final String HEADER_SIDE_EFFECT_TEXT = "Side Effect Text";
	
	protected static final String HEADER_RECOMMENDED_ACTION = "Recommended Action";
	
	protected static final String HEADER_NOTES = "Notes";
	
	private final ConceptService conceptService;
	
	@Autowired
	public MedicationSideEffectLineProcessor(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public MedicationSideEffect fill(MedicationSideEffect instance, CsvLine line) throws IllegalArgumentException {
		String drugUuid = line.get(HEADER_DRUG_UUID, true);
		Drug drug = conceptService.getDrugByUuid(drugUuid);
		if (drug == null) {
			throw new IllegalArgumentException("No drug found for uuid '" + drugUuid + "'");
		}
		instance.setDrug(drug);
		
		String classification = line.get(HEADER_CLASSIFICATION, true);
		try {
			instance.setClassification(SideEffectClassification.valueOf(classification.toUpperCase()));
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
			        "Invalid classification '" + classification + "' (expected common or serious)");
		}
		
		String conceptUuid = line.getString(HEADER_CONCEPT_UUID);
		Concept concept = null;
		if (StringUtils.isNotBlank(conceptUuid)) {
			concept = conceptService.getConceptByUuid(conceptUuid);
			if (concept == null) {
				throw new IllegalArgumentException("No concept found for uuid '" + conceptUuid + "'");
			}
		}
		instance.setSideEffectConcept(concept);
		
		instance.setSideEffectText(line.getString(HEADER_SIDE_EFFECT_TEXT));
		instance.setRecommendedAction(line.getString(HEADER_RECOMMENDED_ACTION));
		instance.setNotes(line.getString(HEADER_NOTES));
		
		if (concept == null && StringUtils.isBlank(instance.getSideEffectText())) {
			throw new IllegalArgumentException("either 'Side Effect Concept Uuid' or 'Side Effect Text' must be provided");
		}
		
		return instance;
	}
}
