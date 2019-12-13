package org.openmrs.module.initializer.api.appt.servicedefinitions;

import java.sql.Time;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
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
	
	private AppointmentServiceDefinitionService appointmentServiceService;
	
	private SpecialityService specialityService;
	
	private LocationService locationService;
	
	@Autowired
	public AppointmentsServiceDefinitionLineProcessor(
	    @Qualifier("appointmentServiceService") AppointmentServiceDefinitionService appointmentServiceService,
	    @Qualifier("specialityService") SpecialityService specialityService,
	    @Qualifier("locationService") LocationService locationService) {
		super();
		this.appointmentServiceService = appointmentServiceService;
		this.specialityService = specialityService;
		this.locationService = locationService;
	}
	
	@Override
	protected AppointmentServiceDefinition bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		AppointmentServiceDefinition definition = appointmentServiceService
		        .getAppointmentServiceByUuid(uuid);
		
		if (definition == null) {
			String name = line.get(HEADER_NAME, true); // should fail if name column missing
			definition = Utils.fetchBahmniAppointmentServiceDefinition(name, appointmentServiceService);
		}
		
		if (definition == null) {
			definition = new AppointmentServiceDefinition();
			if (!StringUtils.isEmpty(uuid)) {
				definition.setUuid(uuid);
			}
		}
		
		return definition;
	}
	
	public AppointmentServiceDefinition fill(AppointmentServiceDefinition definition, CsvLine line)
	        throws IllegalArgumentException {
		
		String name = line.get(HEADER_NAME, true); // should fail is name column missing
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException(
			        "An AppointmentServiceDefinition must at least be provided a name: '" + line.toString() + "'");
		}
		definition.setName(name);
		definition.setDescription(line.getString(HEADER_DESC));
		
		String serviceDuration = line.getString(HEADER_DURATION);
		if (!StringUtils.isEmpty(serviceDuration)) {
			Integer duration = Utils.getIntegerFromString(serviceDuration);
			if (duration != null) {
				definition.setDurationMins(duration);
			}
		}
		
		String serviceStartTime = line.getString(HEADER_START_TIME);
		if (!StringUtils.isEmpty(serviceStartTime)) {
			Date startTime = Utils.getTimeFromString(serviceStartTime);
			if (startTime != null) {
				definition.setStartTime(new Time(startTime.getTime()));
			}
		}
		
		String serviceEndTime = line.getString(HEADER_END_TIME);
		if (!StringUtils.isEmpty(serviceEndTime)) {
			Date endTime = Utils.getTimeFromString(serviceEndTime);
			definition.setEndTime(new Time(endTime.getTime()));
		}
		
		String serviceMaxLoad = line.getString(HEADER_MAX_LOAD);
		if (!StringUtils.isEmpty(serviceMaxLoad)) {
			Integer maxLoad = Utils.getIntegerFromString(serviceMaxLoad);
			if (maxLoad != null) {
				definition.setMaxAppointmentsLimit(maxLoad);
			}
		}
		
		String serviceSpeciality = line.getString(HEADER_SPECIALITY);
		if (!StringUtils.isEmpty(serviceSpeciality)) {
			Speciality fetchedSpeciality = Utils.fetchBahmniAppointmentSpeciality(serviceSpeciality, specialityService);
			if (fetchedSpeciality != null) {
				definition.setSpeciality(fetchedSpeciality);
			}
		}
		
		String serviceLocation = line.getString(HEADER_LOCATION);
		if (!StringUtils.isEmpty(serviceLocation)) {
			Location fetchedLocation = Utils.fetchLocation(line.getString(HEADER_LOCATION), locationService);
			if (fetchedLocation != null && Utils.isAppointmentLocation(fetchedLocation)) {
				definition.setLocation(fetchedLocation);
			}
		}
		
		definition.setColor(line.getString(HEADER_LABEL_COLOUR));
		
		return definition;
	}
}
