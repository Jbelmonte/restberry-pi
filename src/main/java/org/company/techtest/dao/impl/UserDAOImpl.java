package org.company.techtest.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.company.core.dao.exceptions.ConstraintViolationException;
import org.company.core.dao.exceptions.NotFoundException;
import org.company.core.dao.exceptions.PersistenceException;
import org.company.techtest.dao.UserDAO;
import org.company.techtest.model.User;

/**
 * User data layer implementation
 */
public class UserDAOImpl implements UserDAO {
	private Map<String, User> db = new HashMap<String, User>();
	private long lqstIndex = 0L;

	public UserDAOImpl() {
		// Add mock data
		loadMockData();
	}

	protected void loadMockData() {
		try {
			User user1 = new User();
			user1.setUsername(MOCK_USERNAME);
			user1.setPassword(MOCK_PASSWORD);
			user1.setRoles(MOCK_ROLES);
			save(user1);
		} catch (PersistenceException e) {
			// Won't happen
			e.printStackTrace();
		}
	}

	@Override
	public User findById(String id) throws PersistenceException {
		if (!existsUserWithId(id)) {
			throw new NotFoundException("There's no user with the specified id");
		}
		return db.get(id);
	}

	@Override
	public String save(User t) throws PersistenceException {
		if (t.getId() != null) {
			throw new NotFoundException("Trying to create an existing user");
		}

		// Validate
		validateBeforeStoring(t);

		// Persist
		String id = nextAutoincrementId();
		t.setId(id);
		db.put(id, t);
		return id;
	}

	@Override
	public void update(User t) throws PersistenceException {
		if (!existsUserWithId(t.getId())) {
			throw new NotFoundException("There's no user with the specified id");
		}
		
		// Do not update password
		User existing = findById(t.getId());
		t.setPassword(existing.getPassword());

		// Validate
		validateBeforeStoring(t);

		// Persist
		db.put(t.getId(), t);
	}

	@Override
	public List<User> findAll() throws PersistenceException {
		return new ArrayList<User>(db.values());
	}

	@Override
	public void remove(String id) throws PersistenceException {
		if (!existsUserWithId(id)) {
			throw new NotFoundException("There's no user with the specified id");
		}

		db.remove(id);
	}

	/**
	 * Performs data integrity validations. For that demo, all fields are
	 * required and at least one role.
	 * 
	 * @param u
	 *            User
	 * @throws PersistenceException
	 *             If user information is not valid.
	 */
	private void validateBeforeStoring(User u) throws ConstraintViolationException {
		if (StringUtils.isEmpty(u.getUsername())) {
			throw new ConstraintViolationException("Username is required");
		} else if (StringUtils.isEmpty(u.getId()) && StringUtils.isEmpty(u.getPassword())) {
			throw new ConstraintViolationException("Password is required");
		} else if (u.getRoles() == null || u.getRoles().isEmpty()) {
			throw new ConstraintViolationException("Users require at least one role");
		}
	}

	/**
	 * Checks whether a given ID corresponds to an existing user or not.
	 * 
	 * @param id
	 *            User identifier
	 * @return Whether it exists a user with the given ID or not.
	 */
	private boolean existsUserWithId(String id) {
		return db.containsKey(id);
	}

	/**
	 * Emulate sequences or autoincrements for ID.
	 * 
	 * @return Next autoincrement ID.
	 */
	private String nextAutoincrementId() {
		return new Long(++lqstIndex).toString();
	}
}
