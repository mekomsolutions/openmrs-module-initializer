package org.openmrs.module.initializer.api.metadata;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetadataTermMappingsCsvParser extends CsvParser<MetadataTermMapping, MetadataMappingServiceWrapper, MetadataTermMappingsLineProcessor> {
	
	@Autowired
	public MetadataTermMappingsCsvParser(MetadataMappingServiceWrapper service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.METADATA_MAPPINGS;
	}
	
	@Override
	protected MetadataTermMapping save(MetadataTermMapping instance) {
		return service.saveMetadataTermMapping(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(MetadataTermMapping instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new MetadataTermMappingsLineProcessor(headerLine, service));
	}
}
