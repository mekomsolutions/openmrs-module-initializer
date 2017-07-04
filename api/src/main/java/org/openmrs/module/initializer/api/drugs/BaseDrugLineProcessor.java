package org.openmrs.module.initializer.api.drugs;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.c.NestedConceptLineProcessor;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
public class BaseDrugLineProcessor extends BaseLineProcessor<Drug, ConceptService> {
	
	protected static String HEADER_STRENGTH = "strength";
	
	protected static String HEADER_DOSAGE_FORM = "concept dosage form";
	
	protected static String HEADER_CONCEPT_DRUG = "concept drug";
	
	public BaseDrugLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	protected Drug fill(Drug drug, CsvLine line) throws IllegalArgumentException {
		
		String drugName = line.get(HEADER_NAME, true); // should fail is name column missing
		if (drugName == null) {
			throw new IllegalArgumentException("A drug must at least be provided a name: '" + line.toString() + "'");
		}
		drug.setName(drugName);
		drug.setDescription(line.getString(HEADER_DESC, ""));
		drug.setStrength(line.getString(HEADER_STRENGTH, ""));
		
		Concept conceptDrug = NestedConceptLineProcessor.fetchConcept(line.get(HEADER_CONCEPT_DRUG), service);
		drug.setConcept(conceptDrug);
		
		Concept conceptDosageForm = NestedConceptLineProcessor.fetchConcept(line.get(HEADER_DOSAGE_FORM), service);
		drug.setDosageForm(conceptDosageForm);
		
		return drug;
	}
}
