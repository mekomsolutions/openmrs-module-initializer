package org.openmrs.module.initializer.api.datafilter.mappings;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "datafilter:*" })
public class DataFilterMappingsLoader extends BaseCsvLoader<DataFilterMapping, DataFilterMappingsCsvParser> {
	
	@Autowired
	public void setParser(DataFilterMappingsCsvParser parser) {
		this.parser = parser;
	}
}
