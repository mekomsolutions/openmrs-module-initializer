package org.openmrs.module.initializer.api.loc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.LocationTagListParser;

/*
 * This kind of test case can be used to quickly trial the parsing routines on test CSVs
 */
public class LocationLineProcessorTest {
	
	private LocationService ls = mock(LocationService.class);
	
	@Before
	public void setup() {
		
		/*
		 * fetching a location tag by name returns a location tag with the name set
		 */
		when(ls.getLocationTagByName(any(String.class))).thenAnswer(new Answer<LocationTag>() {
			
			@Override
			public LocationTag answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String name = (String) args[0];
				LocationTag t = new LocationTag(name, "");
				return t;
			}
		});
	}
	
	@Test
	public void fill_shouldParseTags() {
		
		// Setup
		String[] headerLine = { "Tags" };
		String[] line = { "Login Location; Visit Location" };
		
		// Replay
		LocationLineProcessor p = new LocationLineProcessor(ls, new LocationTagListParser(ls));
		p.setHeaderLine(headerLine);
		Location c = p.fill(new Location(), new CsvLine(p, line));
		
		// Verif
		Set<LocationTag> tags = c.getTags();
		Assert.assertEquals(2, tags.size());
		Set<String> names = new HashSet<String>();
		for (LocationTag t : tags) {
			names.add(t.getName());
		}
		Assert.assertTrue(names.contains("Login Location"));
		Assert.assertTrue(names.contains("Visit Location"));
	}
}
