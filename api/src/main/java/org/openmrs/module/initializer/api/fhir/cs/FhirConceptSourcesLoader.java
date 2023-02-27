package org.openmrs.module.initializer.api.fhir.cs;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "fhir2:1.6.* - 9.*" })
public class FhirConceptSourcesLoader extends BaseCsvLoader<FhirConceptSource, FhirConceptSourceCsvParser> {
	
	@Autowired
	public void setParser(FhirConceptSourceCsvParser parser) {
		this.parser = parser;
	}
}
