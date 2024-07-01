/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.fhir.cpm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirContactPointMap;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@OpenmrsProfile(modules = { "fhir2:1.11.0" }, openmrsPlatformVersion = "2.6.3")
public class FhirContactPointMapLoader extends BaseCsvLoader<FhirContactPointMap, FhirContactPointMapCsvParser> {
	
	@Autowired
	public void setParser(FhirContactPointMapCsvParser parser) {
		this.parser = parser;
	}
	
}
