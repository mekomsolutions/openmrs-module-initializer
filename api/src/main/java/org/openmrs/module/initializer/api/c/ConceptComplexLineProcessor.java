package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;

public class ConceptComplexLineProcessor extends BaseConceptLineProcessor {
	
	protected static String DATATYPE_COMPLEX = "Complex";
	
	protected static String HEADER_HANDLER = "complex data handler";
	
	public ConceptComplexLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	@Override
	protected Concept fill(Concept instance, CsvLine line) throws IllegalArgumentException {
		
		if (!DATATYPE_COMPLEX.equals(line.get(BaseConceptLineProcessor.HEADER_DATATYPE))) {
			return instance;
		}
		
		ConceptComplex cc = new ConceptComplex(instance);
		if (instance.getId() != null) {
			cc = service.getConceptComplex(instance.getId());
		}
		cc.setDatatype(service.getConceptDatatypeByName(DATATYPE_COMPLEX));
		
		cc.setHandler(line.getString(HEADER_HANDLER));
		
		return cc;
	}
}
