package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.Verifies;

import java.util.Locale;

public class DomainProgramWorkFlowStatesInitializerServiceTest extends DomainProgramWorkFlowsInitializerServiceTest {
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PROG_WF_ST;
	}
	
	@Before
	public void setup() {
		
		super.setup();

		// load the programWorkflows from csv.
		getService().loadProgramWorkflows();
		
		// Concepts to be used for programWorkflowStates
		{
			Concept stateConcept1 = new Concept();
			stateConcept1.setShortName(new ConceptName("stateConcept1", Locale.ENGLISH));
			stateConcept1.setConceptClass(cs.getConceptClassByName("Program"));
			stateConcept1.setDatatype(cs.getConceptDatatypeByName("Text"));
			stateConcept1 = cs.saveConcept(stateConcept1);

			Concept stateConcept2 = new Concept();
			stateConcept2.setShortName(new ConceptName("stateConcept2", Locale.ENGLISH));
			stateConcept2.setConceptClass(cs.getConceptClassByName("Program"));
			stateConcept2.setDatatype(cs.getConceptDatatypeByName("Text"));
			stateConcept2 = cs.saveConcept(stateConcept2);
		}
		// A existing programWorkflowState to be linked to another programWorkflow
		{
			ProgramWorkflowState state = new ProgramWorkflowState();
			state.setConcept(cs.getConceptByName("stateConcept2"));
			state.setUuid("31c82d66-245c-11e1-9cf0-00248140a5b");
			state.setInitial(true);
			state.setTerminal(true);
			ProgramWorkflow workflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			workflow.addState(state);
		}
	}
	
	@Test
	@Verifies(value = "should load and save programWorkflowStates from the CSV", method = "")
	public void loadProgramWorkFlows_shouldLoadProgramWorkflowStatesAccordingToCsvFiles() {
		
		// Replay
		getService().loadProgramWorkflowStates();
		
		// A created programWorkflowState
		{
			ProgramWorkflowState state = pws.getStateByUuid("cfa241f4-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(state);
			Assert.assertEquals(pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb"), state.getProgramWorkflow());
			Assert.assertEquals(cs.getConceptByName("stateConcept1"), state.getConcept());
			Assert.assertEquals("stateConcept1", state.getName());
			Assert.assertEquals("stateConcept1", state.getDescription());
			Assert.assertEquals(false, state.isRetired());
			Assert.assertEquals(false, state.getInitial());
			Assert.assertEquals(true, state.getTerminal());
			
			ProgramWorkflow workflow = pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5eb");
			Assert.assertEquals(state, workflow.getState(state.getId()));
		}
		// A retired programWorkflowState
		{
			ProgramWorkflowState state = pws.getStateByUuid("cfa24690-2700-102b-80cb-0017a47871b2");
			Assert.assertNull(state);
		}
		// An edited programWorkflowState
		{
			ProgramWorkflowState state = pws.getStateByUuid("cfa244b0-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(state);
			Assert.assertEquals(pws.getWorkflowByUuid("2b98bc76-245c-11e1-9cf0-00248140a5ee"), state.getProgramWorkflow());
			Assert.assertEquals(cs.getConceptByName("stateConcept2"), state.getConcept());
		}
	}
}
