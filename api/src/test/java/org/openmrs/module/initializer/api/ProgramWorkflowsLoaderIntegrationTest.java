package org.openmrs.module.initializer.api;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.ProgramWorkflowsLoader;
import org.openmrs.module.initializer.api.loaders.ProgramsLoader;
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
	
	@Before
	public void setup() {
		
		ProgramsLoaderIntegrationTest.setupPrograms(cs, pws);
		progLoader.load();
		
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("TB Treatment Status (workflow)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Workflow"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		
		// a workflow to be added to another program
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
	}
	
	@Test
	public void loadProgramWorkFlows_shouldLoadProgramWorkflowsAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// created workflow
		{
			ProgramWorkflow wf = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			Assert.assertNotNull(wf);
			Program prog = pws.getProgramByName("TB Program");
			Assert.assertEquals(prog, wf.getProgram());
			Assert.assertEquals(cs.getConceptByName("TB Treatment Status (workflow)"), wf.getConcept());
			Assert.assertEquals("TB Treatment Status (workflow)", wf.getName());
			Assert.assertEquals("TB Treatment Status (workflow)", wf.getDescription());
			Assert.assertFalse(wf.isRetired());
			Assert.assertEquals(wf, prog.getWorkflow(wf.getId()));
		}
		
		// workflow added to a second program
		{
			ProgramWorkflow wf = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			Assert.assertTrue(pws.getProgramByName("TB Program").getAllWorkflows().contains(wf));
			Assert.assertTrue(pws.getProgramByName("AIDS Program").getAllWorkflows().contains(wf));
		}
	}
}
