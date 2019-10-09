package org.openmrs.module.initializer.api.appt.speciality;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.appt.specialities.SpecialityLineProcessor;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class SpecialityLineProcessorTest {
	
	private SpecialityService ss = mock(SpecialityService.class);
	
	@Before
	public void setup() {
	}
	
	@Test
	public void fill_shouldParsePersonAttributeType() {
		
		// Setup
		String[] headerLine = { "Name" };
		String[] line = { "Speciality name" };
		
		// Replay
		SpecialityLineProcessor s = new SpecialityLineProcessor(ss);
		s.setHeaderLine(headerLine);
		Speciality speciality = s.fill(new Speciality(), new CsvLine(s, line));
		
		// Verif
		Assert.assertEquals("Speciality name", speciality.getName());
	}
}
