package org.openmrs.module.initializer.api.cohort.ct;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.cohort.CohortType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@OpenmrsProfile(modules = { "cohort:3.2.*" })
public class CohortTypeLineProcessor extends BaseLineProcessor<CohortType> {
	
	@Override
	public CohortType fill(CohortType instance, CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			instance.setUuid(line.getUuid());
		}
		
		instance.setName(line.getName(true));
		instance.setDescription(line.get("Description"));
		
		return instance;
	}
}
