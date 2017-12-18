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
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.Verifies;

public class DomainMDSInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_MDS;
	}
	
	@Test
	@Verifies(value = "should import all valid MDS packages 'preferring theirs'", method = "importMetadataSharingPackages()")
	public void importMetadataSharingPackages_shouldImportMDSPackages() {
		
		// Replay
		PersonAttributeType personAttType = null;
		personAttType = Context.getPersonService().getPersonAttributeTypeByUuid("b3b6d540-a32e-44c7-91b3-292d97667518");
		Assert.assertEquals("Race", personAttType.getName());
		
		getService().importMetadataSharingPackages();
		
		// Verif
		PatientIdentifierType patientIdType = Context.getPatientService()
		        .getPatientIdentifierTypeByUuid("0d2ac572-8de3-46c8-9976-1f78899c599f");
		Assert.assertEquals("National ID card number", patientIdType.getName());
		
		personAttType = Context.getPersonService().getPersonAttributeTypeByUuid("b3b6d540-a32e-44c7-91b3-292d97667518");
		Assert.assertEquals("Email address", personAttType.getName());
		Assert.assertFalse(personAttType.isRetired());
		personAttType = Context.getPersonService().getPersonAttributeTypeByUuid("c1f4a004-3f10-11e4-adec-0800271c1b75");
		Assert.assertEquals("education", personAttType.getName());
		Assert.assertTrue(personAttType.isRetired());
		
		// RelationshipType relType =
		// Context.getPersonService().getRelationshipTypeByUuid(
		// "2a5f4ff4-a179-4b8a-aa4c-40f71956ebbc");
		// Assert.assertEquals("Provider supervisor to provider supervisee
		// relationship", relType.getDescription());
		// Assert.assertTrue(relType.isRetired());
	}
}
