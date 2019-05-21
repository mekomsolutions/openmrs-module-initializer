package org.openmrs.module.initializer.api.c;

import static org.mockito.Mockito.mock;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
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
		p.setHeaderLine(headerLine);
		Concept c = p.fill(new Concept(), new CsvLine(p, line));
		
		// Verif
		Assert.assertTrue(CollectionUtils.isEmpty(c.getNames()));
		Assert.assertTrue(CollectionUtils.isEmpty(c.getDescriptions()));
		Assert.assertNull(c.getConceptClass());
		Assert.assertNull(c.getDatatype());
	}
}
