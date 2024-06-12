package org.openmrs.module.initializer.api;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.web.rest.resource.BillableServiceResource;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.billing.BillableServiceCsvParser;
import org.openmrs.module.initializer.api.billing.BillableServicesLineProcessor;

public class BillableServiceCsvParserTest {
    
    @Mock
    private BillableServiceResource billableServiceResource;

    @Mock
    private BillableServicesLineProcessor processor;

    @InjectMocks
    private BillableServiceCsvParser parser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDomain() {
        assertEquals(Domain.BILLABLE_SERVICES, parser.getDomain());
    }

    @Test
    public void testBootstrapWithExistingService() {
        String uuid = "44ebd6cd-04ad-4eba-8ce1-0de4564bfd17";
        CsvLine csvLine = new CsvLine(new String[]{"Uuid"}, new String[]{uuid});
        
        BillableService existingService = new BillableService();
        when(billableServiceResource.getByUniqueId(uuid)).thenReturn(existingService);

        BillableService result = parser.bootstrap(csvLine);

        assertNotNull(result);
        assertEquals(existingService, result);
    }

    @Test
    public void testBootstrapWithNewService() {
        String uuid = "44ebd6cd-04ad-4eba-8ce1-0de4564bfd17";
        CsvLine csvLine = new CsvLine(new String[]{"Uuid"}, new String[]{uuid});

        when(billableServiceResource.getByUniqueId(uuid)).thenReturn(null);

        BillableService result = parser.bootstrap(csvLine);

        assertNotNull(result);
        assertNull(result.getId()); // New service should not have an ID
        assertEquals(uuid, result.getUuid());
    }

    @Test
    public void testSave() {
        BillableService service = new BillableService();
        when(billableServiceResource.save(service)).thenReturn(service);

        BillableService result = parser.save(service);

        assertNotNull(result);
        assertEquals(service, result);
    }
}
