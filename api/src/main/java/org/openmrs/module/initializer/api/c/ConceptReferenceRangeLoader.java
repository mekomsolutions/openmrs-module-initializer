package org.openmrs.module.initializer.api.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ConceptReferenceRangeLoader extends BaseCsvLoader<ConceptReferenceRange, ConceptReferenceRangeCsvParser> {

    @Autowired
    public void setParser(ConceptsCsvParser parser) {
        this.parser = parser;
    }

    @Override
    protected void preload(File file) {
    }
}
