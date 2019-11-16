package org.openmrs.module.initializer.api.ot;

import org.openmrs.OrderType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderTypesLoader extends BaseCsvLoader<OrderType, OrderTypesCsvParser> {
	
	@Autowired
	public void setParser(OrderTypesCsvParser parser) {
		this.parser = parser;
	}
}
