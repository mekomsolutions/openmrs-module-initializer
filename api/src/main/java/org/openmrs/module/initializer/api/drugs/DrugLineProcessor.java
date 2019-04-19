package org.openmrs.module.initializer.api.drugs;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.Utils;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
public class DrugLineProcessor extends BaseLineProcessor<Drug, ConceptService> {
	
	protected static String HEADER_STRENGTH = "strength";
	
	protected static String HEADER_DOSAGE_FORM = "concept dosage form";
	
	protected static String HEADER_CONCEPT_DRUG = "concept drug";
	
	public DrugLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	@Override
	protected Drug bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		Drug drug = service.getDrugByUuid(uuid);
		if (drug == null) {
			drug = service.getDrugByNameOrId(line.get(HEADER_NAME));
		}
		if (drug == null) {
			drug = new Drug();
			if (!StringUtils.isEmpty(uuid)) {
				drug.setUuid(uuid);
			}
		}
		
		drug.setRetired(getVoidOrRetire(line.asLine()));
		
		return drug;
	}
	
	protected Drug fill(Drug drug, CsvLine line) throws IllegalArgumentException {
		
		String drugName = line.get(HEADER_NAME, true); // should fail is name column missing
		if (drugName == null) {
			throw new IllegalArgumentException("A drug must at least be provided a name: '" + line.toString() + "'");
		}
		drug.setName(drugName);
		drug.setDescription(line.getString(HEADER_DESC, ""));
		drug.setStrength(line.getString(HEADER_STRENGTH, ""));
		
		Concept conceptDrug = Utils.fetchConcept(line.get(HEADER_CONCEPT_DRUG), service);
		drug.setConcept(conceptDrug);
		
		Concept conceptDosageForm = Utils.fetchConcept(line.get(HEADER_DOSAGE_FORM), service);
		drug.setDosageForm(conceptDosageForm);
		
		return drug;
	}
}
