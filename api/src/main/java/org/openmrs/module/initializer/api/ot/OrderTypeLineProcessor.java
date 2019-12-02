package org.openmrs.module.initializer.api.ot;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptClass;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptClassListParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderTypeLineProcessor extends BaseLineProcessor<OrderType> {
	
	protected static String JAVA_CLASS_NAME = "java class name";
	
	protected static String HEADER_CONCEPT_CLASSES = "concept classes";
	
	private OrderService orderService;
	
	private ConceptClassListParser conceptClassListParser;
	
	@Autowired
	public OrderTypeLineProcessor(@Qualifier("orderService") OrderService orderService,
	    ConceptClassListParser conceptClassListParser) {
		this.orderService = orderService;
		this.conceptClassListParser = conceptClassListParser;
	}
	
	@Override
	protected OrderType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		OrderType orderType = orderService.getOrderTypeByUuid(uuid);
		if (orderType == null) {
			orderType = orderService.getOrderTypeByName(line.get(HEADER_NAME));
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
		
		String parentIdentifier = line.getString(PARENT, "");
		if (!StringUtils.isEmpty(parentIdentifier)) {
			orderType.setParent(Utils.getParentOrderType(orderService, javaClassName, parentIdentifier));
		}
		
		String conceptClassesStr = line.getString(HEADER_CONCEPT_CLASSES, "");
		if (!StringUtils.isEmpty(conceptClassesStr)) {
			orderType.setConceptClasses(
			    new HashSet<ConceptClass>(conceptClassListParser.parseList(line.get(HEADER_CONCEPT_CLASSES))));
		}
		
		return orderType;
	}
}
