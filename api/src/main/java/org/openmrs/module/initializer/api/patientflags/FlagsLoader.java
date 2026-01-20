package org.openmrs.module.initializer.api.patientflags;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Loads Flags from CSV files.
 */
@Component("initializer.flagFlagsLoader")
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class FlagsLoader extends BaseCsvLoader<Flag, FlagsCsvParser> {
	
	@Autowired
	public void setParser(FlagsCsvParser parser) {
		this.parser = parser;
	}
}
