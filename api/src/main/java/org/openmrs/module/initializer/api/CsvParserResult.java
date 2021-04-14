package org.openmrs.module.initializer.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the result of {@link CsvParser#process(List)}, an object containing the CSV lines that
 * could potentially be retried and the collection of error details for those lines that could not
 * be successfully be processed.
 */
public class CsvParserResult {
	
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
	
	private List<String[]> remainingLines = new ArrayList<String[]>();
	
	private List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
	
	public CsvParserResult() {
	}
	
	public void addRemainingLine(String[] line) {
		remainingLines.add(line);
	}
	
	public void addError(CsvLine line, Exception e) {
		errorDetails.add(new ErrorDetails(line, e));
	}
	
	public List<String[]> getRemainingLines() {
		return remainingLines;
	}
	
	public List<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}
	
}
