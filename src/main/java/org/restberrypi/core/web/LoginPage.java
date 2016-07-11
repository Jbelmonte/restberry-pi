package org.restberrypi.core.web;

import org.restberrypi.core.jaxrs.annotations.Path;
import org.restberrypi.core.security.Constants;
import org.restberrypi.core.web.security.Page;

import com.sun.net.httpserver.HttpExchange;

@Path(Constants.LOGIN_URL)
public class LoginPage implements Page {
	String basePath;

	public LoginPage() {
	}

	@Override
	public String renderPage(HttpExchange exchange) {
		String template = new StringBuilder("<html><body><form method=\"POST\" action=\"").append(basePath)
				.append(Constants.LOGIN_URL).append("\">").append("<div>Username: <input type=\"text\" name=\"")
				.append(Constants.LOGIN_USERNAME_PARAM).append("\"></div><br>")
				.append("<div>Password: <input type=\"password\" name=\"").append(Constants.LOGIN_PASSWORD_PARAM)
				.append("\"></div><br>")
				.append("<div><button type=\"submit\">Login</button></div></form></body></html>").toString();
		return template;
	}

	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
