package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.programs.ProgramsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramsLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_PROG;
	}
	
	@Override
	public Integer getOrder() {
		return 7;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new ProgramsCsvParser(is, service);
	}
	
}
