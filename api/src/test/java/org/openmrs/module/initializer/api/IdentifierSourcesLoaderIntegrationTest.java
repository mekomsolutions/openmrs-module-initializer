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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.idgen.IdentifierSourcesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class IdentifierSourcesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private IdentifierSourceService idgenService;
	
	@Autowired
	private IdentifierSourcesLoader loader;
	
	@Before
	public void setup() {
		
		PatientIdentifierType type = Context.getPatientService().getPatientIdentifierType(1);
		type = new PatientIdentifierType();
		type.setName("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID");
		Context.getPatientService().savePatientIdentifierType(type);
		
		{
			IdentifierPool src = new IdentifierPool();
			src.setName("Test identifier pool source");
			src.setUuid("c1d8a345-3f10-11e4-adec-0800271c1b75");
			src.setRetired(false);
			src.setIdentifierType(type);
			idgenService.saveIdentifierSource(src);
		}
		
		{
			RemoteIdentifierSource src = new RemoteIdentifierSource();
			src.setName("Test remote identifier source");
			src.setUuid("c1d90956-3f10-11e4-adec-0800271c1b75");
			src.setRetired(false);
			src.setIdentifierType(type);
			src.setUrl("http://example.com");
			idgenService.saveIdentifierSource(src);
		}
		
		{
			IdentifierPool src = new IdentifierPool();
			src.setName("Test identifier pool source 2");
			src.setUuid("ef35fb58-6618-411a-a331-bff960a29d40");
			src.setRetired(false);
			src.setIdentifierType(type);
			idgenService.saveIdentifierSource(src);
		}
	}
	
	@Test
	public void load_shouldModifyAndCreateIdentifierSources() {
		
		// Replay
		loader.load();
		
		// Verif sources marked for retirement
		Assert.assertTrue(idgenService.getIdentifierSourceByUuid("c1d8a345-3f10-11e4-adec-0800271c1b75").isRetired());
		Assert.assertTrue(idgenService.getIdentifierSourceByUuid("c1d90956-3f10-11e4-adec-0800271c1b75").isRetired());
		
		// Verif the source marked for creation
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid("1af1422c-8c65-438d-9770-cbb723821bc8");
			Assert.assertNotNull(source);
			Assert.assertTrue(source instanceof SequentialIdentifierGenerator);
			SequentialIdentifierGenerator src = (SequentialIdentifierGenerator) source;
			
			Assert.assertEquals("Test sequential source #1", src.getName());
			Assert.assertEquals("Test sequential source description #1", src.getDescription());
			Assert.assertEquals("001000", src.getFirstIdentifierBase());
			Assert.assertTrue(7 == src.getMinLength());
			Assert.assertTrue(7 == src.getMaxLength());
			Assert.assertEquals("0123456789", src.getBaseCharacterSet());
			Assert.assertFalse(src.isRetired());
		}
		
		// Verif the source marked for edition
		{
			IdentifierSource source = idgenService.getIdentifierSourceByUuid("ef35fb58-6618-411a-a331-bff960a29d40");
			Assert.assertNotNull(source);
			Assert.assertTrue(source instanceof IdentifierPool);
			IdentifierPool src = (IdentifierPool) source;
			
			Assert.assertEquals("RENAMED Identifier Pool", src.getName());
			Assert.assertEquals("RENAMED Identifier Pool description", src.getDescription());
			Assert.assertFalse(src.isRetired());
		}
	}
}
