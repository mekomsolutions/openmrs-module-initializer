package org.openmrs.module.initializer.api;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
	
	private static final long LOCK_TIMEOUT_MS = 10 * 60 * 1000; // 10 minutes
	
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
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#tryAcquireLock(String)
	 */
	@Override
	public Boolean tryAcquireLock(String nodeId) {
		long now = System.currentTimeMillis();
		String hql = "UPDATE InitializerLock " + "SET locked = true, lockedAt = current_timestamp, lockedBy = :node "
		        + "WHERE id = 1 AND (locked = false OR lockedAt < :expiryTime)";
		
		Date expiryTime = new Date(now - LOCK_TIMEOUT_MS);
		int updated = sessionFactory.getCurrentSession().createQuery(hql).setParameter("node", nodeId)
		        .setParameter("expiryTime", expiryTime).executeUpdate();
		
		return updated == 1;
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#releaseLock(String)
	 */
	@Override
	public void releaseLock(String nodeId) {
		String hql = "UPDATE InitializerLock " + "SET locked = false, lockedAt = null, lockedBy = null "
		        + "WHERE id = 1 AND lockedBy = :node";
		sessionFactory.getCurrentSession().createQuery(hql).setParameter("node", nodeId).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#forceReleaseLock()
	 */
	public void forceReleaseLock() {
		String hql = "UPDATE InitializerLock " + "SET locked = false, lockedAt = null, lockedBy = null " + "WHERE id = 1";
		sessionFactory.getCurrentSession().createQuery(hql).executeUpdate();
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#isLocked()
	 */
	@Override
	public Boolean isLocked() {
		Boolean locked = (Boolean) sessionFactory.getCurrentSession()
		        .createQuery("SELECT locked FROM InitializerLock WHERE id = 1").uniqueResult();
		return Boolean.TRUE.equals(locked);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<InitializerChecksum> getAll() {
		return sessionFactory.getCurrentSession().createQuery("FROM InitializerChecksum").list();
	}
	
	@Override
	public void deleteAll() {
		sessionFactory.getCurrentSession().createQuery("DELETE FROM InitializerChecksum").executeUpdate();
	}
	
	@Override
	public void saveOrUpdate(InitializerChecksum checksum) {
		sessionFactory.getCurrentSession().saveOrUpdate(checksum);
	}
}
