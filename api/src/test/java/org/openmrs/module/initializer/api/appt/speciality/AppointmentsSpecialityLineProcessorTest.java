package org.openmrs.module.initializer.api.appt.speciality;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.appt.specialities.AppointmentsSpecialityLineProcessor;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class AppointmentsSpecialityLineProcessorTest {
	
	private SpecialityService ss = mock(SpecialityService.class);
	
	@Test
	public void fill_shouldParseSpeciality() {
		
		// Setup
		String[] headerLine = { "Name" };
		String[] line = { "Speciality name" };
		
		// Replay
		AppointmentsSpecialityLineProcessor p = new AppointmentsSpecialityLineProcessor(ss);
		Speciality speciality = p.fill(new Speciality(), new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertEquals("Speciality name", speciality.getName());
	}
}
