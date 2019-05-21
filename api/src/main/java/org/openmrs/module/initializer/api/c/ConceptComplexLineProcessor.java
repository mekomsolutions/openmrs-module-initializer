package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.conceptComplexLineProcessor")
public class ConceptComplexLineProcessor extends ConceptLineProcessor {
	
	protected static String DATATYPE_COMPLEX = "Complex";
	
	protected static String HEADER_HANDLER = "complex data handler";
	
	@Autowired
	public ConceptComplexLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super(conceptService);
	}
	
	@Override
	protected Concept fill(Concept instance, CsvLine line) throws IllegalArgumentException {
		
		if (!DATATYPE_COMPLEX.equals(line.get(ConceptLineProcessor.HEADER_DATATYPE))) {
			return instance;
		}
		
		ConceptComplex cc = new ConceptComplex(instance);
		if (instance.getId() != null) { // below overrides any other processors work, so this one should be called first
			cc = conceptService.getConceptComplex(instance.getId());
		}
		cc.setDatatype(conceptService.getConceptDatatypeByName(DATATYPE_COMPLEX));
		
		cc.setHandler(line.getString(HEADER_HANDLER));
		
		return cc;
	}
}
