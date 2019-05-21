package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.conceptNumericLineProcessor")
public class ConceptNumericLineProcessor extends ConceptLineProcessor {
	
	protected static String DATATYPE_NUMERIC = "Numeric";
	
	protected static String HEADER_AH = "absolute high";
	
	protected static String HEADER_CH = "critical high";
	
	protected static String HEADER_NH = "normal high";
	
	protected static String HEADER_AL = "absolute low";
	
	protected static String HEADER_CL = "critical low";
	
	protected static String HEADER_NL = "normal low";
	
	protected static String HEADER_ALLOWDECIMALS = "allow decimals";
	
	protected static String HEADER_UNITS = "units";
	
	protected static String HEADER_PRECISION = "display precision";
	
	@Autowired
	public ConceptNumericLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super(conceptService);
	}
	
	@Override
	protected Concept fill(Concept instance, CsvLine line) throws IllegalArgumentException {
		
		if (!DATATYPE_NUMERIC.equals(line.get(ConceptLineProcessor.HEADER_DATATYPE))) {
			return instance;
		}
		
		ConceptNumeric cn = new ConceptNumeric(instance);
		if (instance.getId() != null) { // below overrides any other processors work, so this one should be called first
			cn = conceptService.getConceptNumeric(instance.getId());
		}
		cn.setDatatype(conceptService.getConceptDatatypeByName(DATATYPE_NUMERIC));
		
		cn.setHiAbsolute(line.getDouble(HEADER_AH));
		cn.setHiCritical(line.getDouble(HEADER_CH));
		cn.setHiNormal(line.getDouble(HEADER_NH));
		cn.setLowAbsolute(line.getDouble(HEADER_AL));
		cn.setLowCritical(line.getDouble(HEADER_CL));
		cn.setLowNormal(line.getDouble(HEADER_NL));
		cn.setAllowDecimal(line.getBool(HEADER_ALLOWDECIMALS));
		cn.setUnits(line.get(HEADER_UNITS));
		cn.setDisplayPrecision(line.getInt(HEADER_PRECISION));
		
		return cn;
	}
}
