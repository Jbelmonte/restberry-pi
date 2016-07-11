package org.business.techtest.api.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.company.techtest.AppStarter;
import org.company.techtest.api.resources.dto.UserDto;
import org.company.techtest.dao.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restberrypi.core.security.Constants;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static com.jayway.restassured.RestAssured.*;

public class ITestUserResources {

	private static final String BASE_URI = "http://localhost";
	private static final int PORT = 8080;
	private AppStarter server;
	private String userId = null;
	
	@BeforeClass
	public static void config() {
		RestAssured.baseURI = BASE_URI;
		RestAssured.port = PORT;
		RestAssured.basePath = "/rest/users";
		RestAssured.defaultParser = Parser.JSON;
	}

	@Before
	public void startServer() throws IOException {
		server = new AppStarter();
		server.setPort(PORT);
		server.start();
		
		// Add user with ROLE_USER
		UserDto user = new UserDto();
		user.setUsername("user");
		user.setPassword("user");
		user.setRoles(Arrays.asList(Constants.ROLE_USER));
		UserDto added = given().
				auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
				contentType(ContentType.JSON).
				body(user).
			post().as(UserDto.class);
		userId = added.getId();
	}

	@After
	public void stopServer() {
		server.stop();
	}

	@Test
	public void testListRequiresCredentials() {
		given().
		when().get("/").
			then().statusCode(401);
	}
	
	@Test
	public void testList() {
		Response response = given().
								auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
							when().get("/").andReturn();
		assertThat("Success", response.statusCode(), is(200));
		assertThat("Returns JSON", response.contentType(), is("application/json"));

		UserDto[] users = response.as(UserDto[].class);
		assertThat("It still contains two items", users.length, is(2));
	}

	@Test
	public void testGetUserRequiresCredentials() {
		given().
		when().get("/" + UserDAO.MOCK_USER_ID).
			then().statusCode(401);
	}
	
	@Test
	public void testGetUserAcceptsUserRole() {
		given().
			auth().basic("user", "user").
		when().get("/" + userId).
			then().statusCode(200).
			contentType(ContentType.JSON).
			body("id", is(userId));
	}

	@Test
	public void testGetUnknownUser() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
		when().get("/invalid").
			then().statusCode(404);
	}

	@Test
	public void testGetUser() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
		when().get("/" + UserDAO.MOCK_USER_ID).
			then().statusCode(200).
			contentType(ContentType.JSON).
			body("id", equalTo(UserDAO.MOCK_USER_ID)).
			body("username", equalTo(UserDAO.MOCK_USERNAME)).
			body("password", nullValue());
	}

	@Test
	public void testAddUserRequiresCredentials() {
		given().
		when().post("/").
			then().statusCode(401);
	}
	
	@Test
	public void testAddUserRequiresAdminRole() {
		UserDto user = new UserDto();
		user.setUsername("test");
		user.setPassword("test");
		user.setRoles(Arrays.asList("ADMIN"));

		given().
			auth().basic("user", "user").
			contentType(ContentType.JSON).
			body(user).
		when().post().
			then().statusCode(403);
		
		// User is not added
		UserDto[] users = given().
								auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
							when().get("/").as(UserDto[].class);
		assertThat("User is not added", users, not(arrayContainingInAnyOrder(hasProperty("username", is("test")))));
	}
	
	@Test
	public void testAddUserInvalidParameters() {
		UserDto user = new UserDto();
		user.setUsername("test");

		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			contentType(ContentType.JSON).
			body(user).
		when().post().
			then().statusCode(400);
		
		// User is not added
		UserDto[] users = given().
								auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
							when().get("/").as(UserDto[].class);
		assertThat("User is not added", users, not(arrayContainingInAnyOrder(hasProperty("username", is("test")))));
	}

	@Test
	public void testAddUser() {
		UserDto user = new UserDto();
		user.setUsername("test");
		user.setPassword("test");
		user.setRoles(Arrays.asList("ADMIN"));
		
		// Add user
		UserDto resp = given().
							auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
							contentType(ContentType.JSON).
							body(user).
						when().post().as(UserDto.class);
		assertThat("Response contains a new id", resp, hasProperty("id", notNullValue()));
		
		// Test request with that user
		UserDto getted = given().
							auth().basic(user.getUsername(), user.getPassword()).
						when().get("/" + resp.getId()).as(UserDto.class);
		assertThat("Get by new id returns the same user", getted, comparesEqualTo(resp));
	}

	@Test
	public void testUpdateUserRequiresCredentials() {
		given().
		when().put("/" + UserDAO.MOCK_USER_ID).
			then().statusCode(401);
	}

	@Test
	public void testUpdateUserRequiresAdminRole() {
		given().
			auth().basic("user", "user").
		when().put("/" + userId).
			then().statusCode(403);
	}
	
	@Test
	public void testUpdateUnknownUser() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			contentType(ContentType.JSON).
			body(new UserDto()).
		when().put("/invalid").
			then().statusCode(404);
	}

	@Test
	public void testUpdateUser() {
		UserDto user = new UserDto();
		user.setRoles(Arrays.asList(Constants.ROLE_USER, Constants.ROLE_PAGE1, Constants.ROLE_PAGE3));
		UserDto updated = given().
				auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
				contentType(ContentType.JSON).
				body(user).
			put("/" + userId).as(UserDto.class);
		assertThat("User maintains ROLE_USER role and gains ROLE_PAGE1 and ROLE_PAGE3", updated.getRoles(),
				containsInAnyOrder(Constants.ROLE_USER, Constants.ROLE_PAGE1, Constants.ROLE_PAGE3));
	}

	@Test
	public void testRemoveUserRequiresCredentials() {
		given().
		when().delete("/" + UserDAO.MOCK_USER_ID).
			then().statusCode(401);
	}

	@Test
	public void testRemoveUserRequiresAdminRole() {
		given().
			auth().basic("user", "user").
		when().delete("/" + userId).
			then().statusCode(403);
	}

	@Test
	public void testRemoveUnknownUser() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
		when().delete("/invalid").
			then().statusCode(404);
	}
	

	@Test
	public void testRemoveUser() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
		when().delete("/" + userId).
			then().statusCode(200);
		
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
		when().delete("/" + userId).
			then().statusCode(404);
	}

	
	
	
	
	@Test
	public void testResourceNotFound() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			basePath("/rest/employees").
		when().get().
			then().statusCode(404);
	}

	@Test
	public void testMethodNotAllowed() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
		when().delete("/").
			then().statusCode(405);
	}
	
	@Test
	public void testNotAcceptable() {
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			accept(ContentType.XML).
		when().get("/" + userId).
			then().statusCode(406);
	}
	
	@Test
	public void testUnsupportedMediaType() {
		UserDto newUser = new UserDto();
		given().
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			contentType(ContentType.XML).
			body(newUser).
		when().post().
			then().statusCode(415);
	}

	/*
	 * @GET
	 * @Produces("application/json")
	 * @Permissions(Constants.ROLE_ADMIN) public List<UserDto> list() throws
	 * ResourceException;
	 * 
	 * @Path("/")
	 * @POST
	 * @Consumes("application/json")
	 * @Produces("application/json")
	 * @Permissions(Constants.ROLE_ADMIN) public UserDto addUser(@BodyParam
	 * UserDto userDto) throws ResourceException;
	 * 
	 * @Path("/:id")
	 * @GET
	 * @Produces("application/json")
	 * @Permissions({ Constants.ROLE_ADMIN, Constants.ROLE_USER }) public
	 * UserDto getUser(@PathParam("id") String id) throws ResourceException;
	 * 
	 * @Path("/:id")
	 * @PUT
	 * @Consumes("application/json")
	 * @Produces("application/json")
	 * @Permissions(Constants.ROLE_ADMIN) public UserDto
	 * updateUser(@PathParam("id") String id, @BodyParam UserDto userDto) throws
	 * ResourceException;
	 * 
	 * @Path("/:id")
	 * @DELETE
	 * @Produces("application/json")
	 * @Permissions(Constants.ROLE_ADMIN) public void
	 * removeUser(@PathParam("id") String id) throws ResourceException;
	 */

}
