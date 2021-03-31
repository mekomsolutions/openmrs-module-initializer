package org.openmrs.module.initializer.api.visittypes;

import org.apache.commons.lang3.StringUtils;

import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class VisitTypesCsvParser extends CsvParser<VisitType, BaseLineProcessor<VisitType>> {
	
	private VisitService visitService;
	
	@Autowired
	public VisitTypesCsvParser(@Qualifier("visitService") VisitService visitService, VisitTypeLineProcessor processor) {
		super(processor);
		this.visitService = visitService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.VISIT_TYPES;
	}
	
	@Override
	public VisitType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		VisitType visitType = visitService.getVisitTypeByUuid(uuid);
		
		if (visitType == null) {
			
			visitType = visitService.getVisitTypes(line.getName(true)).stream()
			        .filter(vt -> vt.getName().equals(line.getName(true))).findFirst().orElse(null);
			
		}
		if (visitType == null) {
			visitType = new VisitType();
			if (!StringUtils.isEmpty(uuid)) {
				visitType.setUuid(uuid);
			}
		}
		
		return visitType;
	}
	
	@Override
	public VisitType save(VisitType instance) {
		return visitService.saveVisitType(instance);
	}
}
