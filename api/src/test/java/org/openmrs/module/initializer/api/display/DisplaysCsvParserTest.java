package org.openmrs.module.initializer.api.display;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;

public class DisplaysCsvParserTest {

	private InitializerMessageSource msgSource = mock(InitializerMessageSource.class);
	
	private CsvParser someParser = mock(CsvParser.class);
	
	private DisplayLineProcessor displayProcessor;
	
	private DisplaysCsvParser displayParser;
	
	@Before
	public void setup() {
		displayParser = new DisplaysCsvParser(new DisplayLineProcessor(msgSource));
		displayParser.setBootstrapParser(someParser);
	}
	
	@Test
	public void bootstrap_shouldReturnNullGivenVoidTrue() {
		// Setup
		CsvLine line = new CsvLine(new String[] {"uuid","void/retire"}, new String[] {"d9e04a9d-d534-4a02-9c40-1c173f3d1d4b", "True"});
		
		// Replay
		OpenmrsObject obj = displayParser.bootstrap(line);
		
		// Verify
		assertEquals(null, obj);
	}
	
	@Test
	public void bootstrap_shouldReturnNullGivenNoUuid() {
		// Setup
		CsvLine line = new CsvLine(new String[] {"uuid","void/retire"}, new String[] {"", "True"});
		
		// Replay
		OpenmrsObject obj = displayParser.bootstrap(line);
		
		// Verify
		assertEquals(null, obj);
	}
	
	@Test
	public void bootstrap_shouldBootstrapObjectGivenUuidPresentAndObjectNotVoid() {
		// Setup
		CsvLine line = new CsvLine(new String[] {"uuid","void/retire"}, new String[] {"d9e04a9d-d534-4a02-9c40-1c173f3d1d4b", "False"});
		
		// Replay
		OpenmrsObject obj = displayParser.bootstrap(line);
		
		// Verify
		verify(someParser, times(1)).bootstrap(line);
	}
}
