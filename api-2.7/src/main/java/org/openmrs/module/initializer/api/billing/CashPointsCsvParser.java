package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.CashPointService;
import org.openmrs.module.billing.api.model.CashPoint;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:2.0.0 - 9.*" })
public class CashPointsCsvParser extends CsvParser<CashPoint, BaseLineProcessor<CashPoint>> {
	
	private final CashPointService cashPointService;
	
	@Autowired
	public CashPointsCsvParser(@Qualifier("cashPointService") CashPointService cashPointService,
	    CashPointsLineProcessor processor) {
		super(processor);
		this.cashPointService = cashPointService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CASH_POINTS;
	}
	
	@Override
	public CashPoint bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		CashPoint cashPoint = null;
		if (StringUtils.isNotBlank(uuid)) {
			cashPoint = cashPointService.getCashPointByUuid(uuid);
		}
		if (cashPoint == null) {
			cashPoint = new CashPoint();
			if (StringUtils.isNotBlank(uuid)) {
				cashPoint.setUuid(uuid);
			}
		}
		return cashPoint;
	}
	
	@Override
	public CashPoint save(CashPoint instance) {
		return cashPointService.saveCashPoint(instance);
	}
}
