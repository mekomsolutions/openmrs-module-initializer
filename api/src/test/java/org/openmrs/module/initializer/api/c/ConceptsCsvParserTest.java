package org.openmrs.module.initializer.api.c;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptAttributeType;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.initializer.api.utils.ConceptListParser;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CustomDatatypeUtil.class)
public class ConceptsCsvParserTest {
	
	private ConceptService cs;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(CustomDatatypeUtil.class);
		cs = mock(ConceptService.class);
		
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
		ConceptDatatype typeNA = new ConceptDatatype();
		typeNA.setName("N/A");
		when(cs.getConceptDatatypeByName(eq("N/A"))).thenReturn(typeNA);
		
		when(CustomDatatypeUtil.getDatatype(eq(FreeTextDatatype.class.getName()), anyString()))
		        .thenReturn((CustomDatatype) new FreeTextDatatype());
		when(CustomDatatypeUtil.getDatatype(eq(DateDatatype.class.getName()), anyString()))
		        .thenReturn((CustomDatatype) new DateDatatype());
		
		ConceptAttributeType autditDateAttType = new ConceptAttributeType();
		autditDateAttType.setName("Audit Date");
		autditDateAttType.setDatatypeClassname(DateDatatype.class.getName());
		
		ConceptAttributeType emailAttrType = new ConceptAttributeType();
		emailAttrType.setName("Email Address");
		emailAttrType.setDatatypeClassname(FreeTextDatatype.class.getName());
		
		when(cs.getConceptAttributeTypeByName("Audit Date")).thenReturn(autditDateAttType);
		when(cs.getConceptAttributeTypeByName("Email Address")).thenReturn(emailAttrType);
		
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
	public void process_shouldParseBaseCsv() throws IOException {
		// setup
		InputStream is = getClass().getClassLoader()
		        .getResourceAsStream("testAppDataDir/configuration/concepts/concepts_base.csv");
		
		// replay
		ConceptsCsvParser parser = new ConceptsCsvParser(cs, new ConceptLineProcessor(cs),
		        new ConceptNumericLineProcessor(cs), new ConceptComplexLineProcessor(cs),
		        new NestedConceptLineProcessor(cs, new ConceptListParser(cs)), new MappingsConceptLineProcessor(cs),
		        new ConceptAttributeLineProcessor(cs));
		parser.setInputStream(is);
		
		List<String[]> lines = parser.process(parser.getLines()).getFailingLines();
		
		// verif
		Assert.assertEquals(1, lines.size());
	}
	
	@Test
	public void process_shouldParseCsvWithBom() throws IOException {
		// setup
		InputStream is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/with_bom.csv");
		
		// replay
		ConceptsCsvParser parser = new ConceptsCsvParser(cs, new ConceptLineProcessor(cs),
		        new ConceptNumericLineProcessor(cs), new ConceptComplexLineProcessor(cs),
		        new NestedConceptLineProcessor(cs, new ConceptListParser(cs)), new MappingsConceptLineProcessor(cs),
		        new ConceptAttributeLineProcessor(cs));
		parser.setInputStream(is);
		
		List<String[]> lines = parser.process(parser.getLines()).getFailingLines();
		
		// verify
		Assert.assertEquals(1, lines.size());
	}
	
	@Test
	public void process_shouldFailOnMisformattedCsv() throws IOException {
		ConceptsCsvParser parser = new ConceptsCsvParser(cs, new ConceptLineProcessor(cs),
		        new ConceptNumericLineProcessor(cs), new ConceptComplexLineProcessor(cs),
		        new NestedConceptLineProcessor(cs, new ConceptListParser(cs)), new MappingsConceptLineProcessor(cs),
		        new ConceptAttributeLineProcessor(cs));
		InputStream is = null;
		
		is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/concepts_no_uuid.csv");
		parser.setInputStream(is);
		Assert.assertEquals(1, parser.process(parser.getLines()).getFailingLines().size());
		
		is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/concepts_no_fsn.csv");
		parser.setInputStream(is);
		Assert.assertEquals(1, parser.process(parser.getLines()).getFailingLines().size());
		
		is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/csv/concepts_no_shortname.csv");
		parser.setInputStream(is);
		Assert.assertEquals(1, parser.process(parser.getLines()).getFailingLines().size());
	}
}
