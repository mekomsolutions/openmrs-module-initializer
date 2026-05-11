package org.openmrs.module.initializer.api.procedure;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@OpenmrsProfile(modules = { "emrapi:3.4.* - 9.*" })
@Component
public class ProcedureTypeLineProcessor extends BaseLineProcessor<ProcedureType> {
	
	@Override
	public ProcedureType fill(ProcedureType instance, CsvLine line) throws IllegalArgumentException {
		String name = line.getName(true);
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("'Name' is required for procedure types.");
		}
		instance.setName(name);
		instance.setDescription(line.get(HEADER_DESC));
		return instance;
	}
}
