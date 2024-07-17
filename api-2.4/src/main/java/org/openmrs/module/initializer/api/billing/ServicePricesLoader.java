package org.openmrs.module.initializer.api.billing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "billing:1.1.0 - 9.*" })
public class ServicePricesLoader extends BaseCsvLoader<PaymentMode, ServicePricesCsvParser> {
	
	@Autowired
	public void setParser(ServicePricesCsvParser parser) {
		this.parser = parser;
	}
}
