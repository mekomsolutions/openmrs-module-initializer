package org.openmrs.module.initializer.api.c;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class ConceptsCsvParserTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	final private Locale localeEn = Locale.ENGLISH;
	
	final private Locale localeKm = new Locale("km", "KH");
	
	@Before
	public void setup() {
		
		ConceptClass classQuestion = new ConceptClass();
		classQuestion.setName("Question");
		when(cs.getConceptClassByName(eq("Question"))).thenReturn(classQuestion);
		ConceptClass classMisc = new ConceptClass();
		classMisc.setName("Misc");
		when(cs.getConceptClassByName(eq("Misc"))).thenReturn(classMisc);
		
		ConceptDatatype typeCoded = new ConceptDatatype();
		typeCoded.setName("Coded");
		when(cs.getConceptDatatypeByName(eq("Coded"))).thenReturn(typeCoded);
		ConceptDatatype typeText = new ConceptDatatype();
		typeText.setName("Text");
		when(cs.getConceptDatatypeByName(eq("Text"))).thenReturn(typeText);
		
		when(cs.saveConcept(any(Concept.class))).thenAnswer(new Answer<Concept>() {
			
			@Override
			public Concept answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				Concept c = (Concept) args[0];
				c.setId(new Random().nextInt()); // ensuring that all concepts have IDs
				return c;
			}
		});
		
		when(cs.getConceptByUuid(any(String.class))).thenAnswer(new Answer<Concept>() {
			
			@Override
			public Concept answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				Concept c = new Concept();
				c.setUuid((String) args[0]);
				return c;
			}
		});
	}
	
	@Test
	public void saveAll_shouldParseBaseCsv() throws IOException {
		// setup
		InputStream is = getClass().getClassLoader()
		        .getResourceAsStream("testAppDataDir/configuration/concepts/concepts_base.csv");
		
		// replay
		List<Concept> concepts = new ConceptsCsvParser(is, cs).saveAll();
		
		// verif
		Assert.assertEquals(14, concepts.size());
		Concept c = null;
		c = concepts.get(0);
		Assert.assertEquals("Cambodia_Nationality", c.getFullySpecifiedName(localeEn).getName());
		Assert.assertEquals("Coded", c.getDatatype().getName());
		Assert.assertEquals("Question", c.getConceptClass().getName());
		Assert.assertEquals("កម្ពុជា_សញ្ជាតិ", c.getFullySpecifiedName(localeKm).getName());
		
		c = concepts.get(4);
		Assert.assertEquals("db2f4fc4-3171-11e7-93ae-92361f002671", c.getUuid());
		Assert.assertEquals("Cambodia_Phnong", c.getFullySpecifiedName(localeEn).getName());
		Assert.assertEquals("Text", c.getDatatype().getName());
		Assert.assertEquals("Misc", c.getConceptClass().getName());
		Assert.assertEquals("ព្នង", c.getShortestName(localeKm, true).getName());
		
		// the faulty uuid should not be in there
		Set<String> uuids = new HashSet<String>();
		for (Concept cpt : concepts) {
			uuids.add(cpt.getUuid());
		}
		Assert.assertFalse(uuids.contains("foobar"));
	}
	
	@Test
	public void saveAll_shouldFailOnMisformattedCsv() throws IOException {
		InputStream is = null;
		
		is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/concepts_no_uuid.csv");
		Assert.assertTrue(new ConceptsCsvParser(is, cs).saveAll().isEmpty());
		
		is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/concepts_no_fsn.csv");
		Assert.assertTrue(new ConceptsCsvParser(is, cs).saveAll().isEmpty());
		
		is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/concepts_no_shortname.csv");
		Assert.assertTrue(new ConceptsCsvParser(is, cs).saveAll().isEmpty());
	}
}
