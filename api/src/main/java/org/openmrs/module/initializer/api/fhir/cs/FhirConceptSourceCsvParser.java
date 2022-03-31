package org.openmrs.module.initializer.api.fhir.cs;

import org.hibernate.SessionFactory;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.api.dao.FhirConceptSourceDao;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.Domain.FHIR_CONCEPT_SOURCES;

@Component
@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirConceptSourceCsvParser extends CsvParser<FhirConceptSource, BaseLineProcessor<FhirConceptSource>> {
	
	public static final String CONCEPT_SOURCE_NAME_HEADER = "Concept source name";
	
	private final FhirConceptSourceDao dao;
	
	private final SessionFactory sessionFactory;
	
	@Autowired
	protected FhirConceptSourceCsvParser(FhirConceptSourceDao dao,
	    @Qualifier("sessionFactory") SessionFactory sessionFactory, BaseLineProcessor<FhirConceptSource> lineProcessor) {
		super(lineProcessor);
		
		this.dao = dao;
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Domain getDomain() {
		return FHIR_CONCEPT_SOURCES;
	}
	
	@Override
	public FhirConceptSource bootstrap(CsvLine line) throws IllegalArgumentException {
		String name = line.getString(CONCEPT_SOURCE_NAME_HEADER);
		
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException(
					"'concept source name' was not found for FHIR concept source " + line.getUuid());
		}
		
		return dao.getFhirConceptSourceByConceptSourceName(name).orElseGet(() -> {
			FhirConceptSource newConceptSource = new FhirConceptSource();
			newConceptSource.setName(name);
			return newConceptSource;
		});
	}
	
	@Override
	public FhirConceptSource save(FhirConceptSource instance) {
		sessionFactory.getCurrentSession().saveOrUpdate(instance);
		return instance;
	}
}
