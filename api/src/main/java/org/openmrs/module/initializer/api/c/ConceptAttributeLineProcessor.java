package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.ConceptAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("initializer.conceptAttributeLineProcessor")
public class ConceptAttributeLineProcessor extends ConceptLineProcessor {
	
	@Autowired
	public ConceptAttributeLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super(conceptService);
	}
	
	@Override
	public Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		Map<String, String> conceptAttributesMap = Utils.getAttributeTypeHeadersMap(line.getHeaderLine());
		
		List<ConceptAttributeType> conceptAttributeTypes = conceptService.getAllConceptAttributeTypes();
		
		// Filtering out non existing Attribute types
		Map<String, String> filteredAttributeTypes = conceptAttributesMap.entrySet().stream().filter(map -> {
			for (ConceptAttributeType ca : conceptAttributeTypes) {
				if (ca.getName().equals(map.getValue()) || ca.getUuid().equals(map.getValue()))
					return true;
			}
			if (line.get(map.getKey()).isEmpty())
				return false;
			throw new IllegalArgumentException("Attribute Type does not exist");
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		// Clear Concept attributes before filling
		Set<ConceptAttribute> cas = concept.getAttributes();
		cas.clear();
		
		// Fill the Concept Attributes Set
		filteredAttributeTypes.forEach((attHeader, attribute) -> {
			ConceptAttribute ca = new ConceptAttribute();
			ConceptAttributeType cat = conceptService.getConceptAttributeTypeByUuid(attribute);
			if (cat == null) {
				cat = conceptService.getConceptAttributeTypeByName(attribute);
			}
			ca.setAttributeType(cat);
			ca.setConcept(concept);
			ca.setCreator(Context.getAuthenticatedUser());
			ca.setDateCreated(new Date());
			ca.setValue(CustomDatatypeUtil.getDatatype(ca.getAttributeType()).fromReferenceString(line.get(attHeader)));
			CustomDatatypeUtil.saveIfDirty(ca);
			cas.add(ca);
		});
		
		concept.setAttributes(cas);
		return concept;
	}
}
