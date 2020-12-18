package org.openmrs.module.initializer.api.visittypes;

import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This is the first level line processor for visitTypes. It allows to parse and
 * save visit types with the minimal set of required fields.
 */
@Component
public class VisitTypeLineProcessor extends BaseLineProcessor<VisitType> {

	private VisitService visitService;

	@Autowired
	public VisitTypeLineProcessor(@Qualifier("visitService") VisitService visitService) {
		this.visitService = visitService;
	}

	@Override
	public VisitType fill(VisitType visitType, CsvLine line) throws IllegalArgumentException {

		visitType.setName(line.getName(true));
		visitType.setDescription(line.getString(HEADER_DESC, line.getName(true) + " Visit"));

		return visitType;
	}
}
