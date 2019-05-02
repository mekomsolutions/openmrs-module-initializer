package org.openmrs.module.initializer.api.metadata;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;

/**
 * This wrapper class for {@link MetadataMappingService} since it does not extend
 * {@link OpenmrsService}.
 */
public class MetadataMappingServiceWrapper implements OpenmrsService {
	
	protected MetadataMappingService mdms;
	
	public MetadataMappingServiceWrapper(MetadataMappingService mdms) {
		this.mdms = mdms;
	}
	
	public void setMetadataMappingService(MetadataMappingService mdms) {
		this.mdms = mdms;
	}
	
	@Override
	public void onStartup() {
		
	}
	
	@Override
	public void onShutdown() {
		
	}
	
	/**
	 * Fetches a metadataterm for the uuid.
	 * 
	 * @param sourceName The metada source name.
	 * @param mappingCode
	 * @return The {@MetadataTermMapping } instance if found, new instance otherwise.
	 */
	public MetadataTermMapping getMetadataTermMapping(String sourceName, String mappingCode) {
		MetadataSource source = getMetadataSourceByName(sourceName);
		MetadataTermMapping mapping = mdms.getMetadataTermMapping(source, mappingCode);
		
		return mapping;
	}
	
	/**
	 * Fetches a metadataterm for the uuid.
	 * 
	 * @param uuid The metada term mapping Uuid
	 * @return The {@MetadataTermMapping } instance if found, new instance otherwise.
	 */
	public MetadataTermMapping getMetadataTermMappingByUuid(String uuid) {
		MetadataTermMapping mapping = mdms.getMetadataTermMappingByUuid(uuid);
		return mapping;
	}
	
	/**
	 * Fetches a metadata source for the name.
	 * 
	 * @param sourceName The metadata source name.
	 * @param service
	 * @return The {@MetadataSource } instance if found, throws exception otherwise.
	 */
	public MetadataSource getMetadataSourceByName(String sourceName) throws IllegalArgumentException {
		MetadataSource source = mdms.getMetadataSourceByName(sourceName);
		if (source == null) {
			throw new IllegalArgumentException("No source with name " + sourceName);
		}
		return source;
	}
	
	public MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping mapping) {
		return mdms.saveMetadataTermMapping(mapping);
	}
}
