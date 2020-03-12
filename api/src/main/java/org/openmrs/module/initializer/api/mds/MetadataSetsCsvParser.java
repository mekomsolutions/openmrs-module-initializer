package org.openmrs.module.initializer.api.mds;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataSetsCsvParser extends CsvParser<MetadataSet, BaseLineProcessor<MetadataSet>> {
	
	private MetadataMappingService mdmService;
	
	@Autowired
	protected MetadataSetsCsvParser(MetadataMappingService mds, BaseLineProcessor<MetadataSet> lineProcessor) {
		super(lineProcessor);
		mdmService = mds;
	}
	
	@Override
	public MetadataSet bootstrap(CsvLine line) throws IllegalArgumentException {
		MetadataSet metadataSet = null;
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			metadataSet = mdmService.getMetadataSetByUuid(uuid);
		}
		if (metadataSet == null) {
			metadataSet = new MetadataSet();
			metadataSet.setUuid(uuid);
		}
		return metadataSet;
	}
	
	@Override
	public MetadataSet save(MetadataSet metadataSet) {
		return mdmService.saveMetadataSet(metadataSet);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.METADATA_SETS;
	}
	
}
