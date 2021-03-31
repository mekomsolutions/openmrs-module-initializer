package org.openmrs.module.initializer.api.idgen.autogen;

import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "idgen:4.6.0" })
public class AutoGenerationOptionLineProcessor extends BaseLineProcessor<AutoGenerationOption> {
	
	final public static String IDENTIFIER_TYPE = "identifier type";
	
	final public static String LOCATION = "location";
	
	final public static String IDENTIFIER_SOURCE = "identifier source";
	
	final public static String MANUAL_ENTRY_ENABLED = "manual entry enabled";
	
	final public static String AUTO_GEN_ENABLED = "auto generation enabled";
	
	private LocationService ls;
	
	private IdentifierSourceService iss;
	
	private PatientService ps;
	
	@Autowired
	public AutoGenerationOptionLineProcessor(@Qualifier("locationService") LocationService ls, IdentifierSourceService iss,
	    @Qualifier("patientService") PatientService ps) {
		this.ls = ls;
		this.iss = iss;
		this.ps = ps;
	}
	
	@Override
	public AutoGenerationOption fill(AutoGenerationOption option, CsvLine line) throws IllegalArgumentException {
		
		option.setAutomaticGenerationEnabled(line.getBool(AUTO_GEN_ENABLED));
		option.setManualEntryEnabled(line.getBool(MANUAL_ENTRY_ENABLED));
		
		// Fill identifier type
		{
			String identifierTypeRef = line.get(IDENTIFIER_TYPE, true);
			option.setIdentifierType(Utils.fetchPatientIdentifierType(identifierTypeRef, ps));
		}
		// Fill identifier source
		{
			String identifierSourceRef = line.get(IDENTIFIER_SOURCE, true);
			IdentifierSource source = iss.getIdentifierSourceByUuid(identifierSourceRef);
			option.setSource(source);
		}
		// Fill location
		{
			String locationRef = line.get(LOCATION);
			option.setLocation(Utils.fetchLocation(locationRef, ls));
		}
		
		return option;
	}
}
