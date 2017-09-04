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
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.impl.Utils;

public class DomainJKVInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	private ConceptService cs;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_JKV;
	}
	
	@Before
	public void setup() {
		cs = Context.getConceptService();
		
		ConceptSource source = new ConceptSource();
		source.setName("Cambodia");
		source = cs.saveConceptSource(source);
		
		// A concept to be fetched via JKV
		{
			Concept c = new Concept();
			c.setUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			c.setFullySpecifiedName(new ConceptName("CONCEPT_FOR_FETCHING", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			{
				ConceptMapType mapType = cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
				Utils.MappingWrapper mappingWrapper = new Utils.MappingWrapper("Cambodia:123", mapType, cs);
				c.addConceptMapping(mappingWrapper.getConceptMapping());
			}
			cs.saveConcept(c);
		}
	}
	
	@Test
	public void loadJsonKeyValues_shouldFetchConceptsFromAllPossibleKeys() {
		// Replay
		getService().loadJsonKeyValues();
		
		Concept c1 = getService().getConceptFromKey("impl.purpose.concept.uuid");
		Assert.assertNotNull(c1);
		Concept c2 = getService().getConceptFromKey("impl.purpose.concept.fsn");
		Assert.assertEquals(c1, c2);
		Concept c3 = getService().getConceptFromKey("impl.purpose.concept.mapping");
		Assert.assertEquals(c2, c3);
		
		Assert.assertNull(getService().getConceptFromKey("__invalid_json_key__"));
		Assert.assertEquals(c1, getService().getConceptFromKey("__invalid_json_key__", c1));
	}
}
