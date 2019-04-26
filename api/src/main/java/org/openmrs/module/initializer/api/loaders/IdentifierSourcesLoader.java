package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:*" })
public class IdentifierSourcesLoader extends BaseCsvLoader {
	
	@Autowired
	private IdentifierSourceService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_IDGEN;
	}
	
	@Override
	public Integer getOrder() {
		return 11;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new IdentifierSourcesCsvParser(is, service);
	}
	
}
