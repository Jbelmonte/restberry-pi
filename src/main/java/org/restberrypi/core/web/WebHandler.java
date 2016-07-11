package org.restberrypi.core.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restberrypi.core.helper.IntrospectionHelper;
import org.restberrypi.core.security.Authorizator;
import org.restberrypi.core.security.Constants;
import org.restberrypi.core.web.security.Page;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebHandler implements HttpHandler {
	public static final Log LOGGER = LogFactory.getLog(WebHandler.class);

	private Map<String, Page> pages = new HashMap<String, Page>();
	private final String basePath;
	private final Authorizator authorizator;

	public WebHandler(String basePath, Authorizator authorizator) {
		this.basePath = basePath;
		this.authorizator = authorizator;
	}

	public void registerPage(Page page) {
		String path = IntrospectionHelper.getResourcePath(page.getClass());
		registerPage(path, page);
	}

	public void registerPage(String path, Page page) {
		page.setBasePath(basePath);
		this.pages.put(path, page);
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		// Path suffix
		String path = exchange.getRequestURI().getPath().substring(basePath.length());
		LOGGER.info("Received message [" + method + "] " + path);

		if (pages.containsKey(path)) {
			Page page = pages.get(path);
			List<String> requiredPermissions = IntrospectionHelper.getRequiredPermissions(page.getClass());
			if (authorizator.hasEnoughPermissions(exchange, requiredPermissions)) {
				LOGGER.info("Render page " + page.getClass().getSimpleName());
				success(exchange, page.renderPage(exchange));
			} else if (authorizator.isLogged(exchange)) {
				LOGGER.info("Logged but not enough permissions. Redirect to unauthorized page.");
				redirectToUnauthorized(exchange);
			} else {
				LOGGER.info("Not logged, redirect to login.");
				redirectToLogin(exchange);
			}
		} else {
			LOGGER.info("Page not found: " + path);
			notFound(exchange);
		}
		exchange.close();
	}

	private void success(HttpExchange exchange, String content) throws IOException {
		byte[] data = content.getBytes("UTF8");

		exchange.getResponseHeaders().add("Content-Type", "text/html");
		exchange.sendResponseHeaders(200, data.length);
		exchange.getResponseBody().write(data);
	}

	private void notFound(HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Location", basePath + Constants.NOT_FOUND_URL);
		exchange.sendResponseHeaders(302, -1);
	}

	private void redirectToLogin(HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Location", basePath + Constants.LOGIN_URL);
		exchange.sendResponseHeaders(302, -1);
	}

	private void redirectToUnauthorized(HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Location", basePath + Constants.UNAUTHORIZED_URL);
		exchange.sendResponseHeaders(302, -1);
	}
}
