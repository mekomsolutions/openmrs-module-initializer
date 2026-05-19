package org.openmrs.module.initializer.api.procedure;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@OpenmrsProfile(modules = { "emrapi:3.4.* - 9.*" })
@Component
public class ProcedureTypesLoader extends BaseCsvLoader<ProcedureType, ProcedureTypesCsvParser> {
	
	@Autowired
	public void setParser(ProcedureTypesCsvParser parser) {
		this.parser = parser;
	}
}
