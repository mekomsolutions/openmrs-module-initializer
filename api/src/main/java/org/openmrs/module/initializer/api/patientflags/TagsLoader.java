package org.openmrs.module.initializer.api.patientflags;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Loads Tags from CSV files.
 */
@OpenmrsProfile(modules = { "patientflags:3.* - 9.*" })
public class TagsLoader extends BaseCsvLoader<Tag, TagsCsvParser> {
	
	@Autowired
	public void setParser(TagsCsvParser parser) {
		this.parser = parser;
	}
}
