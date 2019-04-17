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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
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
	public void fetchProgram_shouldReturnProgramFromGivenId() throws Exception {
		
		ConceptService cs = Context.getConceptService();
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		ProgramsLoaderIntegrationTest.setupPrograms(cs, pws);
		
		Program prog = new Program();
		prog.setUuid("eae98b4c-e195-403b-b34a-82d94103b2c0");
		prog.setConcept(cs.getConceptByName("TB Program"));
		prog.setOutcomesConcept(cs.getConceptByName("TB Program Outcomes"));
		prog.setName("TB Program");
		prog = pws.saveProgram(prog);
		
		// fetch program by it's name
		Assert.assertEquals(prog, Utils.fetchProgram("TB Program", pws, cs));
		// fetch program by it's UUID
		Assert.assertEquals(prog, Utils.fetchProgram("eae98b4c-e195-403b-b34a-82d94103b2c0", pws, cs));
		// fetch program by it's underlying concept UUID
		Assert.assertEquals(prog, Utils.fetchProgram("3ccc7158-26fe-102b-80cb-0017a47871b2", pws, cs));
		
	}
}
