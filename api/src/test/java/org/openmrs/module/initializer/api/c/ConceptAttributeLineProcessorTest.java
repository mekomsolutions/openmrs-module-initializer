package org.openmrs.module.initializer.api.c;

import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.openmrs.module.initializer.api.BaseAttributeLineProcessor.HEADER_ATTRIBUTE_PREFIX;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.ConceptAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.initializer.api.CsvLine;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ConceptAttributeLineProcessorTest {
	
	private MockedStatic<Context> contextMock;
	
	private MockedStatic<CustomDatatypeUtil> customDatatypeUtilMock;
	
	@AfterEach
	public void closeStaticMocks() {
		contextMock.close();
		customDatatypeUtilMock.close();
	}
	
	private ConceptService cs;
	
	private ConceptAttributeLineProcessor processor;
	
	private static final String AUDIT_DATE_ATT_TYPE_UUID = "nb803h59-a1b8-4da9-969a-9a18sf3241f0";
	
	private static final String EMAIL_ATT_TYPE_UUID = "ghe03f57-ty5f-4dav-960a-4a18df3241fe";
	
	private DateDatatype dateDatatype;
	
	@BeforeEach
	public void setup() {
		contextMock = Mockito.mockStatic(Context.class);
		customDatatypeUtilMock = Mockito.mockStatic(CustomDatatypeUtil.class);
		
		when(CustomDatatypeUtil.getDatatype(eq(FreeTextDatatype.class.getName()), any()))
		        .thenReturn((CustomDatatype) new FreeTextDatatype());
		when(CustomDatatypeUtil.getDatatype(eq(DateDatatype.class.getName()), any()))
		        .thenReturn((CustomDatatype) new DateDatatype());
		when(Context.getRuntimeProperties()).thenReturn(new Properties());
		
		cs = mock(ConceptService.class);
		processor = new ConceptAttributeLineProcessor(cs);
		dateDatatype = new DateDatatype();
		
		ConceptAttributeType autditDateAttType = new ConceptAttributeType();
		autditDateAttType.setName("Audit Date");
		autditDateAttType.setUuid(AUDIT_DATE_ATT_TYPE_UUID);
		autditDateAttType.setDatatypeClassname(DateDatatype.class.getName());
		
		ConceptAttributeType emailAttrType = new ConceptAttributeType();
		emailAttrType.setName("Email Address");
		emailAttrType.setDatatypeClassname(FreeTextDatatype.class.getName());
		
		when(cs.getConceptAttributeTypeByUuid(AUDIT_DATE_ATT_TYPE_UUID)).thenReturn(autditDateAttType);
		when(cs.getConceptAttributeTypeByUuid(EMAIL_ATT_TYPE_UUID)).thenReturn(emailAttrType);
	}
	
	@Test
	public void fill_shouldParseConceptAttributes() {
		// Setup
		String[] headerLine = { HEADER_ATTRIBUTE_PREFIX + AUDIT_DATE_ATT_TYPE_UUID,
		        HEADER_ATTRIBUTE_PREFIX + EMAIL_ATT_TYPE_UUID };
		String[] line = { "2013-03-19", "jdoe@example.com" };
		
		// Replay
		Concept concept = processor.fill(new Concept(), new CsvLine(headerLine, line));
		
		// Verify
		Collection<ConceptAttribute> attributes = concept.getActiveAttributes();
		Assertions.assertEquals(2, attributes.size());
		Object[] attributesArray = attributes.toArray();
		Object auditDate = ((ConceptAttribute) attributesArray[0]).getValue();
		Assertions.assertTrue(auditDate instanceof Date);
		MatcherAssert.assertThat(dateDatatype.serialize(((Date) auditDate)), is("2013-03-19"));
		MatcherAssert.assertThat(((ConceptAttribute) attributesArray[1]).getValue(), is("jdoe@example.com"));
	}
}
