package org.openmrs.module.initializer.validator;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class Validator {
	
	public static void main(String[] args) {
		//		JUnitCore junit = new JUnitCore();
		//		Result result = junit.run(ConfigValidationTest.class);
		//		System.out.println("Success: " + result.wasSuccessful());
		
		JUnitCore.main(ConfigValidationTest.class.getCanonicalName());
		
	}
}
