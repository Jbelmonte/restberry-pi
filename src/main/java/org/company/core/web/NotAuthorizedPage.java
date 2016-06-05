package org.company.core.web;

import org.company.core.jaxrs.annotations.Path;
import org.company.core.security.Constants;
import org.company.core.web.security.Page;

import com.sun.net.httpserver.HttpExchange;

@Path(Constants.UNAUTHORIZED_URL)
public class NotAuthorizedPage implements Page {
	String basePath;

	public NotAuthorizedPage() {
	}

	@Override
	public String renderPage(HttpExchange exchange) {
		return "<html><body>Oops! You have no permissions! <br> <a href=\"" + basePath + Constants.INDEX_URL
				+ "\">Go to index</a></body></html>";
	}

	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
