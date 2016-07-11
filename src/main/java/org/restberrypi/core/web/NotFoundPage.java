package org.restberrypi.core.web;

import org.restberrypi.core.jaxrs.annotations.Path;
import org.restberrypi.core.security.Constants;
import org.restberrypi.core.web.security.Page;

import com.sun.net.httpserver.HttpExchange;

@Path(Constants.NOT_FOUND_URL)
public class NotFoundPage implements Page {
	String basePath;

	public NotFoundPage() {
	}

	@Override
	public String renderPage(HttpExchange exchange) {
		return "<html><body>Oops! Not found! <br> <a href=\"" + basePath + Constants.INDEX_URL
				+ "\">Go to index</a></body></html>";
	}

	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
