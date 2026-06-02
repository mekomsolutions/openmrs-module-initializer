package org.openmrs.module.initializer.api.billing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.model.CashierItemPrice;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class CashierItemPriceLoader extends BaseCsvLoader<CashierItemPrice, CashierItemPriceCsvParser> {
	
	@Autowired
	public void setParser(CashierItemPriceCsvParser parser) {
		this.parser = parser;
	}
}
