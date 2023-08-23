package org.openmrs.module.initializer.api.fhir.cpm;

import org.apache.commons.lang.StringUtils;
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

import java.util.Optional;

@OpenmrsProfile(modules = { "fhir2:1.6.* - 9.*" })
public class FhirContactPointMapCsvParser extends CsvParser<FhirContactPointMap, BaseLineProcessor<FhirContactPointMap>> {
	
	private static final String ATTRIBUTE_TYPE_DOMAIN_HEADER = "Attribute Type Domain";
	
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
		String attributeTypeDomain = line.getString(ATTRIBUTE_TYPE_DOMAIN_HEADER);
		BaseAttributeType<?> baseAttributeType;
		PersonAttributeType personAttributeType;
		FhirContactPointMap newFhirContactPointMap = new FhirContactPointMap();
		
		if (StringUtils.isBlank(attributeTypeDomain)) {
			throw new IllegalArgumentException("Attribute Type Domain is missing.");
		}
		
		switch (attributeTypeDomain) {
			case LOCATION:
				baseAttributeType = locationService.getLocationAttributeTypeByName(attributeTypeDomain);
				Optional<FhirContactPointMap> locationContactPointMap = fhirContactPointMapService.getFhirContactPointMapForAttributeType(
						baseAttributeType);
				
				if (locationContactPointMap.isPresent()) {
					return locationContactPointMap.get();
				}
				
				newFhirContactPointMap.setSystem(locationContactPointMap.get().getSystem());
				newFhirContactPointMap.setUse(locationContactPointMap.get().getUse());
				newFhirContactPointMap.setRank(locationContactPointMap.get().getRank());
				break;
			case PERSON:
				personAttributeType = personService.getPersonAttributeTypeByName(attributeTypeDomain);
				Optional<FhirContactPointMap> contactPointMap = fhirContactPointMapService.getFhirContactPointMapForPersonAttributeType(
						personAttributeType);
				
				if (contactPointMap.isPresent()) {
					return contactPointMap.get();
				}
				
				FhirContactPointMap newPersonContactPointMap = new FhirContactPointMap();
				newPersonContactPointMap.setSystem(contactPointMap.get().getSystem());
				newPersonContactPointMap.setUse(contactPointMap.get().getUse());
				newPersonContactPointMap.setRank(contactPointMap.get().getRank());
				break;
			case PROVIDER:
				baseAttributeType = providerService.getProviderAttributeTypeByUuid(attributeTypeDomain);
				Optional<FhirContactPointMap> providerContactPointMap = fhirContactPointMapService.getFhirContactPointMapForAttributeType(
						baseAttributeType);
				
				if (providerContactPointMap.isPresent()) {
					return providerContactPointMap.get();
				}
				
				FhirContactPointMap newProviderContactPointMap = new FhirContactPointMap();
				newProviderContactPointMap.setSystem(providerContactPointMap.get().getSystem());
				newProviderContactPointMap.setUse(providerContactPointMap.get().getUse());
				newProviderContactPointMap.setRank(providerContactPointMap.get().getRank());
				break;
		}
		return newFhirContactPointMap;
	}

	
	@Override
	public FhirContactPointMap save(FhirContactPointMap instance) {
		return fhirContactPointMapService.saveFhirContactPointMap(instance);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.FHIR_CONTACT_POINT_MAP;
	}
}
