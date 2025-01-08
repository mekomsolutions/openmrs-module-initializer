package org.openmrs.module.initializer.api.lineprocessor;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_7_Test;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.conceptreferencerange.ConceptReferenceRangeLineProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class ConceptReferenceRangeLineProcessorTest extends DomainBaseModuleContextSensitive_2_7_Test {
	
	@Autowired
	private ConceptReferenceRangeLineProcessor conceptReferenceRangeLineProcessor;
	
	public void setup() {
		executeDataSet("testdata/test-concepts.xml");
		executeDataSet("testdata/test-concepts-numeric.xml");
	}
	
	@Test
	public void fill_shouldParseConceptReferenceRange() {
		String[] headerLine = { "Uuid", "Concept Numeric uuid", "Absolute low", "Critical low", "Normal low", "Normal high",
		        "Critical high", "Absolute high", "Criteria" };
		String[] line = { "bc059100-4ace-4af5-afbf-2da7f3a34acf", "a09ab2c5-878e-4905-b25d-5784167d0216", "-100.5", "-85.7",
		        "-50.3", "45.1", "78", "98.8", "$patient.getAge() > 3" };
		
		ConceptReferenceRange conceptReferenceRange = conceptReferenceRangeLineProcessor.fill(new ConceptReferenceRange(),
		    new CsvLine(headerLine, line));
		
		Assert.assertNotNull(conceptReferenceRange);
		Assert.assertNotNull(conceptReferenceRange.getConceptNumeric());
		Assert.assertEquals("a09ab2c5-878e-4905-b25d-5784167d0216", conceptReferenceRange.getConceptNumeric().getUuid());
		Assert.assertEquals(-100.5, conceptReferenceRange.getLowAbsolute(), 0.01);
		Assert.assertEquals(-85.7, conceptReferenceRange.getLowCritical(), 0.01);
		Assert.assertEquals(-50.3, conceptReferenceRange.getLowNormal(), 0.01);
		Assert.assertEquals(45.1, conceptReferenceRange.getHiNormal(), 0.01);
		Assert.assertEquals(78.0, conceptReferenceRange.getHiCritical(), 0.01);
		Assert.assertEquals(98.8, conceptReferenceRange.getHiAbsolute(), 0.01);
		Assert.assertEquals("$patient.getAge() > 3", conceptReferenceRange.getCriteria());
	}
	
	@Test
	public void fill_shouldThrowExceptionForInvalidConceptNumericUuid() {
		String[] headerLine = { "Uuid", "Concept Numeric uuid", "Absolute low", "Critical low", "Normal low", "Normal high",
		        "Critical high", "Absolute high", "Criteria" };
		String[] line = { "bc059100-4ace-4af5-afbf-2da7f3a34acf", "invalid-uuid", "-100.5", "-85.7", "-50.3", "45.1", "78",
		        "98.8", "$patient.getAge() > 3" };
		
		Assert.assertThrows(IllegalArgumentException.class,
		    () -> conceptReferenceRangeLineProcessor.fill(new ConceptReferenceRange(), new CsvLine(headerLine, line)));
	}
	
	@Test
	public void fill_shouldThrowExceptionIfConceptNumericUuidNotFound() {
		String[] headerLine = { "Uuid", "Concept Numeric uuid", "Absolute low", "Critical low", "Normal low", "Normal high",
		        "Critical high", "Absolute high", "Criteria" };
		String[] line = { "bc059100-4ace-4af5-afbf-2da7f3a34acf", "b09ab2c5-878e-4905-b25d-5784d67d0216", "-100.5", "-85.7",
		        "-50.3", "45.1", "78", "98.8", "$patient.getAge() > 3" };
		
		Assert.assertThrows(IllegalArgumentException.class,
		    () -> conceptReferenceRangeLineProcessor.fill(new ConceptReferenceRange(), new CsvLine(headerLine, line)));
		
	}
}
