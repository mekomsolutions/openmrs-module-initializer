package org.openmrs.module.initializer.api.mdm;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataTermMappingsLineProcessor extends BaseLineProcessor<MetadataTermMapping> {
	
	final public static String MAPPING_SOURCE = "mapping source";
	
	final public static String MAPPING_CODE = "mapping code";
	
	final public static String METADATA_CLASS_NAME = "metadata class name";
	
	final public static String METADATA_UUID = "metadata uuid";
	
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
	public MetadataTermMapping fill(MetadataTermMapping mapping, CsvLine line) throws IllegalArgumentException {
		MetadataSource source = mdmService.getMetadataSourceByName(line.get(MAPPING_SOURCE, true));
		
		mapping.setMetadataSource(source);
		mapping.setCode(line.get(MAPPING_CODE, true));
		mapping.setMetadataUuid(line.get(METADATA_UUID, true));
		mapping.setMetadataClass(line.get(METADATA_CLASS_NAME, true));
		
		return mapping;
	}
}
