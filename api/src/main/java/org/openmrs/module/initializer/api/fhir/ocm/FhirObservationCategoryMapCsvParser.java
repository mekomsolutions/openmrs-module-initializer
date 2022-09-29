package org.openmrs.module.initializer.api.fhir.ocm;

import org.hibernate.SessionFactory;
import org.openmrs.ConceptClass;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirObservationCategoryMap;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.Domain.FHIR_OBSERVATION_CATEGORY_MAPS;

@Component
@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirObservationCategoryMapCsvParser extends CsvParser<FhirObservationCategoryMap, BaseLineProcessor<FhirObservationCategoryMap>> {
	
	public static final String FHIR_OBS_CATEGORY_HEADER = "Fhir observation category";
	
	public static final String CONCEPT_CLASS_HEADER = "Concept class";
	
	private final SessionFactory sessionFactory;
	
	@Autowired
	protected FhirObservationCategoryMapCsvParser(@Qualifier("sessionFactory") SessionFactory sessionFactory,
	    BaseLineProcessor<FhirObservationCategoryMap> lineProcessor) {
		super(lineProcessor);
		
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Domain getDomain() {
		return FHIR_OBSERVATION_CATEGORY_MAPS;
	}
	
	@Override
	public FhirObservationCategoryMap bootstrap(CsvLine line) throws IllegalArgumentException {
		FhirObservationCategoryMap result = null;
		
		String fhirObsCategory = line.get(FHIR_OBS_CATEGORY_HEADER);
		String conceptClass = line.get(CONCEPT_CLASS_HEADER);
		
		if (fhirObsCategory != null && !fhirObsCategory.isEmpty() && conceptClass != null && !conceptClass.isEmpty()) {
			result = (FhirObservationCategoryMap) sessionFactory.getCurrentSession()
			        .createQuery("from " + FhirObservationCategoryMap.class.getSimpleName()
			                + " where observationCategory = :fhirObsCategory and conceptClass = (" + "select cc from "
			                + ConceptClass.class.getSimpleName() + " cc where cc.name = :conceptClassName" + ")")
			        .setParameter("fhirObsCategory", fhirObsCategory).setParameter("conceptClassName", conceptClass)
			        .uniqueResult();
		}
		
		if (result == null && line.getUuid() != null && !line.getUuid().isEmpty()) {
			result = (FhirObservationCategoryMap) sessionFactory.getCurrentSession()
			        .createQuery("from " + FhirObservationCategoryMap.class.getSimpleName() + " where uuid = :uuid")
			        .setParameter("uuid", line.getUuid()).uniqueResult();
		}
		
		if (result == null) {
			result = new FhirObservationCategoryMap();
			result.setUuid(line.getUuid());
		}
		
		return result;
	}
	
	@Override
	public FhirObservationCategoryMap save(FhirObservationCategoryMap instance) {
		sessionFactory.getCurrentSession().saveOrUpdate(instance);
		return instance;
	}
}
