package org.openmrs.module.initializer.api;

import java.util.Arrays;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class CsvLine {
	
	protected String[] line;
	
	@SuppressWarnings("rawtypes")
	protected BaseLineProcessor processor;
	
	@SuppressWarnings("rawtypes")
	public CsvLine(BaseLineProcessor processor, String[] line) {
		this.line = line;
		this.processor = processor;
	}
	
	public String[] asLine() {
		return line;
	}
	
	public String get(String header, boolean canThrowException) throws IllegalArgumentException {
		String val = null;
		try {
			val = line[processor.getColumn(header)];
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
	
	@Override
	public String toString() {
		return Arrays.toString(line);
	}
}
