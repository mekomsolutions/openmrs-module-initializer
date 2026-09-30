package org.openmrs.module.initializer.api;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.messagesource.MessageSourceService;

/**
 * Extends the BaseLineProcessor by adding methods to retrieve name and description based
 * on the configured name and description and translations of these if appropriate.
 */
public abstract class BaseMetadataLineProcessor<T extends OpenmrsMetadata> extends BaseLineProcessor<T> {
	
	public static final String TRANSLATE = "_translate_";
	
	protected MessageSourceService messageSourceService;
	
	public BaseMetadataLineProcessor(MessageSourceService messageSourceService) {
		super();
		this.messageSourceService = messageSourceService;
	}
	
	/**
	 * @return the name defined in the current line, which can indicate if it should come from a message
	 *         code
	 */
	public String getName(T metadata, CsvLine line) {
		String name = line.getName(true);
		if (TRANSLATE.equals(name)) {
			String code = "ui.i18n." + metadata.getClass().getSimpleName() + ".name." + metadata.getUuid();
			name = messageSourceService.getMessage(code);
		}
		return name;
	}
	
	/**
	 * @return the description defined in the current line, which can indicate if it should come from a
	 *         message code
	 */
	public String getDescription(T metadata, CsvLine line) {
		String description = line.get(HEADER_DESC);
		if (TRANSLATE.equals(description)) {
			String code = "ui.i18n." + metadata.getClass().getSimpleName() + ".description." + metadata.getUuid();
			description = messageSourceService.getMessage(code);
		}
		return description;
	}
}
