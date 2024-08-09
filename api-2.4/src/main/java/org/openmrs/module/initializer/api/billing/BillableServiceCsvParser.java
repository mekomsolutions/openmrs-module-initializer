package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.IBillableItemsService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:1.1.0 - 9.*" })
public class BillableServiceCsvParser extends CsvParser<BillableService, BaseLineProcessor<BillableService>> {
	
	private final IBillableItemsService billableItemsService;
	
	private final BillableServicesLineProcessor billableServicesLineProcessor;
	
	@Autowired
	public BillableServiceCsvParser(@Qualifier("billableItemsService") IBillableItemsService billableItemsService,
	    BillableServicesLineProcessor billableServicesLineProcessor) {
		super(billableServicesLineProcessor);
		this.billableItemsService = billableItemsService;
		this.billableServicesLineProcessor = billableServicesLineProcessor;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.BILLABLE_SERVICES;
	}
	
	@Override
	public BillableService bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		
		BillableService billableService = billableItemsService.getByUuid(uuid);
		
		if (billableService == null) {
			billableService = new BillableService();
			if (!StringUtils.isEmpty(uuid)) {
				billableService.setUuid(uuid);
			}
		}
		
		return billableService;
	}
	
	@Override
	public BillableService save(BillableService instance) {
		return billableItemsService.save(instance);
	}
	
	@Override
	protected void setLineProcessors(String version) {
		lineProcessors.clear();
		lineProcessors.add(billableServicesLineProcessor);
	}
}
