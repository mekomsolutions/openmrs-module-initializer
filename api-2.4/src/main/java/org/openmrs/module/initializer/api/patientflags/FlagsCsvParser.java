package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Parses CSV files for Flag entities.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class FlagsCsvParser extends CsvParser<Flag, BaseLineProcessor<Flag>> {
	
	private final FlagService flagService;
	
	@Autowired
	public FlagsCsvParser(@Qualifier("flagService") FlagService flagService, FlagsLineProcessor processor) {
		super(processor);
		this.flagService = flagService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.FLAGS;
	}
	
	@Override
	public Flag bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		Flag flag = flagService.getFlagByUuid(uuid);
		if (flag == null) {
			flag = new Flag();
			if (StringUtils.isNotBlank(uuid)) {
				flag.setUuid(uuid);
			}
		}
		return flag;
	}
	
	@Override
	public Flag save(Flag instance) {
		flagService.saveFlag(instance);
		return instance;
	}
}
