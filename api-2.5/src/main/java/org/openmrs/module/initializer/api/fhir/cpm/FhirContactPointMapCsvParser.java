package org.openmrs.module.initializer.api.fhir.cpm;

import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.fhir2.api.FhirContactPointMapService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.fhir2.model.FhirContactPointMap;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "fhir2:1.11.* - 9.*" }, openmrsPlatformVersion = "2.5.13 - 2.5.*, 2.6.2 - 2.6.*, 2.7.* - 9.*")
public class FhirContactPointMapCsvParser extends CsvParser<FhirContactPointMap, BaseLineProcessor<FhirContactPointMap>> {
	
	public static final String ATTRIBUTE_TYPE_DOMAIN_HEADER = "Entity name";
	
	public static final String ATTRIBUTE_TYPE = "Attribute type";
	
	private static final String LOCATION = "location";
	
	private static final String PERSON = "person";
	
	private static final String PROVIDER = "provider";
	
	private final LocationService locationService;
	
	private final PersonService personService;
	
	private final ProviderService providerService;
	
	private final FhirContactPointMapService fhirContactPointMapService;
	
	@Autowired
	protected FhirContactPointMapCsvParser(FhirContactPointMapService fhirContactPointMapService,BaseLineProcessor<FhirContactPointMap> lineProcessor,
			LocationService locationService, PersonService personService, ProviderService providerService) {
		super(lineProcessor);
		this.fhirContactPointMapService = fhirContactPointMapService;
		this.locationService = locationService;
		this.personService = personService;
		this.providerService = providerService;
	}
	
	@Override
	public FhirContactPointMap bootstrap(CsvLine line) throws IllegalArgumentException {
		FhirContactPointMap contactPointMap = null;
		if (line.getUuid() != null) {
			contactPointMap = fhirContactPointMapService.getFhirContactPointMapByUuid(line.getUuid())
					.orElse(null);
		}
		
		if (contactPointMap != null) {
			return contactPointMap;
		}
		
		String attributeTypeDomain = line.get(ATTRIBUTE_TYPE_DOMAIN_HEADER, true);
		String attributeType = line.get(ATTRIBUTE_TYPE, true);
		
		if (attributeTypeDomain.equals(PERSON)) {
			PersonAttributeType personAttributeType = getPersonAttributeType(attributeType);
			
			if (personAttributeType == null) {
				throw new IllegalArgumentException("PersonAttributeType " + attributeType
						+ " does not exist. Please ensure your Initializer configuration contains this attribute type.");
			}
			
			contactPointMap = fhirContactPointMapService.getFhirContactPointMapForPersonAttributeType(personAttributeType)
					.orElse(null);
		} else {
			BaseAttributeType<?> baseAttributeType = getBaseAttributeType(attributeTypeDomain, attributeType);
			
			if (baseAttributeType == null) {
				throw new IllegalArgumentException(
						"Could not find attribute type " + attributeType + " for attribute domain " + attributeTypeDomain);
			}
			
			contactPointMap = fhirContactPointMapService.getFhirContactPointMapForAttributeType(baseAttributeType)
					.orElse(null);
		}
		
		if (contactPointMap != null) {
			return contactPointMap;
		}
		
		return new FhirContactPointMap();
	}

	
	@Override
	public FhirContactPointMap save(FhirContactPointMap instance) {
		return fhirContactPointMapService.saveFhirContactPointMap(instance);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.FHIR_CONTACT_POINT_MAP;
	}
	
	protected PersonAttributeType getPersonAttributeType(String attributeType) {
		PersonAttributeType personAttributeType = personService.getPersonAttributeTypeByName(attributeType);
		
		if (personAttributeType != null) {
			return personAttributeType;
		}
		
		personAttributeType = personService.getPersonAttributeTypeByUuid(attributeType);
		
		return personAttributeType;
	}
	
	protected BaseAttributeType<?> getBaseAttributeType(String attributeDomain, String attributeType) {
		BaseAttributeType<?> baseAttributeType = null;
		
		switch (attributeDomain) {
			case LOCATION:
				baseAttributeType = locationService.getLocationAttributeTypeByName(attributeType);
				
				if (baseAttributeType != null) {
					return baseAttributeType;
				}
				
				return locationService.getLocationAttributeTypeByUuid(attributeType);
				break;
			case PROVIDER:
				baseAttributeType = providerService.getProviderAttributeTypeByName(attributeType);
				
				if (baseAttributeType != null) {
					return baseAttributeType;
				}
				
				return providerService.getProviderAttributeTypeByUuid(attributeType);
			break;
		}
		return baseAttributeType;
	}
}
