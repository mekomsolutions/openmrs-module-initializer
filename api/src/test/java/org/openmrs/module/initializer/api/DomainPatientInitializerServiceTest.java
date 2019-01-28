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
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;

public class DomainPatientInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("patientService")
	private PatientService ps;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_PATIENTS;
	}
	
	@Before
	public void setup() {
		LocationService ls = Context.getLocationService();
		PatientIdentifierType pit = ps.getPatientIdentifierTypeByName("Old Identification Number");
		Location xanadu = ls.getLocation("Xanadu");
		
		// patient to edit
		{
			Patient pt = new Patient();
			pt.setUuid("a03e395c-b881-49b7-b6fc-983f6bddc7fc");
			pt.addName(new PersonName("Frodo", null, "Baggins"));
			PatientIdentifier pid = new PatientIdentifier("0001", pit, xanadu);
			pid.setPreferred(true);
			pt.addIdentifier(pid);
			pt.setGender("M");
			pt.setDateCreated(getDate(2019, Calendar.JANUARY, 2, 0, 0, 0, 0));
			ps.savePatient(pt);
		}
		// patient to retire
		{
			Patient pt = new Patient();
			pt.setUuid("fbb1c87d-16d8-4d75-a205-53c6da4742f1");
			pt.addName(new PersonName("Gandalf", "the", "Grey"));
			PatientIdentifier pid = new PatientIdentifier("0002", pit, xanadu);
			pid.setPreferred(true);
			pt.addIdentifier(pid);
			pt.setGender("M");
			ps.savePatient(pt);
		}
		
	}
	
	@Test
	public void loadPatients_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		getService().loadPatients();
		
		// Verify creation of Peregrin (Pippin), who has all fields, multiple addresses,
		// and multiple names, including whitespace
		{
			Patient pt = ps.getPatients("Took").get(0);
			PatientIdentifierType pit = ps.getPatientIdentifierTypeByName("Old Identification Number");
			Assert.assertEquals("0003", pt.getPatientIdentifier(pit).getIdentifier());
			Assert.assertEquals("Xanadu", pt.getPatientIdentifier(pit).getLocation().getName());
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
			Date creationDate = getDate(2019, Calendar.JANUARY, 10, 0, 0, 0, 0);
			Assert.assertEquals(creationDate, pt.getDateCreated());
			Assert.assertEquals("The Shire", pt.getAddresses().iterator().next().getCityVillage());
		}
		// Verify creation of Meriadoc (Mary), who has no optional data
		{
			Patient pt = ps.getPatients("Meriadoc").get(0);
			PatientIdentifierType pit = ps.getPatientIdentifierTypeByName("Old Identification Number");
			Assert.assertEquals("0004", pt.getPatientIdentifier(pit).getIdentifier());
			Assert.assertEquals("Xanadu", pt.getPatientIdentifier(pit).getLocation().getName());
			Assert.assertEquals("Meriadoc", pt.getGivenName());
			Assert.assertEquals("Brandybuck", pt.getFamilyName());
			Assert.assertEquals("M", pt.getGender());
			Assert.assertTrue(
			    String.format("Expected dateCreated to be within 10 seconds of now. Instead found %s", pt.getDateCreated()),
			    (new Date()).getTime() - pt.getDateCreated().getTime() < 10000);
		}
		
		// Verify edit
		{
			Patient pt = ps.getPatients("Frodo").get(0);
			Assert.assertEquals("Mad Dog", pt.getMiddleName());
			Assert.assertEquals("The Shire", pt.getAddresses().iterator().next().getCityVillage());
			Date oldCreationDate = getDate(2019, Calendar.JANUARY, 2, 0, 0, 0, 0);
			Assert.assertEquals(pt.getDateCreated(), oldCreationDate);
		}
		// Verif retire
		{
			Patient pt = ps.getPatientByUuid("fbb1c87d-16d8-4d75-a205-53c6da4742f1");
			Assert.assertThat(pt.isVoided(), is(true));
			log.warn(String.format("Pt is voided: %s", pt.isVoided()));
			log.warn(String.format("Pt is personVoided: %s", pt.isPersonVoided()));
		}
	}
	
	private Date getDate(int year, int month, int day, int hour, int minute, int second, int tzOffsetHours) {
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gc.set(year, month, day, hour, minute, second);
		Date dateNoOffset = gc.getTime();
		Date offsetDate = new Date(dateNoOffset.getTime() + (tzOffsetHours * 60 * 60 * 1000));
		offsetDate.setTime((offsetDate.getTime() / 1000) * 1000); // trim ms value
		return offsetDate;
	}
}
