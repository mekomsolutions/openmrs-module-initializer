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
    
    protected static final String HEADER_UUID = "uuid";
    
    protected static final String HEADER_NAME = "name";
    
    protected static final String HEADER_PRICE = "price";
    
    protected static final String HEADER_PAYMENT_MODE = "paymentMode";
    
    protected static final String HEADER_ITEM = "item";
    
    protected static final String HEADER_BILLABLE_SERVICE = "billableService";
    
    private final ConceptService conceptService;
    
    @Autowired
    public ServicePricesLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
        super();
        this.conceptService = conceptService;
    }
    
    @Override
    public PaymentMode fill(PaymentMode paymentMode, CsvLine line) throws IllegalArgumentException {
        if (line.containsHeader(HEADER_UUID)) {
            String uuid = line.getString(HEADER_UUID);
            if (StringUtils.isNotBlank(uuid)) {
                paymentMode.setUuid(uuid);
            }
        }
        paymentMode.setName(line.get(HEADER_NAME, true));
        
        if (line.containsHeader(HEADER_PRICE)) {
            String price = line.getString(HEADER_PRICE);
            if (StringUtils.isNotBlank(price)) {
                paymentMode.addAttributeType(HEADER_PRICE, "String", null, false);
            }
        }
        
        if (line.containsHeader(HEADER_PAYMENT_MODE)) {
            String paymentModeStr = line.getString(HEADER_PAYMENT_MODE);
            if (StringUtils.isNotBlank(paymentModeStr)) {
                paymentMode.addAttributeType(HEADER_PAYMENT_MODE, "String", null, false);
            }
        }
        
        if (line.containsHeader(HEADER_ITEM)) {
            String item = line.getString(HEADER_ITEM);
            if (StringUtils.isNotBlank(item)) {
                paymentMode.addAttributeType(HEADER_ITEM, "String", null, false);
            }
        }
        
        if (line.containsHeader(HEADER_BILLABLE_SERVICE)) {
            String billableService = line.getString(HEADER_BILLABLE_SERVICE);
            if (StringUtils.isNotBlank(billableService)) {
                paymentMode.addAttributeType(HEADER_BILLABLE_SERVICE, "String", null, false);
            }
        }
        
        return paymentMode;
    }
}
