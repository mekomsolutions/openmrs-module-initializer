package org.openmrs.module.initializer.api;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.openmrs.module.initializer.api.entities.InitializerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	@Override
	public void removeExpiredLock(String lockName) {
		String hql = "DELETE FROM InitializerLock WHERE lockName = :name AND lockUntil < :now";
		sessionFactory.getCurrentSession().createQuery(hql).setParameter("name", lockName).setParameter("now", new Date())
		        .executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#tryAcquireLock(String)
	 */
	@Override
	public Boolean tryAcquireLock(String lockName, Date lockUntil, String lockedBy) {
		try {
			InitializerLock lock = new InitializerLock();
			lock.setLockName(lockName);
			lock.setLockUntil(lockUntil);
			lock.setLockedBy(lockedBy);
			
			sessionFactory.getCurrentSession().save(lock);
			sessionFactory.getCurrentSession().flush();
			
			return true;
		}
		catch (PersistenceException e) {
			if (ExceptionUtils.getRootCause(e) instanceof ConstraintViolationException) {
				return false;
			}
			throw e;
		}
	}
	
	@Override
	public void deleteLock(String lockName) {
		sessionFactory.getCurrentSession().createQuery("DELETE FROM InitializerLock WHERE lockName = :name")
		        .setParameter("name", lockName).executeUpdate();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<InitializerChecksum> getAll() {
		return sessionFactory.getCurrentSession().createQuery("FROM InitializerChecksum").list();
	}
	
	@Override
	public void saveOrUpdate(InitializerChecksum checksum) {
		sessionFactory.getCurrentSession().saveOrUpdate(checksum);
	}
	
	@Override
	public void deleteByFilePath(String filePath) {
		sessionFactory.getCurrentSession().createQuery("DELETE FROM InitializerChecksum WHERE filePath = :filePath")
		        .setParameter("filePath", filePath).executeUpdate();
	}
}
