package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:1.1.0 - 9.*" })
public class ServicePricesLineProcessor extends BaseLineProcessor<PaymentMode> {
    
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
        paymentMode.setUuid(line.get(HEADER_UUID, true));
        paymentMode.setName(line.get(HEADER_NAME, true));
        processAttribute(line, HEADER_PRICE, paymentMode);
        processAttribute(line, HEADER_PAYMENT_MODE, paymentMode);
        processAttribute(line, HEADER_ITEM, paymentMode);
        processAttribute(line, HEADER_BILLABLE_SERVICE, paymentMode);
        
        return paymentMode;
    }
    
    private void processAttribute(CsvLine line, String header, PaymentMode paymentMode) {
        String value = line.getString(header);
        if (StringUtils.isNotBlank(value)) {
            paymentMode.addAttributeType(header, "String", null, false);
        }
    }
}
