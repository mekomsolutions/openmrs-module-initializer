/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.loaders;

import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;

public class LoadersIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Test
	public void getLoaders_shouldBeUnivoquelyOrdered() {
		
		List<Loader> loaders = getService().getLoaders();
		
		for (int i = 1; i < loaders.size(); i++) {
			Assert.assertThat(loaders.get(i).getOrder(), greaterThan(loaders.get(i - 1).getOrder()));
		}
		
		// System.out.println("Here is the list of loaders in order:");
		// for (Loader l : loaders) {
		// System.out.format("%4d%100s%n", l.getOrder(), l.toString());
		// }
		
	}
}
