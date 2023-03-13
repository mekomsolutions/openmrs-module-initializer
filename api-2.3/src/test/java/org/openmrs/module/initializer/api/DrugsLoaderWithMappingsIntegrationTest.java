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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.DrugReferenceMap;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_3_Test;
import org.openmrs.module.initializer.api.drugs.DrugsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DrugsLoaderWithMappingsIntegrationTest extends DomainBaseModuleContextSensitive_2_3_Test {
	
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
		{
			Concept c = new Concept();
			c.setShortName(new ConceptName("Albendazole", Locale.ENGLISH));
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
		// Concept Sources and Concept Map Types to use for mappings
		{
			ConceptSource source = new ConceptSource();
			source.setName("CS1");
			source.setDescription("Concept Source 1");
			cs.saveConceptSource(source);
		}
		{
			ConceptSource source = new ConceptSource();
			source.setName("CS2");
			source.setDescription("Concept Source 2");
			cs.saveConceptSource(source);
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
			assertMappings(d, "SAME-AS:CS1:M1");
		}
		
		// a drug without dosage form
		{
			Drug d = cs.getDrug("Erythromycine 500mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Erythromycine"), d.getConcept());
			Assert.assertNull(d.getDosageForm());
			assertMappings(d, "SAME-AS:CS1:M2", "SAME-AS:CS1:M3");
		}
		
		// an edited drug
		{
			Drug d = cs.getDrug("Metronidazole 500mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Metronidazole (new)"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
			assertMappings(d, "broader-than:CS2:M4");
		}
		// an edited drug fetched by name
		{
			Drug d = cs.getDrugByUuid("42f010f8-26fe-102b-80cb-0017a47871b2");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("d4T"), d.getConcept());
			Assert.assertEquals("30mg", d.getStrength());
			assertMappings(d, "broader-than:CS1:M5", "broader-than:CS2:M6");
		}
		// a new drug that starts out retired
		{
			Drug d = cs.getDrugByUuid("6e764d43-ae8b-11eb-8168-0242ac110002");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Metronidazole (new)"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
			Assert.assertEquals("250mg", d.getStrength());
			Assert.assertTrue(d.getRetired());
			assertMappings(d);
		}
		{
			Drug d = cs.getDrug("Albendazole 200mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Albendazole"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
			assertMappings(d, "SAME-AS:CS1:M7", "SAME-AS:CS1:M8");
		}
	}
	
	protected void assertMappings(Drug d, String... mappings) {
		Assert.assertEquals(d.getDrugReferenceMaps().size(), mappings.length);
		for (String mapping : mappings) {
			String[] components = mapping.split(":");
			String mapTypeName = components[0];
			String sourceName = components[1];
			String code = components[2];
			boolean found = false;
			for (DrugReferenceMap m : d.getDrugReferenceMaps()) {
				if (m.getConceptMapType().getName().equalsIgnoreCase(mapTypeName)) {
					if (m.getConceptReferenceTerm().getConceptSource().getName().equalsIgnoreCase(sourceName)) {
						if (m.getConceptReferenceTerm().getCode().equalsIgnoreCase(code)) {
							found = true;
						}
					}
				}
			}
			if (!found) {
				Assert.fail("Mapping " + mapping + " not found.");
			}
		}
	}
}
