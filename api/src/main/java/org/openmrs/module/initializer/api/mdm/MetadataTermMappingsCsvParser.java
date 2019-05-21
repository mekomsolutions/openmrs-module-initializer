package org.openmrs.module.initializer.api.mdm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataTermMappingsCsvParser extends CsvParser<MetadataTermMapping, BaseLineProcessor<MetadataTermMapping>> {
	
	private MetadataMappingService mdmService;
	
	@Autowired
	public MetadataTermMappingsCsvParser(MetadataMappingService mdms, MetadataTermMappingsLineProcessor processor) {
		super(processor);
		this.mdmService = mdms;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.METADATA_MAPPINGS;
	}
	
	@Override
	protected MetadataTermMapping save(MetadataTermMapping instance) {
		return mdmService.saveMetadataTermMapping(instance);
	}
}
