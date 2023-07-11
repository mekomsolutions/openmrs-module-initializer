package org.openmrs.module.initializer.api.cohort.cat;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.cohort.CohortAttributeType;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = {"cohort:3.2.* - 9.*"})
public class CohortAttributeTypeLoader extends BaseCsvLoader<CohortAttributeType, CohortAttributeTypeCsvParser> {

    @Override
    public Domain getDomain() {
        return Domain.COHORT_ATTRIBUTE_TYPES;
    }

    @Autowired
    public void setParser(CohortAttributeTypeCsvParser parser) {
        this.parser = parser;
    }
}
