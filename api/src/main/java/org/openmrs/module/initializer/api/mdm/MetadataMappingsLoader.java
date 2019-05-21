package org.openmrs.module.initializer.api.mdm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataMappingsLoader extends BaseCsvLoader<MetadataTermMappingsCsvParser> {
	
	@Autowired
	public void setParser(MetadataTermMappingsCsvParser parser) {
		this.parser = parser;
	}
}
