package org.openmrs.module.initializer.api.idgen;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:*" })
public class IdentifierSourcesCsvParser extends CsvParser<IdgenSourceWrapper, BaseLineProcessor<IdgenSourceWrapper>> {
	
	private IdentifierSourceService idgenService;
	
	private SequentialIdentifierGeneratorLineProcessor seqProcessor;
	
	private RemoteIdentifierSourceLineProcessor remoteProcessor;
	
	private IdentifierPoolLineProcessor poolProcessor;
	
	@Autowired
	public IdentifierSourcesCsvParser(IdentifierSourceService idgenService, CommonIdentifierSourceLineProcessor processor,
	    SequentialIdentifierGeneratorLineProcessor seqProcessor, RemoteIdentifierSourceLineProcessor remoteProcessor,
	    IdentifierPoolLineProcessor poolProcessor) {
		super(processor);
		this.idgenService = idgenService;
		this.seqProcessor = seqProcessor;
		this.remoteProcessor = remoteProcessor;
		this.poolProcessor = poolProcessor;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.IDGEN;
	}
	
	protected IdentifierSource newIdentifierSource(CsvLine line) throws IllegalArgumentException {
		
		switch (IdentifierSourceLineProcessor.getIdentifierSourceType(line)) {
			
			case POOL:
				return new IdentifierPool();
			case REMOTE:
				return new RemoteIdentifierSource();
			case SEQUENTIAL:
				return new SequentialIdentifierGenerator();
			default:
				throw new IllegalArgumentException(
				        "No identifier source type could be guessed from the CSV line: '" + line.toString() + "'.");
				
		}
	}
	
	@Override
	public IdgenSourceWrapper bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		IdentifierSource source = idgenService.getIdentifierSourceByUuid(uuid);
		
		if (source == null) {
			source = newIdentifierSource(line);
			if (!StringUtils.isEmpty(uuid)) {
				source.setUuid(uuid);
			}
		}
		
		return new IdgenSourceWrapper(source);
	}
	
	@Override
	public IdgenSourceWrapper save(IdgenSourceWrapper instance) {
		return new IdgenSourceWrapper(idgenService.saveIdentifierSource(instance.getIdentifierSource()));
	}
	
	@Override
	protected void setLineProcessors(String version) {
		lineProcessors.clear();
		lineProcessors.add(getSingleLineProcessor());
		lineProcessors.add(seqProcessor);
		lineProcessors.add(remoteProcessor);
		lineProcessors.add(poolProcessor);
	}
}
