package org.openmrs.module.initializer.api.gp;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.test.Verifies;

import com.thoughtworks.xstream.XStreamException;

public class GlobalPropertiesConfigDeserializerTest {
	
	@Test
	@Verifies(value = "should deserialize config", method = "fromXML(InputStream input)")
	public void shouldDeserializeConfig() {
		
		GlobalPropertiesConfig config = InitializerSerializer.getGlobalPropertiesConfig(getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/gp.xml"));
		
		Assert.assertEquals("addresshierarchy.i18nSupport", config.getGlobalProperties().get(0).getProperty());
		Assert.assertEquals("true", (String) config.getGlobalProperties().get(0).getPropertyValue());
		Assert.assertEquals("locale.allowed.list", config.getGlobalProperties().get(1).getProperty());
		Assert.assertEquals("en, km_KH", (String) config.getGlobalProperties().get(1).getPropertyValue());
	}
	
	@Test
	@Verifies(value = "should deserialize config with unmapped fields", method = "fromXML(InputStream input)")
	public void shouldDeserializeConfigWithUnmappedFields() {
		
		GlobalPropertiesConfig config = InitializerSerializer.getGlobalPropertiesConfig(getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/gp_unmmaped_fields.xml"));
		
		Assert.assertEquals("addresshierarchy.i18nSupport", config.getGlobalProperties().get(0).getProperty());
		Assert.assertEquals("true", (String) config.getGlobalProperties().get(0).getPropertyValue());
		Assert.assertEquals("locale.allowed.list", config.getGlobalProperties().get(1).getProperty());
		Assert.assertEquals("en, km_KH", (String) config.getGlobalProperties().get(1).getPropertyValue());
	}
	
	@Test(expected = XStreamException.class)
	@Verifies(value = "should throw XStream exception on invalid config", method = "fromXML(InputStream input)")
	public void shouldThrowException() {
		
		InitializerSerializer.getGlobalPropertiesConfig(getClass().getClassLoader().getResourceAsStream(
		    "org/openmrs/module/initializer/include/gp_error.xml"));
	}
}
