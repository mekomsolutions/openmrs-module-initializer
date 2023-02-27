package org.openmrs.module.initializer.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Hibernate class for Concepts <br>
 * <br>
 * Use {@link InitializerService} to access these methods
 * 
 * @see InitializerConceptService
 */
@Component
public class HibernateInitializerDAO implements InitializerDAO {
	
	private static final Logger log = LoggerFactory.getLogger(HibernateInitializerDAO.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Concept getConceptByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptName.class);
		
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			criteria.add(Restrictions.ilike("name", name));
		} else {
			criteria.add(Restrictions.eq("name", name));
		}
		
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.eq("conceptNameType", ConceptNameType.FULLY_SPECIFIED));
		
		criteria.createAlias("concept", "concept");
		criteria.add(Restrictions.eq("concept.retired", false));
		
		@SuppressWarnings("unchecked")
		List<ConceptName> list = criteria.list();
		
		if (list.size() == 1) {
			return list.iterator().next().getConcept();
		} else if (list.isEmpty()) {
			log.warn("No concept found for '" + name + "'");
		} else {
			throw new RuntimeException("Multiple concepts with the same fully specified name found for '" + name + "'");
		}
		return null;
	}
}
