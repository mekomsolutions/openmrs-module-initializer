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
public class OrderFrequenciesCsvParser extends CsvParser<OrderFrequency, BaseLineProcessor<OrderFrequency>> {
	
	private OrderService orderService;
	
	@Autowired
	public OrderFrequenciesCsvParser(@Qualifier("orderService") OrderService orderService,
	    OrderFrequencyLineProcessor processor) {
		super(processor);
		this.orderService = orderService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ORDER_FREQUENCIES;
	}
	
	@Override
	protected OrderFrequency save(OrderFrequency instance) {
		return orderService.saveOrderFrequency(instance);
	}
}
