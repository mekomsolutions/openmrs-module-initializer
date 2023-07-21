package org.openmrs.module.initializer.api.cohort.cat;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.cohort.CohortAttributeType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(modules = { "cohort:3.2.* - 9.*" })
public class CohortAttributeTypeLineProcessor extends BaseLineProcessor<CohortAttributeType> {
	
	@Override
	public CohortAttributeType fill(CohortAttributeType instance, CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			instance.setUuid(line.getUuid());
		}
		
		instance.setName(line.getName(true));
		instance.setDescription(line.get("Description"));
		instance.setDatatypeConfig(line.get("Datatype classname", true));
		instance.setDatatypeConfig(line.get("Preferred handler classname"));
		instance.setDatatypeConfig(line.get("Handler config"));
		instance.setMinOccurs(line.getInt("Min occurs"));
		instance.setMaxOccurs(line.getInt("Max occurs"));
		
		return instance;
	}
}
