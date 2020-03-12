package org.openmrs.module.initializer.api.mds;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataSetMembersCsvParser extends CsvParser<MetadataSetMember, BaseLineProcessor<MetadataSetMember>> {
	
	private MetadataMappingService mdmService;
	
	@Autowired
	public MetadataSetMembersCsvParser(MetadataMappingService mds, BaseLineProcessor<MetadataSetMember> lineProcessor) {
		super(lineProcessor);
		mdmService = mds;
	}
	
	@Override
	public MetadataSetMember bootstrap(CsvLine line) throws IllegalArgumentException {
		MetadataSetMember member = null;
		String uuid = line.getUuid();
		if (StringUtils.isNotBlank(uuid)) {
			member = mdmService.getMetadataSetMemberByUuid(uuid);
		}
		if (member == null) {
			member = new MetadataSetMember();
			member.setUuid(uuid);
		}
		return member;
	}
	
	@Override
	public MetadataSetMember save(MetadataSetMember setMember) {
		return mdmService.saveMetadataSetMember(setMember);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.METADATA_SET_MEMBERS;
	}
}
