package org.openmrs.module.initializer.api.queues;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.queue.model.Queue;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "queue:*" })
public class QueueLoader extends BaseCsvLoader<Queue, QueueCsvParser> {
	
	@Autowired
	public void setParser(QueueCsvParser parser) {
		this.parser = parser;
	}
}
