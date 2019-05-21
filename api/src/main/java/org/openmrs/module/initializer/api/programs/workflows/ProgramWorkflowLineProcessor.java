package org.openmrs.module.initializer.api.programs.workflows;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProgramWorkflowLineProcessor extends BaseLineProcessor<ProgramWorkflow> {
	
	protected static String HEADER_PROGRAM = "program";
	
	protected static String HEADER_WORKFLOW_CONCEPT = "workflow concept";
	
	private ProgramWorkflowService pwfService;
	
	private ConceptService conceptService;
	
	/**
	 * @param headerLine The header line the processor will refer to.
	 * @param pwfService
	 */
	@Autowired
	public ProgramWorkflowLineProcessor(@Qualifier("programWorkflowService") ProgramWorkflowService pwService,
	    @Qualifier("conceptService") ConceptService conceptService) {
		this.pwfService = pwService;
		this.conceptService = conceptService;
	}
	
	@Override
	protected ProgramWorkflow bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		
		String id = uuid;
		if (id == null) {
			id = line.get(HEADER_WORKFLOW_CONCEPT);
		}
		
		ProgramWorkflow wf = Utils.fetchProgramWorkflow(id, pwfService, Context.getConceptService());
		if (wf == null) {
			wf = new ProgramWorkflow();
			if (!StringUtils.isEmpty(uuid)) {
				wf.setUuid(uuid);
			}
		}
		
		return wf;
	}
	
	@Override
	protected ProgramWorkflow fill(ProgramWorkflow wf, CsvLine line) throws IllegalArgumentException {
		
		Concept c = Utils.fetchConcept(line.get(HEADER_WORKFLOW_CONCEPT), conceptService);
		wf.setConcept(c);
		
		wf.setName(Utils.getBestMatchName(c, Context.getLocale()));
		wf.setDescription(Utils.getBestMatchDescription(c, Context.getLocale()));
		
		Program prog = Utils.fetchProgram(line.get(HEADER_PROGRAM, true), pwfService, conceptService);
		
		// workflows must be bound to a program
		if (prog == null) {
			throw new IllegalArgumentException("No program could be fetched from the CSV line: '" + line.toString() + "'.");
		}
		// workflows linked to a program can't be moved to another program
		if (wf.getProgram() != null && !prog.equals(wf.getProgram())) {
			throw new IllegalArgumentException(
			        "A workflow ('" + wf.getName() + "') already linked to a program ('" + wf.getProgram().getName()
			                + "') cannot be added to another program, CSV line: '" + line.toString() + "'.");
		}
		wf.setProgram(prog);
		
		return wf;
	}
}
