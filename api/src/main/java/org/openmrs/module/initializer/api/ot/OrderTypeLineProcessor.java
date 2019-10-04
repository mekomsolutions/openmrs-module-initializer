package org.openmrs.module.initializer.api.ot;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.OrderType;
import org.openmrs.Privilege;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderTypeLineProcessor extends BaseLineProcessor<OrderType> {
	
	protected static class Helper {
		
		public static final OrderType PARENT_ORDER_TYPE = null;
		
		public OrderType getParentOrderType(String javaClassName, String uuid) {
			OrderType parentOrdertype = PARENT_ORDER_TYPE;
			if (javaClassName.equals("org.openmrs.Order")) {
				parentOrdertype = getParentOrderType(Context.getOrderService(), uuid);
			}
			// TODO verify if Context.getOrderService() is enough to handle more specific java class names (...like org.openmrs.DrugOrder)
			return parentOrdertype;
		}
		
		public static OrderType getParentOrderType(OrderService os, String uuid) {
			OrderType parentOrderType = os.getOrderTypeByUuid(uuid);
			if (parentOrderType != null) {
				return parentOrderType;
			} else {
				return PARENT_ORDER_TYPE;
			}
		}
		
		public Privilege getPrivilege(String privilege) throws IllegalArgumentException {
			try {
				return Context.getUserService().getPrivilege(privilege);
			}
			catch (APIException e) {
				throw new IllegalArgumentException("'" + privilege + "' is not a valid privilege.", e);
			}
		}
	}
	
	protected static String JAVA_CLASS_NAME = "java class name";
	
	protected static String PARENT_UUID = "parent uuid";
	
	protected Helper helper;
	
	private OrderService orderService;
	
	@Autowired
	public OrderTypeLineProcessor(@Qualifier("orderService") OrderService orderService) {
		this.orderService = orderService;
		this.helper = new Helper();
	}
	
	public void setHelper(Helper helper) {
		this.helper = helper;
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
		orderType.setDescription(line.get(HEADER_DESC));
		
		String javaClassName = line.get(JAVA_CLASS_NAME);
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
		
		String parentUuid = line.get(PARENT_UUID);
		if (!StringUtils.isEmpty(parentUuid)) {
			parentUuid = UUID.fromString(parentUuid).toString();
			orderType.setParent(helper.getParentOrderType(javaClassName, parentUuid));
		}
		
		return orderType;
	}
}
