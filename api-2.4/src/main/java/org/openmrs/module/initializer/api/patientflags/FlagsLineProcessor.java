package org.openmrs.module.initializer.api.patientflags;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Processes CSV lines for Flag entities.
 */
@Component("initializer.flagsLineProcessor")
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class FlagsLineProcessor extends BaseLineProcessor<Flag> {
	
	protected static final String HEADER_CRITERIA = "criteria";
	
	protected static final String HEADER_EVALUATOR = "evaluator";
	
	protected static final String HEADER_MESSAGE = "message";
	
	protected static final String HEADER_PRIORITY = "priority";
	
	protected static final String HEADER_ENABLED = "enabled";
	
	protected static final String HEADER_TAGS = "tags";
	
	private FlagService flagService;
	
	private TagListParser tagListParser;
	
	@Autowired
	public FlagsLineProcessor(@Qualifier("flagService") FlagService flagService,
	    @Qualifier("initializer.tagListParser") TagListParser tagListParser) {
		this.flagService = flagService;
		this.tagListParser = tagListParser;
	}
	
	@Override
	public Flag fill(Flag flag, CsvLine line) throws IllegalArgumentException {
		flag.setName(line.get(HEADER_NAME, true));
		flag.setDescription(line.get(HEADER_DESC));
		
		// Required fields
		flag.setCriteria(line.get(HEADER_CRITERIA, true));
		flag.setEvaluator(line.get(HEADER_EVALUATOR, true));
		flag.setMessage(line.get(HEADER_MESSAGE, true));
		
		// Optional priority
		String priorityId = line.getString(HEADER_PRIORITY, "");
		if (StringUtils.isNotBlank(priorityId)) {
			Priority priority = fetchPriority(priorityId);
			if (priority == null) {
				throw new IllegalArgumentException(
				        "The priority referenced by '" + priorityId + "' does not point to any known priority.");
			}
			flag.setPriority(priority);
		} else {
			flag.setPriority(null);
		}
		
		// Optional enabled flag (defaults to true if not specified)
		String enabledStr = line.getString(HEADER_ENABLED, "");
		if (StringUtils.isNotBlank(enabledStr)) {
			flag.setEnabled(Boolean.parseBoolean(enabledStr.trim()));
		} else {
			flag.setEnabled(true); // Default to enabled
		}
		
		// Optional tags
		String tagsStr = line.getString(HEADER_TAGS, "");
		if (StringUtils.isNotBlank(tagsStr)) {
			Set<Tag> tags = new HashSet<Tag>(tagListParser.parseList(tagsStr));
			flag.setTags(tags);
		} else if (flag.getTags() != null) {
			// Clear tags if not specified (only if tags were previously set)
			flag.getTags().clear();
		}
		
		return flag;
	}
	
	/**
	 * Fetches a Priority by UUID or name.
	 */
	private Priority fetchPriority(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		// Try UUID first
		Priority priority = flagService.getPriorityByUuid(id);
		if (priority != null) {
			return priority;
		}
		
		// Try name
		priority = flagService.getPriorityByName(id);
		if (priority != null) {
			return priority;
		}
		
		return null;
	}
}
