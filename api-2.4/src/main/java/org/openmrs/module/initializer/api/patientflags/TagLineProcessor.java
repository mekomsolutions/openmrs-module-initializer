package org.openmrs.module.initializer.api.patientflags;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Role;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.UserService;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.RoleListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Processes CSV lines for Tag entities.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class TagLineProcessor extends BaseLineProcessor<Tag> {
	
	protected static final String HEADER_ROLES = "roles";
	
	protected static final String HEADER_DISPLAY_POINTS = "display points";
	
	private RoleListParser roleListParser;
	
	private DisplayPointListParser displayPointListParser;
	
	@Autowired
	public TagLineProcessor(RoleListParser roleListParser, DisplayPointListParser displayPointListParser) {
		this.roleListParser = roleListParser;
		this.displayPointListParser = displayPointListParser;
	}
	
	@Override
	public Tag fill(Tag tag, CsvLine line) throws IllegalArgumentException {
		tag.setName(line.get(HEADER_NAME, true));
		tag.setDescription(line.get(HEADER_DESC));
		
		// Optional roles
		String rolesStr = line.getString(HEADER_ROLES, "");
		if (StringUtils.isNotBlank(rolesStr)) {
			Set<Role> roles = new HashSet<Role>(roleListParser.parseList(rolesStr));
			tag.setRoles(roles);
		} else if (tag.getRoles() != null) {
			// Clear roles if not specified (only if roles were previously set)
			tag.getRoles().clear();
		}
		
		// Optional display points
		String displayPointsStr = line.getString(HEADER_DISPLAY_POINTS, "");
		if (StringUtils.isNotBlank(displayPointsStr)) {
			Set<DisplayPoint> displayPoints = new HashSet<DisplayPoint>(displayPointListParser.parseList(displayPointsStr));
			tag.setDisplayPoints(displayPoints);
		} else if (tag.getDisplayPoints() != null) {
			// Clear display points if not specified (only if display points were previously set)
			tag.getDisplayPoints().clear();
		}
		
		return tag;
	}
}
