package org.openmrs.module.initializer.api.billing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.billing.api.impl.BillableServiceServiceImpl;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BillableServicesCsvParserTest {
	
	@Mock
	private BillableServiceServiceImpl billableServiceService;
	
	@Mock
	private BillableServicesLineProcessor processor;
	
	@InjectMocks
	private BillableServicesCsvParser parser;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void getDomain_shouldReturnBillableServicesDomain() {
		Assertions.assertEquals(Domain.BILLABLE_SERVICES, parser.getDomain());
	}
	
	@Test
	public void bootstrap_shouldReturnExistingServiceGivenUuidPresent() {
		// setup
		String uuid = "44ebd6cd-04ad-4eba-8ce1-0de4564bfd17";
		CsvLine csvLine = new CsvLine(new String[] { "Uuid" }, new String[] { uuid });
		BillableService existingService = new BillableService();
		when(billableServiceService.getBillableServiceByUuid(uuid)).thenReturn(existingService);
		
		// Replay
		BillableService result = parser.bootstrap(csvLine);
		
		// Verify
		assertNotNull(result);
		assertEquals(existingService, result);
	}
	
	@Test
	public void bootstrap_shouldReturnNewServiceGivenUuidNotPresent() {
		// Setup
		String uuid = "44ebd6cd-04ad-4eba-8ce1-0de4564bfd17";
		CsvLine csvLine = new CsvLine(new String[] { "Uuid" }, new String[] { uuid });
		when(billableServiceService.getBillableServiceByUuid(uuid)).thenReturn(null);
		
		// Replay
		BillableService result = parser.bootstrap(csvLine);
		
		// Verify
		assertNotNull(result);
		assertEquals(uuid, result.getUuid());
	}
	
	@Test
	public void save_shouldReturnSavedService() {
		// Setup
		BillableService service = new BillableService();
		when(billableServiceService.saveBillableService(service)).thenReturn(service);
		
		// Replay
		BillableService result = parser.save(service);
		
		// Verify
		assertNotNull(result);
		assertEquals(service, result);
	}
}
