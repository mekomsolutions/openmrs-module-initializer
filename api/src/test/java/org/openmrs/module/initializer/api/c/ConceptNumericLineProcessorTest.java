package org.openmrs.module.initializer.api.c;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
	
	@BeforeEach
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
		ConceptNumeric cn = (ConceptNumeric) p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertEquals(ConceptNumericLineProcessor.DATATYPE_NUMERIC, cn.getDatatype().getName());
		Assertions.assertEquals(0, cn.getLowAbsolute().compareTo(-100.5));
		Assertions.assertEquals(0, cn.getLowCritical().compareTo(-85.7));
		Assertions.assertEquals(0, cn.getLowNormal().compareTo(-50.3));
		Assertions.assertEquals(0, cn.getHiNormal().compareTo(45.1));
		Assertions.assertEquals(0, cn.getHiCritical().compareTo(78.0));
		Assertions.assertEquals(0, cn.getHiAbsolute().compareTo(98.8));
		Assertions.assertEquals("foo", cn.getUnits());
		Assertions.assertTrue(cn.getAllowDecimal());
		Assertions.assertEquals(1, cn.getDisplayPrecision().intValue());
	}
	
	@Test
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		ConceptNumericLineProcessor p = new ConceptNumericLineProcessor(cs);
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertFalse(c instanceof ConceptNumeric);
	}
	
	@Test
	public void fill_shouldFailWhenCannotParse() {
		assertThrows(NumberFormatException.class, () -> {
			
			// Setup
			String[] headerLine = { "Data type", "Absolute low" };
			String[] line = { "Numeric", "-100.5a" };
			
			// Replay
			ConceptNumericLineProcessor p = new ConceptNumericLineProcessor(cs);
			p.fill(new Concept(), new CsvLine(headerLine, line));
			
		});
	}
	
	@Test
	public void fill_shouldOverrideProvidedDataType() {
		
		// Setup
		when(cs.getConceptNumeric(any(Integer.class))).thenReturn(null);
		String[] headerLine = { "Data type", "Absolute low" };
		String[] line = { "Numeric", "11.11" };
		
		// Replay
		ConceptNumericLineProcessor p = new ConceptNumericLineProcessor(cs);
		ConceptNumeric cn = (ConceptNumeric) p.fill(new Concept(1), new CsvLine(headerLine, line));
		
		// Verify
		verify(cs, atLeast(1)).getConceptNumeric(any(Integer.class));
		Assertions.assertEquals(ConceptNumericLineProcessor.DATATYPE_NUMERIC, cn.getDatatype().getName());
		Assertions.assertEquals(0, cn.getLowAbsolute().compareTo(11.11));
	}
}
