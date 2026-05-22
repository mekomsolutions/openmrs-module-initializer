package org.openmrs.module.initializer.api.billing;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.BillableServiceService;
import org.openmrs.module.billing.api.PaymentModeService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.CashierItemPrice;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.stockmanagement.api.StockManagementService;
import org.openmrs.module.stockmanagement.api.model.StockItem;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class CashierItemPriceLineProcessor extends BaseLineProcessor<CashierItemPrice> {
	
	protected static final String HEADER_NAME = "name";
	
	protected static final String HEADER_PRICE = "price";
	
	protected static final String HEADER_PAYMENT_MODE = "payment mode";
	
	protected static final String HEADER_STOCK_ITEM = "stock item";
	
	protected static final String HEADER_BILLABLE_SERVICE = "billable service";
	
	private final PaymentModeService paymentModeService;
	
	private final StockManagementService stockManagementService;
	
	private final BillableServiceService billableServiceService;
	
	@Autowired
	public CashierItemPriceLineProcessor(PaymentModeService paymentModeService,
	    StockManagementService stockManagementService, BillableServiceService billableServiceService) {
		this.paymentModeService = paymentModeService;
		this.stockManagementService = stockManagementService;
		this.billableServiceService = billableServiceService;
	}
	
	@Override
	public CashierItemPrice fill(CashierItemPrice instance, CsvLine line) throws IllegalArgumentException {
		instance.setName(line.get(HEADER_NAME, true));
		
		instance.setPrice(new BigDecimal(line.get(HEADER_PRICE, true)));
		
		String paymentModeUuid = line.get(HEADER_PAYMENT_MODE, true);
		PaymentMode paymentMode = paymentModeService.getPaymentModeByUuid(paymentModeUuid);
		if (paymentMode == null) {
			throw new IllegalArgumentException("No PaymentMode found with UUID '" + paymentModeUuid + "'");
		}
		instance.setPaymentMode(paymentMode);
		
		String stockItemUuid = line.getString(HEADER_STOCK_ITEM);
		String billableServiceUuid = line.getString(HEADER_BILLABLE_SERVICE);
		boolean hasStockItem = StringUtils.isNotBlank(stockItemUuid);
		boolean hasBillableService = StringUtils.isNotBlank(billableServiceUuid);
		if (hasStockItem == hasBillableService) {
			throw new IllegalArgumentException("Exactly one of '" + HEADER_STOCK_ITEM + "' or '" + HEADER_BILLABLE_SERVICE
			        + "' must be set on each row");
		}
		
		if (hasStockItem) {
			StockItem stockItem = stockManagementService.getStockItemByUuid(stockItemUuid);
			if (stockItem == null) {
				throw new IllegalArgumentException("No StockItem found with UUID '" + stockItemUuid + "'");
			}
			instance.setItem(stockItem);
		} else {
			BillableService billableService = billableServiceService.getBillableServiceByUuid(billableServiceUuid);
			if (billableService == null) {
				throw new IllegalArgumentException("No BillableService found with UUID '" + billableServiceUuid + "'");
			}
			instance.setBillableService(billableService);
		}
		
		return instance;
	}
}
