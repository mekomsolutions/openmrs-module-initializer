package org.openmrs.module.initializer.api.lineprocessor;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.procedure.ProcedureTypeLineProcessor;

public class ProcedureTypeLineProcessorTest {
	
	private final ProcedureTypeLineProcessor processor = new ProcedureTypeLineProcessor();
	
	@Test
	public void fill_shouldPopulateNameAndDescription() {
		String[] headers = { "Uuid", "Name", "Description" };
		String[] line = { "9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b1234", "Appendectomy", "Surgical removal of the appendix" };
		
		ProcedureType type = processor.fill(new ProcedureType(), new CsvLine(headers, line));
		
		Assert.assertEquals("Appendectomy", type.getName());
		Assert.assertEquals("Surgical removal of the appendix", type.getDescription());
	}
	
	@Test
	public void fill_shouldAllowMissingDescription() {
		String[] headers = { "Uuid", "Name", "Description" };
		String[] line = { "9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b1234", "Cholecystectomy", null };
		
		ProcedureType type = processor.fill(new ProcedureType(), new CsvLine(headers, line));
		
		Assert.assertEquals("Cholecystectomy", type.getName());
		Assert.assertNull(type.getDescription());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldThrowWhenNameIsBlank() {
		String[] headers = { "Uuid", "Name", "Description" };
		String[] line = { "9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b1234", null, "Description without a name" };
		
		processor.fill(new ProcedureType(), new CsvLine(headers, line));
	}
}
