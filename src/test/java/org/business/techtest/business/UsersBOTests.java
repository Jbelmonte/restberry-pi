package org.business.techtest.business;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.company.core.exceptions.BusinessException;
import org.company.techtest.business.UsersBO;
import org.company.techtest.business.impl.UsersBOImpl;
import org.company.techtest.dao.UserDAO;
import org.company.techtest.model.User;
import org.junit.Before;
import org.junit.Test;

public class UsersBOTests {
	private UsersBO usersBo;

	@Before
	public void before() throws IOException {
		usersBo = new UsersBOImpl();
	}

	@Test
	public void testUserCount() throws BusinessException {
		assertEquals("Initially it contains one user", usersBo.getAllUsers().size(), 1);
		User userById = usersBo.getUser(UserDAO.MOCK_USER_ID);
		assertThat("Existing user is" + UserDAO.MOCK_USERNAME, userById.getUsername(), is(UserDAO.MOCK_USERNAME));

		User userByUsername = usersBo.getUserByUsername(UserDAO.MOCK_USERNAME);
		assertThat("Finding by username or ID gives the same result", userById, is(sameInstance(userByUsername)));
	}

	@Test(expected = BusinessException.class)
	public void testLoginFailed() throws BusinessException {
		usersBo.validateCredentials(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD + "_");
		fail("Login should have failed with invalid credentials");
	}

	@Test(expected = BusinessException.class)
	public void testLoginUnkownUser() throws BusinessException {
		usersBo.validateCredentials("user2", "user2");
		fail("Login should have failed for invalid users");
	}

	@Test
	public void testLoginSuccess() throws BusinessException {
		User user = usersBo.validateCredentials(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD);
		assertThat("Login resolved valid user", user.getId(), is(UserDAO.MOCK_USER_ID));
	}

}
