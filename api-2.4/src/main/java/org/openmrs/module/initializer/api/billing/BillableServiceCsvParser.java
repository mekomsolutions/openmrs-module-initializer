package org.openmrs.module.initializer.api.billing;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.web.rest.resource.BillableServiceResource;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@OpenmrsProfile(modules = { "billing:*" })
public class BillableServiceCsvParser extends CsvParser<BillableService, BaseLineProcessor<BillableService>> {

    private final BillableServiceResource billableServiceResource;

    @Autowired
    public BillableServiceCsvParser(@Qualifier("billableService.billableServiceResource") BillableServiceResource billableServiceResource, BillableServicesLineProcessor processor) {
        super(processor);
        this.billableServiceResource = billableServiceResource;
    }

    @Override
    public Domain getDomain() {
        return Domain.BILLABLE_SERVICES;
    }

    @Override
    public BillableService bootstrap(CsvLine line) throws IllegalArgumentException {
        String uuid = line.getUuid();
        BillableService billableService = billableServiceResource.getByUniqueId(uuid);
        if (billableService == null) {
            billableService = new BillableService();
        }
        if (StringUtils.isNotBlank(uuid)) {
            billableService.setUuid(uuid);
        }
        return billableService;
    }

    @Override
    public BillableService save(BillableService instance) {
        return billableServiceResource.save(instance);
    }
}
