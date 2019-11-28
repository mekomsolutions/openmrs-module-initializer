package org.openmrs.module.initializer.api.datafilter.mappings;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.datafilter.impl.api.DataFilterService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the first level line processor for Data Filter entity to basis mappings.
 */
@OpenmrsProfile(modules = { "datafilter:*" })
public class DataFilterMappingLineProcessor extends BaseLineProcessor<DataFilterMapping> {
	
	private DataFilterService dfs;
	
	private SessionFactory sessionFactory;
	
	protected static String HEADER_ENTITY_UUID = "entity uuid";
	
	protected static String HEADER_ENTITY_CLASS = "entity class";
	
	protected static String HEADER_BASIS_UUID = "basis uuid";
	
	protected static String HEADER_BASIS_CLASS = "basis class";
	
	@Transactional(readOnly = true)
	public OpenmrsObject getOpenmrsObject(String uuid, String className) throws HibernateException, ClassNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Class.forName(className));
		criteria.add(Restrictions.eq("uuid", uuid));
		return (OpenmrsObject) criteria.uniqueResult();
	}
	
	@Autowired
	public DataFilterMappingLineProcessor(@Qualifier("dataFilterService") DataFilterService dfs,
	    SessionFactory sessionFactory) {
		super();
		this.dfs = dfs;
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	protected DataFilterMapping bootstrap(CsvLine line) throws IllegalArgumentException {
		
		OpenmrsObject entity = null;
		OpenmrsObject basis = null;
		try {
			
			entity = getOpenmrsObject(line.get(HEADER_ENTITY_UUID, true), line.get(HEADER_ENTITY_CLASS, true));
			basis = getOpenmrsObject(line.get(HEADER_BASIS_UUID, true), line.get(HEADER_BASIS_CLASS, true));
			
		}
		catch (HibernateException | ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		
		if (entity == null || basis == null) {
			throw new IllegalArgumentException(
			        "Either the entity or the basis could not be fetched from database on the following CSV line: "
			                + line.toString());
		}
		
		return new DataFilterMapping(entity, basis);
	}
	
	@Override
	protected boolean voidOrRetire(boolean isVoidedOrRetired, DataFilterMapping instance) {
		return false;
	}
	
	public DataFilterMapping fill(DataFilterMapping mapping, CsvLine line) throws IllegalArgumentException {
		
		boolean revoke = getVoidOrRetire(line);
		
		if (revoke) {
			dfs.revokeAccess(mapping.getEntity(), mapping.getBasis());
		} else {
			dfs.grantAccess(mapping.getEntity(), mapping.getBasis());
		}
		
		return mapping;
	}
}
