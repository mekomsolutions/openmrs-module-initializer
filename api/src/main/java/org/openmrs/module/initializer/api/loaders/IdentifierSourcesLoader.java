package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:*" })
public class IdentifierSourcesLoader extends BaseCsvLoader<IdentifierSourcesCsvParser> {
	
	@Autowired
	public void setParser(IdentifierSourcesCsvParser parser) throws IOException {
		this.parser = parser;
	}
	
}
