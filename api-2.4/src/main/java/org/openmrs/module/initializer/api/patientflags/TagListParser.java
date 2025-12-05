package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.api.utils.ListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Parses a list of Tag identifiers (UUID or name) and fetches the corresponding Tag entities.
 */
@Component("initializer.tagListParser")
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class TagListParser extends ListParser<Tag> {
	
	private FlagService flagService;
	
	@Autowired
	public TagListParser(@Qualifier("flagService") FlagService flagService) {
		this.flagService = flagService;
	}
	
	@Override
	protected Tag fetch(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		// Try UUID first
		Tag tag = flagService.getTagByUuid(id);
		if (tag != null) {
			return tag;
		}
		
		// Try name
		tag = flagService.getTag(id);
		if (tag != null) {
			return tag;
		}
		
		return null;
	}
}
