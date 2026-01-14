package org.openmrs.module.initializer.api.billing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.model.CashPoint;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class CashPointsLoader extends BaseCsvLoader<CashPoint, CashPointsCsvParser> {
	
	@Autowired
	public void setParser(CashPointsCsvParser parser) {
		this.parser = parser;
	}
	
}
