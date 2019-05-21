package org.openmrs.module.initializer.api.c;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class ConceptNumericLineProcessorTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	@Before
	public void setup() {
		
		when(cs.getConceptDatatypeByName(any(String.class))).thenAnswer(new Answer<ConceptDatatype>() {
			
			@Override
			public ConceptDatatype answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String name = (String) args[0];
				ConceptDatatype datatype = new ConceptDatatype();
				datatype.setName(name);
				return datatype;
			}
		});
	}
	
	@Test
	public void fill_shouldParseConceptNumeric() {
		
		// Setup
		String[] headerLine = { "Data type", "Absolute low", "Critical low", "Normal low", "Normal high", "Critical high",
		        "Absolute high", "Units", "Allow decimals", "Display precision" };
		String[] line = { "Numeric", "-100.5", "-85.7", "-50.3", "45.1", "78", "98.8", "foo", "yes", "1" };
		
		// Replay
		ConceptNumericLineProcessor p = new ConceptNumericLineProcessor(cs);
		p.setHeaderLine(headerLine);
		ConceptNumeric cn = (ConceptNumeric) p.fill(new Concept(), new CsvLine(p, line));
		
		// Verif
		Assert.assertEquals(ConceptNumericLineProcessor.DATATYPE_NUMERIC, cn.getDatatype().getName());
		Assert.assertEquals(0, cn.getLowAbsolute().compareTo(-100.5));
		Assert.assertEquals(0, cn.getLowCritical().compareTo(-85.7));
		Assert.assertEquals(0, cn.getLowNormal().compareTo(-50.3));
		Assert.assertEquals(0, cn.getHiNormal().compareTo(45.1));
		Assert.assertEquals(0, cn.getHiCritical().compareTo(78.0));
		Assert.assertEquals(0, cn.getHiAbsolute().compareTo(98.8));
		Assert.assertEquals("foo", cn.getUnits());
		Assert.assertTrue(cn.getAllowDecimal());
		Assert.assertEquals(1, cn.getDisplayPrecision().intValue());
	}
	
	@Test
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		ConceptNumericLineProcessor p = new ConceptNumericLineProcessor(cs);
		p.setHeaderLine(headerLine);
		Concept c = p.fill(new Concept(), new CsvLine(p, line));
		
		// Verif
		Assert.assertFalse(c instanceof ConceptNumeric);
	}
	
	@Test(expected = NumberFormatException.class)
	public void fill_shouldFailWhenCannotParse() {
		
		// Setup
		String[] headerLine = { "Data type", "Absolute low" };
		String[] line = { "Numeric", "-100.5a" };
		
		// Replay
		ConceptNumericLineProcessor p = new ConceptNumericLineProcessor(cs);
		p.setHeaderLine(headerLine);
		p.fill(new Concept(), new CsvLine(p, line));
	}
}
