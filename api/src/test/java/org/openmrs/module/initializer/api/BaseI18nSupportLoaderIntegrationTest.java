package org.openmrs.module.initializer.api;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseI18nSupportLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private InitializerService iniz;
	
	@Autowired
	@Qualifier("encounterService")
	private EncounterService es;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	protected InitializerMessageSource inizSrc;
	
	private Locale localeEn = Locale.ENGLISH;
	
	private Locale localeKm = new Locale("km", "KH");
	
	@Before
	public void setUp() throws Exception {
		inizSrc = (InitializerMessageSource) Context.getMessageSourceService().getActiveMessageSource();
		executeDataSet("testdata/test-metadata.xml");
		
		// Testing on EncounterTypes and Locations domains
		for (Loader loader : iniz.getLoaders()) {
			if (loader.getDomainName().equals(Domain.ENCOUNTER_TYPES.getName())
			        || loader.getDomainName().equals(Domain.LOCATIONS.getName())) {
				loader.load();
			}
		}
	}
	
	@Test
	public void loadI18nMessages_shouldLoadI18nMessagePropertiesOnCscLoaderDomains() {
		// setup
		EncounterType et = es.getEncounterType("Triage Encounter");
		String etUuid = et.getUuid();
		Location loc = ls.getLocation("The Lake Clinic-Cambodia");
		String locUuid = loc.getUuid();
		
		// verify EncounterTypes domain i18n
		Assert.assertEquals("Triage Encounter (translated)",
		    Context.getMessageSourceService().getMessage("ui.i18n.EncounterType.name." + etUuid, null, localeEn));
		Assert.assertEquals("ទ្រីយ៉ាហ្គេនស៊ើរ",
		    Context.getMessageSourceService().getMessage("ui.i18n.EncounterType.name." + etUuid, null, localeKm));
		Assert.assertEquals("Medical History Encounter (translated)", Context.getMessageSourceService()
		        .getMessage("ui.i18n.EncounterType.name.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeEn));
		Assert.assertEquals("ប្រវត្តិសាស្រ្តវេជ្ជសាស្រ្ត", Context.getMessageSourceService()
		        .getMessage("ui.i18n.EncounterType.name.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeKm));
		
		Assert.assertEquals("Triage Encounter (translated)",
		    Context.getMessageSourceService().getMessage("org.openmrs.EncounterType." + etUuid, null, localeEn));
		Assert.assertEquals("ទ្រីយ៉ាហ្គេនស៊ើរ",
		    Context.getMessageSourceService().getMessage("org.openmrs.EncounterType." + etUuid, null, localeKm));
		Assert.assertEquals("Medical History Encounter (translated)", Context.getMessageSourceService()
		        .getMessage("org.openmrs.EncounterType.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeEn));
		Assert.assertEquals("ប្រវត្តិសាស្រ្តវេជ្ជសាស្រ្ត", Context.getMessageSourceService()
		        .getMessage("org.openmrs.EncounterType.aaa1a367-3047-4833-af27-b30e2dac9028", null, localeKm));
		
		// verify Locations domain i18n
		Assert.assertEquals("The Lake Clinic-Cambodia (translated)",
		    Context.getMessageSourceService().getMessage("ui.i18n.Location.name." + locUuid, null, localeEn));
		Assert.assertEquals("គ្លីនីកគ្លីនិក - ប្រទេសកម្ពុជា",
		    Context.getMessageSourceService().getMessage("ui.i18n.Location.name." + locUuid, null, localeKm));
		Assert.assertEquals("Acme Clinic (translated)", Context.getMessageSourceService()
		        .getMessage("ui.i18n.Location.name.a03e395c-b881-49b7-b6fc-983f6bddc7fc", null, localeEn));
		Assert.assertEquals("គ្លីនិកអាមី", Context.getMessageSourceService()
		        .getMessage("ui.i18n.Location.name.a03e395c-b881-49b7-b6fc-983f6bddc7fc", null, localeKm));
		
		Assert.assertEquals("The Lake Clinic-Cambodia (translated)",
		    Context.getMessageSourceService().getMessage("org.openmrs.Location." + locUuid, null, localeEn));
		Assert.assertEquals("គ្លីនីកគ្លីនិក - ប្រទេសកម្ពុជា",
		    Context.getMessageSourceService().getMessage("org.openmrs.Location." + locUuid, null, localeKm));
		Assert.assertEquals("Acme Clinic (translated)", Context.getMessageSourceService()
		        .getMessage("org.openmrs.Location.a03e395c-b881-49b7-b6fc-983f6bddc7fc", null, localeEn));
		Assert.assertEquals("គ្លីនិកអាមី", Context.getMessageSourceService()
		        .getMessage("org.openmrs.Location.a03e395c-b881-49b7-b6fc-983f6bddc7fc", null, localeKm));
	}
}
