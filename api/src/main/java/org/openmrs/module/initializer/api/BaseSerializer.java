package org.openmrs.module.initializer.api;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Use this serializer instead of a bare {@link XStream} if you want to ignore unmapped fields.
 */
public class BaseSerializer extends XStream {
	
	public BaseSerializer() {
		super();
	}
	
	public BaseSerializer(HierarchicalStreamDriver hierarchicalStreamDriver) {
		super(hierarchicalStreamDriver);
	}
	
	@Override
	protected MapperWrapper wrapMapper(MapperWrapper next) {
		return new MapperWrapper(
		                         next) {
			
			@Override
			public boolean shouldSerializeMember(Class definedIn, String fieldName) {
				if (definedIn == Object.class) {
					//This is not compatible with implicit collections where item name is not defined
					return false;
				} else {
					return super.shouldSerializeMember(definedIn, fieldName);
				}
			}
		};
	}
}
