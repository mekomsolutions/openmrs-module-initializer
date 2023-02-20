package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.api.impl.ConceptServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("initializer.ConceptService")
public class InitializerConceptServiceImpl implements InitializerConceptService {

	private static final Logger log = LoggerFactory.getLogger(HibernateInitializerConceptDAO.class);
	
	@Autowired
	private InitializerConceptDAO dao;
	
	public Concept getConceptByName(String name) {
		return dao.getConceptByName(name);
	}
}
