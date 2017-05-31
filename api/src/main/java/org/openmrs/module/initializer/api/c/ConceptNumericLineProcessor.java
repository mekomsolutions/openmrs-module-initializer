package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

public class ConceptNumericLineProcessor extends BaseLineProcessor<Concept, ConceptService> {
	
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
	
	public ConceptNumericLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	@Override
	protected Concept fill(Concept instance, CsvLine line) throws IllegalArgumentException {
		
		if (!DATATYPE_NUMERIC.equals(line.get(BaseConceptLineProcessor.HEADER_DATATYPE))) {
			return instance;
		}
		
		ConceptNumeric cn = new ConceptNumeric(instance);
		if (instance.getId() != null) {
			cn = service.getConceptNumeric(instance.getId());
		}
		cn.setDatatype(service.getConceptDatatypeByName(DATATYPE_NUMERIC));
		
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
