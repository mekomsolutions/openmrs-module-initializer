package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:*" })
public class ServicePricesLineProcessor extends BaseLineProcessor<PaymentMode> {
	
	protected static String HEADER_NAME = "Service Name";
	
	protected static String HEADER_SHORT_NAME = "Short Name";
	
	protected static String HEADER_CONCEPT = "Concept";
	
	protected static String HEADER_SERVICE_TYPE = "Service Type";
	
	protected static String HEADER_SERVICE_STATUS = "Service Status";
	
	private final ConceptService conceptService;
	
	@Autowired
	public ServicePricesLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super();
		this.conceptService = conceptService;
	}
	
	@Override
	public PaymentMode fill(PaymentMode paymentMode, CsvLine line) throws IllegalArgumentException {
		paymentMode.setName(line.get(HEADER_NAME, true));
		if (line.containsHeader(HEADER_SHORT_NAME)) {
			String shortName = line.getString(HEADER_SHORT_NAME);
			if (StringUtils.isNotBlank(shortName)) {
				paymentMode.addAttributeType("Short Name", "String", null, false);
			}
		}
		if (line.containsHeader(HEADER_CONCEPT)) {
			String concept = line.getString(HEADER_CONCEPT);
			if (StringUtils.isNotBlank(concept)) {
				paymentMode.addAttributeType("Concept", "String", null, false);
			}
		}
		if (line.containsHeader(HEADER_SERVICE_TYPE)) {
			String serviceType = line.getString(HEADER_SERVICE_TYPE);
			if (StringUtils.isNotBlank(serviceType)) {
				paymentMode.addAttributeType("Service Type", "String", null, false);
			}
		}
		if (line.containsHeader(HEADER_SERVICE_STATUS)) {
			String serviceStatus = line.getString(HEADER_SERVICE_STATUS);
			if (StringUtils.isNotBlank(serviceStatus)) {
				paymentMode.addAttributeType("Service Status", "String", null, false);
			}
		}
		
		return paymentMode;
	}
}
