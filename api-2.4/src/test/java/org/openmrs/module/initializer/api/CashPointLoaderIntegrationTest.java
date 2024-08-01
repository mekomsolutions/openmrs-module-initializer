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
		executeDataSet("testdata/test-concepts-2.4.xml");
		{
			// To be edited
			Location location = locationService.getLocationByUuid("c4bb4f44-726d-11eb-9439-0242ac130002");
			
			CashPoint cashPoint = new CashPoint();
			cashPoint.setUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
			cashPoint.setName("OPD Cash Point");
			cashPoint.setDescription("Opd cash point for billing");
			cashPoint.setLocation(location);
			iCashPointService.save(cashPoint);
		}
		
		{
			// To be retired
			Location location = locationService.getLocationByUuid("c4bb4f44-726d-11eb-9439-0242ac130003");
			
			CashPoint cashPoint = new CashPoint();
			cashPoint.setUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
			cashPoint.setName("IPD Cash Point");
			cashPoint.setDescription("IPD cash point for billing");
			cashPoint.setLocation(location);
			cashPoint.setRetired(false);
			iCashPointService.save(cashPoint);
		}
	}
	
	@Test
	public void load_shouldLoadCashPointsAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creation
		{
			CashPoint cashPoint = iCashPointService.getByUuid("8e48e0be-1a31-4bd3-a54d-ace82653f8b8");
			assertNotNull(cashPoint);
			assertEquals("MCH Cash Point", cashPoint.getName());
			assertEquals("MCH cash point for billing", cashPoint.getDescription());
			assertEquals(locationService.getLocationByUuid("c4bb4f44-726d-11eb-9439-0242ac130004"), cashPoint.getLocation());
		}
		
		// Verify edition
		{
			CashPoint cashPoint = iCashPointService.getByUuid("54065383-b4d4-42d2-af4d-d250a1fd2590");
			assertNotNull(cashPoint);
			assertEquals("OPD Cash Point Modified", cashPoint.getName());
			assertEquals("Opd cash point for billing", cashPoint.getDescription());
			assertEquals(locationService.getLocationByUuid("c4bb4f44-726d-11eb-9439-0242ac130002"), cashPoint.getLocation());
		}
		
		// Verify retirement
		{
			CashPoint cashPoint = iCashPointService.getByUuid("8e48e0be-1a31-4bd3-a54d-ace82653f8b8");
			assertTrue(cashPoint.getRetired());
			
		}
		
		// Verify unretirement
		{
			CashPoint cashPoint = iCashPointService.getByUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
			cashPoint.setRetired(false);
			iCashPointService.save(cashPoint);
			
			CashPoint unretiredCashPoint = iCashPointService.getByUuid("c56a108f-e3c5-4881-a5e8-a796601883b9");
			assertFalse(unretiredCashPoint.getRetired());
		}
	}
}
