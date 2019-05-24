package org.openmrs.module.initializer.api.mdm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataMappingsLoader extends BaseCsvLoader<MetadataTermMapping, MetadataTermMappingsCsvParser> {
	
	@Autowired
	public void setParser(MetadataTermMappingsCsvParser parser) {
		this.parser = parser;
	}
}
