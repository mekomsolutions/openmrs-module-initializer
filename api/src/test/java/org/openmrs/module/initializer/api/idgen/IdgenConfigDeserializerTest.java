package org.openmrs.module.initializer.api.idgen;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.test.Verifies;

import com.thoughtworks.xstream.XStream;

public class IdgenConfigDeserializerTest {
	
	@Test
	@Verifies(value = "should deserialize SequentialIdentifierGenerator", method = "fromXML(InputStream input)")
	public void shouldDeserializeSequentialIdentifierGenerator() throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream(
		    "org/openmrs/module/initializer/include/idgen_SequentialIdentifierGenerator.xml");
		
		XStream xs = InitializerSerializer.getIdgenConfigSerializer();
		
		Object obj = xs.fromXML(is);
		Assert.assertNotNull(obj);
		SequentialIdentifierGenerator src = (SequentialIdentifierGenerator) obj;
		
		Assert.assertEquals("Test sequential source name", src.getName());
		Assert.assertEquals("Test sequential source description", src.getDescription());
		Assert.assertEquals("001000", src.getFirstIdentifierBase());
		Assert.assertTrue(7 == src.getMinLength());
		Assert.assertTrue(7 == src.getMaxLength());
		Assert.assertEquals("0123456789", src.getBaseCharacterSet());
		Assert.assertFalse(src.isRetired()); // not <retired/> tag means retired=false
	}
	
	@Test
	@Verifies(value = "should deserialize IdentifierSource", method = "fromXML(InputStream input)")
	public void shouldDeserializeIdentifierSource() throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream(
		    "org/openmrs/module/initializer/include/idgen_IdentifierSource.xml");
		
		XStream xs = InitializerSerializer.getIdgenConfigSerializer();
		
		Object obj = xs.fromXML(is);
		Assert.assertNotNull(obj);
		IdentifierSource src = (IdentifierSource) obj;
		
		Assert.assertEquals("c1d90956-3f10-11e4-adec-0800271c1b75", src.getUuid());
		Assert.assertTrue(src.isRetired());
	}
	
	@Test
	@Verifies(value = "should deserialize identifier sources config", method = "fromXML(InputStream input)")
	public void shouldDeserializeConfig() throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream("org/openmrs/module/initializer/include/idgen.xml");
		
		XStream xs = InitializerSerializer.getIdgenConfigSerializer();
		
		Object obj = xs.fromXML(is);
		Assert.assertNotNull(obj);
		IdgenConfig cfg = (IdgenConfig) obj;
		
		Assert.assertTrue(cfg.getIdentifierSources().size() == 3);
		IdentifierSource src = null;
		src = cfg.getIdentifierSources().get(0);
		Assert.assertEquals("c1d8a345-3f10-11e4-adec-0800271c1b75", src.getUuid());
		Assert.assertTrue(src.isRetired());
		src = cfg.getIdentifierSources().get(1);
		Assert.assertEquals("c1d90956-3f10-11e4-adec-0800271c1b75", src.getUuid());
		Assert.assertTrue(src.isRetired());
		
		src = cfg.getIdentifierSources().get(2);
		Assert.assertEquals("Test sequential source name", src.getName());
		Assert.assertEquals("Test sequential source description", src.getDescription());
		Assert.assertNull(src.getUuid());
		Assert.assertFalse(src.isRetired()); // not <retired/> tag means retired=false
	}
}
