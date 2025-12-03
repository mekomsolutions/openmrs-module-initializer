package org.openmrs.module.initializer.api.patientflags;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Loads Priorities from CSV files.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class PrioritiesLoader extends BaseCsvLoader<Priority, PrioritiesCsvParser> {
	
	@Autowired
	public void setParser(PrioritiesCsvParser parser) {
		this.parser = parser;
	}
}
