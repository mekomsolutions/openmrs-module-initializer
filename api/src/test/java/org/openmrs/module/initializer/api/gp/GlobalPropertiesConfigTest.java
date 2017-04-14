package org.openmrs.module.initializer.api.gp;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.module.initializer.api.BaseSerializer;
import org.openmrs.test.Verifies;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class GlobalPropertiesConfigTest {
	
	private XStream xs;
	
	@Before
	public void setup() throws IOException {
		
		xs = new BaseSerializer();
		xs.alias("config", GlobalPropertiesConfig.class);
		xs.alias("globalProperty", GlobalProperty.class);
		xs.aliasField("value", GlobalProperty.class, "propertyValue");
		
	}
	
	@Test
	@Verifies(value = "should", method = "fromXML(InputStream input)")
	public void shouldDeserializeRegularConfig() {
		
		Object obj = xs.fromXML(getClass().getClassLoader().getResourceAsStream(
		    "org/openmrs/module/initializer/include/gp.xml"));
		GlobalPropertiesConfig config = (GlobalPropertiesConfig) obj;
		
		Assert.assertEquals("addresshierarchy.i18nSupport", config.getGlobalProperties().get(0).getProperty());
		Assert.assertEquals("true", (String) config.getGlobalProperties().get(0).getPropertyValue());
		Assert.assertEquals("locale.allowed.list", config.getGlobalProperties().get(1).getProperty());
		Assert.assertEquals("en, km_KH", (String) config.getGlobalProperties().get(1).getPropertyValue());
	}
	
	@Test
	@Verifies(value = "should", method = "fromXML(InputStream input)")
	public void shouldDeserializeConfigWithUnmappedFields() {
		
		Object obj = xs.fromXML(getClass().getClassLoader().getResourceAsStream(
		    "org/openmrs/module/initializer/include/gp_unmmaped_fields.xml"));
		GlobalPropertiesConfig config = (GlobalPropertiesConfig) obj;
		
		Assert.assertEquals("addresshierarchy.i18nSupport", config.getGlobalProperties().get(0).getProperty());
		Assert.assertEquals("true", (String) config.getGlobalProperties().get(0).getPropertyValue());
		Assert.assertEquals("locale.allowed.list", config.getGlobalProperties().get(1).getProperty());
		Assert.assertEquals("en, km_KH", (String) config.getGlobalProperties().get(1).getPropertyValue());
	}
	
	@Test(expected = XStreamException.class)
	@Verifies(value = "should", method = "fromXML(InputStream input)")
	public void shouldThrowException() {
		
		xs.fromXML(getClass().getClassLoader().getResourceAsStream("org/openmrs/module/initializer/include/gp_error.xml"));
	}
}
