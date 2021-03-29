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
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.OpenConceptLabLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.openmrs.module.openconceptlab.Utils;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.io.InputStream;
import org.openmrs.Concept;

public class OpenConceptLabLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private OpenConceptLabLoader loader;
	
	@Test
	public void load_shouldImportOCLPackages() throws IOException {
		
		Utils utils = null;
		ZipFile zipFile = new ZipFile(
		        "src/test/resources/testAppDataDir/configuration/openconceptlab/CIEL_v2021-01-29.20210204075515.zip");
		InputStream is = utils.extractExportInputStreamFromZip(zipFile);
		
		loader.load();
		
		{
		Concept c = null;
		c = Context.getConceptService().getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
		Assert.assertEquals(2, c.getSetMembers().size());
		Assert.assertTrue(c.isSet());
		}
		
	}
}
