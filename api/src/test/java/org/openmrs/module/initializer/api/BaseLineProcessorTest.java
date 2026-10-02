package org.openmrs.module.initializer.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.initializer.api.c.LocalizedHeader;

public class BaseLineProcessorTest {
	
	@Test
	public void getVersion_shouldReturnTheVersionRhs() {
		
		String version = BaseLineProcessor.VERSION_LHS + "1";
		
		Set<String> hl1 = new HashSet<String>();
		hl1.add("Name");
		hl1.add("Description");
		hl1.add("Class");
		hl1.add("Datatype");
		hl1.add(version);
		hl1.add("Numeric unit");
		hl1.add("Mapping");
		
		// a regular header line
		Assertions.assertEquals("1", BaseLineProcessor.getVersion(hl1.toArray(new String[hl1.size()])));
		
		// the empty header line
		String[] hl2 = new String[0];
		Assertions.assertEquals(BaseLineProcessor.UNDEFINED_METADATA_VALUE, BaseLineProcessor.getVersion(hl2));
		
		// the header line with no version
		Set<String> hl3 = new HashSet<String>(hl1);
		hl3.remove(version);
		Assertions.assertEquals(BaseLineProcessor.UNDEFINED_METADATA_VALUE,
		    BaseLineProcessor.getVersion(hl3.toArray(new String[hl3.size()])));
	}
	
	@Test
	public void getVersion_shouldFailWithMultipleVersions() {
		assertThrows(IllegalArgumentException.class, () -> {
			
			String version1 = BaseLineProcessor.VERSION_LHS + "1";
			String version2 = BaseLineProcessor.VERSION_LHS + "2";
			
			Set<String> hl = new HashSet<String>();
			hl.add("Name");
			hl.add("Description");
			hl.add(version2);
			hl.add("Class");
			hl.add("Datatype");
			hl.add(version1);
			hl.add("Numeric unit");
			hl.add("Mapping");
			
			BaseLineProcessor.getVersion(hl.toArray(new String[hl.size()]));
			
		});
	}
	
	@Test
	public void getVersion_shouldFailWithNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			BaseLineProcessor.getVersion(null);
			
		});
	}
	
	@Test
	public void createIndexMap_shouldMapHeaderNames() {
		
		String version = BaseLineProcessor.VERSION_LHS + "1";
		
		List<String> hl1 = new ArrayList<String>();
		hl1.add("Name");
		hl1.add("Description");
		hl1.add("Class");
		hl1.add("Datatype");
		hl1.add(version);
		hl1.add("Numeric unit");
		hl1.add("Mapping");
		
		// a regular header line
		Map<String, Integer> indexMap = BaseLineProcessor.createIndexMap(hl1.toArray(new String[hl1.size()]));
		Assertions.assertEquals(0, BaseLineProcessor.getColumn(indexMap, "Name"));
		Assertions.assertEquals(1, BaseLineProcessor.getColumn(indexMap, "Description"));
		Assertions.assertEquals(2, BaseLineProcessor.getColumn(indexMap, "Class"));
		Assertions.assertEquals(3, BaseLineProcessor.getColumn(indexMap, "Datatype"));
		Assertions.assertEquals(4, BaseLineProcessor.getColumn(indexMap, version));
		Assertions.assertEquals(5, BaseLineProcessor.getColumn(indexMap, "Numeric unit"));
		Assertions.assertEquals(6, BaseLineProcessor.getColumn(indexMap, "Mapping"));
	}
	
	@Test
	public void createIndexMap_shouldFailWithNonUniqueHeaders() {
		assertThrows(IllegalArgumentException.class, () -> {
			
			List<String> hl1 = new ArrayList<String>();
			hl1.add("Name");
			hl1.add("Description");
			hl1.add("Class");
			hl1.add("Datatype");
			hl1.add("Numeric unit");
			hl1.add("Mapping");
			hl1.add("Datatype");
			
			BaseLineProcessor.createIndexMap(hl1.toArray(new String[hl1.size()]));
			
		});
	}
	
	@Test
	public void getLocalizedHeaders_shouldListLocalizedHeaders() {
		
		String version = BaseLineProcessor.VERSION_LHS + "1";
		
		List<String> hl1 = new ArrayList<String>();
		hl1.add("Name:en");
		hl1.add("Name:km_KH");
		hl1.add("Description:en");
		hl1.add("Description:km_KH");
		hl1.add("Class");
		hl1.add("Datatype");
		hl1.add(version);
		hl1.add("Numeric unit");
		hl1.add("Mapping");
		
		Locale localeKm = new Locale("km", "KH");
		
		// a regular header line
		Map<String, LocalizedHeader> lhMap = LocalizedHeader.getLocalizedHeadersMap(hl1.toArray(new String[hl1.size()]));
		
		// Verif
		Assertions.assertEquals(2, lhMap.size());
		LocalizedHeader lh = null;
		Assertions.assertTrue(lhMap.containsKey("name"));
		lh = lhMap.get("name");
		Assertions.assertEquals(2, lh.getLocales().size());
		Assertions.assertTrue(lh.getLocales().contains(Locale.ENGLISH));
		Assertions.assertTrue(lh.getLocales().contains(localeKm));
		lh = lhMap.get("description");
		Assertions.assertEquals(2, lh.getLocales().size());
		Assertions.assertTrue(lh.getLocales().contains(Locale.ENGLISH));
		Assertions.assertTrue(lh.getLocales().contains(localeKm));
	}
	
	@Test
	public void getUuid_shouldValidateUuid() {
		
		String uuid_1 = "da681b9e-315c-11e7-93ae-92361f002671";
		String uuid_4 = "0de631be-86dd-4288-9a71-585e28bd20e1";
		
		String[] headerLine = { "uuid" };
		{
			String[] line = { uuid_1 };
			String uuid = new CsvLine(headerLine, line).getUuid();
			Assertions.assertEquals(uuid_1, uuid);
		}
		{
			String[] line = { uuid_4 };
			String uuid = new CsvLine(headerLine, line).getUuid();
			Assertions.assertEquals(uuid_4, uuid);
		}
		{
			String[] line = { null };
			String uuid = new CsvLine(headerLine, line).getUuid();
			Assertions.assertEquals(null, uuid);
		}
		{
			String[] line = { "" };
			String uuid = new CsvLine(headerLine, line).getUuid();
			Assertions.assertEquals("", uuid);
		}
	}
	
	@Test
	public void getUuid_shouldFailOnInvalidUuid() {
		assertThrows(IllegalArgumentException.class, () -> {
			String[] headerLine = { "uuid" };
			String[] line = { "foobar" };
			new CsvLine(headerLine, line).getUuid();
			
		});
	}
}
