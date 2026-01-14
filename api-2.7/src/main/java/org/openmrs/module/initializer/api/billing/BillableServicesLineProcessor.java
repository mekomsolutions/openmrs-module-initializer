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
 * This is the first level line processor for Billable Services
 */
@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class BillableServicesLineProcessor extends BaseLineProcessor<BillableService> {
	
	protected static final String HEADER_NAME = "service name";
	
	protected static final String HEADER_DESC = "short name";
	
	protected static final String HEADER_SERVICE = "concept";
	
	protected static final String HEADER_SERVICE_TYPE = "service type";
	
	protected static final String HEADER_SERVICE_STATUS = "service status";
	
	private final ConceptService conceptService;
	
	@Autowired
	public BillableServicesLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super();
		this.conceptService = conceptService;
	}
	
	@Override
	public BillableService fill(BillableService billableService, CsvLine line) throws IllegalArgumentException {
		billableService.setName(line.get(HEADER_NAME, true));
		billableService.setShortName(line.getString(HEADER_DESC));
		
		String service = line.get(HEADER_SERVICE, true);
		billableService.setConcept(Utils.fetchConcept(service, conceptService));
		
		String serviceType = line.get(HEADER_SERVICE_TYPE, true);
		billableService.setServiceType(Utils.fetchConcept(serviceType, conceptService));
		
		String serviceStatus = line.getString(HEADER_SERVICE_STATUS);
		billableService.setServiceStatus(
		    StringUtils.isNotBlank(serviceStatus) ? BillableServiceStatus.valueOf(serviceStatus.toUpperCase())
		            : BillableServiceStatus.ENABLED);
		
		return billableService;
	}
}
