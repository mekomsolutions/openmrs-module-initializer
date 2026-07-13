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
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.medicationsideeffects.model.MedicationSideEffect;
import org.openmrs.module.medicationsideeffects.service.MedicationSideEffectService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "medicationsideeffects:1.0.0 - 9.*" })
public class MedicationSideEffectsCsvParser extends CsvParser<MedicationSideEffect, BaseLineProcessor<MedicationSideEffect>> {
	
	private final MedicationSideEffectService service;
	
	@Autowired
	public MedicationSideEffectsCsvParser(MedicationSideEffectService service,
	    MedicationSideEffectLineProcessor lineProcessor) {
		super(lineProcessor);
		this.service = service;
	}
	
	@Override
	public MedicationSideEffect bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		MedicationSideEffect sideEffect = service.getByUuid(uuid);
		if (sideEffect == null) {
			sideEffect = new MedicationSideEffect();
			if (StringUtils.isNotBlank(uuid)) {
				sideEffect.setUuid(uuid);
			}
		}
		return sideEffect;
	}
	
	@Override
	public MedicationSideEffect save(MedicationSideEffect instance) {
		return service.save(instance);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.MEDICATION_SIDE_EFFECTS;
	}
}
