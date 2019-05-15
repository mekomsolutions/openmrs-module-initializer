package org.openmrs.module.initializer.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class UtilsTest {
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(Context.class);
		AdministrationService as = mock(AdministrationService.class);
		when(Context.getAdministrationService()).thenReturn(as);
		when(as.getAllowedLocales()).thenReturn(Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN));
		when(Context.getLocale()).thenReturn(Locale.ENGLISH);
		
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		when(Context.getProgramWorkflowService()).thenReturn(pws);
		
		ConceptService cs = mock(ConceptService.class);
		when(Context.getConceptService()).thenReturn(cs);
	}
	
	@Test
	public void getBestMatchName_shouldReturnBestMatchForConceptName() throws Exception {
		
		Concept c = new Concept();
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("A name in English");
			cn.setLocale(Locale.ENGLISH);
			c.addName(cn);
		}
		
		Assert.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assert.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.GERMAN));
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("An FSN in English");
			cn.setLocalePreferred(true);
			cn.setLocale(Locale.ENGLISH);
			c.setFullySpecifiedName(cn);
		}
		
		Assert.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assert.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.GERMAN));
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("A preferred name in English");
			cn.setLocalePreferred(true);
			cn.setLocale(Locale.ENGLISH);
			c.addName(cn);
		}
		
		Assert.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assert.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.GERMAN));
	}
	
	@Test
	public void fetchProgram_shouldReturnProgramFromAnyId() throws Exception {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		Program prog = new Program();
		prog.setUuid("program-uuid");
		prog.setName("Program Name");
		
		when(pws.getProgramByName("Program Name")).thenReturn(prog);
		when(pws.getProgramByUuid("program-uuid")).thenReturn(prog);
		when(Utils.fetchConcept("concept-uuid", cs)).thenReturn(c);
		when(pws.getProgramsByConcept(c)).thenReturn(Arrays.asList(prog));
		
		Assert.assertEquals(prog, Utils.fetchProgram("Program Name", pws, cs));
		Assert.assertEquals(prog, Utils.fetchProgram("program-uuid", pws, cs));
		Assert.assertEquals(prog, Utils.fetchProgram("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchProgram_shouldReturnNullWhenMultipleMatchesByConcept() {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		when(pws.getProgramsByConcept(c)).thenReturn(Arrays.asList(new Program(), new Program()));
		
		Assert.assertNull(Utils.fetchProgram("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchWorkflow_shouldReturnWorkflowFromAnyId() throws Exception {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		ProgramWorkflow wf = new ProgramWorkflow();
		wf.setUuid("workflow-uuid");
		
		when(pws.getWorkflowByUuid("workflow-uuid")).thenReturn(wf);
		when(Utils.fetchConcept("concept-uuid", cs)).thenReturn(c);
		when(pws.getProgramWorkflowsByConcept(c)).thenReturn(Arrays.asList(wf));
		
		Assert.assertEquals(wf, Utils.fetchProgramWorkflow("workflow-uuid", pws, cs));
		Assert.assertEquals(wf, Utils.fetchProgramWorkflow("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchWorkflow_shouldReturnNullWhenMultipleMatchesByConcept() {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		when(pws.getProgramWorkflowsByConcept(c)).thenReturn(Arrays.asList(new ProgramWorkflow(), new ProgramWorkflow()));
		
		Assert.assertNull(Utils.fetchProgramWorkflow("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchWorkflowState_shouldReturnWorkflowStateFromAnyId() throws Exception {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		ProgramWorkflowState state = new ProgramWorkflowState();
		state.setUuid("state-uuid");
		
		when(pws.getStateByUuid("state-uuid")).thenReturn(state);
		when(Utils.fetchConcept("concept-uuid", cs)).thenReturn(c);
		when(pws.getProgramWorkflowStatesByConcept(c)).thenReturn(Arrays.asList(state));
		
		Assert.assertEquals(state, Utils.fetchProgramWorkflowState("state-uuid", pws, cs));
		Assert.assertEquals(state, Utils.fetchProgramWorkflowState("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchWorkflowState_shouldReturnNullWhenMultipleMatchesByConcept() {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		when(pws.getProgramWorkflowStatesByConcept(c))
		        .thenReturn(Arrays.asList(new ProgramWorkflowState(), new ProgramWorkflowState()));
		
		Assert.assertNull(Utils.fetchProgramWorkflowState("concept-uuid", pws, cs));
	}
}
