package org.company.core.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.company.core.security.Constants;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

public class ParameterMapFilter extends Filter {
	public static final String FILTER_DESC = "Converts query string and body parameter to a Map "
			+ "and stores it as an attribute named " + Constants.ATTRIBUTE_PARAMETER_MAP + " in HttpExchange instance.";

	public ParameterMapFilter() {
	}

	@Override
	public String description() {
		return FILTER_DESC;
	}

	@Override
	public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
		String queryString = null;
		if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			queryString = exchange.getRequestURI().getQuery();
		} else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())
				&& "application/x-www-form-urlencoded".equals(exchange.getRequestHeaders().getFirst("Content-Type"))) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			queryString = br.readLine();
		}

		Map<String, String> parameterMap = new HashMap<String, String>();
		if (StringUtils.isNotBlank(queryString)) {
			List<NameValuePair> queryParameters = URLEncodedUtils.parse(queryString, Charset.forName("UTF8"));
			for (NameValuePair pair : queryParameters) {
				parameterMap.put(pair.getName(), pair.getValue());
			}
		}
		exchange.setAttribute(Constants.ATTRIBUTE_PARAMETER_MAP, parameterMap);
		chain.doFilter(exchange);
	}

}
