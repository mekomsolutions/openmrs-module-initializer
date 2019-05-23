package org.openmrs.module.initializer.api.freq;

import org.openmrs.OrderFrequency;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderFrequenciesCsvParser extends CsvParser<OrderFrequency, OrderService, BaseLineProcessor<OrderFrequency, OrderService>> {
	
	@Autowired
	public OrderFrequenciesCsvParser(@Qualifier("orderService") OrderService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ORDER_FREQUENCIES;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new OrderFrequencyLineProcessor(headerLine, service));
	}
	
	@Override
	protected OrderFrequency save(OrderFrequency instance) {
		return service.saveOrderFrequency(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(OrderFrequency instance) {
		return instance.isRetired();
	}
}
