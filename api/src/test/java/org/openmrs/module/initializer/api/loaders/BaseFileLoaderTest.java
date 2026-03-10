package org.openmrs.module.initializer.api.loaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.OrderedFile;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;

public class BaseFileLoaderTest {
	
	private TestLoader testLoader;
	
	@TempDir
	File tempDir;
	
	@Mock
	InitializerService iniz;
	
	@Mock
	ConfigDirUtil dirUtil;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		testLoader = spy(new TestLoader());
		testLoader.setIniz(iniz);
		testLoader.setDirUtil(dirUtil);
		
		List<File> files = Arrays.asList(new File("test1.txt"), new File("test2.txt"), new File("test3.txt"));
		when(dirUtil.getFiles(any(), any())).thenReturn(files);
		
		when(iniz.getChecksumIfChanged(any()))
		        .thenAnswer(i -> new InitializerChecksum(i.getArguments()[0].toString(), "checksum"));
	}
	
	@Test
	public void loadUnsafe_shouldLoadFileIfChecksumChanged() throws Exception {
		// Setup
		File file = new File(tempDir, "test.csv");
		file.createNewFile();
		
		when(dirUtil.getFiles(any(), any())).thenReturn(Collections.singletonList(file));
		
		InitializerChecksum checksum = mock(InitializerChecksum.class);
		when(iniz.getChecksumIfChanged(file.toPath())).thenReturn(checksum);
		
		// Replay
		testLoader.loadUnsafe(Collections.emptyList(), true);
		
		// Verify
		verify(testLoader, times(1)).load(file);
		verify(iniz, times(1)).saveOrUpdateChecksum(checksum);
	}
	
	@Test
	public void loadUnsafe_shouldNotLoadFileIfChecksumNotChanged() throws Exception {
		// Setup
		File file = new File(tempDir, "test.csv");
		file.createNewFile();
		
		when(dirUtil.getFiles(any(), any())).thenReturn(Collections.singletonList(file));
		
		when(iniz.getChecksumIfChanged(file.toPath())).thenReturn(null);
		
		// Replay
		testLoader.loadUnsafe(Collections.emptyList(), true);
		
		// Verify
		verify(testLoader, never()).load(file);
		verify(iniz, never()).saveOrUpdateChecksum(any());
	}
	
	public static class TestLoader extends BaseFileLoader {
		
		private ConfigDirUtil dirUtil;
		
		public void setIniz(InitializerService iniz) {
			this.iniz = iniz;
		}
		
		public void setDirUtil(ConfigDirUtil dirUtil) {
			this.dirUtil = dirUtil;
		}
		
		@Override
		public ConfigDirUtil getDirUtil() {
			return dirUtil;
		}
		
		@Override
		protected Domain getDomain() {
			return null;
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
		protected String getFileExtension() {
			return "csv";
		}
		
		@Override
		public String getDomainName() {
			return "test";
		}
	}
	
	@Test
	public void load_shouldLoadSafely() throws Exception {
		// replay
		testLoader.load();
		
		// verify
		verify(iniz, times(3)).getChecksumIfChanged(any());
	}
	
	@Test
	public void loadUnsafe_shouldThrowEarly() throws Exception {
		// replay
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			testLoader.loadUnsafe(Collections.emptyList(), true);
		});
		
		// verify
		Assert.assertTrue(thrown.getMessage().endsWith("Error right from file 1."));
		verify(iniz, times(1)).getChecksumIfChanged(any());
		verify(iniz, times(0)).saveOrUpdateChecksum(any());
	}
	
	@Test
	public void loadUnsafe_shouldThrowOnPreloadAsDefaultGivenDoThrowTrue() throws Exception {
		// setup
		BaseFileLoader fl = new BaseFileLoader() {
			
			@Override
			protected String getFileExtension() {
				return testLoader.getFileExtension();
			}
			
			@Override
			public ConfigDirUtil getDirUtil() {
				return testLoader.getDirUtil();
			}
			
			@Override
			public OrderedFile toOrderedFile(File file) {
				return testLoader.toOrderedFile(file);
			}
			
			@Override
			protected void load(File file) throws Exception {
				testLoader.load(file);
			}
			
			@Override
			protected Domain getDomain() {
				return testLoader.getDomain();
			}
			
			@Override
			public String getDomainName() {
				return testLoader.getDomainName();
			}
			
			@Override
			protected void preload(final File file) throws Exception {
				throw new RuntimeException("Failed to preload from file 1.");
			}
		};
		
		// replay
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			fl.loadUnsafe(Collections.emptyList(), true);
		});
		
		// verify
		Assert.assertTrue(thrown.getMessage().endsWith("Failed to preload from file 1."));
	}
	
	@Test
	public void loadUnsafe_shouldNotThrowOnPreloadException() throws Exception {
		// setup
		BaseFileLoader fl = new BaseFileLoader() {
			
			@Override
			protected String getFileExtension() {
				return testLoader.getFileExtension();
			}
			
			@Override
			public ConfigDirUtil getDirUtil() {
				return testLoader.getDirUtil();
			}
			
			@Override
			public OrderedFile toOrderedFile(File file) {
				return testLoader.toOrderedFile(file);
			}
			
			@Override
			protected void load(File file) throws Exception {
				testLoader.load(file);
			}
			
			@Override
			protected Domain getDomain() {
				return testLoader.getDomain();
			}
			
			@Override
			public String getDomainName() {
				return testLoader.getDomainName();
			}
			
			@Override
			protected void preload(final File file) throws Exception {
				throw new RuntimeException("Failed to preload from file 1.");
			}
			
			// throwingOnPreload overridden to always not throw on pre-loading
			@Override
			protected boolean throwingOnPreload(boolean doThrow) {
				return false;
			}
		};
		
		// replay
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			fl.loadUnsafe(Collections.emptyList(), true);
		});
		
		// verify
		Assert.assertFalse(thrown.getMessage().endsWith("Failed to preload from file 1."));
	}
}
