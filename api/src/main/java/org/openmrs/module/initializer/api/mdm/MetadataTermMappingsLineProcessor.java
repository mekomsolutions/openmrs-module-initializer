package org.openmrs.module.initializer.api.mdm;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataTermMappingsLineProcessor extends BaseLineProcessor<MetadataTermMapping> {
	
	protected static String MAPPING_SOURCE = "Mapping source";
	
	protected static String MAPPING_CODE = "Mapping code";
	
	protected static String METADATA_CLASS_NAME = "Metadata class name";
	
	protected static String METADATA_UUID = "Metadata Uuid";
	
	private MetadataMappingService mdmService;
	
	/**
	 * @param headerLine The header line the processor will refer to.
	 * @param service
	 */
	@Autowired
	public MetadataTermMappingsLineProcessor(MetadataMappingService mdmService) {
		this.mdmService = mdmService;
	}
	
	@Override
	protected MetadataTermMapping bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		String sourceName = line.get(MAPPING_SOURCE, true);
		String mappingCode = line.get(MAPPING_CODE, true);
		
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
	protected MetadataTermMapping fill(MetadataTermMapping mapping, CsvLine line) throws IllegalArgumentException {
		MetadataSource source = mdmService.getMetadataSourceByName(line.get(MAPPING_SOURCE, true));
		mapping.setMetadataSource(source);
		mapping.setCode(line.get(MAPPING_CODE, true));
		mapping.setMetadataUuid(line.get(METADATA_UUID, true));
		mapping.setMetadataClass(line.get(METADATA_CLASS_NAME, true));
		
		return mapping;
	}
}
