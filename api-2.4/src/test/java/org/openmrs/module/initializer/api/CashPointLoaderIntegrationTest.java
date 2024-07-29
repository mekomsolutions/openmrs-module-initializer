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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.billing.api.ICashPointService;
import org.openmrs.module.billing.api.model.CashPoint;
import org.openmrs.module.initializer.api.billing.CashPointLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CashPointLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService locationService;
	
	@Autowired
	private ICashPointService iCashPointService;
	
	@Autowired
	private CashPointLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void shouldCreateCashPointsFromCSV() {
		// Replay
		loader.load();
		
		// Verify creation for all CashPoints
		CashPoint cashPoint1 = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
		assertNotNull(cashPoint1);
		assertEquals("OPD Cash Point", cashPoint1.getName());
		assertEquals("Opd cash point for billing", cashPoint1.getDescription());
		
		Location location1 = locationService.getLocation("ART Clinic");
		assertNotNull(location1);
		assertEquals("ART Clinic", location1.getName());
		assertEquals(location1, cashPoint1.getLocation());
		
		CashPoint cashPoint2 = iCashPointService.getByUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
		assertNotNull(cashPoint2);
		assertEquals("IPD Cash Point", cashPoint2.getName());
		assertEquals("IPD cash point for billing", cashPoint2.getDescription());
		
		Location location2 = locationService.getLocation("Inpatient Ward");
		assertNotNull(location2);
		assertEquals("Inpatient Ward", location2.getName());
		assertEquals(location2, cashPoint2.getLocation());
		
		CashPoint cashPoint3 = iCashPointService.getByUuid("8e48e0be-1a31-4bd3-a54d-ace82653f8b8");
		assertNotNull(cashPoint3);
		assertEquals("MCH Cash Point", cashPoint3.getName());
		assertEquals("MCH cash point for billing", cashPoint3.getDescription());
		
		Location location3 = locationService.getLocation("MCH Clinic");
		assertNotNull(location3);
		assertEquals("MCH Clinic", location3.getName());
		assertEquals(location3, cashPoint3.getLocation());
	}
	
	@Test
	public void shouldEditExistingCashPointFromCSV() {
		// Modify an existing entity in the CSV
		CashPoint cashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
		cashPoint.setName("OPD Cash Point Updated");
		iCashPointService.save(cashPoint);
		
		CashPoint updatedCashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
		assertEquals("OPD Cash Point Updated", updatedCashPoint.getName());
	}
	
	@Test
	public void shouldRetireAndUnretireCashPointFromCSV() {
		// Retire an existing entity in the CSV
		CashPoint cashPoint = iCashPointService.getByUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
		cashPoint.setRetired(true);
		iCashPointService.save(cashPoint);
		
		CashPoint retiredCashPoint = iCashPointService.getByUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
		assertTrue(retiredCashPoint.getRetired());
		
		// Unretire the entity
		retiredCashPoint.setRetired(false);
		iCashPointService.save(retiredCashPoint);
		
		CashPoint unretiredCashPoint = iCashPointService.getByUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
		assertFalse(unretiredCashPoint.getRetired());
	}
}
