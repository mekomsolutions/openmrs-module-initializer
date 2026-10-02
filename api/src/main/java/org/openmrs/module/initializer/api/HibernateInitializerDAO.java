package org.openmrs.module.initializer.api;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

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
		String nameClause;
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			nameClause = "lower(cn.name) = lower(:name)";
		} else {
			nameClause = "cn.name = :name";
		}
		
		return sessionFactory.getCurrentSession()
		        .createQuery("select distinct cn.concept from ConceptName cn where " + nameClause
		                + " and cn.voided = false and cn.conceptNameType = :conceptNameType and cn.concept.retired = false",
		            Concept.class)
		        .setParameter("name", name).setParameter("conceptNameType", ConceptNameType.FULLY_SPECIFIED).list();
	}
}
