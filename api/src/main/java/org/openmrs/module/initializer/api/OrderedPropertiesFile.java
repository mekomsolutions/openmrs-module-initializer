package org.openmrs.module.initializer.api;

import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.Properties;

/**
 * Reads the order of a properites file based on the value of a special code named "_order". For
 * example: _order = 100
 */
public class OrderedPropertiesFile extends OrderedFile {
	
	private Properties properties;
	
	public OrderedPropertiesFile(File file) {
		super(file.getAbsolutePath());
	}
	
	@Override
	protected Integer fetchOrder(File file) throws Exception {
		properties = new Properties();
		OpenmrsUtil.loadProperties(properties, file);
		String order = properties.getProperty("_order");
		if (order != null) {
			try {
				return Integer.valueOf(order);
			}
			catch (Exception e) {
				log.warn("_order property of '" + order + "' cannot be parsed to an Integer");
			}
		}
		return Integer.MAX_VALUE;
	}
	
	public Properties getProperties() {
		return properties;
	}
}
