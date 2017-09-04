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

import java.util.List;

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
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

public class DomainIdgenInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private IdentifierSourceService idgenService;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_IDGEN;
	}
	
	@Before
	public void setup() {
		
		PatientIdentifierType type = Context.getPatientService().getPatientIdentifierType(1);
		
		IdentifierPool src1 = new IdentifierPool();
		src1.setName("Test identifier pool source");
		src1.setUuid("c1d8a345-3f10-11e4-adec-0800271c1b75");
		src1.setRetired(false);
		src1.setIdentifierType(type);
		idgenService.saveIdentifierSource(src1);
		
		RemoteIdentifierSource src2 = new RemoteIdentifierSource();
		src2.setName("Test remote identifier source");
		src2.setUuid("c1d90956-3f10-11e4-adec-0800271c1b75");
		src2.setRetired(false);
		src2.setIdentifierType(type);
		src2.setUrl("http://example.com");
		idgenService.saveIdentifierSource(src2);
		
		type = new PatientIdentifierType();
		type.setName("PATIENTIDENTIFIERTYPE_1_OPENMRS_ID");
		Context.getPatientService().savePatientIdentifierType(type);
	}
	
	@Test
	@Verifies(value = "should save modifications on exisiting identifier sources and create new identifier sources", method = "configureIdgen()")
	public void configureIdgen_shouldModifyAndCreateIdentifierSources() {
		
		// Replay
		getService().configureIdgen();
		
		// Verif sources marked for modification
		Assert.assertTrue(idgenService.getIdentifierSourceByUuid("c1d8a345-3f10-11e4-adec-0800271c1b75").isRetired());
		Assert.assertTrue(idgenService.getIdentifierSourceByUuid("c1d90956-3f10-11e4-adec-0800271c1b75").isRetired());
		
		// Verif the source marked for creation
		List<IdentifierSource> sources = idgenService.getAllIdentifierSources(false);
		Assert.assertEquals(1, sources.size());
		SequentialIdentifierGenerator src = (SequentialIdentifierGenerator) sources.get(0);
		
		Assert.assertEquals("Test sequential source name", src.getName());
		Assert.assertEquals("Test sequential source description", src.getDescription());
		Assert.assertEquals("001000", src.getFirstIdentifierBase());
		Assert.assertTrue(7 == src.getMinLength());
		Assert.assertTrue(7 == src.getMaxLength());
		Assert.assertEquals("0123456789", src.getBaseCharacterSet());
		Assert.assertFalse(src.isRetired()); // not <retired/> tag means retired=false
	}
}
