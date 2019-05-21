package org.openmrs.module.initializer.api;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.programs.ProgramsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ProgramsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@Autowired
	private ProgramsLoader loader;
	
	public static void setupPrograms(ConceptService cs, ProgramWorkflowService pws) {
		
		// Concepts to be used as 'program concepts'
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("TB Program", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("AIDS Program", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Oncology Program", Locale.ENGLISH));
			c.addDescription(
			    new ConceptDescription("A regular oncology program with traditional chimotherapy.", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Intensive Oncology Program", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Mental Health Program", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Program"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		
		// Concepts to be used as 'outcomes concepts'
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("TB Program Outcomes", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("AIDS Program Outcomes", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Oncology Program Outcomes", Locale.ENGLISH));
			c.setSet(false);
			c.setDatatype(cs.getConceptDatatypeByName("N/A"));
			c.setConceptClass(cs.getConceptClassByName("ConvSet"));
			c = cs.saveConcept(c);
		}
		
		// A program to be edited
		{
			Program prog = new Program();
			prog.setUuid("5dc2a3b0-863c-4074-8f84-45762c3aa04c");
			prog.setConcept(cs.getConceptByName("Intensive Oncology Program"));
			prog.setOutcomesConcept(cs.getConceptByName("Oncology Program Outcomes"));
			prog.setName("Intensive Oncology Program");
			prog.setDescription("A special oncology program with stronger and even experimental treatments.");
			prog = pws.saveProgram(prog);
		}
		
		// A program to be retired
		{
			{
				Concept c = new Concept();
				c.setShortName(new ConceptName("Ayurvedic Medicine Program", Locale.ENGLISH));
				c.setConceptClass(cs.getConceptClassByName("Program"));
				c.setDatatype(cs.getConceptDatatypeByName("Text"));
				c = cs.saveConcept(c);
			}
			{
				Concept c = new Concept();
				c.setShortName(new ConceptName("Ayurvedic Medicine Program Program Outcomes", Locale.ENGLISH));
				c.setConceptClass(cs.getConceptClassByName("ConvSet"));
				c.setSet(false);
				c.setDatatype(cs.getConceptDatatypeByName("N/A"));
				c = cs.saveConcept(c);
			}
			Program prog = new Program();
			prog.setUuid("28f3da50-3f56-4e4e-93cd-66f334970480");
			prog.setConcept(cs.getConceptByName("Ayurvedic Medicine Program"));
			prog.setOutcomesConcept(cs.getConceptByName("Ayurvedic Medicine Program Program Outcomes"));
			prog.setName("Ayurvedic Medicine Program");
			prog.setDescription("An atypical program using Ayurvedic medicine.");
			prog = pws.saveProgram(prog);
		}
	}
	
	@Before
	public void setup() {
		setupPrograms(cs, pws);
	}
	
	@Test
	public void load_shouldLoadProgramsAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// created programs
		{
			Program prog = pws.getProgramByName("TB Program");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("TB Program"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("TB Program Outcomes"), prog.getOutcomesConcept());
			Assert.assertEquals("TB Program", prog.getDescription());
			
		}
		{
			Program prog = pws.getProgramByName("AIDS Program");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("AIDS Program"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("AIDS Program Outcomes"), prog.getOutcomesConcept());
			Assert.assertEquals("AIDS Program", prog.getDescription());
		}
		{
			Program prog = pws.getProgramByName("Mental Health Program");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("Mental Health Program"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("Mental Health Program Outcomes"), prog.getOutcomesConcept());
			Assert.assertEquals("Mental Health Program", prog.getDescription());
		}
		
		// an edited program
		{
			Program prog = pws.getProgramByName("Oncology Program");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("Oncology Program"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("Oncology Program Outcomes"), prog.getOutcomesConcept());
			Assert.assertEquals("Oncology Program", prog.getName());
			Assert.assertEquals("A regular oncology program with traditional chimotherapy.", prog.getDescription());
		}
		
		// an retired program
		{
			Program prog = pws.getProgramByName("Ayurvedic Medicine Program");
			Assert.assertNotNull(prog);
			Assert.assertTrue(prog.isRetired());
		}
	}
}
