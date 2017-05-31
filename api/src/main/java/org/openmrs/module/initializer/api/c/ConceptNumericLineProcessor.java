package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;

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
	protected Concept fill(Concept instance, String[] line) throws IllegalArgumentException {
		
		ConceptNumeric cn = new ConceptNumeric(instance);
		if (instance.getId() != null) {
			cn = service.getConceptNumeric(instance.getId());
			// We need to re-run the base as the above would have overwritten it.
			cn = (ConceptNumeric) (new BaseConceptLineProcessor(headerLine, service).fill(cn, line));
		}
		cn.setDatatype(service.getConceptDatatypeByName(DATATYPE_NUMERIC));
		
		String val = null;
		try {
			val = line[getColumn(HEADER_AH)];
		}
		catch (IllegalArgumentException e) {}
		cn.setHiAbsolute(parseDouble(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_CH)];
		}
		catch (IllegalArgumentException e) {}
		cn.setHiCritical(parseDouble(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_NH)];
		}
		catch (IllegalArgumentException e) {}
		cn.setHiNormal(parseDouble(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_AL)];
		}
		catch (IllegalArgumentException e) {}
		cn.setLowAbsolute(parseDouble(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_CL)];
		}
		catch (IllegalArgumentException e) {}
		cn.setLowCritical(parseDouble(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_NL)];
		}
		catch (IllegalArgumentException e) {}
		cn.setLowNormal(parseDouble(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_ALLOWDECIMALS)];
		}
		catch (IllegalArgumentException e) {}
		cn.setAllowDecimal(parseBool(val));
		
		val = null;
		try {
			val = line[getColumn(HEADER_UNITS)];
		}
		catch (IllegalArgumentException e) {}
		cn.setUnits(val);
		
		val = null;
		try {
			val = line[getColumn(HEADER_PRECISION)];
		}
		catch (IllegalArgumentException e) {}
		cn.setDisplayPrecision(parseInt(val));
		
		return cn;
	}
}
