package org.openmrs.module.initializer.api;

import java.io.InputStream;

import org.openmrs.GlobalProperty;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesConfig;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
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
		return new MapperWrapper(next) {
			
			@Override
			public boolean shouldSerializeMember(Class definedIn, String fieldName) {
				if (definedIn == Object.class) {
					// This is not compatible with implicit collections where item name is not
					// defined
					return false;
				} else {
					return super.shouldSerializeMember(definedIn, fieldName);
				}
			}
		};
	}
	
	/**
	 * @return Deserializer for {@link GlobalPropertiesConfig}
	 */
	public static XStream getGlobalPropertiesConfigSerializer() {
		final XStream xs = new InitializerSerializer();
		xs.alias("config", GlobalPropertiesConfig.class);
		xs.alias("globalProperty", GlobalProperty.class);
		xs.aliasField("value", GlobalProperty.class, "propertyValue");
		return xs;
	}
	
	public static GlobalPropertiesConfig getGlobalPropertiesConfig(InputStream is) {
		return (GlobalPropertiesConfig) getGlobalPropertiesConfigSerializer().fromXML(is);
	}
	
	/**
	 * XStream converter that ensures that missing <retired/> tags lead to retired set to false on
	 * children instances of {@link IdentifierSource}.
	 */
	public static class IdentifierSourceConverter extends ReflectionConverter {
		
		public IdentifierSourceConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
			super(mapper, reflectionProvider);
		}
		
		@Override
		public boolean canConvert(Class type) {
			return IdentifierSource.class.isAssignableFrom(type);
		}
		
		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			IdentifierSource src = (IdentifierSource) super.unmarshal(reader, context);
			if (src.isRetired() == null) {
				src.setRetired(false);
			}
			return src;
		}
	}
}
