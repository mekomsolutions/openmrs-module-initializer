package org.openmrs.module.initializer.api.ot;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderTypeLineProcessor extends BaseLineProcessor<OrderType> {
	
	protected static String JAVA_CLASS_NAME = "java class name";
	
	private OrderService orderService;
	
	@Autowired
	public OrderTypeLineProcessor(@Qualifier("orderService") OrderService orderService) {
		this.orderService = orderService;
	}
	
	@Override
	protected OrderType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		OrderType orderType = orderService.getOrderTypeByUuid(uuid);
		if (orderType == null) {
			orderType = new OrderType();
			if (!StringUtils.isEmpty(uuid)) {
				orderType.setUuid(uuid);
			}
		}
		
		return orderType;
	}
	
	@Override
	protected OrderType fill(OrderType orderType, CsvLine line) throws IllegalArgumentException {
		
		orderType.setName(line.get(HEADER_NAME));
		orderType.setDescription(line.getString(HEADER_DESC, ""));
		
		String javaClassName = line.getString(JAVA_CLASS_NAME, Order.class.getName());
		if (!StringUtils.isEmpty(javaClassName)) {
			try {
				Class.forName(javaClassName);
			}
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(
				        "'" + javaClassName + "' does not represent a valid Java or OpenMRS class.", e);
			}
			orderType.setJavaClassName(javaClassName);
		}
		
		String lookup = line.getString(PARENT,"");
		if (!StringUtils.isEmpty(lookup)) {
			orderType.setParent(Utils.getParentOrderType(orderService, javaClassName, lookup));
		}
		
		return orderType;
	}
}
