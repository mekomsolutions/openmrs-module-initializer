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
	protected ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	protected ProgramWorkflowService pws;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PROG;
	}
	
	@Before
	public void setup() {
		
		// Concepts to be used as 'program concepts'
		{
			Concept programConcept1 = new Concept();
			programConcept1.setShortName(new ConceptName("programConceptTest1", Locale.ENGLISH));
			programConcept1.setConceptClass(cs.getConceptClassByName("Program"));
			programConcept1.setDatatype(cs.getConceptDatatypeByName("Text"));
			programConcept1 = cs.saveConcept(programConcept1);
			
			Concept programConcept2 = new Concept();
			programConcept2.setShortName(new ConceptName("programConceptTest2", Locale.ENGLISH));
			programConcept2.setConceptClass(cs.getConceptClassByName("Program"));
			programConcept2.setDatatype(cs.getConceptDatatypeByName("Text"));
			programConcept2 = cs.saveConcept(programConcept2);
			
			Concept programConcept3 = new Concept();
			programConcept3.setShortName(new ConceptName("programConceptTest3", Locale.ENGLISH));
			programConcept3.setConceptClass(cs.getConceptClassByName("Program"));
			programConcept3.setDatatype(cs.getConceptDatatypeByName("Text"));
			programConcept3 = cs.saveConcept(programConcept3);
		}
		// Concepts to be used as 'outcomes concepts'
		{
			Concept outcomeConcept1 = new Concept();
			outcomeConcept1.setShortName(new ConceptName("outcomesConceptTest1", Locale.ENGLISH));
			outcomeConcept1.setConceptClass(cs.getConceptClassByName("ConvSet"));
			outcomeConcept1.setSet(false);
			outcomeConcept1.setDatatype(cs.getConceptDatatypeByName("N/A"));
			outcomeConcept1 = cs.saveConcept(outcomeConcept1);
			
			Concept outcomeConcept2 = new Concept();
			outcomeConcept2.setShortName(new ConceptName("outcomesConceptTest2", Locale.ENGLISH));
			outcomeConcept2.setConceptClass(cs.getConceptClassByName("ConvSet"));
			outcomeConcept2.setSet(false);
			outcomeConcept2.setDatatype(cs.getConceptDatatypeByName("N/A"));
			outcomeConcept2 = cs.saveConcept(outcomeConcept2);
			
			Concept outcomeConcept3 = new Concept();
			outcomeConcept3.setShortName(new ConceptName("outcomesConceptTest3", Locale.ENGLISH));
			outcomeConcept3.setConceptClass(cs.getConceptClassByName("ConvSet"));
			outcomeConcept3.setSet(false);
			outcomeConcept3.setDatatype(cs.getConceptDatatypeByName("N/A"));
			outcomeConcept3 = cs.saveConcept(outcomeConcept3);
		}
		// A program to be edited
		{
			Program prog = new Program();
			prog.setUuid("5dc2a3b0-863c-4074-8f84-45762c3aa04c");
			prog.setConcept(cs.getConceptByName("programConceptTest1"));
			prog.setOutcomesConcept(cs.getConceptByName("outcomesConceptTest1"));
			prog.setName("Test Program");
			prog.setDescription("Program Description");
			prog = pws.saveProgram(prog);
		}
	}
	
	@Test
	@Verifies(value = "should load and save programs from the CSV", method = "")
	public void loadPrograms_shouldLoadProgramsAccordingToCsvFiles() {
		
		// Replay
		getService().loadPrograms();
		
		// created programs
		{
			Program prog = pws.getProgramByName("programConceptTest1");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("programConceptTest1"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("outcomesConceptTest1"), prog.getOutcomesConcept());
			Assert.assertEquals("programConceptTest1", prog.getDescription());
			
		}
		{
			Program prog = pws.getProgramByName("programConceptTest2");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("programConceptTest2"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("outcomesConceptTest2"), prog.getOutcomesConcept());
			Assert.assertEquals("programConceptTest2", prog.getDescription());
		}
		// an edited program
		{
			Program prog = pws.getProgramByName("programConceptTest3");
			Assert.assertNotNull(prog);
			Assert.assertEquals(cs.getConceptByName("programConceptTest3"), prog.getConcept());
			Assert.assertEquals(cs.getConceptByName("outcomesConceptTest3"), prog.getOutcomesConcept());
			Assert.assertEquals("programConceptTest3", prog.getName());
			Assert.assertEquals("programConceptTest3", prog.getDescription());
		}
	}
}
