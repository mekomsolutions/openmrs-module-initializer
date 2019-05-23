package org.openmrs.module.initializer.api.loaders;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.metadata.MetadataTermMappingsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataMappingsLoader extends BaseCsvLoader<MetadataTermMappingsCsvParser> {
	
	@Autowired
	public void setParser(MetadataTermMappingsCsvParser parser) {
		this.parser = parser;
	}
}
