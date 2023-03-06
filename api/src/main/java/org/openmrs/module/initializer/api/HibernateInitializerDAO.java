package org.openmrs.module.initializer.api;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
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
 * The Hibernate class for database related functions <br>
 * <br>
 * Use {@link InitializerService} to access these methods
 * 
 * @see InitializerService
 */
public class HibernateInitializerDAO implements InitializerDAO {
	
	private static final Logger log = LoggerFactory.getLogger(HibernateInitializerDAO.class);
	
	private SessionFactory sessionFactory;
	
	/**
	 * Sets the session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#getUnretiredConceptsByFullySpecifiedName(String)
	 */
	@Override
	public List<Concept> getUnretiredConceptsByFullySpecifiedName(String name) {
		if (StringUtils.isBlank(name)) {
			return Collections.emptyList();
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
		criteria.setProjection(Projections.distinct(Projections.property("concept")));
		
		@SuppressWarnings("unchecked")
		List<Concept> list = criteria.list();
		return list;
	}
}
