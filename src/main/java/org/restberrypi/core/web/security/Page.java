package org.restberrypi.core.web.security;

import com.sun.net.httpserver.HttpExchange;

/**
 * Inferface for published dynamic pages
 */
public interface Page {
	public String renderPage(HttpExchange exchange);
	public void setBasePath(String basePath);
}
