package org.openmrs.module.initializer.api.c;

import static org.mockito.Mockito.mock;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class BaseConceptLineProcessorTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	@Test
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		ConceptLineProcessor p = new ConceptLineProcessor(cs);
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertTrue(CollectionUtils.isEmpty(c.getNames()));
		Assertions.assertTrue(CollectionUtils.isEmpty(c.getDescriptions()));
		Assertions.assertNull(c.getConceptClass());
		Assertions.assertNull(c.getDatatype());
	}
}
