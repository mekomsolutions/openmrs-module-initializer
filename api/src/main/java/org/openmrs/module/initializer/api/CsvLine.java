package org.openmrs.module.initializer.api;

import java.sql.Time;
import java.util.Arrays;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class CsvLine {
	
	private String[] headerLine;
	
	protected String[] line;
	
	public CsvLine(String[] headerLine, String[] line) {
		this.headerLine = headerLine;
		this.line = line;
	}
	
	public String[] getHeaderLine() {
		return headerLine;
	}
	
	public String[] asLine() {
		return line;
	}
	
	public String get(String header, boolean canThrowException) throws IllegalArgumentException {
		String val = null;
		try {
			val = line[BaseLineProcessor.getColumn(headerLine, header)];
		}
		catch (IllegalArgumentException e) {
			if (canThrowException) {
				throw new IllegalArgumentException(e);
			}
		}
		return val;
	}
	
	public String get(String header) {
		return get(header, false);
	}
	
	public String getUuid() throws IllegalArgumentException {
		
		String str = get(BaseLineProcessor.HEADER_UUID, true);
		
		// TODO Reuse UUID.fromString(str) when MRSCMNS-59 is implemented
		if (!StringUtils.isEmpty(str)) {
			boolean validUuid = str.length() == 36 || "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAA".equals(str)
			        || "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAA".equals(str);
			if (!validUuid) {
				throw new IllegalArgumentException(
				        "'" + str + "' did not pass the soft check for being a valid OpenMRS UUID.");
			}
			
			// str = UUID.fromString(str).toString();
		}
		return str;
	}
	
	public String getString(String header, String defaultValue) {
		String val = get(header);
		if (val == null) {
			val = defaultValue;
		}
		return val;
	}
	
	public String getString(String header) {
		return getString(header, null);
	}
	
	public Double getDouble(String header) throws NumberFormatException {
		String val = get(header);
		if (StringUtils.isEmpty(val)) {
			return null;
		} else {
			return Double.parseDouble(val);
		}
	}
	
	public Integer getInt(String header) throws NumberFormatException {
		String val = get(header);
		if (StringUtils.isEmpty(val)) {
			return null;
		} else {
			return Integer.parseInt(val);
		}
	}
	
	public Boolean getBool(String header) {
		String val = get(header);
		if (StringUtils.isEmpty(val)) {
			return null;
		} else {
			return BooleanUtils.toBoolean(val);
		}
	}
	
	/**
	 * Converts a string into a java.sql.Time Object
	 * @param header a string representing time. Only hours and minutes are expected (eg. "17:00") 
	 * @return a java.sql.Time Object representing the time, null if the parameter is empty.
	 */
	public Time getSqlTime(String header) {
		String val = get(header);
		if (StringUtils.isEmpty(val)) {
			return null;
		} else {
			return Time.valueOf(val+":00"); //Append :00 just because that's what Bahmni does to allow users to only provide hours and minutes
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(line);
	}
}
