package org.restberrypi.core.security;

import org.company.techtest.model.User;
import org.restberrypi.core.filters.ParameterMapFilter;

import com.sun.net.httpserver.HttpExchange;

/**
 * Class providing constant values to all application in terms of security.
 */
public final class Constants {

	/**
	 * @see {@link ParameterMapFilter}
	 */
	public static final String ATTRIBUTE_PARAMETER_MAP = "ATTRIBUTE_PARAMETER_MAP";

	/**
	 * Realm.
	 */
	public static final String REALM = "TechTest";

	/**
	 * Username for anonymous (not logged) requests.
	 */
	public static final String ANONYMOUS_USERNAME = "anonymous";

	/**
	 * Login endpoint.
	 */
	public static final String LOGIN_URL = "/login.html";
	
	/**
	 * Username parameter name in login.
	 */
	public static final String LOGIN_USERNAME_PARAM = "username";
	
	/**
	 * Password parameter name in login.
	 */
	public static final String LOGIN_PASSWORD_PARAM = "password";

	/**
	 * Logout endpoint.
	 */
	public static final String LOGOUT_URL = "/logout.html";

	/**
	 * Not found page.
	 */
	public static final String NOT_FOUND_URL = "/404.html";

	/**
	 * Unauthorized page displayed when user has not enough permissions
	 */
	public static final String UNAUTHORIZED_URL = "/restricted.html";

	/**
	 * Main page
	 */
	public static final String INDEX_URL = "/index.html";

	/**
	 * Available pages
	 */
	public static final String PAGE_1_URL = "/page1.html";
	public static final String PAGE_2_URL = "/page2.html";
	public static final String PAGE_3_URL = "/page3.html";

	/**
	 * Attribute inside {@link HttpExchange} to find the logged {@link User}
	 * entity.
	 */
	public static final String LOGGED_USER = "__loggedUser";

	/**
	 * Session cookie name in site's context.
	 */
	public static final String SEC_COOKIE_NAME = "JBRSESSID";

	/**
	 * Cookie session lifetime. Number of minutes of inactivity before closing
	 * session.
	 */
	public static final int SESSION_TIMEOUT_MINUTES = 5;

	/**
	 * REST API roles
	 */
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_USER = "USER";

	/**
	 * Site roles
	 */
	public static final String ROLE_PAGE1 = "PAGE_1";
	public static final String ROLE_PAGE2 = "PAGE_2";
	public static final String ROLE_PAGE3 = "PAGE_3";

	private Constants() {
	}

}
