package org.business.techtest.dao;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;

import org.company.core.dao.exceptions.ConstraintViolationException;
import org.company.core.dao.exceptions.NotFoundException;
import org.company.core.dao.exceptions.PersistenceException;
import org.company.techtest.dao.UserDAO;
import org.company.techtest.dao.impl.UserDAOImpl;
import org.company.techtest.model.User;
import org.junit.Before;
import org.junit.Test;

public class UserDAOTests {

	private UserDAO userDao;

	@Before
	public void before() throws IOException {
		userDao = new UserDAOImpl();
	}

	/*
	 * PersistenceException; public void remove(String id) throws
	 */

	@Test
	public void testInitialUserCount() throws PersistenceException {
		assertEquals("Initially it contains one user", userDao.findAll().size(), 1);

		User user = userDao.findById(UserDAO.MOCK_USER_ID);
		assertThat("Existing user is" + UserDAO.MOCK_USERNAME, user.getUsername(), is(UserDAO.MOCK_USERNAME));
	}

	@Test
	public void testFindExistingUser() throws PersistenceException {
		assertThat("It finds an existing user", userDao.findById(UserDAO.MOCK_USER_ID), notNullValue());
	}

	@Test(expected = NotFoundException.class)
	public void testUserNotFound() throws PersistenceException {
		userDao.findById("invalid");
		fail("Find by id should have failed for an unexisting ID");
	}

	@Test(expected = ConstraintViolationException.class)
	public void testRequiredUsername() throws PersistenceException {
		User user = new User();
		userDao.save(user);
		fail("Add should have failed because 'username' id null");
	}

	@Test(expected = ConstraintViolationException.class)
	public void testRequiredPassword() throws PersistenceException {
		User user = new User();
		user.setUsername("user2");
		userDao.save(user);
		fail("Add should have failed because 'password' id null");
	}

	@Test(expected = ConstraintViolationException.class)
	public void testRequiredRoles() throws PersistenceException {
		User user = new User();
		user.setUsername("user2");
		user.setPassword("pwd");
		userDao.save(user);
		fail("Add should have failed because 'roles' are null");
	}

	@Test(expected = PersistenceException.class)
	public void testAddUserWithId() throws PersistenceException {
		User user = new User();
		user.setId("dummy");
		user.setUsername("newUser");
		user.setPassword("pwd");
		user.setRoles(Arrays.asList("ADMIN", "USER"));

		userDao.save(user);
		fail("Add should have failed because 'id' was not null");
	}

	@Test
	public void testAddUser() throws PersistenceException {
		User user = new User();
		user.setUsername("newUser");
		user.setPassword("pwd");
		user.setRoles(Arrays.asList("ADMIN", "USER"));

		String newId = userDao.save(user);
		User added = userDao.findById(newId);

		assertThat("Both parameter and result users are the same instance", added, sameInstance(user));
		assertThat("It contains a new ID", newId, notNullValue());
	}

	@Test(expected = NotFoundException.class)
	public void testUpdateUnexistingUser() throws PersistenceException {
		User user = new User();
		user.setId("dummy");
		user.setUsername("newUser");
		user.setPassword("pwd");
		user.setRoles(Arrays.asList("ADMIN", "USER"));

		userDao.update(user);
		fail("Updated should have failed for an invalid user ID");
	}

	@Test
	public void testUpdateExistingUser() throws PersistenceException {
		User user = new User();
		user.setId(UserDAO.MOCK_USER_ID);
		user.setUsername(UserDAO.MOCK_USERNAME + "_");
		user.setPassword(UserDAO.MOCK_PASSWORD + "_");
		user.setRoles(Arrays.asList("USER"));

		userDao.update(user);

		User updated = userDao.findById(user.getId());
		assertThat("Username is not updated", updated.getUsername(), is(UserDAO.MOCK_USERNAME));
		assertThat("Password is not updated", updated.getPassword(), is(UserDAO.MOCK_PASSWORD));
		assertThat("Roles are updated", updated.getRoles(), contains("USER"));
	}

	@Test(expected = NotFoundException.class)
	public void testRemoveUnexistingUser() throws PersistenceException {
		userDao.remove("invalid");
		fail("Remove should have failed for an invalid user ID");
	}

	@Test
	public void testRemoveExistingUser() throws PersistenceException {
		userDao.remove(UserDAO.MOCK_USER_ID);
		assertThat(userDao.findAll(), is(empty()));
	}

}
