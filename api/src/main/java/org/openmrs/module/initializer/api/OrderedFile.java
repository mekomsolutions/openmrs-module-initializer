package org.openmrs.module.initializer.api;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for ordered files. While this class provides the needed structure, it does not provide
 * any actual implementation to fetch the order information from the file. In other words this base
 * class doesn't produce any predictable ordering between {@link OrderedFile} instances.
 */
public class OrderedFile extends File {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected Integer order = Integer.MAX_VALUE;
	
	public OrderedFile(String pathname) {
		super(pathname);
		try {
			order = fetchOrder(this);
		}
		catch (UnsupportedOperationException e) {
			log.warn(e.getMessage());
		}
		catch (Exception e) {
			log.error("There was an error while attempting to read the loading order of a configuration file: "
			        + this.getAbsolutePath(),
			    e);
		}
	}
	
	public OrderedFile(File file) {
		this(file.getAbsolutePath());
	}
	
	/*
	 * Override this method to actively fetch the order from a given configuration file. 
	 */
	protected Integer fetchOrder(File file) throws Exception {
		throw new UnsupportedOperationException(
		        getClass().getSimpleName() + " does not provide an implementation to fetch the loading order from files.");
	}
	
	public Integer getOrder() {
		return order;
	}
	
	@Override
	public int compareTo(File that) {
		if (that instanceof OrderedFile) {
			return this.getOrder().compareTo(((OrderedFile) that).getOrder());
		} else {
			// if 'that' is not ordered then it is assumed to be last
			return this.getOrder().compareTo(Integer.MAX_VALUE);
		}
	}
	
}
