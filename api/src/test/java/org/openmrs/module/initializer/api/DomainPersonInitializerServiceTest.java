/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer ptated at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;

public class DomainPersonInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("personService")
	private PersonService ps;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PERSONS;
	}
	
	@Before
	public void setup() {
		LocationService ls = Context.getLocationService();
		
		// person to edit
		{
			Person person = new Person();
			person.setUuid("a03e395c-b881-49b7-b6fc-983f6bddc7fc");
			person.addName(new PersonName("Frodo", null, "Baggins"));
			person.setGender("M");
			ps.savePerson(person);
		}
		// person to retire
		{
			Person person = new Person();
			person.setUuid("fbb1c87d-16d8-4d75-a205-53c6da4742f1");
			person.addName(new PersonName("Gandalf", "the", "Grey"));
			person.setGender("M");
			ps.savePerson(person);
		}
		
	}
	
	@Test
	public void loadPersons_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		getService().loadPersons();
		
		// Verify creation of Peregrin (Pippin), who has all fields, multiple addresses,
		// and multiple names, including whitespace
		{
			Person pt = ps.getPeople("Took", false).get(0);
			Set<PersonName> names = pt.getNames();
			for (PersonName name : names) {
				if (name.isPreferred()) {
					Assert.assertEquals("Pippin", name.getGivenName());
					Assert.assertNull(name.getMiddleName());
					Assert.assertEquals("-", name.getFamilyName());
				} else {
					Assert.assertEquals("Peregrin", name.getGivenName());
					Assert.assertNull(name.getMiddleName());
					Assert.assertEquals("Took", name.getFamilyName());
				}
			}
			Assert.assertEquals("M", pt.getGender());
			Date birthdate = new GregorianCalendar(1980, Calendar.FEBRUARY, 1).getTime();
			Assert.assertEquals(birthdate, pt.getBirthdate());
			Assert.assertThat(pt.getBirthdateEstimated(), is(false));
			Assert.assertEquals("The Shire", pt.getAddresses().iterator().next().getCityVillage());
		}
		// Verify creation of Meriadoc (Mary), who has no optional data
		{
			Person pt = ps.getPeople("Meriadoc", false).get(0);
			Assert.assertEquals("Meriadoc", pt.getGivenName());
			Assert.assertEquals("Brandybuck", pt.getFamilyName());
			Assert.assertEquals("M", pt.getGender());
			Assert.assertTrue(
			    String.format("Expected dateCreated to be within 10 seconds of now. Instead found %s", pt.getDateCreated()),
			    (new Date()).getTime() - pt.getDateCreated().getTime() < 10000);
		}
		
		// Verify edit
		{
			Person pt = ps.getPeople("Frodo", false).get(0);
			Assert.assertEquals("Mad Dog", pt.getMiddleName());
			Assert.assertEquals("The Shire", pt.getAddresses().iterator().next().getCityVillage());
			Assert.assertThat(pt.getBirthdateEstimated(), is(true));
		}
		// Verif retire
		{
			Person pt = ps.getPersonByUuid("fbb1c87d-16d8-4d75-a205-53c6da4742f1");
			Assert.assertThat(pt.isVoided(), is(true));
			log.warn(String.format("Pt is voided: %s", pt.isVoided()));
			log.warn(String.format("Pt is personVoided: %s", pt.isPersonVoided()));
		}
	}
}
