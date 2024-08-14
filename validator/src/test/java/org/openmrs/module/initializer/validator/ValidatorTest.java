package org.openmrs.module.initializer.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
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
		String trimmedSql = readFile(trimmedCielFile.getAbsolutePath(), Charset.forName("UTF-8"));
		File expectedFile = new File(getClass().getClassLoader().getResource("trimmed_ciel_excerpt.txt").toURI());
		String expectedSql = readFile(expectedFile.getAbsolutePath(), Charset.forName("UTF-8"));
		assertEquals(expectedSql, trimmedSql);
	}
	
	@Test
	public void test_replaceInnerSingleQuotes() {
		String line = "INSERT INTO `concept_description` VALUES (5350,118409,'Impairment of biliary flow at any level from the hepatocyte to Vater\\'s ampulla.','en',1,'2007-10-18 04:28:24',1,NULL,'5350FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF');";
		String expectedLine = "INSERT INTO `concept_description` VALUES (5350,118409,'Impairment of biliary flow at any level from the hepatocyte to Vater''s ampulla.','en',1,'2007-10-18 04:28:24',1,NULL,'5350FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF');";
		assertEquals(expectedLine, Validator.escapeSingleQuotes(line));
	}
	
	@Ignore
	//may work locally but will fail on CIs envs such as Travis, etc
	@Test
	public void initLogFilePath_shouldCreateLogFileInSpecifiedFolder() throws MalformedURLException {
		// setup
		String subDir = RandomStringUtils.random(10, true, false);
		String dir = Paths.get("target", subDir).toUri().toURL().toString();
		
		// replay
		Validator.setLogFilePath(dir);
		
		// verif
		assertEquals(Paths.get(dir, "initializer.log"), Validator.getLogFilePath());
		assertTrue(Files.exists(Validator.getLogFilePath()));
	}
	
	@Test
	//may work locally but will fail on CIs envs such as Travis, etc
	public void initLogFilePath_shouldCreateLogFileInJarFolderAsFallback() throws Exception {
		// setup
		String dir = Paths.get("http://example.com").toString();
		
		// replay
		Validator.setLogFilePath(dir);
		
		// verif
		assertEquals(Validator.getJarDirPath().resolve("initializer.log"), Validator.getLogFilePath());
		assertTrue(Files.exists(Validator.getLogFilePath()));
	}
	
}
