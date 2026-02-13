package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

/**
 * Processes CSV lines for Priority entities.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class PriorityLineProcessor extends BaseLineProcessor<Priority> {
	
	protected static final String HEADER_STYLE = "style";
	
	protected static final String HEADER_RANK = "rank";
	
	@Override
	public Priority fill(Priority priority, CsvLine line) throws IllegalArgumentException {
		priority.setName(line.get(HEADER_NAME, true));
		priority.setDescription(line.get(HEADER_DESC));
		
		// Optional style
		String styleStr = line.getString(HEADER_STYLE, "");
		if (StringUtils.isBlank(styleStr)) {
			priority.setStyle("/**/"); // field is non-nullable but is not used; set to empty comment
		} else {
			priority.setStyle(styleStr.trim());
		}
		
		// Required rank
		String rankStr = line.get(HEADER_RANK, true);
		try {
			priority.setRank(Integer.parseInt(rankStr.trim()));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("The rank value '" + rankStr + "' could not be parsed as an integer.");
		}
		
		return priority;
	}
}
