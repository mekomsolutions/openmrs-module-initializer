package org.openmrs.module.initializer.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the failed outcome of {@link CsvParser#process(List)}, an object containing the failing
 * CSV lines that could potentially be retried, as well as the error details for those lines.
 */
public class CsvFailingLines {
	
	/**
	 * ErrorDetails encompasses a failing line and the exception that was caught when processing it.
	 */
	public class ErrorDetails {
		
		private CsvLine csvLine;
		
		private Exception e;
		
		public ErrorDetails(CsvLine line, Exception e) {
			this.csvLine = line;
			this.e = e;
		}
		
		public CsvLine getCsvLine() {
			return csvLine;
		}
		
		public Exception getException() {
			return e;
		}
		
	}
	
	// failing lines are in the error details but this provides a fast/direct access to them
	private List<String[]> failingLines = new ArrayList<String[]>();
	
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
	
	public void addFailingLine(CsvLine line, Exception e) {
		failingLines.add(line.asLine());
		errorDetails.add(new ErrorDetails(line, e));
	}
	
	public List<String[]> getFailingLines() {
		return failingLines;
	}
	
	public List<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}
	
}
