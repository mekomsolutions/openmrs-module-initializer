package org.openmrs.module.initializer.api.procedure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.emrapi.procedure.ProcedureService;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@OpenmrsProfile(modules = { "emrapi:3.4 - 9.*" })
@Component
public class ProcedureTypesCsvParser extends CsvParser<ProcedureType, BaseLineProcessor<ProcedureType>> {
	
	private final ProcedureService procedureService;
	
	@Autowired
	public ProcedureTypesCsvParser(@Qualifier("procedureService") ProcedureService procedureService,
	    ProcedureTypeLineProcessor lineProcessor) {
		super(lineProcessor);
		this.procedureService = procedureService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROCEDURE_TYPES;
	}
	
	@Override
	public ProcedureType bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		String name = line.getName();
		
		ProcedureType type = null;
		if (StringUtils.isNotBlank(uuid)) {
			type = procedureService.getProcedureTypeByUuid(uuid);
		} else if (StringUtils.isNotBlank(name)) {
			List<ProcedureType> exactMatches = new ArrayList<ProcedureType>();
			List<ProcedureType> raw = procedureService.getProcedureTypesByName(name);
			if (raw != null) {
				for (ProcedureType candidate : raw) {
					if (name.equals(candidate.getName())) {
						exactMatches.add(candidate);
					}
				}
			}
			if (exactMatches.size() > 1) {
				log.warn("{} procedure types matched name '{}'; binding to the first one (uuid={}).", exactMatches.size(),
				    name, exactMatches.get(0).getUuid());
			}
			if (!exactMatches.isEmpty()) {
				type = exactMatches.get(0);
			}
		}
		
		if (type != null) {
			return type;
		}
		
		type = new ProcedureType();
		if (StringUtils.isNotBlank(uuid)) {
			type.setUuid(uuid);
		}
		return type;
	}
	
	@Override
	public ProcedureType save(ProcedureType instance) {
		return procedureService.saveProcedureType(instance);
	}
}
