package org.company.core.dao;

import java.util.List;

import org.company.core.dao.exceptions.PersistenceException;

/**
 * Common DAO
 *
 * @param <T> Type
 */
public interface DAO<T> {
	public List<T> findAll() throws PersistenceException;
	public T findById(String id) throws PersistenceException;
	public String save(T t) throws PersistenceException;
	public void update(T t) throws PersistenceException;
	public void remove(String id) throws PersistenceException;
}
