package org.company.techtest.dao;

import java.util.Arrays;
import java.util.List;

import org.company.core.dao.DAO;
import org.company.techtest.model.User;

public interface UserDAO extends DAO<User> {
	String MOCK_USER_ID = "1";
	String MOCK_USERNAME = "user1";
	String MOCK_PASSWORD = "user1";
	List<String> MOCK_ROLES = Arrays.asList("ADMIN");
}
