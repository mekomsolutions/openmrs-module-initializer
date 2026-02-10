package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Parses CSV files for Priority entities.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class PrioritiesCsvParser extends CsvParser<Priority, BaseLineProcessor<Priority>> {
	
	private final FlagService flagService;
	
	@Autowired
	public PrioritiesCsvParser(@Qualifier("flagService") FlagService flagService, PriorityLineProcessor processor) {
		super(processor);
		this.flagService = flagService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.FLAG_PRIORITIES;
	}
	
	@Override
	public Priority bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		Priority priority = flagService.getPriorityByUuid(uuid);
		
		if (priority == null && StringUtils.isEmpty(uuid)) {
			String name = line.getName();
			if (StringUtils.isNotBlank(name)) {
				priority = flagService.getPriorityByName(name);
			}
		}
		
		if (priority == null) {
			priority = new Priority();
			if (StringUtils.isNotBlank(uuid)) {
				priority.setUuid(uuid);
			}
		}
		
		return priority;
	}
	
	@Override
	public Priority save(Priority instance) {
		flagService.savePriority(instance);
		return instance;
	}
}
