package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class OrderedFileTest {
	
	@Test
	public void orderedFileShouldCompareByNameIfNoOrderSpecified() {
		OrderedFile file1 = new OrderedFile("/configuration/domain/file1.txt");
		OrderedFile file2 = new OrderedFile("/configuration/domain/file2.txt");
		OrderedFile file3 = new OrderedFile("/configuration/domain/concepts/file3.txt");
		
		Assert.assertTrue(file1.compareTo(file2) < 0);
		Assert.assertTrue(file1.compareTo(file3) > 0);
	}
	
	@Test
	public void orderedFileShouldFirstCompareByOrderIfSpecified() {
		OrderedFile file1 = new NumericFile("/configuration/domain/10");
		OrderedFile file2 = new NumericFile("/configuration/domain/9");
		OrderedFile file3 = new NumericFile("/configuration/concepts/concepts.csv");
		
		Assert.assertTrue(file1.compareTo(file2) > 0);
		Assert.assertTrue(file1.compareTo(file3) < 0);
		Assert.assertTrue(file2.compareTo(file3) < 0);
	}
	
	public class NumericFile extends OrderedFile {
		
		public NumericFile(String path) {
			super(path);
		}
		
		@Override
		protected Integer fetchOrder(File file) throws Exception {
			try {
				return Integer.valueOf(file.getName());
			}
			catch (Exception e) {
				return super.fetchOrder(file);
			}
		}
	}
}
