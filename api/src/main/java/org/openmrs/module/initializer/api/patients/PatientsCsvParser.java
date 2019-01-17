package org.openmrs.module.initializer.api.patients;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.initializer.api.CsvParser;

public class PatientsCsvParser extends CsvParser<Patient, PatientService, PatientLineProcessor> {
	
	public PatientsCsvParser(InputStream is, PatientService ps) throws IOException {
		super(is, ps);
	}
	
	@Override
	protected Patient save(Patient instance) {
		return service.savePatient(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Patient instance) {
		return instance.getVoided();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new PatientLineProcessor(headerLine, service));
	}
}
