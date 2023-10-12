package org.openmrs.module.initializer.api.queues;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.openmrs.module.queue.model.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This is the first level line processor for a Queue
 */
@OpenmrsProfile(modules = { "queue:*" })
public class QueueLineProcessor extends BaseLineProcessor<Queue> {
	
	protected static String HEADER_SERVICE = "service";
	
	protected static String HEADER_LOCATION = "location";
	
	private final ConceptService conceptService;
	
	private final LocationService locationService;
	
	@Autowired
	public QueueLineProcessor(@Qualifier("conceptService") ConceptService conceptService,
	    @Qualifier("locationService") LocationService locationService) {
		super();
		this.conceptService = conceptService;
		this.locationService = locationService;
	}
	
	@Override
	public Queue fill(Queue queue, CsvLine line) throws IllegalArgumentException {
		queue.setName(line.get(HEADER_NAME, true));
		queue.setDescription(line.getString(HEADER_DESC));
		if (line.containsHeader(HEADER_SERVICE)) {
			String service = line.getString(HEADER_SERVICE);
			if (StringUtils.isNotBlank(service)) {
				queue.setService(Utils.fetchConcept(service, conceptService));
			} else {
				queue.setService(null);
			}
		}
		if (line.containsHeader(HEADER_LOCATION)) {
			String location = line.getString(HEADER_LOCATION);
			if (StringUtils.isNotBlank(location)) {
				queue.setLocation(Utils.fetchLocation(location, locationService));
			} else {
				queue.setLocation(null);
			}
		}
		return queue;
	}
}
