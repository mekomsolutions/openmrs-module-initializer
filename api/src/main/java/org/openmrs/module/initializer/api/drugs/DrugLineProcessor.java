package org.openmrs.module.initializer.api.drugs;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This is the first level line processor for drugs. It allows to parse and save drugs with the
 * minimal set of required fields.
 */
@Component("initializer.drugLineProcessor")
public class DrugLineProcessor extends BaseLineProcessor<Drug> {
	
	protected static String HEADER_STRENGTH = "strength";
	
	protected static String HEADER_DOSAGE_FORM = "concept dosage form";
	
	protected static String HEADER_CONCEPT_DRUG = "concept drug";
	
	protected ConceptService conceptService;
	
	@Autowired
	public DrugLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public Drug fill(Drug drug, CsvLine line) throws IllegalArgumentException {
		
		drug.setName(line.getName(true));
		drug.setDescription(line.getString(HEADER_DESC, ""));
		drug.setStrength(line.getString(HEADER_STRENGTH, ""));
		
		Concept conceptDrug = Utils.fetchConcept(line.get(HEADER_CONCEPT_DRUG), conceptService);
		if (conceptDrug == null) {
			throw new IllegalArgumentException("No drug concept found for '" + line.get(HEADER_CONCEPT_DRUG) + "'");
		}
		drug.setConcept(conceptDrug);
		
		Concept conceptDosageForm = Utils.fetchConcept(line.get(HEADER_DOSAGE_FORM), conceptService);
		drug.setDosageForm(conceptDosageForm);
		
		return drug;
	}
}
