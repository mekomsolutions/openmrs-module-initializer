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

import static org.hamcrest.CoreMatchers.is;
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
    public void load_shouldLoadCashPointsFromCSV() throws Exception {
        // Replay
        loader.load();

        // Verify fetch
        {
            CashPoint cashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
            assertNotNull(cashPoint);
            assertEquals("OPD Cash Point", cashPoint.getName());
            assertEquals("Opd cash point for billing", cashPoint.getDescription());

            Location location = locationService.getLocationByUuid("fdddc31a-3930-11ea-9712-a73c3c19744f");
            assertNotNull(location);
            assertEquals("ART Clinic", location.getName());
            assertEquals(location, cashPoint.getLocation());
        }

        // Verify edition (Modify an existing entity in the CSV)
        {
            CashPoint cashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
            cashPoint.setName("OPD Cash Point Updated");
            iCashPointService.save(cashPoint);

            loader.load();

            CashPoint updatedCashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
            assertEquals("OPD Cash Point Updated", updatedCashPoint.getName());
        }

        // Verify retirement and un-retire using UUID as pivot in CSV
        {
            CashPoint cashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
            cashPoint.setRetired(true);
            iCashPointService.save(cashPoint);

            loader.load();

            CashPoint retiredCashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
            assertTrue(retiredCashPoint.getRetired());

            retiredCashPoint.setRetired(false);
            iCashPointService.save(retiredCashPoint);

            loader.load();

            CashPoint unretiredCashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
            assertFalse(unretiredCashPoint.getRetired());
        }
    }
}
