package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for a Queue
 */
@OpenmrsProfile(modules = { "billing:*" })
public class ServicePricesLineProcessor extends BaseLineProcessor<PaymentMode> {
    
    protected static String HEADER_NAME = "name";
    protected static String HEADER_DESC = "description";
    
    private final ConceptService conceptService;
    
    @Autowired
    public ServicePricesLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
        super();
        this.conceptService = conceptService;
    }
    
    @Override
    public PaymentMode fill(PaymentMode paymentMode, CsvLine line) throws IllegalArgumentException {
        paymentMode.setName(line.get(HEADER_NAME, true));
        
        // Assuming the description is an attribute to be added to PaymentMode
        if (line.containsHeader(HEADER_DESC)) {
            String description = line.getString(HEADER_DESC);
            if (StringUtils.isNotBlank(description)) {
                paymentMode.addAttributeType("Description", "String", null, false);
            }
        }
                
        return paymentMode;
    }
}
