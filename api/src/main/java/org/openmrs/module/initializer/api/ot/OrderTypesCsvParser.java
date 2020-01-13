package org.openmrs.module.initializer.api.ot;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderTypesCsvParser extends CsvParser<OrderType, BaseLineProcessor<OrderType>> {
	
	private OrderService orderService;
	
	@Autowired
	public OrderTypesCsvParser(@Qualifier("orderService") OrderService orderService, OrderTypeLineProcessor processor) {
		super(processor);
		this.orderService = orderService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ORDER_TYPES;
	}
	
	@Override
	public OrderType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		OrderType orderType = orderService.getOrderTypeByUuid(uuid);
		if (orderType == null) {
			orderType = orderService.getOrderTypeByName(line.getName());
		}
		if (orderType == null) {
			orderType = new OrderType();
			if (!StringUtils.isEmpty(uuid)) {
				orderType.setUuid(uuid);
			}
		}
		
		return orderType;
	}
	
	@Override
	public OrderType save(OrderType instance) {
		return orderService.saveOrderType(instance);
	}
}
