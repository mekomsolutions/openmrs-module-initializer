package org.openmrs.module.initializer.api.programs;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramsCsvParser extends CsvParser<Program, BaseLineProcessor<Program>> {
	
	private ProgramWorkflowService pwfService;
	
	private ConceptService conceptService;
	
	@Autowired
	public ProgramsCsvParser(@Qualifier("programWorkflowService") ProgramWorkflowService pwfService,
	    @Qualifier("conceptService") ConceptService conceptService, ProgramLineProcessor processor) {
		super(processor);
		this.pwfService = pwfService;
		this.conceptService = conceptService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROGRAMS;
	}
	
	@Override
	public Program bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		String id = uuid;
		if (id == null) {
			id = line.getName(); // name is nullable for programs
		}
		if (id == null) {
			id = line.get(ProgramLineProcessor.HEADER_CONCEPT_PROGRAM);
		}
		
		Program program = Utils.fetchProgram(id, pwfService, conceptService);
		if (program == null) {
			program = new Program();
			if (!StringUtils.isEmpty(uuid)) {
				program.setUuid(uuid);
			}
		}
		
		return program;
	}
	
	@Override
	public Program save(Program instance) {
		return pwfService.saveProgram(instance);
	}
}
