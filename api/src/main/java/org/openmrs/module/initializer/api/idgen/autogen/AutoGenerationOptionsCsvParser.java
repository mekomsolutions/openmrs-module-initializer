package org.openmrs.module.initializer.api.idgen.autogen;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:4.6.0" })
public class AutoGenerationOptionsCsvParser extends CsvParser<AutoGenerationOption, BaseLineProcessor<AutoGenerationOption>> {
	
	private IdentifierSourceService service;
	
	@Autowired
	public AutoGenerationOptionsCsvParser(IdentifierSourceService identifierSourceService,
	    BaseLineProcessor<AutoGenerationOption> lineProcessor) {
		super(lineProcessor);
		service = identifierSourceService;
	}
	
	@Override
	public AutoGenerationOption bootstrap(CsvLine line) throws IllegalArgumentException {
		AutoGenerationOption option = null;
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			option = service.getAutoGenerationOptionByUuid(uuid);
		}
		if (option == null) {
			option = new AutoGenerationOption();
			option.setUuid(uuid);
		}
		return option;
	}
	
	@Override
	public AutoGenerationOption save(AutoGenerationOption option) {
		return service.saveAutoGenerationOption(option);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.AUTO_GENERATION_OPTIONS;
	}
	
	@Override
	public boolean setRetired(AutoGenerationOption instance, boolean retired) {
		if (retired) {
			log.warn("An AutoGenerationOption instance ('" + instance.getUuid()
			        + "') was marked to be retired, this is not supported and nothing will happen.");
		}
		return false;
	}
	
}
