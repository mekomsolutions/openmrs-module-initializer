package org.openmrs.module.initializer.api;

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
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	public void prettyPrint_shouldPrettyPrintCsvLines() {
		// setup
		List<CsvLine> lines = new ArrayList<>();
		String[] commonHeader = { "First name", "Last name", "Age" };
		{
			String[] line = { "John", "Doe", "40" };
			lines.add(new CsvLine(commonHeader, line));
		}
		{
			String[] line = { "Paul", "Smith", "20" };
			lines.add(new CsvLine(commonHeader, line));
		}
		
		// replay
		Assert.assertEquals("\n" + "+------------+-----------+-----+\n" + "| First name | Last name | Age |\n"
		        + "+------------+-----------+-----+\n" + "|       John |       Doe |  40 |\n"
		        + "+------------+-----------+-----+\n" + "|       Paul |     Smith |  20 |\n"
		        + "+------------+-----------+-----+",
		    Utils.prettyPrint(lines));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void prettyPrint_shouldThrowWhenCsvLinesHeadersDiffer() {
		// setup
		List<CsvLine> lines = new ArrayList<>();
		{
			String[] header = { "First name", "Last name", "Age" };
			String[] line = { "John", "Doe", "40" };
			lines.add(new CsvLine(header, line));
		}
		{
			String[] header = { "First name", "Last name", "Height" };
			String[] line = { "Phileas", "Fogg", "1.75" };
			lines.add(new CsvLine(header, line));
		}
		
		// replay
		Utils.prettyPrint(lines);
	}
	
	@Test
	public void pastePrint_shouldPastePrintCsvLines() {
		// setup
		List<CsvLine> lines = new ArrayList<>();
		String[] commonHeader = { "First name", "Last name", "Age" };
		{
			String[] line = { "John", "Doe", "40" };
			lines.add(new CsvLine(commonHeader, line));
		}
		{
			String[] line = { "Paul", "Smith", "20" };
			lines.add(new CsvLine(commonHeader, line));
		}
		
		// replay
		Assert.assertEquals("\nFirst name,Last name,Age\n" + "John,Doe,40\n" + "Paul,Smith,20", Utils.pastePrint(lines));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void pastePrint_shouldThrowWhenCsvLinesHeadersDiffer() {
		// setup
		List<CsvLine> lines = new ArrayList<>();
		{
			String[] header = { "First name", "Last name", "Age" };
			String[] line = { "John", "Doe", "40" };
			lines.add(new CsvLine(header, line));
		}
		{
			String[] header = { "First name", "Last name", "Height" };
			String[] line = { "Phileas", "Fogg", "1.75" };
			lines.add(new CsvLine(header, line));
		}
		
		// replay
		Utils.pastePrint(lines);
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
		when(cs.getConceptByUuid("concept-uuid")).thenReturn(c);
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
		when(cs.getConceptByUuid("concept-uuid")).thenReturn(c);
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
		when(cs.getConceptByUuid("concept-uuid")).thenReturn(c);
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
	
	@Test
	public void generateUuidFromObjects_shouldReturnUuidGivenValidArguments() {
		// replay
		String uuid = Utils.generateUuidFromObjects("edaef9f4-2b5b-4b71-9019-74f1d40ad4d7", "Oedema",
		    ConceptNameType.FULLY_SPECIFIED, Locale.ENGLISH);
		
		// verify
		Assert.assertNotNull(uuid);
		Assert.assertEquals("f6fa2a4e-78a3-3378-a30a-c27c67f5734e", uuid);
	}
	
	@Test
	public void generateUuidFromObjects_shouldNotFailGivenNullArguments() {
		// replay
		String uuid = Utils.generateUuidFromObjects("some-uuid", null, ConceptNameType.SHORT, Locale.ENGLISH);
		
		// verify
		Assert.assertNotNull(uuid);
	}
	
	@Test
	public void unProxy_shouldReturnOriginalClassName() {
		Assert.assertEquals("EncounterType", Utils.unProxy("EncounterType$HibernateProxy$ODcBnusu"));
		Assert.assertEquals("EncounterType", Utils.unProxy("EncounterType_$$_javassist_26"));
		Assert.assertEquals("EncounterType", Utils.unProxy("EncounterType"));
	}
	
	@Test
	public void fetchConcept_shouldFetchConceptByUuid() {
		ConceptService cs = mock(ConceptService.class);
		Concept uuidConcept = new Concept();
		when(cs.getConceptByUuid("concept:lookup")).thenReturn(uuidConcept);
		Concept mappingConcept = new Concept();
		when(cs.getConceptByMapping("lookup", "concept")).thenReturn(mappingConcept);
		Concept nameConcept = new Concept();
		when(cs.getConceptByName("concept:lookup")).thenReturn(nameConcept);
		Assert.assertEquals(uuidConcept, Utils.fetchConcept("concept:lookup", cs));
	}
	
	@Test
	public void fetchConcept_shouldFetchConceptByMapping() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptByUuid("concept:lookup")).thenReturn(null);
		Concept mappingConcept = new Concept();
		when(cs.getConceptByMapping("lookup", "concept")).thenReturn(mappingConcept);
		Concept nameConcept = new Concept();
		when(cs.getConceptByName("concept:lookup")).thenReturn(nameConcept);
		Assert.assertEquals(mappingConcept, Utils.fetchConcept("concept:lookup", cs));
	}
	
	@Test
	public void fetchConcept_shouldFetchConceptByName() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptByUuid("concept:lookup")).thenReturn(null);
		when(cs.getConceptByMapping("lookup", "concept")).thenReturn(null);
		Concept nameConcept = new Concept();
		when(cs.getConceptByName("concept:lookup")).thenReturn(nameConcept);
		Assert.assertEquals(nameConcept, Utils.fetchConcept("concept:lookup", cs));
	}
}
