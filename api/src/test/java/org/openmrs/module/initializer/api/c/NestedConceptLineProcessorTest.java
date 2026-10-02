package org.openmrs.module.initializer.api.c;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptListParser;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class NestedConceptLineProcessorTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	@BeforeEach
	public void setup() {
		
		/*
		 * fetching a concept by mapping returns a concept with the mapping as uuid this
		 * allows to verifies that the correct children are indeed found in collections
		 */
		when(cs.getConceptByMapping(any(String.class), any(String.class))).thenAnswer(new Answer<Concept>() {
			
			@Override
			public Concept answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String code = (String) args[0];
				String source = (String) args[1];
				Concept c = new Concept();
				c.setUuid(source + ":" + code);
				return c;
			}
		});
	}
	
	@Test
	public void fill_shouldParseAnswers() {
		
		// Setup
		String[] headerLine = { "Answers", "Members" };
		String[] line = { "cambodia:123; cambodia:456", null };
		
		// Replay
		NestedConceptLineProcessor p = new NestedConceptLineProcessor(cs, new ConceptListParser(cs));
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertFalse(c.getSet());
		Collection<ConceptAnswer> answers = c.getAnswers();
		Assertions.assertEquals(2, answers.size());
		Set<String> uuids = new HashSet<String>();
		for (ConceptAnswer a : answers) {
			uuids.add(a.getAnswerConcept().getUuid());
		}
		Assertions.assertTrue(uuids.contains("cambodia:123"));
		Assertions.assertTrue(uuids.contains("cambodia:456"));
	}
	
	@Test
	public void fill_shouldParseSetMembers() {
		
		// Setup
		String[] headerLine = { "Answers", "Members" };
		String[] line = { null, "cambodia:123; cambodia:456" };
		
		// Replay
		NestedConceptLineProcessor p = new NestedConceptLineProcessor(cs, new ConceptListParser(cs));
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertTrue(c.getSet());
		List<Concept> members = c.getSetMembers();
		Assertions.assertEquals(2, members.size());
		Set<String> uuids = new HashSet<String>();
		for (Concept cpt : members) {
			uuids.add(cpt.getUuid());
		}
		Assertions.assertTrue(uuids.contains("cambodia:123"));
		Assertions.assertTrue(uuids.contains("cambodia:456"));
	}
	
	@Test
	public void fill_shouldHandleNoChildren() {
		
		// Setup
		String[] headerLine = { "Answers", "Members" };
		String[] line = { null, null };
		
		// Replay
		NestedConceptLineProcessor p = new NestedConceptLineProcessor(cs, new ConceptListParser(cs));
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verif
		Assertions.assertFalse(c.getSet());
		Assertions.assertEquals(0, c.getSetMembers().size());
		Assertions.assertEquals(0, c.getAnswers().size());
	}
	
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		NestedConceptLineProcessor p = new NestedConceptLineProcessor(cs, new ConceptListParser(cs));
		Concept c = p.fill(new Concept(), new CsvLine(headerLine, line));
		Assertions.assertNull(c.getAnswers());
		Assertions.assertNull(c.getSetMembers());
	}
}
