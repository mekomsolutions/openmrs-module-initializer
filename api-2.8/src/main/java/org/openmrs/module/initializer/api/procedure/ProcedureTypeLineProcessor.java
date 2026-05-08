package org.openmrs.module.initializer.api.procedure;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@OpenmrsProfile(modules = { "emrapi:3.4 - 9.*" })
@Component
public class ProcedureTypeLineProcessor extends BaseLineProcessor<ProcedureType> {
	
	@Override
	public ProcedureType fill(ProcedureType instance, CsvLine line) throws IllegalArgumentException {
		// Reject blank Name explicitly: getName(true) catches a missing column header but
		// still returns null for blank cells, which would silently null the entity's name
		// and only surface as a downstream NOT NULL constraint violation. Throwing here
		// lands the offending row in CsvFailingLines with a clear message.
		String name = line.getName(true);
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("'Name' is required for procedure types.");
		}
		instance.setName(name);
		instance.setDescription(line.get(HEADER_DESC));
		return instance;
	}
}
