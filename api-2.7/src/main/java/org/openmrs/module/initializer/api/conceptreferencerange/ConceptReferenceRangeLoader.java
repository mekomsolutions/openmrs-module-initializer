package org.openmrs.module.initializer.api.conceptreferencerange;

import org.openmrs.ConceptReferenceRange;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@OpenmrsProfile(openmrsPlatformVersion = "2.7.0 - 2.*")
@Component
public class ConceptReferenceRangeLoader extends BaseCsvLoader<ConceptReferenceRange, ConceptReferenceRangeParser> {
	
	@Autowired
	public void setParser(ConceptReferenceRangeParser parser) {
		this.parser = parser;
	}
	
}
