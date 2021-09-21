package org.openmrs.module.initializer.api.display;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.CsvLine;

public class DisplayLineProcessorTest {
	private InitializerMessageSource msgSource = mock(InitializerMessageSource.class);
	private OpenmrsObject instance = mock(OpenmrsObject.class);
	@Test
	public void fill_shouldParseMessageProperty() {
		// Setup
		DisplayLineProcessor processor = new DisplayLineProcessor(msgSource);
		CsvLine line = new CsvLine(new String[] {"display:en","display:km"}, new String[] {"display-english", "display-cambodia"});
		when(instance.getUuid()).thenReturn("display-uuid");
		
		// Replay
		processor.fill(instance, line);
		
		// Verify
		verify(msgSource, times(4)).addPresentation(any());
	}
}
