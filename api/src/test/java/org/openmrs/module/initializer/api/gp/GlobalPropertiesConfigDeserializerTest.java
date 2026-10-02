package org.openmrs.module.initializer.api.gp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.test.Verifies;

import com.thoughtworks.xstream.XStreamException;

public class GlobalPropertiesConfigDeserializerTest {
	
	@Test
	@Verifies(value = "should deserialize config", method = "fromXML(InputStream input)")
	public void shouldDeserializeConfig() {
		
		GlobalPropertiesConfig config = InitializerSerializer.getGlobalPropertiesConfig(
		    getClass().getClassLoader().getResourceAsStream("org/openmrs/module/initializer/include/gp.xml"));
		
		Assertions.assertEquals("addresshierarchy.i18nSupport", config.getGlobalProperties().get(0).getProperty());
		Assertions.assertEquals("true", (String) config.getGlobalProperties().get(0).getPropertyValue());
		Assertions.assertEquals("locale.allowed.list", config.getGlobalProperties().get(1).getProperty());
		Assertions.assertEquals("en, km_KH", (String) config.getGlobalProperties().get(1).getPropertyValue());
	}
	
	@Test
	@Verifies(value = "should deserialize config with unmapped fields", method = "fromXML(InputStream input)")
	public void shouldDeserializeConfigWithUnmappedFields() {
		
		GlobalPropertiesConfig config = InitializerSerializer.getGlobalPropertiesConfig(getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/gp_unmmaped_fields.xml"));
		
		Assertions.assertEquals("addresshierarchy.i18nSupport", config.getGlobalProperties().get(0).getProperty());
		Assertions.assertEquals("true", (String) config.getGlobalProperties().get(0).getPropertyValue());
		Assertions.assertEquals("locale.allowed.list", config.getGlobalProperties().get(1).getProperty());
		Assertions.assertEquals("en, km_KH", (String) config.getGlobalProperties().get(1).getPropertyValue());
	}
	
	@Test
	@Verifies(value = "should throw XStream exception on invalid config", method = "fromXML(InputStream input)")
	public void shouldThrowException() {
		assertThrows(XStreamException.class, () -> {
			
			InitializerSerializer.getGlobalPropertiesConfig(
			    getClass().getClassLoader().getResourceAsStream("org/openmrs/module/initializer/include/gp_error.xml"));
			
		});
	}
}
