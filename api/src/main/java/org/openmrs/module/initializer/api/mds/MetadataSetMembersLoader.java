package org.openmrs.module.initializer.api.mds;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "metadatamapping:*" })
public class MetadataSetMembersLoader extends BaseCsvLoader<MetadataSetMember, MetadataSetMembersCsvParser> {
	
	@Autowired
	public void setParser(MetadataSetMembersCsvParser parser) {
		this.parser = parser;
	}
}
