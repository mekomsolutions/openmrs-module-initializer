package org.openmrs.module.initializer.api.fhir.cpm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirContactPointMap;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "fhir2:1.11.* - 9.*" }, openmrsPlatformVersion = "2.5.13 - 2.5.*, 2.6.2 - 2.6.*, 2.7.* - 9.*")
public class FhirContactPointMapLoader extends BaseCsvLoader<FhirContactPointMap, FhirContactPointMapCsvParser> {
	
	@Autowired
	public void setParser(FhirContactPointMapCsvParser parser) {
		this.parser = parser;
	}
	
}
