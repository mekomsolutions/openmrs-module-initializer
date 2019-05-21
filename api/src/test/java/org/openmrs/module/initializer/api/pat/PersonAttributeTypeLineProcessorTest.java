package org.openmrs.module.initializer.api.pat;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypeLineProcessor.Helper;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class PersonAttributeTypeLineProcessorTest {
	
	private PersonService ps = mock(PersonService.class);
	
	private Helper helper = mock(Helper.class);
	
	@Before
	public void setup() {
		
		when(helper.getPrivilege(any(String.class))).thenAnswer(new Answer<Privilege>() {
			
			@Override
			public Privilege answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String privilegeName = (String) args[0];
				Privilege privilege = new Privilege(privilegeName, "Privilege desc.");
				return privilege;
			}
		});
	}
	
	@Test
	public void fill_shouldParsePersonAttributeType() {
		
		// Setup
		String[] headerLine = { "Name", "Description", "Format", "Foreign uuid", "Searchable", "Edit privilege" };
		String[] line = { "PAT name", "PAT desc.", "org.openmrs.Concept", "12a00ba4-4470-11e7-a919-92ebcb67fe33", "true",
		        "Edit Privilege" };
		
		// Replay
		PersonAttributeTypeLineProcessor p = new PersonAttributeTypeLineProcessor(ps);
		p.setHelper(helper);
		p.setHeaderLine(headerLine);
		PersonAttributeType pat = p.fill(new PersonAttributeType(), new CsvLine(p, line));
		
		// Verif
		Assert.assertEquals("PAT name", pat.getName());
		Assert.assertEquals("PAT desc.", pat.getDescription());
		Assert.assertEquals("org.openmrs.Concept", pat.getFormat());
		Assert.assertTrue(pat.isSearchable());
		Assert.assertEquals("Edit Privilege", pat.getEditPrivilege().getName());
		Assert.assertEquals("Privilege desc.", pat.getEditPrivilege().getDescription());
	}
	
	@Test
	public void fill_shouldParseWithNameAndFormatOnly() {
		
		// Setup
		String[] headerLine = { "Name", "Format" };
		String[] line = { "PAT name", "java.lang.String" };
		
		// Replay
		PersonAttributeTypeLineProcessor p = new PersonAttributeTypeLineProcessor(ps);
		p.setHelper(helper);
		p.setHeaderLine(headerLine);
		PersonAttributeType pat = p.fill(new PersonAttributeType(), new CsvLine(p, line));
		
		// Verif
		Assert.assertEquals("PAT name", pat.getName());
		Assert.assertEquals("java.lang.String", pat.getFormat());
	}
	
	public void fill_shouldHandleMissingHeaders() {
		
		// Setup
		String[] headerLine = {};
		String[] line = {};
		
		// Replay
		PersonAttributeTypeLineProcessor p = new PersonAttributeTypeLineProcessor(ps);
		p.setHelper(helper);
		p.setHeaderLine(headerLine);
		PersonAttributeType pat = p.fill(new PersonAttributeType(), new CsvLine(p, line));
		
		// Verif
		Assert.assertNull(pat.getName());
		Assert.assertNull(pat.getFormat());
	}
}
