package org.company.core.web;

import org.company.core.jaxrs.annotations.Path;
import org.company.core.security.Constants;
import org.company.core.web.security.Page;

import com.sun.net.httpserver.HttpExchange;

@Path(Constants.LOGIN_URL)
public class LoginPage implements Page {
	String basePath;

	public LoginPage() {
	}

	@Override
	public String renderPage(HttpExchange exchange) {
		String template = "<html><body><form method=\"POST\" action=\"" + basePath + Constants.LOGIN_URL + "\">"
				+ "<div>Username: <input type=\"text\" name=\"username\"></div><br>"
				+ "<div>Password: <input type=\"password\" name=\"password\"></div><br>"
				+ "<div><button type=\"submit\">Login</button></div></form></body></html>";
		return template;
	}

	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
