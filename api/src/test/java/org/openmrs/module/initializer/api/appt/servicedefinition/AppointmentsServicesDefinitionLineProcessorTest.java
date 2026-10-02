package org.openmrs.module.initializer.api.appt.servicedefinition;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentServiceDefinitionLineProcessor;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class AppointmentsServicesDefinitionLineProcessorTest {
	
	private SpecialityService ss = mock(SpecialityService.class);
	
	private LocationService ls = mock(LocationService.class);
	
	@Test
	public void fill_shouldParseAppointmentsServiceDefinition() {
		
		// Setup
		String[] headerLine = { "Name", "Description", "Duration", "Max Load" };
		String[] line = { "X-Ray", "Radiology Service", "30", "50" };
		
		// Replay
		AppointmentServiceDefinitionLineProcessor p = new AppointmentServiceDefinitionLineProcessor(ss, ls);
		AppointmentServiceDefinition definition = p.fill(new AppointmentServiceDefinition(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertEquals("X-Ray", definition.getName());
		Assertions.assertEquals("Radiology Service", definition.getDescription());
		Assertions.assertEquals(Integer.valueOf(30), definition.getDurationMins());
		Assertions.assertEquals(Integer.valueOf(50), definition.getMaxAppointmentsLimit());
	}
}
