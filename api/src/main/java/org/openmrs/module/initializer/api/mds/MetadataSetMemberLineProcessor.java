package org.openmrs.module.initializer.api.mds;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.mdm.MetadataTermMappingsLineProcessor;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataSetMemberLineProcessor extends BaseLineProcessor<MetadataSetMember> {
	
	final public static String METADATA_SET_UUID = "metadata set uuid";
	
	final public static String METADATA_CLASS = "metadata class";
	
	final public static String SORT_WEIGHT = "sort weight";
	
	@Autowired
	private MetadataMappingService service;
	
	@Override
	public MetadataSetMember fill(MetadataSetMember setMember, CsvLine line) throws IllegalArgumentException {
		setMember.setName(line.get(HEADER_NAME));
		setMember.setDescription(line.get(HEADER_DESC));
		setMember.setSortWeight(line.getDouble(SORT_WEIGHT));
		setMember.setMetadataClass(line.get(METADATA_CLASS, true));
		setMember.setMetadataUuid(line.get(MetadataTermMappingsLineProcessor.METADATA_UUID, true));
		MetadataSet set = service.getMetadataSetByUuid(line.get(METADATA_SET_UUID, true));
		setMember.setMetadataSet(set);
		return setMember;
	}
}
