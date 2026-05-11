package org.openmrs.module.initializer.api.procedure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.emrapi.procedure.ProcedureService;
import org.openmrs.module.emrapi.procedure.ProcedureType;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvLine;

public class ProcedureTypesCsvParserTest {
	
	private static final String[] HEADERS = { "Uuid", "Name", "Description" };
	
	@Mock
	private ProcedureService procedureService;
	
	@Mock
	private ProcedureTypeLineProcessor lineProcessor;
	
	@InjectMocks
	private ProcedureTypesCsvParser parser;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldReturnExistingTypeWhenUuidMatches() {
		String uuid = "9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b1234";
		ProcedureType existing = new ProcedureType();
		when(procedureService.getProcedureTypeByUuid(uuid)).thenReturn(existing);
		
		ProcedureType result = parser.bootstrap(new CsvLine(HEADERS, new String[] { uuid, "Appendectomy", "" }));
		
		assertEquals(existing, result);
		verify(procedureService, never()).getProcedureTypesByName(anyString());
	}
	
	@Test
	public void shouldNotFallBackToNameWhenUuidIsProvidedButMisses() {
		// A typo in the UUID must NOT silently rebind to a name-matching record — that
		// would mutate an unrelated row. Instead, the parser creates a new instance bearing
		// the user-supplied UUID.
		String typoedUuid = "00000000-0000-0000-0000-000000000000";
		when(procedureService.getProcedureTypeByUuid(typoedUuid)).thenReturn(null);
		
		ProcedureType result = parser.bootstrap(new CsvLine(HEADERS, new String[] { typoedUuid, "Appendectomy", "" }));
		
		assertNotNull(result);
		assertNull(result.getId());
		assertEquals(typoedUuid, result.getUuid());
		verify(procedureService, never()).getProcedureTypesByName(anyString());
	}
	
	@Test
	public void shouldLookUpByNameWhenUuidIsBlank() {
		ProcedureType existing = new ProcedureType();
		existing.setName("Appendectomy");
		existing.setUuid("9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b9999");
		when(procedureService.getProcedureTypesByName("Appendectomy")).thenReturn(Collections.singletonList(existing));
		
		ProcedureType result = parser.bootstrap(new CsvLine(HEADERS, new String[] { "", "Appendectomy", "" }));
		
		assertSame(existing, result);
	}
	
	@Test
	public void shouldReturnFreshInstanceWhenNeitherUuidNorNameMatches() {
		when(procedureService.getProcedureTypesByName("Brand New")).thenReturn(Collections.<ProcedureType> emptyList());
		
		ProcedureType result = parser.bootstrap(new CsvLine(HEADERS, new String[] { "", "Brand New", "" }));
		
		assertNotNull(result);
		assertNull(result.getId());
		assertNotEquals("", result.getUuid());
	}
	
	@Test
	public void shouldReturnFreshInstanceWhenBothUuidAndNameAreBlank() {
		ProcedureType result = parser.bootstrap(new CsvLine(HEADERS, new String[] { "", "", "" }));
		
		assertNotNull(result);
		assertNull(result.getId());
		assertNotNull(result.getUuid());
		verify(procedureService, never()).getProcedureTypeByUuid(anyString());
		verify(procedureService, never()).getProcedureTypesByName(anyString());
	}
	
	@Test
	public void shouldFilterMultiMatchByExactName() {
		ProcedureType extra = new ProcedureType();
		extra.setName("Appendectomy Plus");
		ProcedureType exact = new ProcedureType();
		exact.setName("Appendectomy");
		exact.setUuid("9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b9999");
		when(procedureService.getProcedureTypesByName("Appendectomy")).thenReturn(Arrays.asList(extra, exact));
		
		ProcedureType result = parser.bootstrap(new CsvLine(HEADERS, new String[] { "", "Appendectomy", "" }));
		
		assertSame(exact, result);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowWhenMultipleExactNameMatches() {
		ProcedureType a = new ProcedureType();
		a.setName("Appendectomy");
		a.setUuid("9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b0001");
		ProcedureType b = new ProcedureType();
		b.setName("Appendectomy");
		b.setUuid("9d9aa7c1-2e0e-4b1e-8b57-7e6f1a0b0002");
		when(procedureService.getProcedureTypesByName("Appendectomy")).thenReturn(Arrays.asList(a, b));
		
		parser.bootstrap(new CsvLine(HEADERS, new String[] { "", "Appendectomy", "" }));
	}
	
	@Test
	public void shouldReportProcedureTypesDomain() {
		assertEquals(Domain.PROCEDURE_TYPES, parser.getDomain());
	}
	
	@Test
	public void shouldDelegateSaveToProcedureService() {
		ProcedureType incoming = new ProcedureType();
		ProcedureType persisted = new ProcedureType();
		when(procedureService.saveProcedureType(incoming)).thenReturn(persisted);
		
		ProcedureType result = parser.save(incoming);
		
		assertSame(persisted, result);
		verify(procedureService).saveProcedureType(incoming);
	}
}
