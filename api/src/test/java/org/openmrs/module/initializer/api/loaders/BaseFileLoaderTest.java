package org.openmrs.module.initializer.api.loaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.openmrs.module.initializer.api.OrderedFile;

public class BaseFileLoaderTest {
	
	@Mock
	private ConfigDirUtil dirUtil;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		List<File> files = Arrays.asList(new File("test1.txt"), new File("test2.txt"), new File("test3.txt"));
		
		when(dirUtil.getFiles(eq("txt"), any(List.class))).thenReturn(files);
		when(dirUtil.getChecksumIfChanged(any(File.class))).thenReturn("2b2f585d-checksum");
	}
	
	private class TestLoader extends BaseFileLoader {
		
		private class TestOrderedFile extends OrderedFile {
			
			private static final long serialVersionUID = 1L;
			
			public TestOrderedFile(File file) {
				super(file);
			}
			
			@Override
			protected Integer fetchOrder(File file) throws Exception {
				return order;
			}
			
		}
		
		@Override
		protected String getFileExtension() {
			return "txt";
		}
		
		@Override
		public ConfigDirUtil getDirUtil() {
			return dirUtil;
		}
		
		@Override
		public OrderedFile toOrderedFile(File file) {
			return new TestOrderedFile(file);
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
		// replay
		new TestLoader().load();
		
		// verify
		verify(dirUtil, times(3)).writeChecksum(any(), any());
	}
	
	@Test
	public void loadUnsafe_shouldThrowEarly() throws Exception {
		// replay
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			new TestLoader().loadUnsafe(Collections.emptyList(), true);
		});
		
		// verify
		Assert.assertTrue(thrown.getMessage().endsWith("Error right from file 1."));
		verify(dirUtil, times(0)).writeChecksum(any(), any());
	}
}
