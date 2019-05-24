package org.openmrs.module.initializer.api.idgen;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:*" })
public class IdentifierSourcesLoader extends BaseCsvLoader<IdgenSourceWrapper, IdentifierSourcesCsvParser> {
	
	@Autowired
	public void setParser(IdentifierSourcesCsvParser parser) {
		this.parser = parser;
	}
}
