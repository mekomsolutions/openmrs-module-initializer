package org.openmrs.module.initializer.api.mds;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.metadatamapping.MetadataSet;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataSetLineProcessor extends BaseLineProcessor<MetadataSet> {
	
	@Override
	public MetadataSet fill(MetadataSet metadataSet, CsvLine line) throws IllegalArgumentException {
		metadataSet.setName(line.getName());
		metadataSet.setDescription(line.get(HEADER_DESC));
		return metadataSet;
	}
	
}
