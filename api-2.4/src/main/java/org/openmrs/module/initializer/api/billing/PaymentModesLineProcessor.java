package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(modules = { "billing:1.1.0 - 9.*" })
public class PaymentModesLineProcessor extends BaseLineProcessor<PaymentMode> {
	
	protected static final String HEADER_ATTRIBUTES = "attributes";
	
	public PaymentModesLineProcessor() {
		super();
	}
	
	@Override
	public PaymentMode fill(PaymentMode paymentMode, CsvLine line) throws IllegalArgumentException {
		paymentMode.setName(line.get(HEADER_NAME, true));
		
		String attributes = line.get(HEADER_ATTRIBUTES, false);
		if (StringUtils.isNotBlank(attributes)) {
			if (paymentMode.getAttributeTypes() != null) {
				paymentMode.getAttributeTypes().clear();
			}
			for (String attribute : attributes.split(BaseLineProcessor.LIST_SEPARATOR)) {
				String[] parts = attribute.trim().split("::");
				if (parts.length > 3) {
					paymentMode.addAttributeType(parts[0].trim(), parts[1].trim(), parts[2].trim(),
					    Boolean.parseBoolean(parts[3].trim()));
				} else if (parts.length > 2) {
					paymentMode.addAttributeType(parts[0].trim(), parts[1].trim(), parts[2].trim(), false);
				} else if (parts.length > 1) {
					paymentMode.addAttributeType(parts[0].trim(), parts[1].trim(), null, false);
				} else {
					paymentMode.addAttributeType(parts[0].trim(), null, null, false);
				}
			}
		}
		
		return paymentMode;
	}
}
