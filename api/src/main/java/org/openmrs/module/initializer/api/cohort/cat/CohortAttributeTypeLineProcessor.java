package org.openmrs.module.initializer.api.cohort.cat;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.module.cohort.api.CohortService;
import org.openmrs.module.cohort.CohortAttribute;
import org.openmrs.module.cohort.CohortAttributeType;
import org.openmrs.module.initializer.api.BaseAttributeLineProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.conceptAttributeLineProcessor")
public class CohortAttributeTypeLineProcessor extends BaseAttributeLineProcessor<Cohort, CohortAttributeType, CohortAttribute> {
	
	private CohortService cohortService;
	
	@Autowired()
	public CohortAttributeTypeLineProcessor(@Qualifier("cohortService") CohortService cohortService) {
		this.cohortService = cohortService;
	}
	
	@Override
	public CohortAttributeType getAttributeType(String identifier) throws IllegalArgumentException {
		
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("A blank attribute type identifier was provided.");
		}
		CohortAttributeType ret = cohortService.getAttributeTypeByUuid(identifier);
		if (ret == null) {
			ret = cohortService.getAttributeTypeByName(identifier);
		}
		return ret;
	}
	
	@Override
	public CohortAttribute newAttribute() {
		return new CohortAttribute();
	}
	
}
