package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.CashierItemPriceService;
import org.openmrs.module.billing.api.model.CashierItemPrice;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class CashierItemPriceCsvParser extends CsvParser<CashierItemPrice, BaseLineProcessor<CashierItemPrice>> {
	
	private final CashierItemPriceService cashierItemPriceService;
	
	@Autowired
	public CashierItemPriceCsvParser(CashierItemPriceService cashierItemPriceService,
	    CashierItemPriceLineProcessor cashierItemPriceLineProcessor) {
		super(cashierItemPriceLineProcessor);
		this.cashierItemPriceService = cashierItemPriceService;
	}
	
	@Override
	public CashierItemPrice bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		
		CashierItemPrice cashierItemPrice = cashierItemPriceService.getCashierItemPriceByUuid(uuid);
		if (cashierItemPrice == null) {
			cashierItemPrice = new CashierItemPrice();
			if (StringUtils.isNotBlank(uuid)) {
				cashierItemPrice.setUuid(uuid);
			}
		}
		return cashierItemPrice;
	}
	
	@Override
	public CashierItemPrice save(CashierItemPrice instance) {
		return cashierItemPriceService.saveCashierItemPrice(instance);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CASHIER_ITEM_PRICES;
	}
}
