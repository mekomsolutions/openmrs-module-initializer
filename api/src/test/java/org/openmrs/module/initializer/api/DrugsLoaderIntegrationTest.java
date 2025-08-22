/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.DrugIngredient;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.drugs.DrugsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DrugsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private DrugsLoader loader;
	
	@Before
	public void setup() {
		
		// A concept to be used as 'dosage form'
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Tablet", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		// Concepts to be used as a 'drug'
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Cetirizine", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Drug"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Erythromycine", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Drug"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Metronidazole (new)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Drug"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
		}
		// drugs to be edited
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Metronidazole (old)", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Drug"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			Drug d = new Drug();
			d.setUuid("2bcf7212-d218-4572-8893-25c4b5b71934");
			d.setName("Metronidazole 500mg Tablet");
			d.setConcept(c);
			d = cs.saveDrug(d);
		}
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("D4T", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Drug"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c = cs.saveConcept(c);
			
			Drug d = new Drug();
			d.setUuid("42f010f8-26fe-102b-80cb-0017a47871b2");
			d.setName("d4T 30");
			d.setConcept(c);
			d.setStrength("100mg");
			d = cs.saveDrug(d);
		}
		{
			Drug d = new Drug();
			d.setUuid("8abf401a-7f65-11f0-9e36-be568b1ab237");
			d.setName("Drug with Erythromycine");
			d.setConcept(cs.getConceptByName("d4T"));
			d.setStrength("20mg");
			DrugIngredient ingredient = new DrugIngredient();
			ingredient.setDrug(d);
			ingredient.setIngredient(cs.getConceptByName("Erythromycine"));
			ingredient.setStrength(20.0);
			ingredient.setUnits(cs.getConceptByName("mg"));
			d.getIngredients().add(ingredient);
			cs.saveDrug(d);
		}
	}
	
	@Test
	public void load_shouldLoadDrugsAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// a vanilla drug
		{
			Drug d = cs.getDrug("Cetirizine 10mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Cetirizine"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
		}
		
		// a drug without dosage form
		{
			Drug d = cs.getDrug("Erythromycine 500mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Erythromycine"), d.getConcept());
			Assert.assertNull(d.getDosageForm());
		}
		
		// an edited drug
		{
			Drug d = cs.getDrug("Metronidazole 500mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Metronidazole (new)"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
		}
		// an edited drug fetched by name
		{
			Drug d = cs.getDrugByUuid("42f010f8-26fe-102b-80cb-0017a47871b2");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("d4T"), d.getConcept());
			Assert.assertEquals("30mg", d.getStrength());
		}
		// a new drug that starts out retired
		{
			Drug d = cs.getDrugByUuid("6e764d43-ae8b-11eb-8168-0242ac110002");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Metronidazole (new)"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
			Assert.assertEquals("250mg", d.getStrength());
			Assert.assertTrue(d.getRetired());
		}
	}
	
	@Test
	public void load_shouldAddIngredientsToDrugs() {
		loader.load();
		{
			Drug d = cs.getDrug("Combo Drug");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("d4T"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
			Assert.assertEquals("10mg", d.getStrength());
			Collection<DrugIngredient> ingredients = d.getIngredients();
			Concept erythromycine = cs.getConceptByName("Erythromycine");
			Concept cetirizine = cs.getConceptByName("Cetirizine");
			Concept mg = cs.getConceptByName("mg");
			Assert.assertEquals(2, ingredients.size());
			for (DrugIngredient ingredient : ingredients) {
				if (ingredient.getIngredient().equals(erythromycine)) {
					Assert.assertEquals((Double) 4.0, ingredient.getStrength());
					Assert.assertEquals(mg, ingredient.getUnits());
				} else if (ingredient.getIngredient().equals(cetirizine)) {
					Assert.assertEquals((Double) 6.0, ingredient.getStrength());
					Assert.assertEquals(mg, ingredient.getUnits());
				} else {
					Assert.fail("Unexpected ingredient " + ingredient);
				}
			}
		}
	}
	
	@Test
	public void load_shouldRemoveIngredientsFromDrugs() {
		
		Drug drug = cs.getDrugByUuid("8abf401a-7f65-11f0-9e36-be568b1ab237");
		Assert.assertNotNull(drug);
		Assert.assertEquals("Drug with Erythromycine", drug.getName());
		Assert.assertEquals(1, drug.getIngredients().size());
		DrugIngredient erythromycine = drug.getIngredients().iterator().next();
		Assert.assertEquals(cs.getConceptByName("Erythromycine"), erythromycine.getIngredient());
		Assert.assertEquals((Double) 20.0, erythromycine.getStrength());
		Assert.assertEquals(cs.getConceptByName("mg"), erythromycine.getUnits());
		
		loader.load();
		
		drug = cs.getDrugByUuid("8abf401a-7f65-11f0-9e36-be568b1ab237");
		Assert.assertNotNull(drug);
		Assert.assertEquals("Drug without Erythromycine", drug.getName());
		Assert.assertEquals(1, drug.getIngredients().size());
		DrugIngredient cetirizine = drug.getIngredients().iterator().next();
		Assert.assertNotEquals(erythromycine.getUuid(), cetirizine.getUuid());
		Assert.assertEquals(cs.getConceptByName("Cetirizine"), cetirizine.getIngredient());
		Assert.assertEquals((Double) 15.0, cetirizine.getStrength());
		Assert.assertEquals(cs.getConceptByName("mg"), cetirizine.getUnits());
	}
	
	@Test
	public void load_shouldModifyIngredientsInDrugs() {
		
		loader.load();
		
		Drug drug = cs.getDrugByUuid("8abf401a-7f65-11f0-9e36-be568b1ab237");
		Assert.assertNotNull(drug);
		Assert.assertEquals("Drug without Erythromycine", drug.getName());
		Assert.assertEquals(1, drug.getIngredients().size());
		DrugIngredient cetirizine = drug.getIngredients().iterator().next();
		Assert.assertEquals(cs.getConceptByName("Cetirizine"), cetirizine.getIngredient());
		Assert.assertEquals((Double) 15.0, cetirizine.getStrength());
		
		cetirizine.setStrength(20.0);
		cs.saveDrug(drug);
		
		drug = cs.getDrugByUuid("8abf401a-7f65-11f0-9e36-be568b1ab237");
		Assert.assertEquals(1, drug.getIngredients().size());
		DrugIngredient originalIngredient = drug.getIngredients().iterator().next();
		Assert.assertEquals((Double) 20.0, originalIngredient.getStrength());
		
		loader.getDirUtil().deleteChecksums();
		loader.load();
		
		drug = cs.getDrugByUuid("8abf401a-7f65-11f0-9e36-be568b1ab237");
		Assert.assertEquals(1, drug.getIngredients().size());
		DrugIngredient modifiedIngredient = drug.getIngredients().iterator().next();
		// NOTE: Ideally we'd test this, but due to a bug in core with saving drug ingredients, it doesn't work unless you are on very specific versions of core
		//Assert.assertEquals(originalIngredient.getUuid(), modifiedIngredient.getUuid());
		Assert.assertEquals((Double) 15.0, modifiedIngredient.getStrength());
	}
}
