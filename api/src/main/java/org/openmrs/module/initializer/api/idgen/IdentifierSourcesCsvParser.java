package org.openmrs.module.initializer.api.idgen;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;

public class IdentifierSourcesCsvParser extends CsvParser<IdgenSourceWrapper, IdentifierSourceService, BaseLineProcessor<IdgenSourceWrapper, IdentifierSourceService>> {
	
	public IdentifierSourcesCsvParser(InputStream is, IdentifierSourceService service) throws IOException {
		super(is, service);
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new BaseIdentifierSourceLineProcessor(headerLine, service));
		addLineProcessor(new SequentialIdentifierGeneratorLineProcessor(headerLine, service));
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
