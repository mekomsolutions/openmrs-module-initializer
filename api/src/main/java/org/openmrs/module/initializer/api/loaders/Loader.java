package org.openmrs.module.initializer.api.loaders;

import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openmrs.module.initializer.api.ConfigDirUtil;

/**
 * A loader is an instance able to load the metadata associated with a domain. It is pointing to a
 * given subfolder of the configuration folder through configuring an instance of
 * {@link ConfigDirUtil}. It carries an order of priority, loaders will be processed in order. The
 * lower the order the higher the priority. All loaders should extend {@link BaseLoader} rather than
 * implementing the current interface directly.
 */
public interface Loader extends Comparable<Loader> {
	
	/**
	 * @return The domain name for the domain covered by the loader.
	 */
	String getDomainName();
	
	/**
	 * @return The order of the loader.
	 */
	Integer getOrder();
	
	/**
	 * @return The dir util for domain folder of the loader.
	 */
	ConfigDirUtil getDirUtil();
	
	/**
	 * Triggers the processing (or "loading") of the domain's files.
	 * 
	 * @param wildcardExclusions A list of wildcard file filter patterns to filter out unwanted files.
	 * @see WildcardFileFilter
	 */
	void load(List<String> wildcardExclusions);
	
	/**
	 * @see #load(List)
	 */
	void load();
	
}
