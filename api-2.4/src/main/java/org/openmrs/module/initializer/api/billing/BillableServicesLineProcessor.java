package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.BillableServiceStatus;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for a Queue
 */
@OpenmrsProfile(modules = { "billing:*" })
public class BillableServicesLineProcessor extends BaseLineProcessor<BillableService> {
    
    protected static String HEADER_NAME = "name";
    protected static String HEADER_DESC = "description";
    protected static String HEADER_SERVICE = "service";
    protected static String HEADER_SERVICE_TYPE = "service type";
    protected static String HEADER_SERVICE_CATEGORY = "service category";
    protected static String HEADER_SERVICE_STATUS = "service status";
    
    private final ConceptService conceptService;
    
    @Autowired
    public BillableServicesLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
        super();
        this.conceptService = conceptService;
    }
    
    @Override
    public BillableService fill(BillableService billableService, CsvLine line) throws IllegalArgumentException {
        billableService.setName(line.get(HEADER_NAME, true));
        billableService.setShortName(line.getString(HEADER_DESC)); // Assuming description is used as shortName
        if (line.containsHeader(HEADER_SERVICE)) {
            String service = line.getString(HEADER_SERVICE);
            if (StringUtils.isNotBlank(service)) {
                billableService.setConcept(Utils.fetchConcept(service, conceptService));
            } else {
                billableService.setConcept(null);
            }
        }
        if (line.containsHeader(HEADER_SERVICE_TYPE)) {
            String serviceType = line.getString(HEADER_SERVICE_TYPE);
            if (StringUtils.isNotBlank(serviceType)) {
                billableService.setServiceType(Utils.fetchConcept(serviceType, conceptService));
            } else {
                billableService.setServiceType(null);
            }
        }
        if (line.containsHeader(HEADER_SERVICE_CATEGORY)) {
            String serviceCategory = line.getString(HEADER_SERVICE_CATEGORY);
            if (StringUtils.isNotBlank(serviceCategory)) {
                billableService.setServiceCategory(Utils.fetchConcept(serviceCategory, conceptService));
            } else {
                billableService.setServiceCategory(null);
            }
        }
        if (line.containsHeader(HEADER_SERVICE_STATUS)) {
            String serviceStatus = line.getString(HEADER_SERVICE_STATUS);
            if (StringUtils.isNotBlank(serviceStatus)) {
                billableService.setServiceStatus(BillableServiceStatus.valueOf(serviceStatus.toUpperCase()));
            } else {
                billableService.setServiceStatus(BillableServiceStatus.ENABLED);
            }
        }
        return billableService;
    }
}
