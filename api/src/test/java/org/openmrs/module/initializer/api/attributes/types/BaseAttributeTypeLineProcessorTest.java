package org.openmrs.module.initializer.api.attributes.types;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseAttributeTypeLineProcessorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	protected BaseAttributeTypeLineProcessor processor;
	
	@Test
	public void fill_shouldParseABaseAttributeType() {
		// Setup
		String[] headerLine = { "Name", "Description", "Min occurs", "Max occurs", "Datatype classname", "Domain" };
		String[] line = { "Facility phone", "Some description..", "0", "5",
		        "org.openmrs.customdatatype.datatype.FreeTextDatatype", "Location" };
		
		// Replay
		LocationAttributeType locAttType = (LocationAttributeType) processor.fill(new LocationAttributeType(),
		    new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertEquals("Facility phone", locAttType.getName());
		Assert.assertEquals(0, locAttType.getMinOccurs().intValue());
		Assert.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", locAttType.getDatatypeClassname());
	}
	
	@Test
	public void bootstrap_shouldBootstrapLocationAttributeType() {
		// Setup
		String[] headerLine = { "uuid", "Name", "Description", "Domain" };
		String[] line = { "0bb29984-3193-11e7-93ae-92367f002671", "Facility phone", "Some description..", "Location" };
		
		// Replay
		BaseAttributeType attType = processor.bootstrap(new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertTrue(attType instanceof LocationAttributeType);
		
	}
	
	@Test
	public void bootstrap_shouldBootstrapVisitAttributeType() {
		// Setup
		String[] headerLine = { "uuid", "Name", "Description", "Domain" };
		String[] line = { "0bb29984-3193-11e7-93ae-92367f002671", "Visit Reason", "Some description..", "Visit" };
		
		// Replay
		BaseAttributeType attType = processor.bootstrap(new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertTrue(attType instanceof VisitAttributeType);
	}
	
	@Test
	public void bootstrap_shouldBootstrapProviderAttributeType() {
		// Setup
		String[] headerLine = { "uuid", "Name", "Description", "Domain" };
		String[] line = { "0bb29984-3193-11e7-93ae-92367f002671", "Provider perception", "Some description..", "Provider" };
		
		// Replay
		BaseAttributeType attType = processor.bootstrap(new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertTrue(attType instanceof ProviderAttributeType);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void bootstrap_shouldFailWhenDomainIsNotSupported() {
		// Setup
		String[] headerLine = { "uuid", "Name", "Description", "Domain" };
		String[] line = { "0bb29984-3193-11e7-93ae-92367f002671", "Facility phone", "Some description..", "Patient" };
		
		// Replay
		processor.bootstrap(new CsvLine(headerLine, line));
		
	}
}
