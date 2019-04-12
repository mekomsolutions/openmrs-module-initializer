package org.openmrs.module.initializer.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class UtilsTest {
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(Context.class);
		AdministrationService as = mock(AdministrationService.class);
		when(Context.getAdministrationService()).thenReturn(as);
		when(as.getAllowedLocales()).thenReturn(Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN));
		when(Context.getLocale()).thenReturn(Locale.ENGLISH);
	}
	
	@Test
	public void getBestMatchName_shouldReturnBestMatchForConceptName() throws Exception {
		
		Concept c = new Concept();
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("A name in English");
			cn.setLocale(Locale.ENGLISH);
			c.addName(cn);
		}
		
		Assert.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assert.assertEquals("A name in English", Utils.getBestMatchName(c, Locale.GERMAN));
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("An FSN in English");
			cn.setLocalePreferred(true);
			cn.setLocale(Locale.ENGLISH);
			c.setFullySpecifiedName(cn);
		}
		
		Assert.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assert.assertEquals("An FSN in English", Utils.getBestMatchName(c, Locale.GERMAN));
		
		{
			ConceptName cn = new ConceptName();
			cn.setName("A preferred name in English");
			cn.setLocalePreferred(true);
			cn.setLocale(Locale.ENGLISH);
			c.addName(cn);
		}
		
		Assert.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals(c.getPreferredName(Locale.ENGLISH).getName(), Utils.getBestMatchName(c, Locale.ENGLISH));
		Assert.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.FRENCH));
		Assert.assertEquals("A preferred name in English", Utils.getBestMatchName(c, Locale.GERMAN));
	}
}
