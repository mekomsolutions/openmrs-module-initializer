package org.openmrs.module.initializer.api.appt.appointmentservicedefinitions;

import java.sql.Time;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.model.ServiceWeeklyAvailability;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.AppointmentServiceTypeListParser;
import org.openmrs.module.initializer.api.utils.AppointmentServiceWeeklyAvailabilityListParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for Bahmni Appointment Service Definition. It allows to parse and save Appointment Service Definitions
 * with the minimal set of required fields.
 */
@OpenmrsProfile(modules = { "appointments:*" })
public class AppointmentsServiceDefinitionLineProcessor extends BaseLineProcessor<AppointmentServiceDefinition> {
		
	protected static String HEADER_SPECIALITY = "speciality";	
	
	protected static String HEADER_LOCATION = "location";	
	
	protected static String HEADER_LABEL_COLOUR = "label colour";
	
	protected static String HEADER_SERVICE_APPOINTMENT_TYPES = "service appointment types";
	
	protected static String HEADER_SERVICE_AVAILABILITIES = "service availabilities";
	
	private AppointmentServiceDefinitionService appointmentServiceDefinitionService;
	
	private SpecialityService bahmniAppointmentSpecialityService;
	
	private LocationService locationService;
	
	private AppointmentServiceTypeListParser appointmentServiceTypeListParser;
	
	private AppointmentServiceWeeklyAvailabilityListParser appointmentServiceWeeklyAvailabilityListParser;
	
	@Autowired
	public AppointmentsServiceDefinitionLineProcessor(@Qualifier("appointmentServiceDefinitionService") AppointmentServiceDefinitionService appointmentServiceDefinitionService,
			@Qualifier("bahmniAppointmentSpecialityService") SpecialityService bahmniAppointmentSpecialityService,
			@Qualifier("locationService") LocationService locationService,
			@Qualifier("appointmentServiceTypeListParser") AppointmentServiceTypeListParser appointmentServiceTypeListParser, 
			@Qualifier("appointmentServiceWeeklyAvailabilityListParser") AppointmentServiceWeeklyAvailabilityListParser appointmentServiceWeeklyAvailabilityListParser) {
		super();
		this.appointmentServiceDefinitionService = appointmentServiceDefinitionService;
		this.bahmniAppointmentSpecialityService = bahmniAppointmentSpecialityService;
		this.locationService = locationService;
		this.appointmentServiceTypeListParser  = appointmentServiceTypeListParser;		
		this.appointmentServiceWeeklyAvailabilityListParser = appointmentServiceWeeklyAvailabilityListParser;
	}
	
	@Override
	protected AppointmentServiceDefinition bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		AppointmentServiceDefinition appointmentServiceDefinition = appointmentServiceDefinitionService.getAppointmentServiceByUuid(uuid);
		
		if (appointmentServiceDefinition == null) {
			String appointmentServiceDefinitionName = line.get(HEADER_NAME, true); // should fail if name column missing
			for (AppointmentServiceDefinition currentAppointmentServiceDefinition : appointmentServiceDefinitionService.getAllAppointmentServices(false)) { //We don't have #getAppointmentServiceByName, so we use a loop
				if(currentAppointmentServiceDefinition.getName().equalsIgnoreCase(appointmentServiceDefinitionName)) {
					appointmentServiceDefinition = currentAppointmentServiceDefinition;
				}
			}
		}
		
		if (appointmentServiceDefinition == null) {
			appointmentServiceDefinition = new AppointmentServiceDefinition();
			if (!StringUtils.isEmpty(uuid)) {
				appointmentServiceDefinition.setUuid(uuid);
			}
		}
		
		return appointmentServiceDefinition;
	}
	
	public AppointmentServiceDefinition fill(AppointmentServiceDefinition appointmentServiceDefinition, CsvLine line) throws IllegalArgumentException {
		
		String appointmentServiceDefinitionName = line.get(HEADER_NAME, true); // should fail is name column missing
		if (StringUtils.isEmpty(appointmentServiceDefinitionName)) {
			throw new IllegalArgumentException("An AppointmentServiceDefinition must at least be provided a name: '" + line.toString() + "'");
		}
		appointmentServiceDefinition.setName(appointmentServiceDefinitionName);
		appointmentServiceDefinition.setDescription(line.getString(HEADER_DESC, ""));
		
		String serviceDuration = line.getString(HEADER_DURATION, "");
		if (!StringUtils.isEmpty(serviceDuration) && Utils.isParsableToInt(serviceDuration)) {
			appointmentServiceDefinition.setDurationMins(Integer.parseInt(serviceDuration));
		}
		appointmentServiceDefinition.setStartTime(Time.valueOf(line.getString(HEADER_START_TIME, "")));
		appointmentServiceDefinition.setEndTime(Time.valueOf(line.getString(HEADER_END_TIME, "")));
		appointmentServiceDefinition.setMaxAppointmentsLimit(Integer.parseInt(line.getString(HEADER_MAX_LOAD, "")));
		appointmentServiceDefinition.setSpeciality(Utils.fetchBahmniAppointmentSpeciality(line.getString(HEADER_SPECIALITY, ""), bahmniAppointmentSpecialityService));
		
		Location location = Utils.fetchLocation(line.getString(HEADER_LOCATION, ""), locationService);
				
		if(location != null && Utils.isAppointmentLocation(location)) {
			appointmentServiceDefinition.setLocation(location);
		}
		
		appointmentServiceDefinition.setColor(line.getString(HEADER_LABEL_COLOUR, ""));
		
		String appointmentServiceTypeStr = line.getString(HEADER_SERVICE_APPOINTMENT_TYPES, "");
		if (!StringUtils.isEmpty(appointmentServiceTypeStr)) {
			appointmentServiceDefinition.setServiceTypes(
			    new HashSet<AppointmentServiceType>(appointmentServiceTypeListParser.parseList(line.get(HEADER_SERVICE_APPOINTMENT_TYPES))));
		}
		
		String appointmentServiceWeeklyAvailabilityStr = line.getString(HEADER_SERVICE_AVAILABILITIES, "");
		if (!StringUtils.isEmpty(appointmentServiceWeeklyAvailabilityStr)) {
			appointmentServiceDefinition.setWeeklyAvailability(
			    new HashSet<ServiceWeeklyAvailability>(appointmentServiceWeeklyAvailabilityListParser.parseList(line.get(HEADER_SERVICE_AVAILABILITIES))));
		}
		
		return appointmentServiceDefinition;
	}
}
