package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.Verifies;

import java.util.Locale;

public class DomainProgramWorkFlowsInitializerServiceTest extends DomainProgramsInitializerServiceTest {
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PROG_WF;
	}
	
	@Before
	public void setup() {
		
		super.setup();

		// load the programs from csv.
		getService().loadPrograms();
		
		// Concepts to be used for programs and programWorkflows
		{
			Concept programWorkflowConcept1 = new Concept();
			programWorkflowConcept1.setShortName(new ConceptName("workflowConcept1", Locale.ENGLISH));
			programWorkflowConcept1.setConceptClass(cs.getConceptClassByName("Program"));
			programWorkflowConcept1.setDatatype(cs.getConceptDatatypeByName("Text"));
			programWorkflowConcept1 = cs.saveConcept(programWorkflowConcept1);
			
			Concept programWorkflowConcept2 = new Concept();
			programWorkflowConcept2.setShortName(new ConceptName("workflowConcept2", Locale.ENGLISH));
			programWorkflowConcept2.setConceptClass(cs.getConceptClassByName("Program"));
			programWorkflowConcept2.setDatatype(cs.getConceptDatatypeByName("Text"));
			programWorkflowConcept2 = cs.saveConcept(programWorkflowConcept2);
		}
		// A existing workflow to be linked to another program
		{
			ProgramWorkflow programWorkflow = new ProgramWorkflow();
			programWorkflow.setConcept(cs.getConceptByName("workflowConcept2"));
			programWorkflow.setUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			
			Program program = pws.getProgramByName("programConceptTest1");
			program.addWorkflow(programWorkflow);
		}
	}
	
	@Test
	@Verifies(value = "should load and save programWorkflows from the CSV", method = "")
	public void loadProgramWorkFlows_shouldLoadProgramWorkflowsAccordingToCsvFiles() {
		
		// Replay
		getService().loadProgramWorkflows();
		
		// A created programWorkflow
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			Assert.assertNotNull(programWorkflow);
			Assert.assertEquals(pws.getProgramByName("programConceptTest1"), programWorkflow.getProgram());
			Assert.assertEquals(cs.getConceptByName("workflowConcept1"), programWorkflow.getConcept());
			Assert.assertEquals("workflowConcept1", programWorkflow.getName());
			Assert.assertEquals("workflowConcept1", programWorkflow.getDescription());
			Assert.assertEquals(false, programWorkflow.isRetired());
			
			Program program = pws.getProgramByName("programConceptTest1");
			Assert.assertEquals(programWorkflow, program.getWorkflow(programWorkflow.getId()));
		}
		// A retired programWorkflow
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ef");
			Assert.assertNull(programWorkflow);
		}
		// An edited programWorkflow
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			Assert.assertNotNull(programWorkflow);
			Assert.assertEquals(pws.getProgramByName("programConceptTest2"), programWorkflow.getProgram());
			Assert.assertEquals(cs.getConceptByName("workflowConcept2"), programWorkflow.getConcept());
		}
	}
}
