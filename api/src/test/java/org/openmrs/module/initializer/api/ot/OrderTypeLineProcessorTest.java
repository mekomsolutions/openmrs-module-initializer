package org.openmrs.module.initializer.api.ot;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.OrderType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptClassListParser;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class OrderTypeLineProcessorTest {
	
	private OrderService os = mock(OrderService.class);
	
	private ConceptService cs = mock(ConceptService.class);
	
	@Test
	public void fill_shouldParseOrderType() {
		
		// Setup
		String[] headerLine = { "Name", "Description", "Java class name", "Parent" };
		String[] line = { "OT name", "OT desc.", "org.openmrs.Order", "01727040-a587-484d-b66a-f0afbae6c281" };
		
		// Replay
		OrderTypeLineProcessor p = new OrderTypeLineProcessor(os, new ConceptClassListParser(cs));
		OrderType ot = p.fill(new OrderType(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertEquals("OT name", ot.getName());
		Assertions.assertEquals("OT desc.", ot.getDescription());
		Assertions.assertEquals("org.openmrs.Order", ot.getJavaClassName());
		// TODO Add test metadata for order type and verify if ot.getParent().getUuid() returns '01727040-a587-484d-b66a-f0afbae6c281' here
	}
	
	@Test
	public void fill_shouldParseWithNameAndJavaClassNameOnly() {
		
		// Setup
		String[] headerLine = { "Name", "Java class name", };
		String[] line = { "OT name", "org.openmrs.Order" };
		
		// Replay
		OrderTypeLineProcessor p = new OrderTypeLineProcessor(os, new ConceptClassListParser(cs));
		OrderType ot = p.fill(new OrderType(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertEquals("OT name", ot.getName());
		Assertions.assertEquals("org.openmrs.Order", ot.getJavaClassName());
	}
	
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		OrderTypeLineProcessor p = new OrderTypeLineProcessor(os, new ConceptClassListParser(cs));
		OrderType ot = p.fill(new OrderType(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertNull(ot.getName());
		Assertions.assertNull(ot.getJavaClassName());
	}
}
