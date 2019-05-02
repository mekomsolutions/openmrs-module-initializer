package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.metadata.MetadataTermMappingsCsvParser;
import org.openmrs.module.initializer.api.metadata.MetadataMappingServiceWrapper;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataMappingsLoader extends BaseCsvLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.METADATA_MAPPINGS;
	}
	
	@Autowired
	private MetadataMappingService service;
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new MetadataTermMappingsCsvParser(is, new MetadataMappingServiceWrapper(service));
	}
}
