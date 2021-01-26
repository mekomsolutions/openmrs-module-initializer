package org.openmrs.module.initializer.api.loaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;

public class BaseFileLoaderTest {
	
	@Mock
	private ConfigDirUtil dirUtil;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		List<File> files = Arrays.asList(new File("test1.txt"), new File("test2.txt"), new File("test3.txt"));
		
		when(dirUtil.getOrderedFiles(eq("txt"), any(List.class), isA(TestLoader.class))).thenReturn(files);
	}
	
	protected class TestLoader extends BaseFileLoader {
		
		@Override
		protected String getFileExtension() {
			return "txt";
		}
		
		@Override
		public ConfigDirUtil getDirUtil() {
			return dirUtil;
		}
		
		@Override
		protected void load(File file) throws Exception {
			switch (file.getName()) {
				case "test1.txt":
					throw new IllegalArgumentException("Error right from file 1.");
				case "test2.txt":
					break;
				case "test3.txt":
					break;
			}
		}
		
		@Override
		protected Domain getDomain() {
			return null;
		}
		
		@Override
		public String getDomainName() {
			return "testdomain";
		}
		
	}
	
	@Test
	public void load_shouldLoadSafely() throws Exception {
		new TestLoader().load();
	}
	
	@Test
	public void loadUnsafe_shouldThrowEarly() throws Exception {
		
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			new TestLoader().loadUnsafe(Collections.emptyList(), true);
		});
		
		Assert.assertEquals("Error right from file 1.", thrown.getMessage());
	}
}
