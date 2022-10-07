package org.openmrs.module.initializer.api.fhir.pis;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@OpenmrsProfile(modules = { "fhir2:1.6.*" })
public class FhirPatientIdentifierSystemLoader extends BaseCsvLoader<FhirPatientIdentifierSystem, FhirPatientIdentifierSystemCsvParser> {
	
	@Autowired
	public void setParser(FhirPatientIdentifierSystemCsvParser parser) {
		this.parser = parser;
	}
}
