package org.openmrs.module.initializer.api.drugs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugIngredient;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component("initializer.ingredientsDrugLineProcessor")
public class IngredientsDrugLineProcessor extends DrugLineProcessor {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected static String HEADER_INGREDIENT = "ingredient";
	
	protected static String HEADER_INGREDIENT_STRENGTH = "strength";
	
	protected static String HEADER_INGREDIENT_UNITS = "units";
	
	@Autowired
	public IngredientsDrugLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super(conceptService);
	}
	
	public Drug fill(Drug drug, CsvLine line) throws IllegalArgumentException {
		
		// Enable loading of 0-N drug ingredients
		
		Set<String> ingredientHeaders = new TreeSet<>();
		for (String header : line.getHeaderLine()) {
			header = header.toLowerCase();
			if (header.startsWith(HEADER_INGREDIENT)) {
				String[] headerComponents = header.split(" ");
				if (headerComponents.length < 2) {
					throw new IllegalArgumentException(
					        "Invalid ingredient header '" + header + "'. Ingredient number expected.");
				}
				ingredientHeaders.add(headerComponents[0] + " " + headerComponents[1]);
			}
		}
		
		// Only modify ingredients in a given drug if ingredient headers are found in the csv
		if (!ingredientHeaders.isEmpty()) {
			
			// Construct a new ingredient from each one specified in the csv
			Map<Concept, DrugIngredient> newIngredients = new LinkedHashMap<>();
			for (String ingredientHeader : ingredientHeaders) {
				
				DrugIngredient newIngredient = new DrugIngredient();
				
				// Ingredient is required.  Only add an ingredient for the drug if it is specified
				String ingredientLookup = line.getString(ingredientHeader);
				if (StringUtils.isNotBlank(ingredientLookup)) {
					Concept ingredient = Utils.fetchConcept(ingredientLookup, conceptService);
					if (ingredient == null) {
						throw new IllegalArgumentException("No concept found for '" + ingredientLookup + "'");
					}
					newIngredient.setIngredient(ingredient);
					
					// Strength is not required, but if non-null, needs to be a double
					Double strength = null;
					String strengthLookup = line.getString(ingredientHeader + " " + HEADER_INGREDIENT_STRENGTH);
					if (StringUtils.isNotBlank(strengthLookup)) {
						try {
							strength = Double.parseDouble(strengthLookup);
						}
						catch (NumberFormatException e) {
							String msg = String.format("Invalid strength, must be a number: " + strengthLookup);
							throw new IllegalArgumentException(msg);
						}
					}
					newIngredient.setStrength(strength);
					
					// Units are not required, but if present, must refer to a valid concept
					Concept units = null;
					String unitsLookup = line.getString(ingredientHeader + " " + HEADER_INGREDIENT_UNITS);
					if (StringUtils.isNotBlank(unitsLookup)) {
						units = Utils.fetchConcept(unitsLookup, conceptService);
						if (units == null) {
							throw new IllegalArgumentException("No concept found for '" + unitsLookup + "'");
						}
					}
					newIngredient.setUnits(units);
					
					newIngredients.put(ingredient, newIngredient);
				}
			}
			
			// Remove or update any of the existing ingredients on the current drug
			for (Iterator<DrugIngredient> i = drug.getIngredients().iterator(); i.hasNext();) {
				DrugIngredient existingIngredient = i.next();
				DrugIngredient newIngredient = newIngredients.remove(existingIngredient.getIngredient());
				if (newIngredient == null) {
					i.remove(); // If an existing ingredient is not found in the new ingredients, remove it
					log.debug("Removing ingredient '" + existingIngredient.getIngredient() + "' from " + drug.getName());
				} else {
					// If an existing ingredient is matched in the new ingredients, update it
					existingIngredient.setStrength(newIngredient.getStrength());
					existingIngredient.setUnits(newIngredient.getUnits());
					log.debug("Updated ingredient '" + existingIngredient + "' for " + drug.getName());
				}
			}
			
			// Add new ingredients to the drug
			for (DrugIngredient newIngredient : newIngredients.values()) {
				newIngredient.setDrug(drug);
				drug.getIngredients().add(newIngredient);
				log.debug("Added ingredient '" + newIngredient.getIngredient() + "' to " + drug.getName());
			}
			
		}
		
		return drug;
	}
}
