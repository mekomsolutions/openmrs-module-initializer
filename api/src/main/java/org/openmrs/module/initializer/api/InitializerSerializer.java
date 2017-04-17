package org.openmrs.module.initializer.api;

import java.io.InputStream;

import org.openmrs.GlobalProperty;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesConfig;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Use this serializer instead of a bare {@link XStream} if you want to ignore unmapped fields.
 */
public class InitializerSerializer extends XStream {
	
	public InitializerSerializer() {
		super();
	}
	
	public InitializerSerializer(HierarchicalStreamDriver hierarchicalStreamDriver) {
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
	
	public static XStream getGlobalPropertiesConfigSerializer() {
		final XStream xstream = new InitializerSerializer();
		xstream.alias("config", GlobalPropertiesConfig.class);
		xstream.alias("globalProperty", GlobalProperty.class);
		xstream.aliasField("value", GlobalProperty.class, "propertyValue");
		return xstream;
	}
	
	public static GlobalPropertiesConfig getGlobalPropertiesConfig(InputStream is) {
		return (GlobalPropertiesConfig) getGlobalPropertiesConfigSerializer().fromXML(is);
	}
}
