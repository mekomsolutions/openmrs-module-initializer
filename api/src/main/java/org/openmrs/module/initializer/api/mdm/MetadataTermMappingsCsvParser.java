package org.openmrs.module.initializer.api.mdm;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
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
	public MetadataTermMapping bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		String sourceName = line.get(MetadataTermMappingsLineProcessor.MAPPING_SOURCE, true);
		String mappingCode = line.get(MetadataTermMappingsLineProcessor.MAPPING_CODE, true);
		
		MetadataTermMapping mapping = null;
		if (!StringUtils.isEmpty(uuid)) {
			mapping = mdmService.getMetadataTermMappingByUuid(uuid);
		}
		if (mapping == null) {
			mapping = mdmService.getMetadataTermMapping(sourceName, mappingCode);
		}
		if (mapping == null) {
			mapping = new MetadataTermMapping();
			if (!StringUtils.isEmpty(uuid)) {
				mapping.setUuid(uuid);
			}
		}
		
		return mapping;
	}
	
	@Override
	public MetadataTermMapping save(MetadataTermMapping instance) {
		return mdmService.saveMetadataTermMapping(instance);
	}
}
