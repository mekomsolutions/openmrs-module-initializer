package org.openmrs.module.initializer.api.datafilter.mappings;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingLineProcessor.HEADER_BASIS_CLASS;
import static org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingLineProcessor.HEADER_BASIS_UUID;
import static org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingLineProcessor.HEADER_ENTITY_CLASS;
import static org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingLineProcessor.HEADER_ENTITY_UUID;

@OpenmrsProfile(modules = { "datafilter:*" })
public class DataFilterMappingsCsvParser extends CsvParser<DataFilterMapping, BaseLineProcessor<DataFilterMapping>> {
	
	private SessionFactory sessionFactory;
	
	@Autowired
	public DataFilterMappingsCsvParser(DataFilterMappingLineProcessor processor, SessionFactory sessionFactory) {
		super(processor);
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.DATAFILTER_MAPPINGS;
	}
	
	@Transactional(readOnly = true)
	public OpenmrsObject getOpenmrsObject(String uuid, String className) throws HibernateException, ClassNotFoundException {
		return (OpenmrsObject) getByUuid(Class.forName(className), uuid);
	}
	
	private <T> T getByUuid(Class<T> clazz, String uuid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		cq.where(cb.equal(root.get("uuid"), uuid));
		return session.createQuery(cq).uniqueResult();
	}
	
	@Override
	public DataFilterMapping bootstrap(CsvLine line) throws IllegalArgumentException {
		
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
			        "Either the entity or the basis could not be fetched from database on the following CSV line:"
			                + line.toString());
		}
		
		return new DataFilterMapping(entity, basis);
	}
	
	@Override
	protected boolean shouldFill(DataFilterMapping instance, CsvLine csvLine) {
		return true;
	}
	
	@Override
	public DataFilterMapping save(DataFilterMapping mapping) {
		mapping.setId(1); // this marks it as "saved"
		return mapping;
	}
	
	@Override
	public boolean setRetired(DataFilterMapping instance, boolean retired) {
		instance.setRetired(false);
		return instance.getRetired();
	}
}
