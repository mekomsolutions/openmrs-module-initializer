package org.openmrs.module.initializer.api.patientflags;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

/**
 * Processes CSV lines for Priority entities.
 */
@Component("initializer.priorityLineProcessor")
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class PriorityLineProcessor extends BaseLineProcessor<Priority> {
	
	protected static final String HEADER_STYLE = "style";
	
	protected static final String HEADER_RANK = "rank";
	
	@Override
	public Priority fill(Priority priority, CsvLine line) throws IllegalArgumentException {
		priority.setName(line.get(HEADER_NAME, true));
		priority.setDescription(line.get(HEADER_DESC));
		
		// Required style
		priority.setStyle(line.get(HEADER_STYLE, true));
		
		// Required rank
		String rankStr = line.get(HEADER_RANK, true);
		if (StringUtils.isNotBlank(rankStr)) {
			try {
				priority.setRank(Integer.parseInt(rankStr.trim()));
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("The rank value '" + rankStr + "' could not be parsed as an integer.");
			}
		}
		
		return priority;
	}
}
