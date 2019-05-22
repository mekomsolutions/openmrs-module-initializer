package org.openmrs.module.initializer.api.freq;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.OrderFrequency;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;

public class OrderFrequenciesCsvParser extends CsvParser<OrderFrequency, OrderService, BaseLineProcessor<OrderFrequency, OrderService>> {
	
	public OrderFrequenciesCsvParser(InputStream is, OrderService service) throws IOException {
		super(is, service);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ORDER_FREQUENCIES;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new OrderFrequencyLineProcessor(headerLine, service));
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
