package org.openmrs.module.initializer.api;

import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.programs.ProgramsLoader;
import org.openmrs.module.initializer.api.programs.workflows.ProgramWorkflowsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ProgramWorkflowsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@Autowired
	private ProgramsLoader progLoader;
	
	@Autowired
	private ProgramWorkflowsLoader loader;
	
	public static void setupWorkflows(ConceptService cs, ProgramWorkflowService pws) {
		
		// a couple of concepts for defining workflows
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("TB Treatment Status (workflow)", Locale.ENGLISH));
			c.setDescriptions(
			    Collections.singleton(new ConceptDescription("TB Treatment Status (workflow)", Locale.ENGLISH)));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Palliative Care (workflow)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Discharge (workflow)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		
		// a workflow whose underlying defining concept will be changed
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Extended Discharge (workflow)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			ProgramWorkflow wf = new ProgramWorkflow();
			wf.setConcept(c);
			wf.setUuid("1b42d0e8-20ad-4bd8-b05d-fbad80a3b665");
			
			Program prog = pws.getProgramByName("AIDS Program");
			prog.addWorkflow(wf);
			pws.saveProgram(prog);
		}
		
		// a workflow to attempt to be added to another program
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Standard Treatment Status (workflow)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			ProgramWorkflow wf = new ProgramWorkflow();
			wf.setConcept(c);
			wf.setUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			
			Program prog = pws.getProgramByName("AIDS Program");
			prog.addWorkflow(wf);
			pws.saveProgram(prog);
		}
		
		// a workflow to be retired
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Electroshock (workflow)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			ProgramWorkflow wf = new ProgramWorkflow();
			wf.setConcept(c);
			wf.setUuid("45a28ee9-20a3-4065-9955-9cb7a0c6a24b");
			
			Program prog = pws.getProgramByName("Mental Health Program");
			prog.addWorkflow(wf);
			pws.saveProgram(prog);
		}
	}
	
	@BeforeEach
	public void setup() {
		
		ProgramsLoaderIntegrationTest.setupPrograms(cs, pws);
		progLoader.load();
		ProgramWorkflowsLoaderIntegrationTest.setupWorkflows(cs, pws);
	}
	
	@Test
	public void load_shouldLoadProgramWorkflowsAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verify
		
		// created workflow
		{
			ProgramWorkflow wf = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			Assertions.assertNotNull(wf);
			Program prog = pws.getProgramByName("TB Program");
			Assertions.assertEquals(prog, wf.getProgram());
			Assertions.assertEquals(cs.getConceptByName("TB Treatment Status (workflow)"), wf.getConcept());
			Assertions.assertEquals("TB Treatment Status (workflow)", wf.getConcept().getName().getName());
			Assertions.assertEquals("TB Treatment Status (workflow)", wf.getConcept().getDescription().getDescription());
			Assertions.assertFalse(wf.isRetired());
			Assertions.assertEquals(wf, prog.getWorkflow(wf.getId()));
		}
		
		// workflow NOT added to a another program
		{
			ProgramWorkflow wf = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			Assertions.assertEquals(pws.getProgramByName("AIDS Program"), wf.getProgram());
			Assertions.assertFalse(pws.getProgramByName("TB Program").getAllWorkflows().contains(wf));
		}
		
		// workflow created without UUID
		{
			Program prog = pws.getProgramByName("AIDS Program");
			ProgramWorkflow wf = prog.getWorkflowByName("Palliative Care (workflow)");
			Assertions.assertNotNull(wf);
		}
		
		// workflow with its concept changed
		{
			ProgramWorkflow wf = pws.getWorkflowByUuid("1b42d0e8-20ad-4bd8-b05d-fbad80a3b665");
			Assertions.assertEquals(cs.getConceptByName("Extended Discharge (workflow)"), wf.getConcept());
		}
		
		// retired workflow
		{
			ProgramWorkflow wf = pws.getWorkflowByUuid("45a28ee9-20a3-4065-9955-9cb7a0c6a24b");
			Assertions.assertTrue(wf.isRetired());
			
			Program prog = pws.getProgramByName("Mental Health Program");
			wf = prog.getWorkflowByName("Electroshock (workflow)");
			Assertions.assertFalse(prog.getWorkflows().contains(wf));
			Assertions.assertTrue(prog.getAllWorkflows().contains(wf));
		}
	}
}
