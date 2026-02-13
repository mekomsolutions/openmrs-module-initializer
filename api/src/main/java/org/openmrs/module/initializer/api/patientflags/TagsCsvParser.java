package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Parses CSV files for Tag entities.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class TagsCsvParser extends CsvParser<Tag, BaseLineProcessor<Tag>> {
	
	private final FlagService flagService;
	
	@Autowired
	public TagsCsvParser(@Qualifier("flagService") FlagService flagService, TagLineProcessor processor) {
		super(processor);
		this.flagService = flagService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.FLAG_TAGS;
	}
	
	@Override
	public Tag bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		Tag tag = flagService.getTagByUuid(uuid);
		
		if (tag == null && StringUtils.isEmpty(uuid)) {
			String name = line.getName();
			if (StringUtils.isNotBlank(name)) {
				tag = flagService.getTag(name);
			}
		}
		
		if (tag == null) {
			tag = new Tag();
			if (StringUtils.isNotBlank(uuid)) {
				tag.setUuid(uuid);
			}
		}
		
		return tag;
	}
	
	@Override
	public Tag save(Tag instance) {
		flagService.saveTag(instance);
		return instance;
	}
}
