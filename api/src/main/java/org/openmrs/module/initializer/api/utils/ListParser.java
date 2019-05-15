package org.openmrs.module.initializer.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.initializer.api.BaseLineProcessor;

public abstract class ListParser<T extends BaseOpenmrsObject> {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * There are cases where we might want to ensure that the object that we will attempt to fetch does
	 * indeed exist. In that case this is the place to process to a last min. save based on whatever
	 * sufficient information that might be embedded in the identifier. Eg. Assuming the identifier is
	 * the <i>name</i> of a location tag, this could for example create and save the location tag with
	 * that name, if none can be found already.
	 * 
	 * @param id The object string identifier.
	 * @return The saved object.
	 */
	protected T lastMinuteSave(String id) {
		return null;
	};
	
	protected abstract T fetch(String id);
	
	/**
	 * Parses a string that contains a list of identifiers to objects then fetches them.
	 * 
	 * @param listString The list of object identifiers. Example with concepts: ["cambodia:123";
	 *            "a92bf372-2fca-11e7-93ae-92361f002671"; "CONCEPT_FULLY_SPECIFIED_NAME"]
	 * @return The list of entities that have been found, null if any error(s).
	 * @throws IllegalArgumentException If anything went wrong when parsing the list.
	 */
	public List<T> parseList(String listString) throws IllegalArgumentException {
		
		if (StringUtils.isEmpty(listString)) {
			return Collections.emptyList();
		}
		
		List<T> elements = new ArrayList<T>();
		
		String[] parts = listString.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String id : parts) {
			id = id.trim();
			
			T element = fetch(id);
			
			if (element == null) {
				element = lastMinuteSave(id);
			}
			
			if (element != null) {
				elements.add(element);
			} else {
				throw new IllegalArgumentException("The object identified by '" + id
				        + "' could not be found in database. The parent or encompassing object referencing the following list was therefore not created/updated: ["
				        + listString + "].");
			}
		}
		
		return elements;
	}
}
