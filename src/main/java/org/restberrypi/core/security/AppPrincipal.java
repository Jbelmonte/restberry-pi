package org.restberrypi.core.security;

import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpPrincipal;

public class AppPrincipal extends HttpPrincipal {
	private List<String> roles;

	public AppPrincipal(String username, String realm) {
		super(username, realm);
	}

	public AppPrincipal setRoles(List<String> roles) {
		this.roles = new ArrayList<String>(roles);
		return this;
	}

	public boolean hasRole(String role) {
		return roles.indexOf(role) != -1;
	}
}
