package org.openmrs.module.initializer.api.ot;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.OrderType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.api.OrderService;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.ot.OrderTypeLineProcessor.Helper;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class OrderTypeLineProcessorTest {
	
	private OrderService os = mock(OrderService.class);
	
	private Helper helper = mock(Helper.class);
	
	@Before
	public void setup() {
		
		when(helper.getPrivilege(any(String.class))).thenAnswer(new Answer<Privilege>() {
			
			@Override
			public Privilege answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String privilegeName = (String) args[0];
				Privilege privilege = new Privilege(privilegeName, "Privilege desc.");
				return privilege;
			}
		});
	}
	
	@Test
	public void fill_shouldParseOrderType() {
		
		// Setup
		String[] headerLine = { "Name", "Description", "Java class name", "Parent uuid" };
		String[] line = { "OT name", "OT desc.", "org.openmrs.Order", "01727040-a587-484d-b66a-f0afbae6c281" };
		
		// Replay

		OrderTypeLineProcessor o = new OrderTypeLineProcessor(os);
		o.setHelper(helper);
		o.setHeaderLine(headerLine);
		
		OrderType parentOrderType = new OrderType("Parent OT name", "Parent OT desc.", "org.openmrs.Order");
		parentOrderType.setUuid("01727040-a587-484d-b66a-f0afbae6c281");
		
		OrderType ot = o.fill(new OrderType(), new CsvLine(o, line));
		
		// Verif
		Assert.assertEquals("OT name", ot.getName());
		Assert.assertEquals("OT desc.", ot.getDescription());
		Assert.assertEquals("org.openmrs.Order", ot.getJavaClassName());
	}
	
	@Test
	public void fill_shouldParseWithNameAndJavaClassNameOnly() {
		
		// Setup
		String[] headerLine = { "Name", "Java class name",};
		String[] line = { "OT name", "org.openmrs.Order" };
		
		// Replay

		OrderTypeLineProcessor o = new OrderTypeLineProcessor(os);
		o.setHelper(helper);
		o.setHeaderLine(headerLine);
		
		OrderType ot = o.fill(new OrderType(), new CsvLine(o, line));
		
		// Verif
		Assert.assertEquals("OT name", ot.getName());
		Assert.assertEquals("org.openmrs.Order", ot.getJavaClassName());
	}
	
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		OrderTypeLineProcessor o = new OrderTypeLineProcessor(os);
		o.setHelper(helper);
		o.setHeaderLine(headerLine);
		OrderType ot = o.fill(new OrderType(), new CsvLine(o, line));
		
		// Verif
		Assert.assertNull(ot.getName());
		Assert.assertNull(ot.getJavaClassName());
	}
}
