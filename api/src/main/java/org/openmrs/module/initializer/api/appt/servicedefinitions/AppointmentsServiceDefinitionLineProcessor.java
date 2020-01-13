package org.openmrs.module.initializer.api.appt.servicedefinitions;

import org.openmrs.Location;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for Bahmni Appointment Service Definition. It allows to
 * parse and save Appointment Service Definitions with the minimal set of required fields.
 */
@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsServiceDefinitionLineProcessor extends BaseLineProcessor<AppointmentServiceDefinition> {
	
	protected static String HEADER_SPECIALITY = "speciality";
	
	protected static String HEADER_LOCATION = "location";
	
	protected static String HEADER_LABEL_COLOUR = "label colour";
	
	private SpecialityService specialityService;
	
	private LocationService locationService;
	
	@Autowired
	public AppointmentsServiceDefinitionLineProcessor(@Qualifier("specialityService") SpecialityService specialityService,
	    @Qualifier("locationService") LocationService locationService) {
		super();
		this.specialityService = specialityService;
		this.locationService = locationService;
	}
	
	@Override
	public AppointmentServiceDefinition fill(AppointmentServiceDefinition definition, CsvLine line)
	        throws IllegalArgumentException {
		
		definition.setName(line.get(HEADER_NAME, true));
		definition.setDescription(line.getString(HEADER_DESC));
		definition.setDurationMins(line.getInt(HEADER_DURATION));
		definition.setStartTime(line.getSqlTime(HEADER_START_TIME));
		definition.setEndTime(line.getSqlTime(HEADER_END_TIME));
		definition.setMaxAppointmentsLimit(line.getInt(HEADER_MAX_LOAD));
		
		Speciality fetchedSpeciality = Utils.fetchBahmniAppointmentSpeciality(line.getString(HEADER_SPECIALITY),
		    specialityService);
		definition.setSpeciality(fetchedSpeciality);
		
		Location fetchedLocation = Utils.fetchLocation(line.getString(HEADER_LOCATION), locationService);
		definition.setLocation(fetchedLocation);
		
		definition.setColor(line.getString(HEADER_LABEL_COLOUR));
		
		return definition;
	}
}
