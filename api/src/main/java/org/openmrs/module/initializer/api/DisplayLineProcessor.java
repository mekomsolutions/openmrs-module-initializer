package org.openmrs.module.initializer.api;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.c.LocalizedHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * The line processor to process the 'diplay:xy' localised headers. This line processor does not
 * modify the instance being filled, this is a decoupled job that saves i18n messages inferred from
 * those headers and the instance being processed.
 */
@Component
public class DisplayLineProcessor extends BaseLineProcessor<BaseOpenmrsObject> {
	
	final public static String HEADER_DISPLAY = "display";
	
	private InitializerMessageSource msgSource;
	
	@Autowired
	public DisplayLineProcessor(@Qualifier("initializer.InitializerMessageSource") InitializerMessageSource msgSource) {
		super();
		this.msgSource = msgSource;
	}
	
	@Override
	public BaseOpenmrsObject fill(BaseOpenmrsObject instance, CsvLine line) throws IllegalArgumentException {
		
		LocalizedHeader l10nHeader = LocalizedHeader.getLocalizedHeader(line.getHeaderLine(), HEADER_DISPLAY);
		
		l10nHeader.getLocales().stream().forEach(locale -> {
			String display = line.get(l10nHeader.getI18nHeader(locale));
			if (!StringUtils.isEmpty(display)) {
				String shortClassName = unProxy(instance.getClass().getSimpleName());
				msgSource.addPresentation(new PresentationMessage(
				        "ui.i18n." + shortClassName + ".name." + instance.getUuid(), locale, display, null));
				msgSource.addPresentation(new PresentationMessage("org.openmrs." + shortClassName + "." + instance.getUuid(),
				        locale, display, null));
			}
		});
		
		return instance; // returned but wasn't changed anyway
	}
	
	/*
	 * Turns a proxy short class name into the original short class name.
	 * Eg. "EncounterType$HibernateProxy$ODcBnusu" or "EncounterType_$$_javassist_26" → "EncounterType"
	 */
	private String unProxy(String shortClassName) {
		int underscoreIndex = shortClassName.indexOf("_$");
		if (underscoreIndex > 0) {
			shortClassName = shortClassName.substring(0, underscoreIndex);
		} else {
			underscoreIndex = shortClassName.indexOf("$");
			if (underscoreIndex > 0) {
				shortClassName = shortClassName.substring(0, underscoreIndex);
			}
		}
		return shortClassName;
	}
}
