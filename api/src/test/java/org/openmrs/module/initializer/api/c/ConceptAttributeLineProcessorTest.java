package org.openmrs.module.initializer.api.c;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptAttributeLineProcessorTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	private Concept testConcept;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
		executeDataSet("testdata/test-concepts-attribute.xml");
		testConcept = cs.getConcept(5501);
		
	}
	
	@Test
	public void fill_ShouldFetchConceptAttributeTypeByName() {
		
		// Setup
		String[] headerLine = { "Fully Specified Name:en", "attribute|Test" };
		String[] line = { "TESTING_CONCEPT", "True" };
		
		// Replay
		ConceptAttributeLineProcessor calp = new ConceptAttributeLineProcessor(cs);
		Concept c = calp.fill(testConcept, new CsvLine(headerLine, line));
		
		//Verify
		
		ConceptAttribute attribute = (ConceptAttribute) c.getAttributes().toArray()[0];
		
		Assert.assertEquals("true", attribute.getValueReference());
		Assert.assertEquals("542c18f3-a837-41d4-92e9-be53dc825302", attribute.getConcept().getUuid());
	}
	
	@Test
	public void fill_ShouldFetchConceptAttributeTypeByUuid() {
		
		// Setup
		String[] headerLine = { "Fully Specified Name:en", "attribute|9516cc50-6f9f-11e0-8414-001e378eb67e" };
		String[] line = { "TESTING", "FalSe" };
		
		// Replay
		ConceptAttributeLineProcessor calp = new ConceptAttributeLineProcessor(cs);
		Concept c = calp.fill(testConcept, new CsvLine(headerLine, line));
		
		//Verify
		
		ConceptAttribute attribute = (ConceptAttribute) c.getAttributes().toArray()[0];
		
		Assert.assertEquals("Test", attribute.getAttributeType().getName());
		Assert.assertEquals("false", attribute.getValueReference());
		Assert.assertEquals("542c18f3-a837-41d4-92e9-be53dc825302", attribute.getConcept().getUuid());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldFailWhenCATNotFoundAndValueIsSet() {
		// Setup
		String[] headerLine = { "Fully Specified Name:en", "attribute|Unavailable" };
		String[] line = { "Test", "True" };
		
		// Replay
		ConceptAttributeLineProcessor calp = new ConceptAttributeLineProcessor(cs);
		Concept c = calp.fill(testConcept, new CsvLine(headerLine, line));
	}
	
	@Test
	public void fill_ShouldProcessWhenCATNotFoundAndValueIsNull() {
		// Setup
		String[] headerLine = { "Fully Specified Name:en", "attribute|Unavailable" };
		String[] line = { "Test", "" };
		
		// Replay
		ConceptAttributeLineProcessor calp = new ConceptAttributeLineProcessor(cs);
		Concept c = calp.fill(testConcept, new CsvLine(headerLine, line));
		
		//Verify
		Assert.assertTrue(c.getAttributes().isEmpty());
	}
}
