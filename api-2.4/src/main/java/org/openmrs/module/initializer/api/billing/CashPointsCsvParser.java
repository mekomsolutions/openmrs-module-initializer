package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.ICashPointService;
import org.openmrs.module.billing.api.model.CashPoint;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:1.1.0 - 9.*" })
public class CashPointsCsvParser extends CsvParser<CashPoint, BaseLineProcessor<CashPoint>> {
	
	private final ICashPointService iCashPointService;
	
	@Autowired
	public CashPointsCsvParser(@Qualifier("cashierCashPointService") ICashPointService iCashPointService,
	    CashPointsLineProcessor processor) {
		super(processor);
		this.iCashPointService = iCashPointService;
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
			cashPoint = iCashPointService.getByUuid(uuid);
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
		return iCashPointService.save(instance);
	}
}
