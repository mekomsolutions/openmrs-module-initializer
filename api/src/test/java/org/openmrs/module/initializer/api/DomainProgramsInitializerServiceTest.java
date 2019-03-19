package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DomainProgramsInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PROG;
	}
	
	@Before
	public void setup() {
		
		// Concepts to be used as 'program concepts' ( with local preferred name )
		{
			ConceptName conceptName1 = new ConceptName();
			conceptName1.setName("Program11");
			conceptName1.setLocalePreferred(true);
			conceptName1.setLocale(Locale.ENGLISH);
			
			ConceptName conceptName2 = new ConceptName();
			conceptName2.setName("Program12");
			conceptName2.setLocale(Locale.ENGLISH);
			
			ConceptName conceptName3 = new ConceptName();
			conceptName3.setName("Programm13");
			conceptName3.setLocalePreferred(true);
			conceptName3.setLocale(Locale.GERMAN);
			
			Set<ConceptName> conceptNames = new HashSet<ConceptName>();
			conceptNames.add(conceptName1);
			conceptNames.add(conceptName2);
			conceptNames.add(conceptName3);
			
			Concept c = new Concept();
			c.setNames(conceptNames);
			c.setFullySpecifiedName(conceptName2);
			c.setShortName(new ConceptName("programConceptTest1", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		// Concepts to be used as 'program concepts' ( with full specified name )
		{
			ConceptName conceptName1 = new ConceptName();
			conceptName1.setName("Program21");
			conceptName1.setLocale(Locale.ENGLISH);
			
			ConceptName conceptName2 = new ConceptName();
			conceptName2.setName("Program22");
			conceptName2.setLocale(Locale.ENGLISH);
			
			Set<ConceptName> conceptNames = new HashSet<ConceptName>();
			conceptNames.add(conceptName1);
			conceptNames.add(conceptName2);
			
			Concept c = new Concept();
			c.setNames(conceptNames);
			c.setShortName(new ConceptName("programConceptTest2", Locale.ENGLISH));
			c.setFullySpecifiedName(conceptName2);
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("programConceptTest3", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		// Concepts to be used as 'outcomes concepts'
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("outcomesConceptTest1", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("outcomesConceptTest2", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("outcomesConceptTest3", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c = cs.saveConcept(c);
		}
		// A program to be edited
		{
			Program pro = new Program();
			pro.setUuid("5dc2a3b0-863c-4074-8f84-45762c3aa04c");
			pro.setConcept(cs.getConceptByName("programConceptTest1"));
			pro.setOutcomesConcept(cs.getConceptByName("outcomesConceptTest1"));
			pro.setName("Test Program");
			pro.setDescription("Program Description");
			pro = pws.saveProgram(pro);
		}
	}
	
	@Test
	@Verifies(value = "should load and save programs from the CSV", method = "")
	public void loadPrograms_shouldLoadProgramsAccordingToCsvFiles() {
		
		// Replay
		getService().loadPrograms();
		
		// created programs
		{
			Program pro = pws.getProgramByName("Program11");
			Assert.assertNotNull(pro);
			Assert.assertEquals(cs.getConceptByName("programConceptTest1"), pro.getConcept());
			Assert.assertEquals(cs.getConceptByName("outcomesConceptTest1"), pro.getOutcomesConcept());
			Assert.assertEquals("Program11", pro.getDescription());
			
		}
		{
			Program pro = pws.getProgramByName("Program22");
			Assert.assertNotNull(pro);
			Assert.assertEquals(cs.getConceptByName("programConceptTest2"), pro.getConcept());
			Assert.assertEquals(cs.getConceptByName("outcomesConceptTest2"), pro.getOutcomesConcept());
			Assert.assertEquals("Program22", pro.getDescription());
		}
		// an edited program
		{
			Program pro = pws.getProgramByName("programConceptTest3");
			Assert.assertNotNull(pro);
			Assert.assertEquals(cs.getConceptByName("programConceptTest3"), pro.getConcept());
			Assert.assertEquals(cs.getConceptByName("outcomesConceptTest3"), pro.getOutcomesConcept());
			Assert.assertEquals("programConceptTest3", pro.getName());
			Assert.assertEquals("programConceptTest3", pro.getDescription());
		}
	}
}
