package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.ProgramWorkflowStatesLoader;
import org.openmrs.module.initializer.api.loaders.ProgramWorkflowsLoader;
import org.openmrs.module.initializer.api.loaders.ProgramsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Locale;

public class ProgramWorkflowStatesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@Autowired
	private ProgramsLoader progLoader;
	
	@Autowired
	private ProgramWorkflowsLoader workflowsLoader;
	
	@Autowired
	private ProgramWorkflowStatesLoader loader;
	
	@Before
	public void setup() {
		
		ProgramsLoaderIntegrationTest.setupPrograms(cs, pws);
		progLoader.load();
		ProgramWorkflowsLoaderIntegrationTest.setupWorkflows(cs, pws);
		workflowsLoader.load();
		
		// a couple of concepts for defining states
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("stateConcept1", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("State"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("stateConcept2", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("State"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("stateConcept4", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("State"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		
		// a state to attempt to be added to another workflow
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("stateConcept3", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("State"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			ProgramWorkflowState state = new ProgramWorkflowState();
			state.setConcept(c);
			state.setUuid("88b717c0-f580-497a-8d2b-026b60dd6bfd");
			state.setTerminal(true);
			state.setInitial(true);
			
			ProgramWorkflow wf = Utils.fetchProgramWorkflow("Palliative Care (workflow)", pws, cs);
			wf.addState(state);
		}
		
		// a state to be retired
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("stateConcept5", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("State"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			ProgramWorkflowState state = new ProgramWorkflowState();
			state.setConcept(c);
			state.setUuid("cfa24690-2700-102b-80cb-0017a47871b2");
			state.setTerminal(true);
			state.setInitial(true);
			
			ProgramWorkflow wf = Utils.fetchProgramWorkflow("Extended Discharge (workflow)", pws, cs);
			wf.addState(state);
		}
		
	}
	
	@Test
	public void loadProgramWorkFlowStates_shouldLoadProgramWorkflowStatesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verify
		
		// created state
		{
			ProgramWorkflowState state = pws.getStateByUuid("cfa241f4-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(state);
			
			ProgramWorkflow wf = Utils.fetchProgramWorkflow("TB Treatment Status (workflow)", pws, cs);
			Assert.assertEquals(wf, state.getProgramWorkflow());
			
			Assert.assertEquals(cs.getConceptByName("stateConcept1"), state.getConcept());
			Assert.assertEquals("stateConcept1", state.getName());
			Assert.assertEquals("stateConcept1", state.getDescription());
			Assert.assertFalse(state.isRetired());
			Assert.assertTrue(state.getInitial());
			Assert.assertFalse(state.getTerminal());
			Assert.assertEquals(state, wf.getState(state.getId()));
		}
		
		// state created with workflow UUID
		{
			ProgramWorkflowState state = pws.getStateByUuid("cfa244b0-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(state);
			Assert.assertEquals(cs.getConceptByName("stateConcept2"), state.getConcept());
			
			ProgramWorkflow wf = Utils.fetchProgramWorkflow("Extended Discharge (workflow)", pws, cs);
			Assert.assertEquals(wf, state.getProgramWorkflow());
		}
		
		// state NOT added to a another workflow
		{
			ProgramWorkflowState state = pws.getStateByUuid("88b717c0-f580-497a-8d2b-026b60dd6bfd");
			Assert.assertEquals(Utils.fetchProgramWorkflow("Palliative Care (workflow)", pws, cs),
			    state.getProgramWorkflow());
			Assert.assertFalse(
			    Utils.fetchProgramWorkflow("TB Treatment Status (workflow)", pws, cs).getStates().contains(state));
		}
		
		// state created without UUID
		{
			ProgramWorkflow wf = Utils.fetchProgramWorkflow("Standard Treatment Status (workflow)", pws, cs);
			ProgramWorkflowState state = wf.getState("stateConcept4");
			Assert.assertNotNull(state);
		}
		
		// retired state
		{
			ProgramWorkflowState state = pws.getStateByUuid("cfa24690-2700-102b-80cb-0017a47871b2");
			Assert.assertTrue(state.isRetired());
			
			ProgramWorkflow wf = Utils.fetchProgramWorkflow("Extended Discharge (workflow)", pws, cs);
			Assert.assertTrue(wf.getStates().contains(state));
			Assert.assertTrue(wf.getStates(true).contains(state));
		}
	}
}
