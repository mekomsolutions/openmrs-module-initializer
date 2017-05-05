package org.openmrs.module.initializer.api.c;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.ConceptService;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class ConceptWithChildrenLineProcessorTest {
	
	private ConceptService cs = mock(ConceptService.class);
	
	@Before
	public void setup() {
		
		/*
		 * fetching a concept by mapping returns a concept with the mapping as uuid
		 * this allows to verifies that the correct children are indeed found in collections
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
	public void getConcept_shouldParseAnswers() {
		
		// Setup
		String[] headerLine = { "Answers", "Members" };
		String[] line = { "cambodia:123; cambodia:456", null };
		
		// Replay
		ConceptWithChildrenLineProcessor p = new ConceptWithChildrenLineProcessor(headerLine, cs);
		Concept c = p.getConcept(null, line, cs);
		
		// Verif
		Assert.assertFalse(c.getSet());
		Collection<ConceptAnswer> answers = c.getAnswers();
		Assert.assertEquals(2, answers.size());
		Set<String> uuids = new HashSet<String>();
		for (ConceptAnswer a : answers) {
			uuids.add(a.getAnswerConcept().getUuid());
		}
		Assert.assertTrue(uuids.contains("cambodia:123"));
		Assert.assertTrue(uuids.contains("cambodia:456"));
	}
	
	@Test
	public void getConcept_shouldParseSetMembers() {
		
		// Setup
		String[] headerLine = { "Answers", "Members" };
		String[] line = { null, "cambodia:123; cambodia:456" };
		
		// Replay
		ConceptWithChildrenLineProcessor p = new ConceptWithChildrenLineProcessor(headerLine, cs);
		Concept c = p.getConcept(null, line, cs);
		
		// Verif
		Assert.assertTrue(c.getSet());
		List<Concept> members = c.getSetMembers();
		Assert.assertEquals(2, members.size());
		Set<String> uuids = new HashSet<String>();
		for (Concept cpt : members) {
			uuids.add(cpt.getUuid());
		}
		Assert.assertTrue(uuids.contains("cambodia:123"));
		Assert.assertTrue(uuids.contains("cambodia:456"));
	}
	
	@Test
	public void getConcept_shouldHandleNoChildren() {
		
		// Setup
		String[] headerLine = { "Answers", "Members" };
		String[] line = { null, null };
		
		// Replay
		ConceptWithChildrenLineProcessor p = new ConceptWithChildrenLineProcessor(headerLine, cs);
		Concept c = p.getConcept(null, line, cs);
		
		// Verif
		Assert.assertFalse(c.getSet());
		Assert.assertEquals(0, c.getSetMembers().size());
		Assert.assertEquals(0, c.getAnswers().size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getConcept_shouldFailIfNoAnswersColumns() {
		
		// Setup
		String[] headerLine = { "Members" };
		String[] line = { null };
		
		// Replay
		ConceptWithChildrenLineProcessor p = new ConceptWithChildrenLineProcessor(headerLine, cs);
		Concept c = p.getConcept(null, line, cs);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getConcept_shouldFailIfNoMembersColumns() {
		
		// Setup
		String[] headerLine = { "Answers" };
		String[] line = { null };
		
		// Replay
		ConceptWithChildrenLineProcessor p = new ConceptWithChildrenLineProcessor(headerLine, cs);
		Concept c = p.getConcept(null, line, cs);
	}
}
