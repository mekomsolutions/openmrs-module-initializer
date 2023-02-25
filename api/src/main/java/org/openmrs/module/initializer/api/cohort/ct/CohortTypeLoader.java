package org.openmrs.module.initializer.api.cohort.ct;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.cohort.CohortType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "cohort:3.2.* - 9.*" })
public class CohortTypeLoader extends BaseCsvLoader<CohortType, CohortTypeCsvParser> {
	
	@Autowired
	public void setParser(CohortTypeCsvParser parser) {
		this.parser = parser;
	}
}
