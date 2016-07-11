package org.restberrypi.core.jaxrs.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.restberrypi.core.jaxrs.exceptions.MappingException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

/**
 * JSON serializaion/deserialization.
 */
public class JsonProvider {

	public JsonProvider() {
	}

	public static void toJson(Object response, String contentType, HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Content-Type", contentType);

		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (response != null) {
			mapper.writeValue(out, response);
		}

		exchange.sendResponseHeaders(200, out.size());
		exchange.getResponseBody().write(out.toByteArray());
	}

	public static <T> T fromJson(Class<T> valueType, HttpExchange exchange) throws MappingException, IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(exchange.getRequestBody(), valueType);
		} catch (JsonParseException | JsonMappingException e) {
			throw new MappingException(e);
		}
	}

}
