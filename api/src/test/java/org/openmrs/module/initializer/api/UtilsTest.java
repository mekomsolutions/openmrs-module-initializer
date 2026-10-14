package org.openmrs.module.initializer.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {
	
	private MockedStatic<Context> contextMock;
	
	@AfterEach
	public void closeStaticMocks() {
		contextMock.close();
	}
	
	@BeforeEach
	public void setUp() {
		contextMock = Mockito.mockStatic(Context.class);
		AdministrationService as = mock(AdministrationService.class);
		when(Context.getAdministrationService()).thenReturn(as);
		when(as.getAllowedLocales()).thenReturn(Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN));
		when(Context.getLocale()).thenReturn(Locale.ENGLISH);
		
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		when(Context.getProgramWorkflowService()).thenReturn(pws);
		
		ConceptService cs = mock(ConceptService.class);
		when(Context.getConceptService()).thenReturn(cs);
		
		InitializerService ics = mock(InitializerService.class);
		when(Context.getService(InitializerService.class)).thenReturn(ics);
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
		Assertions.assertEquals("\n" + "+------------+-----------+-----+\n" + "| First name | Last name | Age |\n"
		        + "+------------+-----------+-----+\n" + "|       John |       Doe |  40 |\n"
		        + "+------------+-----------+-----+\n" + "|       Paul |     Smith |  20 |\n"
		        + "+------------+-----------+-----+",
		    Utils.prettyPrint(lines));
	}
	
	@Test
	public void prettyPrint_shouldThrowWhenCsvLinesHeadersDiffer() {
		assertThrows(IllegalArgumentException.class, () -> {
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
			
		});
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
		Assertions.assertEquals("\nFirst name,Last name,Age\n" + "John,Doe,40\n" + "Paul,Smith,20", Utils.pastePrint(lines));
	}
	
	@Test
	public void pastePrint_shouldThrowWhenCsvLinesHeadersDiffer() {
		assertThrows(IllegalArgumentException.class, () -> {
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
			
		});
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
		
		Assertions.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assertions.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assertions.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assertions.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.GERMAN));
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("An FSN in English");
			cn.setLocalePreferred(true);
			cn.setLocale(Locale.ENGLISH);
			c.setFullySpecifiedName(cn);
		}
		
		Assertions.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assertions.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assertions.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assertions.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.GERMAN));
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("A preferred name in English");
			cn.setLocalePreferred(true);
			cn.setLocale(Locale.ENGLISH);
			c.addName(cn);
		}
		
		Assertions.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assertions.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assertions.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assertions.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.GERMAN));
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
		
		Assertions.assertEquals(prog, Utils.fetchProgram("Program Name", pws, cs));
		Assertions.assertEquals(prog, Utils.fetchProgram("program-uuid", pws, cs));
		Assertions.assertEquals(prog, Utils.fetchProgram("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchProgram_shouldReturnNullWhenMultipleMatchesByConcept() {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		when(pws.getProgramsByConcept(c)).thenReturn(Arrays.asList(new Program(), new Program()));
		
		Assertions.assertNull(Utils.fetchProgram("concept-uuid", pws, cs));
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
		
		Assertions.assertEquals(wf, Utils.fetchProgramWorkflow("workflow-uuid", pws, cs));
		Assertions.assertEquals(wf, Utils.fetchProgramWorkflow("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchWorkflow_shouldReturnNullWhenMultipleMatchesByConcept() {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		when(pws.getProgramWorkflowsByConcept(c)).thenReturn(Arrays.asList(new ProgramWorkflow(), new ProgramWorkflow()));
		
		Assertions.assertNull(Utils.fetchProgramWorkflow("concept-uuid", pws, cs));
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
		
		Assertions.assertEquals(state, Utils.fetchProgramWorkflowState("state-uuid", pws, cs));
		Assertions.assertEquals(state, Utils.fetchProgramWorkflowState("concept-uuid", pws, cs));
	}
	
	@Test
	public void fetchWorkflowState_shouldReturnNullWhenMultipleMatchesByConcept() {
		ConceptService cs = mock(ConceptService.class);
		ProgramWorkflowService pws = mock(ProgramWorkflowService.class);
		
		Concept c = new Concept();
		c.setUuid("concept-uuid");
		when(pws.getProgramWorkflowStatesByConcept(c))
		        .thenReturn(Arrays.asList(new ProgramWorkflowState(), new ProgramWorkflowState()));
		
		Assertions.assertNull(Utils.fetchProgramWorkflowState("concept-uuid", pws, cs));
	}
	
	@Test
	public void generateUuidFromObjects_shouldReturnUuidGivenValidArguments() {
		// replay
		String uuid = Utils.generateUuidFromObjects("edaef9f4-2b5b-4b71-9019-74f1d40ad4d7", "Oedema",
		    ConceptNameType.FULLY_SPECIFIED, Locale.ENGLISH);
		
		// verify
		Assertions.assertNotNull(uuid);
		Assertions.assertEquals("f6fa2a4e-78a3-3378-a30a-c27c67f5734e", uuid);
	}
	
	@Test
	public void generateUuidFromObjects_shouldNotFailGivenNullArguments() {
		// replay
		String uuid = Utils.generateUuidFromObjects("some-uuid", null, ConceptNameType.SHORT, Locale.ENGLISH);
		
		// verify
		Assertions.assertNotNull(uuid);
	}
	
	@Test
	public void unProxy_shouldReturnOriginalClassName() {
		Assertions.assertEquals("EncounterType", Utils.unProxy("EncounterType$HibernateProxy$ODcBnusu"));
		Assertions.assertEquals("EncounterType", Utils.unProxy("EncounterType_$$_javassist_26"));
		Assertions.assertEquals("EncounterType", Utils.unProxy("EncounterType"));
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
		Assertions.assertEquals(uuidConcept, Utils.fetchConcept("concept:lookup", cs));
	}
	
	@Test
	public void fetchConcept_shouldFetchConceptByMapping() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptByUuid("concept:lookup")).thenReturn(null);
		Concept mappingConcept = new Concept();
		when(cs.getConceptByMapping("lookup", "concept")).thenReturn(mappingConcept);
		Concept nameConcept = new Concept();
		when(cs.getConceptByName("concept:lookup")).thenReturn(nameConcept);
		Assertions.assertEquals(mappingConcept, Utils.fetchConcept("concept:lookup", cs));
	}
	
	@Test
	public void fetchConcept_shouldFetchConceptByName() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptByUuid("concept:lookup")).thenReturn(null);
		when(cs.getConceptByMapping("lookup", "concept")).thenReturn(null);
		Concept nameConcept = new Concept();
		
		InitializerService ics = mock(InitializerService.class);
		when(Context.getService(InitializerService.class)).thenReturn(ics);
		when(ics.getUnretiredConceptsByFullySpecifiedName("concept:lookup")).thenReturn(Arrays.asList(nameConcept));
		Assertions.assertEquals(nameConcept, Utils.fetchConcept("concept:lookup", cs));
	}
	
	@Test
	public void getSameAsConceptMapType_shouldFetchBasedOnUuidConstant() {
		ConceptService cs = mock(ConceptService.class);
		ConceptMapType sameAsMapType = new ConceptMapType();
		when(cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID)).thenReturn(sameAsMapType);
		Assertions.assertEquals(sameAsMapType, Utils.getSameAsConceptMapType(cs));
	}
	
	@Test
	public void fetchConceptSource_shouldFetchConceptSourceByName() {
		ConceptSource source = new ConceptSource();
		String lookup = "concept-lookup";
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptSourceByName(lookup)).thenReturn(null);
		when(cs.getConceptSourceByHL7Code(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUniqueId(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUuid(lookup)).thenReturn(null);
		
		Assertions.assertNull(Utils.fetchConceptSource(lookup, cs));
		when(cs.getConceptSourceByName(lookup)).thenReturn(source);
		Assertions.assertEquals(source, Utils.fetchConceptSource(lookup, cs));
	}
	
	@Test
	public void fetchConceptSource_shouldFetchConceptSourceByHl7Code() {
		ConceptSource source = new ConceptSource();
		String lookup = "concept-lookup";
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptSourceByName(lookup)).thenReturn(null);
		when(cs.getConceptSourceByHL7Code(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUniqueId(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUuid(lookup)).thenReturn(null);
		
		Assertions.assertNull(Utils.fetchConceptSource(lookup, cs));
		when(cs.getConceptSourceByHL7Code(lookup)).thenReturn(source);
		Assertions.assertEquals(source, Utils.fetchConceptSource(lookup, cs));
	}
	
	@Test
	public void fetchConceptSource_shouldFetchConceptSourceByUniqueId() {
		ConceptSource source = new ConceptSource();
		String lookup = "concept-lookup";
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptSourceByName(lookup)).thenReturn(null);
		when(cs.getConceptSourceByHL7Code(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUniqueId(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUuid(lookup)).thenReturn(null);
		
		Assertions.assertNull(Utils.fetchConceptSource(lookup, cs));
		when(cs.getConceptSourceByUniqueId(lookup)).thenReturn(source);
		Assertions.assertEquals(source, Utils.fetchConceptSource(lookup, cs));
	}
	
	@Test
	public void fetchConceptSource_shouldFetchConceptSourceByUuid() {
		ConceptSource source = new ConceptSource();
		String lookup = "concept-lookup";
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptSourceByName(lookup)).thenReturn(null);
		when(cs.getConceptSourceByHL7Code(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUniqueId(lookup)).thenReturn(null);
		when(cs.getConceptSourceByUuid(lookup)).thenReturn(null);
		
		Assertions.assertNull(Utils.fetchConceptSource(lookup, cs));
		when(cs.getConceptSourceByUuid(lookup)).thenReturn(source);
		Assertions.assertEquals(source, Utils.fetchConceptSource(lookup, cs));
	}
	
	@Test
	public void getDrugByMapping_shouldReturnNullIfLookupIsNullOrEmpty() {
		ConceptService cs = mock(ConceptService.class);
		Assertions.assertNull(Utils.getDrugByMapping(null, cs));
		Assertions.assertNull(Utils.getDrugByMapping("", cs));
		Assertions.assertNull(Utils.getDrugByMapping("    ", cs));
	}
	
	@Test
	public void getDrugByMapping_shouldReturnNullIfNoSourceAndCodeSpecified() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptSourceByName("source")).thenReturn(null);
		Assertions.assertNull(Utils.getDrugByMapping("source_code", cs));
	}
	
	@Test
	public void getDrugByMapping_shouldReturnNullIfSourceIsNotFound() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptSourceByName("source")).thenReturn(null);
		Assertions.assertNull(Utils.getDrugByMapping("source:code", cs));
	}
	
	@Test
	public void getDrugByMapping_shouldReturnDrugByMapping() {
		ConceptService cs = mock(ConceptService.class);
		Drug drug = new Drug();
		ConceptSource source = new ConceptSource();
		when(cs.getConceptSourceByName("source")).thenReturn(source);
		ConceptMapType sameAsMapType = new ConceptMapType();
		when(cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID)).thenReturn(sameAsMapType);
		when(cs.getDrugByMapping(eq("code"), eq(source), anyCollection())).thenReturn(drug);
		Assertions.assertEquals(drug, Utils.getDrugByMapping("source:code", cs));
	}
	
	@Test
	public void fetchConcept_shouldThrowGivenMoreThanOneConeptWithSameFullySpecifiedName() {
		ConceptService cs = mock(ConceptService.class);
		when(cs.getConceptByUuid("concept:lookup")).thenReturn(null);
		when(cs.getConceptByMapping("lookup", "concept")).thenReturn(null);
		Concept nameConcept = new Concept();
		Concept nameConcept2 = new Concept();
		
		InitializerService ics = mock(InitializerService.class);
		when(Context.getService(InitializerService.class)).thenReturn(ics);
		when(ics.getUnretiredConceptsByFullySpecifiedName("concept:lookup"))
		        .thenReturn(Arrays.asList(nameConcept, nameConcept2));
		Assertions.assertThrows(RuntimeException.class, () -> Utils.fetchConcept("concept:lookup", cs));
	}
}
