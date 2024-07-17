package org.openmrs.module.initializer.api.billing;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "billing:1.1.0 - 9.*" })
public class BillableServiceLoader extends BaseCsvLoader<BillableService, BillableServiceCsvParser> {
	
	@Autowired
	public void setParser(BillableServiceCsvParser parser) {
		this.parser = parser;
	}
}
