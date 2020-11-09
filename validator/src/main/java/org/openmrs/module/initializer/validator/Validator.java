package org.openmrs.module.initializer.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.JUnitCore;

public class Validator {
	
	public static List<String> arguments;
	
	public static void main(String[] args) {
		
		arguments = Collections.unmodifiableList(Arrays.asList(args));
		
		//		JUnitCore junit = new JUnitCore();
		//		Result result = junit.run(ConfigValidationTest.class);
		//		System.out.println("Success: " + result.wasSuccessful());
		
		JUnitCore.main(ConfigurationTest.class.getCanonicalName());
		
	}
}
