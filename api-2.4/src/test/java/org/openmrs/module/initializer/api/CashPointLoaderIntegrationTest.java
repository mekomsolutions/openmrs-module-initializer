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
    public void setup() {
        Location location = new Location();
        location.setUuid("8d6c7b96-29de-102b-86b0-7a5022ba4115");
        location.setName("ART Clinic");
        locationService.saveLocation(location);
    }
    
    @Test
    public void load_shouldLoadCashPointsFromCSV() throws Exception {        
        // Replay
        loader.load();
        
        // Verify
        CashPoint cashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
        assertNotNull(cashPoint);
        assertEquals("OPD Cash Point", cashPoint.getName());
        assertEquals("Opd cash point for billing", cashPoint.getDescription());
        
        Location location = locationService.getLocationByUuid("8d6c7b96-29de-102b-86b0-7a5022ba4115");
        assertEquals(location, cashPoint.getLocation());
    }
}
