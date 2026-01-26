package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.PaymentModeService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class PaymentModesCsvParser extends CsvParser<PaymentMode, BaseLineProcessor<PaymentMode>> {
	
	private final PaymentModeService paymentModeService;
	
	@Autowired
	public PaymentModesCsvParser(@Qualifier("paymentModeService") PaymentModeService paymentModeService,
	    PaymentModesLineProcessor processor) {
		super(processor);
		this.paymentModeService = paymentModeService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PAYMENT_MODES;
	}
	
	@Override
	public PaymentMode bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		PaymentMode paymentMode = paymentModeService.getPaymentModeByUuid(uuid);
		if (paymentMode == null) {
			paymentMode = new PaymentMode();
			if (StringUtils.isNotBlank(uuid)) {
				paymentMode.setUuid(uuid);
			}
		}
		return paymentMode;
	}
	
	@Override
	public PaymentMode save(PaymentMode instance) {
		return paymentModeService.savePaymentMode(instance);
	}
}
