package org.openmrs.module.initializer.api.fhir.ocm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirObservationCategoryMap;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirObservationCategoryMapLoader extends BaseCsvLoader<FhirObservationCategoryMap, FhirObservationCategoryMapCsvParser> {
	
	@Autowired
	public void setParser(FhirObservationCategoryMapCsvParser parser) {
		this.parser = parser;
	}
}
