package org.openmrs.module.initializer.api.display;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.CsvLine;

public class DisplayLineProcessorTest {
	
	@Mock
	private InitializerMessageSource msgSource;
	
	@Mock
	private OpenmrsObject instance;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void fill_shouldParseMessageProperties() {
		// Setup
		DisplayLineProcessor processor = new DisplayLineProcessor(msgSource);
		CsvLine line = new CsvLine(new String[] { "display:en", "display:km" },
		        new String[] { "display-english", "display-cambodia" });
		when(instance.getUuid()).thenReturn("display-uuid");
		
		// Replay
		processor.fill(instance, line);
		
		// Verify
		verify(msgSource, times(4)).addPresentation(any());
	}
}
