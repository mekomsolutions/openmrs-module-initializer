package org.openmrs.module.initializer.api.conceptreferencerange;

import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(openmrsPlatformVersion = "2.7.0 - 2.7.*")
public class ConceptReferenceRangeService implements OpenmrsService {
	
	private final ConceptService conceptService;
	
	@Autowired
	public ConceptReferenceRangeService(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	/**
	 * Gets an <code>ConceptReferenceRange</code> by uuid.
	 * 
	 * @param uuid uuid of the <code>ConceptReferenceRange</code>
	 * @return conceptReferenceRange or <code>null</code>
	 */
	public ConceptReferenceRange getConceptReferenceRangeByUuid(String uuid) {
		return Context.getConceptService().getConceptReferenceRangeByUuid(uuid);
	}
	
	/**
	 * Saves a <code>ConceptReferenceRange</code>.
	 * 
	 * @param referenceRange the <code>ConceptReferenceRange</code> to be saved
	 * @return the saved <code>ConceptReferenceRange</code>
	 */
	public ConceptReferenceRange saveReferenceRange(ConceptReferenceRange referenceRange) {
		return Context.getConceptService().saveConceptReferenceRange(referenceRange);
	}
	
	/**
	 * Gets a {@link ConceptNumeric} by uuid.
	 * 
	 * @param uuid uuid of the {@link ConceptNumeric}
	 */
	public ConceptNumeric getConceptNumericByUuid(String uuid) {
		return Context.getConceptService().getConceptNumericByUuid(uuid);
	}
	
	@Override
	public void onStartup() {
	}
	
	@Override
	public void onShutdown() {
	}
}
