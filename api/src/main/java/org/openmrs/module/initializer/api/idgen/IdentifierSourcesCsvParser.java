package org.openmrs.module.initializer.api.idgen;

import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdentifierSourcesCsvParser extends CsvParser<IdgenSourceWrapper, IdentifierSourceService, BaseLineProcessor<IdgenSourceWrapper, IdentifierSourceService>> {
	
	@Autowired
	public IdentifierSourcesCsvParser(IdentifierSourceService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.IDENTIFIER_SOURCES;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new BaseIdentifierSourceLineProcessor(headerLine, service));
		lineProcessors.add(new SequentialIdentifierGeneratorLineProcessor(headerLine, service));
	}
	
	@Override
	protected IdgenSourceWrapper save(IdgenSourceWrapper instance) {
		return new IdgenSourceWrapper(service.saveIdentifierSource(instance.getIdentifierSource()));
	}
	
	@Override
	protected boolean isVoidedOrRetired(IdgenSourceWrapper instance) {
		return instance.getIdentifierSource().isRetired();
	}
}
