package org.company.techtest.api.resources.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.company.techtest.api.resources.UsersResource;
import org.company.techtest.api.resources.dto.UserDto;
import org.company.techtest.business.UsersBO;
import org.company.techtest.model.User;
import org.restberrypi.core.dao.exceptions.ConstraintViolationException;
import org.restberrypi.core.dao.exceptions.NotFoundException;
import org.restberrypi.core.exceptions.BusinessException;
import org.restberrypi.core.jaxrs.exceptions.InvalidRequestException;
import org.restberrypi.core.jaxrs.exceptions.ResourceException;
import org.restberrypi.core.jaxrs.exceptions.ResourceNotFoundException;

/**
 * Users' exposed REST interface implementation.
 */
public class UsersResourceImpl implements UsersResource {
	private final UsersBO usersBo;

	public UsersResourceImpl(UsersBO usersBo) {
		this.usersBo = usersBo;
	}

	@Override
	public List<UserDto> list() throws ResourceException {
		try {
			List<User> users = usersBo.getAllUsers();
			List<UserDto> dtos = new ArrayList<>(users.size());

			// Map model entities to DTOs
			for (User u : users) {
				dtos.add(map(u));
			}

			return dtos;
		} catch (BusinessException e) {
			throw toResourceException(e);
		}
	}

	@Override
	public UserDto getUser(String id) throws ResourceException {
		try {
			return map(usersBo.getUser(id));
		} catch (BusinessException e) {
			throw toResourceException(e);
		}
	}

	@Override
	public UserDto addUser(UserDto userDto) throws ResourceException {
		try {
			String id = usersBo.addUser(map(userDto));
			userDto.setId(id);
			return userDto;
		} catch (BusinessException e) {
			throw toResourceException(e);
		}
	}

	@Override
	public UserDto updateUser(String id, UserDto userDto) throws ResourceException {
		try {
			userDto.setId(id);
			User user = map(userDto);
			usersBo.updateUser(user);
			return userDto;
		} catch (BusinessException e) {
			throw toResourceException(e);
		} catch (InvalidRequestException e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void removeUser(String id) throws ResourceException {
		try {
			usersBo.removeUser(id);
		} catch (BusinessException e) {
			throw toResourceException(e);
		}
	}

	protected UserDto map(User user) throws ResourceException {
		try {
			UserDto dto = new UserDto();
			BeanUtils.copyProperties(dto, user);
			dto.setPassword(null);
			return dto;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	protected User map(UserDto dto) throws InvalidRequestException {
		try {
			User user = new User();
			BeanUtils.copyProperties(user, dto);
			return user;
		} catch (Exception e) {
			throw new InvalidRequestException("Invalid user information");
		}
	}

	protected ResourceException toResourceException(BusinessException e) {
		if (ExceptionUtils.indexOfThrowable(e, NotFoundException.class) != -1) {
			return new ResourceNotFoundException("There's no user with the given id", e);
		} else if (ExceptionUtils.indexOfThrowable(e, ConstraintViolationException.class) != -1) {
			return new InvalidRequestException("Invalid parameters", e);
		} else {
			return new ResourceException("An error occurred", e);
		}
	}
}
