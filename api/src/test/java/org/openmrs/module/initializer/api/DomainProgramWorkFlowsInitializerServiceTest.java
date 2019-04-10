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
		
		// Concepts to be used for programs and programWorkflows
		{
			Concept programWorkflowConcept1 = new Concept();
			programWorkflowConcept1.setShortName(new ConceptName("concept1", Locale.ENGLISH));
			programWorkflowConcept1.setConceptClass(cs.getConceptClassByName("Program"));
			programWorkflowConcept1.setDatatype(cs.getConceptDatatypeByName("Text"));
			programWorkflowConcept1 = cs.saveConcept(programWorkflowConcept1);
			
			Concept programWorkflowConcept2 = new Concept();
			programWorkflowConcept2.setShortName(new ConceptName("concept2", Locale.ENGLISH));
			programWorkflowConcept2.setConceptClass(cs.getConceptClassByName("Program"));
			programWorkflowConcept2.setDatatype(cs.getConceptDatatypeByName("Text"));
			programWorkflowConcept2 = cs.saveConcept(programWorkflowConcept2);
		}
		// Programs used for add ProgramWorkflows
		{
			Program program1 = new Program();
			program1.setConcept(cs.getConceptByName("programConceptTest1"));
			program1.setOutcomesConcept(cs.getConceptByName("outcomesConceptTest1"));
			program1.setName("program11");
			program1 = pws.saveProgram(program1);
			
			Program program2 = new Program();
			program2.setConcept(cs.getConceptByName("programConceptTest2"));
			program2.setOutcomesConcept(cs.getConceptByName("outcomesConceptTest2"));
			program2.setName("program22");
			program2 = pws.saveProgram(program2);
		}
		// A existing workflow to be linked to another program
		{
			ProgramWorkflow programWorkflow = new ProgramWorkflow();
			programWorkflow.setConcept(cs.getConceptByName("concept2"));
			programWorkflow.setUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			
			Program program = pws.getProgramByName("program11");
			program.addWorkflow(programWorkflow);
		}
	}
	
	@Test
	@Verifies(value = "should load and save programs from the CSV", method = "")
	public void loadProgramWorkFlows_shouldLoadProgramWorkflowsAccordingToCsvFiles() {
		
		// Replay
		getService().loadProgramWorkflows();
		
		// A created programWorkflow
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			Assert.assertNotNull(programWorkflow);
			Assert.assertEquals(pws.getProgramByName("program11"), programWorkflow.getProgram());
			Assert.assertEquals(cs.getConceptByName("concept1"), programWorkflow.getConcept());
			Assert.assertEquals("concept1", programWorkflow.getName());
			Assert.assertEquals("concept1", programWorkflow.getDescription());
			Assert.assertEquals(false, programWorkflow.isRetired());
			
			Program program = pws.getProgramByName("program11");
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
			Assert.assertEquals(pws.getProgramByName("program22"), programWorkflow.getProgram());
			Assert.assertEquals(cs.getConceptByName("concept2"), programWorkflow.getConcept());
		}
	}
}
