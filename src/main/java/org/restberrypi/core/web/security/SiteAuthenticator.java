package org.restberrypi.core.web.security;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.techtest.business.UsersBO;
import org.company.techtest.model.User;
import org.restberrypi.core.exceptions.BusinessException;
import org.restberrypi.core.security.AppPrincipal;
import org.restberrypi.core.security.Constants;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class SiteAuthenticator extends Authenticator {
	public static final Log LOGGER = LogFactory.getLog(SiteAuthenticator.class);

	private final UsersBO usersBo;
	private final String basePath;

	private Map<String, String> tokensUsers = new HashMap<String, String>();

	public SiteAuthenticator(UsersBO usersBo, String basePath) {
		this.usersBo = usersBo;
		this.basePath = basePath;
	}

	@Override
	public Result authenticate(HttpExchange exchange) {
		if (isLogin(exchange)) {
			LOGGER.info("Login requested");
			return doLogin(exchange);
		} else if (isLogout(exchange)) {
			LOGGER.info("Logout requested");
			return doLogout(exchange);
		} else {
			// LOGGER.info("Authorize request");
			return authorize(exchange);
		}
	}

	@SuppressWarnings("unchecked")
	private Result doLogin(HttpExchange exchange) {
		try {
			Map<String, String> body = (Map<String, String>) exchange.getAttribute(Constants.ATTRIBUTE_PARAMETER_MAP);
			String username = body.get(Constants.LOGIN_USERNAME_PARAM);
			String password = body.get(Constants.LOGIN_PASSWORD_PARAM);
			User user = usersBo.validateCredentials(username, password);

			String token = UUID.randomUUID().toString();
			LOGGER.info("Login successful for user " + username + ". Storing cookie with token " + token);
			tokensUsers.put(token, user.getId());

			addTokenCookie(exchange, token);
			return redirectToIndex(exchange);
		} catch (BusinessException e) {
			LOGGER.info("Invalid login, redirect to login again.");
			return redirectToLogin(exchange);
		}
	}

	private Result doLogout(HttpExchange exchange) {
		String currentToken = getTokenCookie(exchange);
		tokensUsers.remove(currentToken);
		removeTokenCookie(exchange);
		return redirectToLogin(exchange);
	}

	protected Result authorize(HttpExchange exchange) {
		String token = getTokenCookie(exchange);
		LOGGER.info("Request with cookie value: " + token);
		if (StringUtils.isNotEmpty(token) && !tokensUsers.containsKey(token)) {
			LOGGER.info("User sent invalid token.");
			return unauthorized(exchange);
		}

		try {
			if (StringUtils.isNotEmpty(token)) {
				User user = usersBo.getUser(tokensUsers.get(token));

				// Refresh cookie expiry time
				addTokenCookie(exchange, token);
				return granted(exchange, user);
			} else {
				return granted(exchange, null);
			}
		} catch (BusinessException e) {
			return unauthorized(exchange);
		}
	}

	protected boolean isLogin(HttpExchange exchange) {
		return exchange.getRequestURI().getPath().endsWith(Constants.LOGIN_URL)
				&& "POST".equalsIgnoreCase(exchange.getRequestMethod());
	}

	protected boolean isLogout(HttpExchange exchange) {
		return exchange.getRequestURI().getPath().endsWith(Constants.LOGOUT_URL);
	}

	protected Result granted(HttpExchange exchange, User user) {
		if (user != null) {
			exchange.setAttribute(Constants.LOGGED_USER, user.getUsername());
			return new Success(createPrincipal(user));
		} else {
			exchange.setAttribute(Constants.LOGGED_USER, null);
			return new Success(createAnonymousPrincipal());
		}
	}

	protected Result unauthorized(HttpExchange exchange) {
		removeTokenCookie(exchange);
		return redirectToLogin(exchange);
	}

	protected Result redirectToLogin(HttpExchange exchange) {
		try {
			// Close connection as there's no login resource
			exchange.getResponseHeaders().add("Location", basePath + Constants.LOGIN_URL);
			exchange.sendResponseHeaders(302, -1);
			return new Failure(302);
		} catch (IOException e) {
			return new Failure(500);
		}
	}

	protected Result redirectToIndex(HttpExchange exchange) {
		try {
			// Close connection as there's no login resource
			exchange.getResponseHeaders().add("Location", basePath + Constants.INDEX_URL);
			exchange.sendResponseHeaders(302, -1);
			return new Failure(302);
		} catch (IOException e) {
			return new Failure(500);
		}
	}

	private HttpPrincipal createPrincipal(User user) {
		return new AppPrincipal(user.getUsername(), Constants.REALM).setRoles(user.getRoles());
	}

	private HttpPrincipal createAnonymousPrincipal() {
		return new AppPrincipal(Constants.ANONYMOUS_USERNAME, Constants.REALM);
	}

	private String getTokenCookie(HttpExchange exchange) {
		List<String> cookies = exchange.getRequestHeaders().get("Cookie");
		if (cookies != null) {
			for (String cookie : cookies) {
				if (cookie.startsWith(Constants.SEC_COOKIE_NAME)) {
					int delim = cookie.indexOf(';');
					String value = (delim == -1) ? cookie.substring(Constants.SEC_COOKIE_NAME.length() + 1)
							: cookie.substring(Constants.SEC_COOKIE_NAME.length() + 1, delim);
					return value.trim();
				}
			}
		}
		return null;
	}

	private void addTokenCookie(HttpExchange exchange, String token) {
		Date expiryDate = getSessionExpiryDate();
		// Fri, 31 Dec 9999 23:59:59 GMT
		DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String expiryTime = df.format(expiryDate);
		LOGGER.info("Store/refresh session cookie with expiry time: " + expiryTime);
		exchange.getResponseHeaders().add("Set-Cookie",
				Constants.SEC_COOKIE_NAME + "=" + token + "; Path=" + basePath + "; Expires=" + expiryTime);
	}

	private void removeTokenCookie(HttpExchange exchange) {
		LOGGER.info("Remove session cookie");
		exchange.getResponseHeaders().add("Set-Cookie",
				Constants.SEC_COOKIE_NAME + "=deleted; Path=" + basePath + "; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
	}

	private Date getSessionExpiryDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.MINUTE, Constants.SESSION_TIMEOUT_MINUTES);
		return calendar.getTime();
	}

}
