package org.openmrs.module.initializer.api.c;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class MappingsConceptLineProcessorTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	@Before
	public void setup() {
		
		/*
		 * fetching a concept map type by uuid returns same-as if that uuid specified, null otherwise
		 */
		when(cs.getConceptMapTypeByUuid(any(String.class))).thenAnswer((Answer<ConceptMapType>) invocation -> {
			Object[] args = invocation.getArguments();
			String uuid = (String) args[0];
			ConceptMapType mapType = null;
			if (uuid.equals(ConceptMapType.SAME_AS_MAP_TYPE_UUID)) {
				mapType = new ConceptMapType();
				mapType.setUuid(uuid);
				mapType.setName("same-as");
			}
			return mapType;
		});
		
		/*
		 * fetching a concept map type by name returns a concept map type with that name
		 * if the map type is "same-as", set the uuid for this map type
		 */
		when(cs.getConceptMapTypeByName(any(String.class))).thenAnswer((Answer<ConceptMapType>) invocation -> {
			Object[] args = invocation.getArguments();
			String name = (String) args[0];
			ConceptMapType mapType = new ConceptMapType();
			if (name.equalsIgnoreCase("same-as")) {
				mapType.setUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
			}
			mapType.setName(name);
			return mapType;
		});
		
		/*
		 * fetching a concept source by name returns a concept source with its name set
		 * as the source string that was requested
		 */
		when(cs.getConceptSourceByName(any(String.class))).thenAnswer((Answer<ConceptSource>) invocation -> {
			Object[] args = invocation.getArguments();
			String sourceStr = (String) args[0];
			ConceptSource source = new ConceptSource();
			source.setName(sourceStr);
			return source;
		});
	}
	
	@Test
	public void fill_shouldParseSameAsMappings() {
		
		// Setup
		String[] headerLine = { "Same as mappings" };
		String[] line = { "cambodia:123; foo:456" };
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs);
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
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
	public void fill_shouldParseMappingsForTypeAndSourceInHeader() {
		
		// Setup
		String[] headerLine = { "mappings|same-as|cambodia", "mappings|broader-than|foo", "mappings|related-to",
		        "mappings|same-as|pih|code", "mappings|same-as|pih|name" };
		String[] line = { "123", "456", "cambodia:789; foo:abc", "5089", "weight" };
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs);
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Collection<ConceptMap> mappings = c.getConceptMappings();
		Assert.assertEquals(6, mappings.size());
		Set<String> names = new HashSet<String>();
		for (ConceptMap m : mappings) {
			String mapType = m.getConceptMapType().getName();
			String source = m.getConceptReferenceTerm().getConceptSource().getName();
			String code = m.getConceptReferenceTerm().getCode();
			names.add(mapType + ":" + source + ":" + code);
		}
		Assert.assertTrue(names.contains("same-as:cambodia:123"));
		Assert.assertTrue(names.contains("broader-than:foo:456"));
		Assert.assertTrue(names.contains("related-to:cambodia:789"));
		Assert.assertTrue(names.contains("related-to:foo:abc"));
		Assert.assertTrue(names.contains("same-as:pih:5089"));
		Assert.assertTrue(names.contains("same-as:pih:weight"));
	}
	
	@Test
	public void fill_shouldHandleNoSameAsMappings() {
		
		// Setup
		String[] headerLine = { "Same as mappings" };
		String[] line = { null };
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs);
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assert.assertTrue(CollectionUtils.isEmpty(c.getConceptMappings()));
	}
	
	public void getConcept_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		MappingsConceptLineProcessor p = new MappingsConceptLineProcessor(cs);
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		Assert.assertNull(c.getConceptMappings());
	}
}
