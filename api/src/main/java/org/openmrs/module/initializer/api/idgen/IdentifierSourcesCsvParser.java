package org.openmrs.module.initializer.api.idgen;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:*" })
public class IdentifierSourcesCsvParser extends CsvParser<IdgenSourceWrapper, BaseLineProcessor<IdgenSourceWrapper>> {
	
	private IdentifierSourceService idgenService;
	
	private CommonIdentifierSourceLineProcessor processor;
	
	private SequentialIdentifierGeneratorLineProcessor seqProcessor;
	
	@Autowired
	public IdentifierSourcesCsvParser(IdentifierSourceService idgenService, CommonIdentifierSourceLineProcessor processor,
	    SequentialIdentifierGeneratorLineProcessor seqProcessor) {
		super();
		
		this.idgenService = idgenService;
		
		this.processor = processor;
		this.seqProcessor = seqProcessor;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.IDENTIFIER_SOURCES;
	}
	
	@Override
	protected IdgenSourceWrapper save(IdgenSourceWrapper instance) {
		return new IdgenSourceWrapper(idgenService.saveIdentifierSource(instance.getIdentifierSource()));
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.clear();
		lineProcessors.add(processor.setHeaderLine(headerLine));
		lineProcessors.add(seqProcessor.setHeaderLine(headerLine));
	}
}
