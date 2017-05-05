package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class OrderableCsvFileTest {
	
	@Test
	public void shouldSortAccordingToCsvOrder() throws IOException {
		
		// Setup
		URL url = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include/csv/orders");
		File[] files = new File(url.getPath()).listFiles(); // no order guaranteed
		
		// Replay
		List<OrderableCsvFile> orderableFiles = new ArrayList<OrderableCsvFile>();
		for (File f : files) {
			orderableFiles.add(new OrderableCsvFile(f, ""));
		}
		Collections.sort(orderableFiles);
		
		// Verif
		Assert.assertEquals("5_order_500.csv", orderableFiles.get(0).getFile().getName());
		Assert.assertEquals("4_order_1000.csv", orderableFiles.get(1).getFile().getName());
		Assert.assertEquals("1_order_1500.csv", orderableFiles.get(2).getFile().getName());
	}
}
