package org.openmrs.module.initializer.api.freq;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.OrderFrequency;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
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
	public OrderFrequency bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		OrderFrequency freq = orderService.getOrderFrequencyByUuid(uuid);
		if (freq == null) {
			freq = new OrderFrequency();
			if (!StringUtils.isEmpty(uuid)) {
				freq.setUuid(uuid);
			}
		}
		
		return freq;
	}
	
	@Override
	public OrderFrequency save(OrderFrequency instance) {
		return orderService.saveOrderFrequency(instance);
	}
}
