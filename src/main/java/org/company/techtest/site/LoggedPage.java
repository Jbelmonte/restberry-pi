package org.company.techtest.site;

import org.company.core.security.Constants;
import org.company.core.web.security.Page;

import com.sun.net.httpserver.HttpExchange;

public abstract class LoggedPage implements Page {
	String basePath;

	public LoggedPage() {
	}

	@Override
	public String renderPage(HttpExchange exchange) {
		String username = (String) exchange.getAttribute(Constants.LOGGED_USER);
		String template = "<html><body><div>Hello " + username + " <a href=\"" + basePath + Constants.LOGOUT_URL
				+ "\">Logout</a></div><br>";
		template += "<div><a href=\"" + basePath + Constants.PAGE_1_URL + "\">Page 1</a></div><br>";
		template += "<div><a href=\"" + basePath + Constants.PAGE_2_URL + "\">Page 2</a></div><br>";
		template += "<div><a href=\"" + basePath + Constants.PAGE_3_URL + "\">Page 3</a></div><br>";
		template += "<div><a href=\"" + basePath + "/invalid.html\">Link to an invalid page</a></div><br>";
		template += "</body></html>";
		return template;
	}

	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
}
