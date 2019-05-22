package org.openmrs.module.initializer.api.metadata;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.metadatamapping.MetadataTermMapping;

public class MetadataTermMappingsCsvParser extends CsvParser<MetadataTermMapping, MetadataMappingServiceWrapper, MetadataTermMappingsLineProcessor> {
	
	public MetadataTermMappingsCsvParser(InputStream is, MetadataMappingServiceWrapper mdms) throws IOException {
		super(is, mdms);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.MDS;
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
		addLineProcessor(new MetadataTermMappingsLineProcessor(headerLine, service));
	}
}
