package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.api.utils.ListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Parses a list of DisplayPoint identifiers (UUID or name) and fetches the corresponding
 * DisplayPoint entities.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class DisplayPointListParser extends ListParser<DisplayPoint> {
	
	private FlagService flagService;
	
	@Autowired
	public DisplayPointListParser(@Qualifier("flagService") FlagService flagService) {
		this.flagService = flagService;
	}
	
	@Override
	protected DisplayPoint fetch(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		// Try UUID first
		DisplayPoint displayPoint = flagService.getDisplayPointByUuid(id);
		if (displayPoint != null) {
			return displayPoint;
		}
		
		// Try name
		displayPoint = flagService.getDisplayPoint(id);
		if (displayPoint != null) {
			return displayPoint;
		}
		
		return null;
	}
}
