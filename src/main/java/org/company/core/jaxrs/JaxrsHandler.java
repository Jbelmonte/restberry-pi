package org.company.core.jaxrs;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.company.core.helper.IntrospectionHelper;
import org.company.core.jaxrs.annotations.BodyParam;
import org.company.core.jaxrs.annotations.Consumes;
import org.company.core.jaxrs.annotations.DELETE;
import org.company.core.jaxrs.annotations.GET;
import org.company.core.jaxrs.annotations.POST;
import org.company.core.jaxrs.annotations.PUT;
import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.annotations.PathParam;
import org.company.core.jaxrs.annotations.Produces;
import org.company.core.jaxrs.annotations.QueryParam;
import org.company.core.jaxrs.exceptions.ForbiddenException;
import org.company.core.jaxrs.exceptions.InvalidRequestException;
import org.company.core.jaxrs.exceptions.MappingException;
import org.company.core.jaxrs.exceptions.MethodNotAllowedException;
import org.company.core.jaxrs.exceptions.NotAcceptableException;
import org.company.core.jaxrs.exceptions.ResourceException;
import org.company.core.jaxrs.exceptions.ResourceNotFoundException;
import org.company.core.jaxrs.exceptions.UnsupportedMediaTypeException;
import org.company.core.jaxrs.json.JsonProvider;
import org.company.core.security.Authorizator;
import org.company.core.security.Constants;
import org.company.techtest.api.resources.Resource;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * REST resources orchestrator based on {@link HttpHandler} and custom JAXRS
 * annotations.
 */
public class JaxrsHandler implements HttpHandler {
	public static final Log LOGGER = LogFactory.getLog(JaxrsHandler.class);

	private Map<String, Resource> resources = new HashMap<String, Resource>();
	private final String basePath;
	private final Authorizator authorizator;

	public JaxrsHandler(String basePath, Authorizator authorizator) {
		this.basePath = basePath;
		this.authorizator = authorizator;
	}

	/**
	 * Registers a new exposed resource.
	 * 
	 * @param resource
	 *            Resource to be exposed.
	 */
	public void registerResource(Resource resource) {
		String path = IntrospectionHelper.getResourcePath(resource.getClass());
		this.resources.put(path, resource);
	}

	/**
	 * Handles incomming messages checking for available resource and methods.
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		// Path suffix
		String path = exchange.getRequestURI().getPath().substring(basePath.length());
		LOGGER.info("Received message [" + method + "] " + path);

		// Find associated resource
		Resource resource = null;
		String resourcePath = null;
		for (String rPath : resources.keySet()) {
			if (path.startsWith(rPath)) {
				resourcePath = rPath;
				resource = resources.get(rPath);
				break;
			}
		}
		if (resource == null) {
			notFound(exchange);
		}

		// Find associated method
		ResourceMethodInvocation handler;
		try {
			String subPath = path.substring(resourcePath.length());
			handler = findAssociatedMethod(resource, subPath, exchange);
		} catch (Exception e) {
			errorResponse(e, exchange);
			return;
		}

		// Execute
		Object response;
		try {
			response = handler.execute();
		} catch (Exception e) {
			errorResponse(e, exchange);
			return;
		}

		// Serialize response
		serializeResponse(response, handler.contentType(), exchange);
		exchange.close();
	}

	/***************************
	 * Response helper methods *
	 ***************************/

	protected void errorResponse(Exception e, HttpExchange exchange) throws IOException {
		if (e instanceof InvalidRequestException) {
			invalidRequest(exchange);
		} else if (e instanceof ResourceNotFoundException) {
			notFound(exchange);
		} else if (e instanceof MethodNotAllowedException) {
			methodNotAllowed(exchange);
		} else if (e instanceof NotAcceptableException) {
			notAcceptable(exchange);
		} else if (e instanceof UnsupportedMediaTypeException) {
			unsupportedMediaType(exchange);
		} else if (e instanceof ForbiddenException) {
			forbidden(exchange);
		} else {
			internalError(exchange);
		}
	}

	protected void invalidRequest(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(400, 0L);
		exchange.close();
	}

	protected void forbidden(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(403, 0L);
		exchange.close();
	}

	protected void notFound(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(404, 0L);
		exchange.close();
	}

	protected void methodNotAllowed(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(405, 0L);
		exchange.close();
	}

	protected void notAcceptable(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(406, 0L);
		exchange.close();
	}

	protected void unsupportedMediaType(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(415, 0L);
		exchange.close();
	}

	protected void internalError(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(500, 0L);
		exchange.close();
	}

	protected void serializeResponse(Object response, String contentType, HttpExchange exchange) throws IOException {
		if (contentType.startsWith("application/json")) {
			JsonProvider.toJson(response, contentType, exchange);
		} else {
			// For demo purporses, I will assume it's always JSON
			JsonProvider.toJson(response, contentType, exchange);
		}
	}

	protected <T> T deserializeInput(Class<T> dataType, String contentType, HttpExchange exchange)
			throws MappingException, IOException {
		if (contentType.startsWith("application/json")) {
			return JsonProvider.fromJson(dataType, exchange);
		} else {
			// For demo purporses, I will assume it's always JSON
			return JsonProvider.fromJson(dataType, exchange);
		}
	}

	/*****************************************
	 * Resource introspection helper methods *
	 *****************************************/

	private ResourceMethodInvocation findAssociatedMethod(Resource resource, String path, HttpExchange exchange)
			throws ResourceException {
		ResourceMethodInvocation mi = null;
		Class<? extends Resource> resourceClass = resource.getClass();
		List<Method> methods = IntrospectionHelper.getAllMethods(resourceClass);
		String contentType = getRequestContentType(exchange);
		String acceptType = getResponseContentType(exchange);

		// Filter by path
		methods = IntrospectionHelper.filterMethodsByPath(methods, path);
		if (methods.isEmpty()) {
			throw new ResourceNotFoundException();
		}

		// Filter by request method
		methods = filterMethodsByRequestMethod(methods, exchange.getRequestMethod());
		if (methods.isEmpty()) {
			throw new MethodNotAllowedException();
		}

		// Filter by content type
		methods = filterMethodsByResponseContentType(methods, acceptType);
		if (methods.isEmpty()) {
			throw new NotAcceptableException();
		}

		// Filter by content type
		methods = filterMethodsByRequestContentType(methods, contentType);
		if (methods.isEmpty()) {
			throw new UnsupportedMediaTypeException();
		}

		// Configuration errors
		if (methods.size() > 1) {
			throw new ResourceException("More than one handler found.");
		}

		// Filter by permissions
		Method method = methods.get(0);
		List<String> requiredPermissions = IntrospectionHelper.getRequiredPermissions(method);
		if (!authorizator.hasEnoughPermissions(exchange, requiredPermissions)) {
			throw new ForbiddenException();
		}

		// Check parameters
		try {
			Object[] parameters = getMethodParameters(method, exchange, path);
			mi = new ResourceMethodInvocation(resource, method, parameters, acceptType);
		} catch (InvalidRequestException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidRequestException(e);
		}

		return mi;
	}

	private List<Method> filterMethodsByRequestMethod(List<Method> methods, String requestMethod) {
		if ("GET".equalsIgnoreCase(requestMethod)) {
			return IntrospectionHelper.filterMethodsWithAnnotationValue(methods, GET.class, null, true);
		} else if ("POST".equalsIgnoreCase(requestMethod)) {
			return IntrospectionHelper.filterMethodsWithAnnotationValue(methods, POST.class, null, true);
		} else if ("PUT".equalsIgnoreCase(requestMethod)) {
			return IntrospectionHelper.filterMethodsWithAnnotationValue(methods, PUT.class, null, true);
		} else if ("DELETE".equalsIgnoreCase(requestMethod)) {
			return IntrospectionHelper.filterMethodsWithAnnotationValue(methods, DELETE.class, null, true);
		} else {
			throw new RuntimeException("Request method not handlet \"yet\".");
		}
	}

	private String getRequestContentType(HttpExchange exchange) {
		String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
		if (StringUtils.isEmpty(contentType) || "*/*".equals(contentType)) {
			// Use JSON if none/any specified
			contentType = "application/json";
		}
		return contentType;
	}

	private String getResponseContentType(HttpExchange exchange) {
		String contentType = exchange.getRequestHeaders().getFirst("Accept");
		if (StringUtils.isEmpty(contentType) || "*/*".equals(contentType)) {
			// Use JSON if none/any specified
			contentType = "application/json";
		}
		return contentType;
	}

	private List<Method> filterMethodsByRequestContentType(List<Method> methods, String contentType) {
		return IntrospectionHelper.filterMethodsWithAnnotationValue(methods, Consumes.class, contentType, false);
	}

	private List<Method> filterMethodsByResponseContentType(List<Method> methods, String contentType) {
		return IntrospectionHelper.filterMethodsWithAnnotationValue(methods, Produces.class, contentType, false);
	}

	protected Object[] getMethodParameters(Method method, HttpExchange exchange, String methodPath)
			throws InvalidRequestException {
		try {
			Class<?>[] paramTypes = method.getParameterTypes();
			Annotation[][] annotations = method.getParameterAnnotations();
			Map<String, String> queryParameters = getQueryParameterMap(exchange);
			Map<String, String> pathParameters = getPathParameterMap(methodPath,
					(String) IntrospectionHelper.getMethodAnnotationValue(method, Path.class));

			List<Object> params = new ArrayList<Object>(paramTypes.length);
			int current = 0;
			for (Annotation[] paramAnnotations : annotations) {
				Class<?> type = paramTypes[current];
				for (Annotation ann : paramAnnotations) {
					if (ann instanceof QueryParam) {
						Object value = parseValue(queryParameters.get(((QueryParam) ann).value()), type);
						params.add(value);
					} else if (ann instanceof BodyParam) {
						Object value = JsonProvider.fromJson(type, exchange);
						params.add(value);
					} else if (ann instanceof PathParam) {
						Object value = parseValue(pathParameters.get(((PathParam) ann).value()), type);
						params.add(value);
					}
				}
				current++;
			}

			return params.toArray();
		} catch (Exception e) {
			throw new InvalidRequestException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> getQueryParameterMap(HttpExchange exchange) {
		return (Map<String, String>) exchange.getAttribute(Constants.ATTRIBUTE_PARAMETER_MAP);
	}

	protected Map<String, String> getPathParameterMap(String requestedPath, String resourcePath) {
		Map<String, String> result = new HashMap<String, String>();
		if (StringUtils.isNotBlank(resourcePath) && StringUtils.isNoneBlank(requestedPath)) {
			String[] requestedParts = requestedPath.split("/");
			String[] resourceParts = resourcePath.split("/");
			for (int i = 0; i < resourceParts.length && i < requestedParts.length; i++) {
				String part = resourceParts[i];
				String paramValue = requestedParts[i];
				if (part.startsWith(":")) {
					String paramName = part.substring(1);
					result.put(paramName, paramValue);
				}
			}
			// new Pattern("/\\:(\\w+)").matcher(input)
		}
		return result;
	}

	protected Object parseValue(String value, Class<?> type) throws NumberFormatException {
		if (Integer.class.isAssignableFrom(type)) {
			return Integer.valueOf(value);
		} else if (String.class.isAssignableFrom(type)) {
			return value;
		}
		throw new RuntimeException("Type not handled " + type.getName());
	}

	public class ResourceMethodInvocation {
		private Resource target;
		private Method method;
		private Object[] params;
		private String responseType;

		public ResourceMethodInvocation(Resource target, Method method, Object[] params, String responseType) {
			super();
			this.target = target;
			this.method = method;
			this.params = params;
			this.responseType = responseType;
		}

		public String contentType() {
			return responseType;
		}

		public Object execute() throws ResourceException {
			try {
				return method.invoke(target, params);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof ResourceException) {
					throw (ResourceException) e.getTargetException();
				} else {
					throw new ResourceException("Error executing handler", e);
				}
			} catch (IllegalAccessException e) {
				throw new InvalidRequestException(e);
			} catch (IllegalArgumentException e) {
				throw new InvalidRequestException(e);
			}
		}
	}

}
