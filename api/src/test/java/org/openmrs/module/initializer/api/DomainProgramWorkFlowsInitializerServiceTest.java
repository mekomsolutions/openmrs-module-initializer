package org.openmrs.module.initializer.api;

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
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DomainProgramWorkFlowsInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PROG_WF;
	}
	
	@Before
	public void setup() {
		
		// Concepts to be used for programs and programWorkflows
		{
			Concept programConcept = new Concept();
			programConcept.setShortName(new ConceptName("programConcept", Locale.ENGLISH));
			programConcept.setConceptClass(cs.getConceptClassByName("Program"));
			programConcept.setDatatype(cs.getConceptDatatypeByName("Text"));
			programConcept = cs.saveConcept(programConcept);
			
			Concept c = new Concept();
			c.setShortName(new ConceptName("outcomesConceptTest", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c = cs.saveConcept(c);
			
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
		// A Program used for add ProgramWorkflows
		{
			Program pro = new Program();
			pro.setConcept(cs.getConceptByName("programConcept"));
			pro.setOutcomesConcept(cs.getConceptByName("outcomesConceptTest1"));
			pro.setName("program1");
			pro.setDescription("Program Description");
			pro = pws.saveProgram(pro);
		}
		// A programWorkFlow to be edited
		{
			ProgramWorkflow programWorkflow = new ProgramWorkflow();
			programWorkflow.setConcept(cs.getConceptByName("concept2"));
			programWorkflow.setUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			
			Program program = pws.getProgramByName("program1");
			program.addWorkflow(programWorkflow);
		}
	}
	
	@Test
	@Verifies(value = "should load and save programs from the CSV", method = "")
	public void loadProgramWorkFlows_shouldLoadProgramWorkflowsAccordingToCsvFiles() {
		
		// Replay
		getService().loadProgramWorkflows();
		
		// created programWorkflows
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			Assert.assertNotNull(programWorkflow);
			Assert.assertEquals(pws.getProgramByName("program1"), programWorkflow.getProgram());
			Assert.assertEquals(cs.getConceptByName("concept1"), programWorkflow.getConcept());
			Assert.assertEquals(false, programWorkflow.isRetired());
			
			Program program = pws.getProgramByName("program1");
			Assert.assertEquals(programWorkflow, program.getWorkflow(programWorkflow.getId()));
			
		}
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ef");
			Assert.assertNull(programWorkflow);
		}
		// An edited programWorkflow
		{
			ProgramWorkflow programWorkflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee");
			Assert.assertNotNull(programWorkflow);
			Assert.assertEquals(true, programWorkflow.isRetired());
			
			Program program = pws.getProgramByName("program1");
			Assert.assertNotNull(program);
			Assert.assertNull(program.getWorkflow(programWorkflow.getId()));
			
		}
	}
}
