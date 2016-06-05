package org.company.techtest.business;

import java.util.List;

import org.company.core.exceptions.BusinessException;
import org.company.techtest.model.User;

/**
 * Users' business layer interface
 */
public interface UsersBO {
	/**
	 * Get a specific user by its internal id.
	 * 
	 * @param id
	 *            User's internal id.
	 * @return User
	 * @throws BusinessException
	 *             If user is not found
	 */
	public User getUser(String id) throws BusinessException;

	/**
	 * Get a specific user by its username.
	 * 
	 * @param username
	 *            Username
	 * @return User
	 * @throws BusinessException
	 *             If user is not found
	 */
	public User getUserByUsername(String username) throws BusinessException;

	/**
	 * Get the whole list of users.
	 * 
	 * @return All registered users.
	 * @throws BusinessException
	 *             If an error occurs during in persistence layer.
	 */
	public List<User> getAllUsers() throws BusinessException;

	/**
	 * Registers a new user.
	 * 
	 * @param user
	 *            The user information.
	 * @return The new internal id.
	 * @throws BusinessException
	 *             If an error occurs in persistence layer.
	 */
	public String addUser(User user) throws BusinessException;

	/**
	 * Updates an existing user.
	 * 
	 * @param user
	 *            The user information.
	 * @throws BusinessException
	 *             If an error occurs in persistence layer.
	 */
	public void updateUser(User user) throws BusinessException;

	/**
	 * Updates an existing user.
	 * 
	 * @param id
	 *            The user identifier.
	 * @throws BusinessException
	 *             If an error occurs in persistence layer.
	 */
	public void removeUser(String id) throws BusinessException;

	/**
	 * Looks for a user with the provided credentials
	 * 
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 * @return The associated user
	 * @throws BusinessException
	 *             If credentials do not correspond to any user
	 */
	public User validateCredentials(String username, String password) throws BusinessException;
}
