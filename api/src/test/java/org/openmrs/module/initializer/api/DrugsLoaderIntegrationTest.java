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
import org.openmrs.Drug;
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
	}
	
	@Test
	public void load_shouldLoadDrugsAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// a vanilla drug
		{
			Drug d = cs.getDrugByNameOrId("Cetirizine 10mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Cetirizine"), d.getConcept());
			Assert.assertEquals(cs.getConceptByName("Tablet"), d.getDosageForm());
		}
		
		// a drug without dosage form
		{
			Drug d = cs.getDrugByNameOrId("Erythromycine 500mg Tablet");
			Assert.assertNotNull(d);
			Assert.assertEquals(cs.getConceptByName("Erythromycine"), d.getConcept());
			Assert.assertNull(d.getDosageForm());
		}
		
		// an edited drug
		{
			Drug d = cs.getDrugByNameOrId("Metronidazole 500mg Tablet");
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
	}
}
