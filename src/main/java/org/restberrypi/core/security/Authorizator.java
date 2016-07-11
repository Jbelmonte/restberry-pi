package org.restberrypi.core.security;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.techtest.business.UsersBO;
import org.company.techtest.model.User;
import org.restberrypi.core.exceptions.BusinessException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public final class Authorizator {
	public static final Log LOGGER = LogFactory.getLog(Authorizator.class);

	private final UsersBO usersBo;

	public Authorizator(UsersBO usersBo) {
		this.usersBo = usersBo;
	}

	public boolean isLogged(HttpExchange exchange) {
		HttpPrincipal principal = exchange.getPrincipal();
		return principal != null && !Constants.ANONYMOUS_USERNAME.equals(principal.getUsername());
	}

	public boolean hasEnoughPermissions(HttpExchange exchange, List<String> requiredPermissions) {
		if (!requiredPermissions.isEmpty()) {
			if (isLogged(exchange)) {
				try {
					HttpPrincipal principal = exchange.getPrincipal();
					User user = usersBo.getUserByUsername(principal.getUsername());
					return !ListUtils.intersection(user.getRoles(), requiredPermissions).isEmpty();
				} catch (BusinessException e) {
					LOGGER.error("Error retrieving user", e);
				}
			}
			return false;
		}
		return true;
	}

}
