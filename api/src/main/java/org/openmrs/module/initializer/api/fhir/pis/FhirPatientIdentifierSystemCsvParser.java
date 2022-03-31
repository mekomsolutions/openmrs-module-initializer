package org.openmrs.module.initializer.api.fhir.pis;

import org.hibernate.SessionFactory;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.Domain.FHIR_PATIENT_IDENTIFIER_SYSTEMS;

@Component
@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirPatientIdentifierSystemCsvParser extends CsvParser<FhirPatientIdentifierSystem, BaseLineProcessor<FhirPatientIdentifierSystem>> {
	
	private final SessionFactory sessionFactory;
	
	@Autowired
	protected FhirPatientIdentifierSystemCsvParser(@Qualifier("sessionFactory") SessionFactory sessionFactory, BaseLineProcessor<FhirPatientIdentifierSystem> lineProcessor) {
		super(lineProcessor);
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Domain getDomain() {
		return FHIR_PATIENT_IDENTIFIER_SYSTEMS;
	}
	
	@Override
	public FhirPatientIdentifierSystem bootstrap(CsvLine line) throws IllegalArgumentException {
		if (line.getUuid() == null || line.getUuid().isEmpty()) {
			return new FhirPatientIdentifierSystem();
		}
		
		FhirPatientIdentifierSystem result = (FhirPatientIdentifierSystem) sessionFactory.getCurrentSession().createQuery("from " + FhirPatientIdentifierSystem.class.getSimpleName() + " where uuid = :uuid")
				.setParameter("uuid", line.getUuid())
				.uniqueResult();
		
		if (result == null) {
			result = new FhirPatientIdentifierSystem();
			result.setUuid(line.getUuid());
		}
		
		return result;
	}
	
	@Override
	public FhirPatientIdentifierSystem save(FhirPatientIdentifierSystem instance) {
		sessionFactory.getCurrentSession().saveOrUpdate(instance);
		return instance;
	}
}
