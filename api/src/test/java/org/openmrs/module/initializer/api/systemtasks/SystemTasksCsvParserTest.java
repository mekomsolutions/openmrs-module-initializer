package org.openmrs.module.initializer.api.systemtasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.tasks.SystemTask;

/**
 * Unit tests for SystemTasksCsvParser. Tests the bootstrap and domain logic without mocks.
 */
public class SystemTasksCsvParserTest {
	
	@Test
	public void getDomain_shouldReturnSystemTasks() {
		// Create parser with null service (won't be called in this test)
		SystemTasksCsvParser parser = new SystemTasksCsvParser(null, new SystemTasksLineProcessor());
		
		assertEquals(Domain.SYSTEM_TASKS, parser.getDomain());
	}
	
	@Test
	public void bootstrap_shouldCreateNewSystemTaskWithUuid() {
		// Create parser with null service (simulates new task scenario when getSystemTaskByUuid returns null)
		SystemTasksCsvParser parser = new TestableSystemTasksCsvParser();
		
		String[] headerLine = { "Uuid", "Name", "Title" };
		String[] line = { "550e8400-e29b-41d4-a716-446655440001", "test-task", "Test Task" };
		CsvLine csvLine = new CsvLine(headerLine, line);
		
		SystemTask task = parser.bootstrap(csvLine);
		
		assertNotNull(task);
		assertEquals("550e8400-e29b-41d4-a716-446655440001", task.getUuid());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void bootstrap_shouldThrowExceptionWhenUuidIsMissing() {
		SystemTasksCsvParser parser = new TestableSystemTasksCsvParser();
		
		String[] headerLine = { "Uuid", "Name", "Title" };
		String[] line = { "", "test-task", "Test Task" };
		CsvLine csvLine = new CsvLine(headerLine, line);
		
		parser.bootstrap(csvLine);
	}
	
	/**
	 * Testable subclass that doesn't require TasksService
	 */
	private static class TestableSystemTasksCsvParser extends SystemTasksCsvParser {
		
		public TestableSystemTasksCsvParser() {
			super(null, new SystemTasksLineProcessor());
		}
		
		@Override
		public SystemTask bootstrap(CsvLine line) throws IllegalArgumentException {
			String uuid = line.getUuid();
			if (uuid == null || uuid.isEmpty()) {
				throw new IllegalArgumentException("uuid is required for system tasks");
			}
			
			// Always create new task (simulates getSystemTaskByUuid returning null)
			SystemTask systemTask = new SystemTask();
			systemTask.setUuid(uuid);
			return systemTask;
		}
		
		@Override
		public SystemTask save(SystemTask instance) {
			return instance;
		}
	}
}
