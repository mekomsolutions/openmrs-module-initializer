package org.openmrs.module.initializer.validator;

import static groovy.json.internal.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {
	
	/*
	 * https://stackoverflow.com/a/326440/321797
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	@Test
	public void test_trimCielSql() throws URISyntaxException, IOException {
		// setup
		File cielFile = new File(getClass().getClassLoader().getResource("ciel_excerpt.txt").toURI());
		
		// replay
		File trimmedCielFile = Validator.trimCielSqlFile(cielFile);
		
		// verify
		String trimmedSql = readFile(trimmedCielFile.getAbsolutePath(), UTF_8);
		File expectedFile = new File(getClass().getClassLoader().getResource("trimmed_ciel_excerpt.txt").toURI());
		String expectedSql = readFile(expectedFile.getAbsolutePath(), UTF_8);
		Assert.assertEquals(expectedSql, trimmedSql);
	}
	
	@Test
	public void test_replaceInnerSingleQuotes() {
		String line = "INSERT INTO `concept_description` VALUES (5350,118409,'Impairment of biliary flow at any level from the hepatocyte to Vater\\'s ampulla.','en',1,'2007-10-18 04:28:24',1,NULL,'5350FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF');";
		String expectedLine = "INSERT INTO `concept_description` VALUES (5350,118409,'Impairment of biliary flow at any level from the hepatocyte to Vater''s ampulla.','en',1,'2007-10-18 04:28:24',1,NULL,'5350FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF');";
		Assert.assertEquals(expectedLine, Validator.replaceInnerSingleQuotes(line));
	}
}
