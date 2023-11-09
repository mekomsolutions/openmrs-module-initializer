package org.openmrs.module.initializer.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Base class for ordered files. While this class provides the needed structure, it does not provide
 * any actual implementation to fetch the order information from the file. In other words this base
 * class doesn't produce any predictable ordering between {@link OrderedFile} instances.
 */
public class OrderedFile extends File {
	
	private static final long serialVersionUID = 1L;
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected Integer order = Integer.MAX_VALUE;
	
	public OrderedFile(String pathname) {
		super(pathname);
		try {
			order = fetchOrder(this);
		}
		catch (UnsupportedOperationException e) {
			log.debug(e.getMessage());
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
		int result = 0;
		if (that instanceof OrderedFile) {
			result = this.getOrder().compareTo(((OrderedFile) that).getOrder());
		} else {
			// if 'that' is not ordered then it is assumed to be last
			result = this.getOrder().compareTo(Integer.MAX_VALUE);
		}
		// If neither ordered file has an explicit order defined, default to ordering by filename
		if (result == 0) {
			result = this.getAbsolutePath().compareTo(that.getAbsolutePath());
		}
		return result;
	}
	
}
