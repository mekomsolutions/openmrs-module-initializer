package org.openmrs.module.initializer.api.c;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Hibernate class for Concepts <br>
 * <br>
 * Use {@link InitializerConceptService} to access these methods
 * 
 * @see InitializerConceptService
 */
@Component
public class HibernateInitializerConceptDAO implements InitializerConceptDAO {
	
	private static final Logger log = LoggerFactory.getLogger(HibernateInitializerConceptDAO.class);
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Concept getConceptByName(String name) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptName.class);
		
		Locale locale = Context.getLocale();
		Locale language = new Locale(locale.getLanguage() + "%");
		criteria.add(Restrictions.or(Restrictions.eq("locale", locale), Restrictions.like("locale", language)));
		
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
