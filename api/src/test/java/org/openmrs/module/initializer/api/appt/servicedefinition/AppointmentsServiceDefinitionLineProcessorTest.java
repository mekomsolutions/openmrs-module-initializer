package org.openmrs.module.initializer.api.appt.servicedefinition;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentsServiceDefinitionLineProcessor;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class AppointmentsServiceDefinitionLineProcessorTest {
	
	private AppointmentServiceDefinitionService asds = mock(AppointmentServiceDefinitionService.class);
	
	private SpecialityService ss = mock(SpecialityService.class);
	
	private LocationService ls = mock(LocationService.class);
	
	@Test
	public void fill_shouldParseAppointmentsServiceDefinition() {
		
		// Setup
		String[] headerLine = { "Name", "Description", "Duration", "Max Load" };
		String[] line = { "X-Ray", "Radiology Service", "30", "50" };
		
		// Replay
		AppointmentsServiceDefinitionLineProcessor p = new AppointmentsServiceDefinitionLineProcessor(asds, ss, ls);
		AppointmentServiceDefinition appointmentServiceDefinition = p.fill(new AppointmentServiceDefinition(),
		    new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertEquals("X-Ray", appointmentServiceDefinition.getName());
		Assert.assertEquals("Radiology Service", appointmentServiceDefinition.getDescription());
		Assert.assertEquals(Integer.valueOf(30), appointmentServiceDefinition.getDurationMins());
		Assert.assertEquals(Integer.valueOf(50), appointmentServiceDefinition.getMaxAppointmentsLimit());
	}
}
