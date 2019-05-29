package org.openmrs.module.initializer.api.drugs;

import org.apache.commons.lang3.StringUtils;
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
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
@Component
public class DrugLineProcessor extends BaseLineProcessor<Drug> {
	
	protected static String HEADER_STRENGTH = "strength";
	
	protected static String HEADER_DOSAGE_FORM = "concept dosage form";
	
	protected static String HEADER_CONCEPT_DRUG = "concept drug";
	
	private ConceptService conceptService;
	
	@Autowired
	public DrugLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	protected Drug bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Drug drug = conceptService.getDrugByUuid(uuid);
		if (drug == null) {
			drug = conceptService.getDrug(line.get(HEADER_NAME));
			
		}
		if (drug == null) {
			drug = new Drug();
			if (!StringUtils.isEmpty(uuid)) {
				drug.setUuid(uuid);
			}
		}
		
		return drug;
	}
	
	@Override
	protected Drug fill(Drug drug, CsvLine line) throws IllegalArgumentException {
		
		String drugName = line.get(HEADER_NAME, true); // should fail is name column missing
		if (drugName == null) {
			throw new IllegalArgumentException("A drug must at least be provided a name: '" + line.toString() + "'");
		}
		drug.setName(drugName);
		drug.setDescription(line.getString(HEADER_DESC, ""));
		drug.setStrength(line.getString(HEADER_STRENGTH, ""));
		
		Concept conceptDrug = Utils.fetchConcept(line.get(HEADER_CONCEPT_DRUG), conceptService);
		drug.setConcept(conceptDrug);
		
		Concept conceptDosageForm = Utils.fetchConcept(line.get(HEADER_DOSAGE_FORM), conceptService);
		drug.setDosageForm(conceptDosageForm);
		
		return drug;
	}
}
