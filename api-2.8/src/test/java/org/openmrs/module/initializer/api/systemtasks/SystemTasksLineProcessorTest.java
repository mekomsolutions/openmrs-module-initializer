package org.openmrs.module.initializer.api.systemtasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.tasks.Priority;
import org.openmrs.module.tasks.SystemTask;

/**
 * Unit tests for SystemTasksLineProcessor. Tests the fill() method directly without mocks.
 */
public class SystemTasksLineProcessorTest {
	
	private SystemTasksLineProcessor processor = new SystemTasksLineProcessor();
	
	@Test
	public void fill_shouldParseAllFields() {
		// Setup
		String[] headerLine = { "Uuid", "Void/Retire", "Name", "Title", "Description", "Priority", "Rationale" };
		String[] line = { "550e8400-e29b-41d4-a716-446655440001", "", "vital-check", "Daily Vital Check",
		        "Check patient vitals", "HIGH", "Routine monitoring" };
		
		// Replay
		SystemTask task = processor.fill(new SystemTask(), new CsvLine(headerLine, line));
		
		// Verify
		assertEquals("vital-check", task.getName());
		assertEquals("Daily Vital Check", task.getTitle());
		assertEquals("Check patient vitals", task.getDescription());
		assertEquals(Priority.HIGH, task.getPriority());
		assertEquals("Routine monitoring", task.getRationale());
	}
	
	@Test
	public void fill_shouldParsePriorityMedium() {
		// Setup
		String[] headerLine = { "Name", "Title", "Priority" };
		String[] line = { "task-1", "Task One", "MEDIUM" };
		
		// Replay
		SystemTask task = processor.fill(new SystemTask(), new CsvLine(headerLine, line));
		
		// Verify
		assertEquals(Priority.MEDIUM, task.getPriority());
	}
	
	@Test
	public void fill_shouldParsePriorityLow() {
		// Setup
		String[] headerLine = { "Name", "Title", "Priority" };
		String[] line = { "task-1", "Task One", "LOW" };
		
		// Replay
		SystemTask task = processor.fill(new SystemTask(), new CsvLine(headerLine, line));
		
		// Verify
		assertEquals(Priority.LOW, task.getPriority());
	}
	
	@Test
	public void fill_shouldParsePriorityCaseInsensitive() {
		// Setup
		String[] headerLine = { "Name", "Title", "Priority" };
		String[] line = { "task-1", "Task One", "high" };
		
		// Replay
		SystemTask task = processor.fill(new SystemTask(), new CsvLine(headerLine, line));
		
		// Verify
		assertEquals(Priority.HIGH, task.getPriority());
	}
	
	@Test
	public void fill_shouldHandleMissingOptionalFields() {
		// Setup - only required fields
		String[] headerLine = { "Name", "Title" };
		String[] line = { "minimal-task", "Minimal Task" };
		
		// Replay
		SystemTask task = processor.fill(new SystemTask(), new CsvLine(headerLine, line));
		
		// Verify
		assertEquals("minimal-task", task.getName());
		assertEquals("Minimal Task", task.getTitle());
		assertNull(task.getDescription());
		assertNull(task.getPriority());
		assertNull(task.getRationale());
	}
	
	@Test
	public void fill_shouldHandleEmptyOptionalFields() {
		// Setup - all fields present but some empty
		String[] headerLine = { "Name", "Title", "Description", "Priority", "Rationale" };
		String[] line = { "task-1", "Task One", null, null, null };
		
		// Replay
		SystemTask task = processor.fill(new SystemTask(), new CsvLine(headerLine, line));
		
		// Verify
		assertEquals("task-1", task.getName());
		assertEquals("Task One", task.getTitle());
		assertNull(task.getDescription());
		assertNull(task.getPriority());
		assertNull(task.getRationale());
	}
	
	@Test
	public void fill_shouldPreserveExistingTaskData() {
		// Setup - existing task with some data
		SystemTask existingTask = new SystemTask();
		existingTask.setUuid("existing-uuid");
		existingTask.setName("old-name");
		existingTask.setTitle("Old Title");
		existingTask.setPriority(Priority.LOW);
		
		String[] headerLine = { "Name", "Title", "Priority" };
		String[] line = { "new-name", "New Title", "HIGH" };
		
		// Replay
		SystemTask task = processor.fill(existingTask, new CsvLine(headerLine, line));
		
		// Verify - UUID preserved, other fields updated
		assertEquals("existing-uuid", task.getUuid());
		assertEquals("new-name", task.getName());
		assertEquals("New Title", task.getTitle());
		assertEquals(Priority.HIGH, task.getPriority());
	}
}
