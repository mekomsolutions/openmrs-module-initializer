package org.openmrs.module.initializer.api.billing;

import org.openmrs.module.billing.api.model.CashPoint;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class CashPointLoader extends BaseCsvLoader<CashPoint, CashPointCsvParser> {
	
	@Autowired
	public void setParser(CashPointCsvParser parser) {
		this.parser = parser;
	}
	
}
