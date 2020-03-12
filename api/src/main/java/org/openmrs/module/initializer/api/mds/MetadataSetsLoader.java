package org.openmrs.module.initializer.api.mds;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataSetsLoader extends BaseCsvLoader<MetadataSet, MetadataSetsCsvParser> {
	
	@Autowired
	public void setParser(MetadataSetsCsvParser parser) {
		this.parser = parser;
	}
}
