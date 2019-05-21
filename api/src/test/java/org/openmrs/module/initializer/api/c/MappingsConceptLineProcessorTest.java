package org.openmrs.module.initializer.api.c;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptMapListParser;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class MappingsConceptLineProcessorTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	@Before
	public void setup() {
		
		/*
		 * fetching a concept map type by uuid returns a concept map type with that uuid
		 */
		when(cs.getConceptMapTypeByUuid(any(String.class))).thenAnswer(new Answer<ConceptMapType>() {
			
			@Override
			public ConceptMapType answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String uuid = (String) args[0];
				ConceptMapType mapType = new ConceptMapType();
				mapType.setUuid(uuid);
				return mapType;
			}
		});
		
		/*
		 * fetching a concept source by name returns a concept source with its name set
		 * as the source string that was requested
		 */
		when(cs.getConceptSourceByName(any(String.class))).thenAnswer(new Answer<ConceptSource>() {
			
			@Override
			public ConceptSource answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String sourceStr = (String) args[0];
				ConceptSource source = new ConceptSource();
				source.setName(sourceStr);
				return source;
			}
		});
	}
	
	@Test
	public void fill_shouldParseSameAsMappings() {
		
		// Setup
		String[] headerLine = { "Same as mappings" };
		String[] line = { "cambodia:123; foo:456" };
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs, new ConceptMapListParser(cs));
		p.setHeaderLine(headerLine);
		Concept c = p.fill(new Concept(), new CsvLine(p, line));
		
		// Verif
		Collection<ConceptMap> mappings = c.getConceptMappings();
		Assert.assertEquals(2, mappings.size());
		Set<String> names = new HashSet<String>();
		for (ConceptMap m : mappings) {
			String source = m.getConceptReferenceTerm().getConceptSource().getName();
			String code = m.getConceptReferenceTerm().getCode();
			names.add(source + ":" + code);
		}
		Assert.assertTrue(names.contains("cambodia:123"));
		Assert.assertTrue(names.contains("foo:456"));
	}
	
	@Test
	public void fill_shouldHandleNoSameAsMappings() {
		
		// Setup
		String[] headerLine = { "Same as mappings" };
		String[] line = { null };
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs, new ConceptMapListParser(cs));
		p.setHeaderLine(headerLine);
		Concept c = p.fill(new Concept(), new CsvLine(p, line));
		
		// Verif
		Assert.assertTrue(CollectionUtils.isEmpty(c.getConceptMappings()));
	}
	
	public void getConcept_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs, new ConceptMapListParser(cs));
		p.setHeaderLine(headerLine);
		Concept c = p.fill(new Concept(), new CsvLine(p, line));
		Assert.assertNull(c.getConceptMappings());
	}
}
