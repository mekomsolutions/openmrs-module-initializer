package org.openmrs.module.initializer.api.queues;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.queue.api.QueueService;
import org.openmrs.module.queue.model.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "queue:*" })
public class QueueCsvParser extends CsvParser<Queue, BaseLineProcessor<Queue>> {
	
	private final QueueService queueService;
	
	@Autowired
	public QueueCsvParser(@Qualifier("queue.QueueService") QueueService queueService, QueueLineProcessor processor) {
		super(processor);
		this.queueService = queueService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.QUEUES;
	}
	
	@Override
	public Queue bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		Queue queue = queueService.getQueueByUuid(uuid).orElse(new Queue());
		if (StringUtils.isNotBlank(uuid)) {
			queue.setUuid(uuid);
		}
		return queue;
	}
	
	@Override
	public Queue save(Queue instance) {
		return queueService.createQueue(instance);
	}
}
