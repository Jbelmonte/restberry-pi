package org.company.techtest;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.core.filters.ParameterMapFilter;
import org.company.core.jaxrs.JaxrsHandler;
import org.company.core.jaxrs.annotations.GET;
import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.security.RestAuthenticator;
import org.company.core.security.Authorizator;
import org.company.core.web.LoginPage;
import org.company.core.web.NotAuthorizedPage;
import org.company.core.web.NotFoundPage;
import org.company.core.web.WebHandler;
import org.company.core.web.security.SiteAuthenticator;
import org.company.techtest.api.resources.Resource;
import org.company.techtest.api.resources.impl.UsersResourceImpl;
import org.company.techtest.business.UsersBO;
import org.company.techtest.business.impl.UsersBOImpl;
import org.company.techtest.site.IndexPage;
import org.company.techtest.site.Page1;
import org.company.techtest.site.Page2;
import org.company.techtest.site.Page3;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * Application starter.
 * 
 * It creates an instance of {@link HttpServer} publishing all required REST API
 * functionalities.
 * 
 * It 'discovers' all classes implementing {@link Resource} interface and
 * exposes them according to their annotations {@link Path}, {@link GET}, etc.
 * 
 * @see {@link org.company.core.jaxrs.annotations}
 * @see {@link JaxrsHandler}
 */
public class AppStarter {
	public static final Log LOGGER = LogFactory.getLog(AppStarter.class);
	public static final int DEFAULT_PORT = 8080;
	public static final int MAX_BACKLOG = 5;

	private static final String REST_ROOT = "/rest";
	private static final String SITE_ROOT = "/site";

	private int port = DEFAULT_PORT;

	// As there's no dependency injection, just store here the reference
	// and pass it everywhere.
	private UsersBO usersBo = new UsersBOImpl();

	public AppStarter() {
	}

	public AppStarter setPort(int port) {
		this.port = port;
		return this;
	}

	public void start() throws IOException {
		// Authorizator
		Authorizator authorizator = new Authorizator(usersBo);

		/*
		 * Create HttpServer instance
		 */
		InetSocketAddress intfc = new InetSocketAddress(this.port);
		LOGGER.info("Serving on " + intfc);
		HttpServer server = HttpServer.create(intfc, MAX_BACKLOG);

		/*
		 * Add REST context with basic authentication mechanism
		 */
		// Create an HttpHandler orchestrator for REST API
		JaxrsHandler restHandler = new JaxrsHandler(REST_ROOT, authorizator);
		// Register all exposed resources
		restHandler.registerResource(new UsersResourceImpl(usersBo));
		// And any other resource

		HttpContext api = server.createContext(REST_ROOT, restHandler);
		LOGGER.info("REST API in context root " + REST_ROOT);
		api.setAuthenticator(new RestAuthenticator(usersBo));
		api.getFilters().add(new ParameterMapFilter());

		/*
		 * Add site context with cookie-based authentication mechanism
		 */
		// Handler for web site
		WebHandler siteHandler = new WebHandler(SITE_ROOT, authorizator);
		// Register all exposed pages
		siteHandler.registerPage(new LoginPage());
		siteHandler.registerPage(new NotFoundPage());
		siteHandler.registerPage(new NotAuthorizedPage());
		siteHandler.registerPage(new IndexPage());
		siteHandler.registerPage(new Page1());
		siteHandler.registerPage(new Page2());
		siteHandler.registerPage(new Page3());
		// And any other page

		HttpContext site = server.createContext(SITE_ROOT, siteHandler);
		LOGGER.info("Site in context root " + SITE_ROOT);
		site.setAuthenticator(new SiteAuthenticator(usersBo, SITE_ROOT));
		site.getFilters().add(new ParameterMapFilter());

		/*
		 * Start
		 */
		server.setExecutor(null);
		server.start();
	}
}
