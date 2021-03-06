package org.restberrypi.core.jaxrs.security;

import org.company.techtest.business.UsersBO;
import org.restberrypi.core.exceptions.BusinessException;
import org.restberrypi.core.security.Constants;

import com.sun.net.httpserver.BasicAuthenticator;

public class RestAuthenticator extends BasicAuthenticator {
	private final UsersBO usersBo;

	public RestAuthenticator(UsersBO usersBo) {
		super(Constants.REALM);

		this.usersBo = usersBo;
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		try {
			usersBo.validateCredentials(username, password);
			return true;
		} catch (BusinessException e) {
			return false;
		}
	}

}
