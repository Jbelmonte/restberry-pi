package org.business.techtest.site;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.company.core.security.Constants;
import org.company.techtest.AppStarter;
import org.company.techtest.api.resources.dto.UserDto;
import org.company.techtest.dao.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;

public class ITestSites {

	private static final String BASE_URI = "http://localhost";
	private static final int PORT = 8080;
	private AppStarter server;
	private String user1Id = null;
	
	@BeforeClass
	public static void config() {
		RestAssured.baseURI = BASE_URI;
		RestAssured.port = PORT;
		RestAssured.basePath = "/site";
	}

	@Before
	public void startServer() throws IOException {
		server = new AppStarter();
		server.setPort(PORT);
		server.start();
		
		// Add user with ROLE_PAGE1
		UserDto user = new UserDto();
		user.setUsername("site1");
		user.setPassword("site1");
		user.setRoles(Arrays.asList(Constants.ROLE_PAGE1));
		UserDto added = given().
				basePath("/rest/users").
				auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
				contentType(ContentType.JSON).
				body(user).
			post().as(UserDto.class);
		user1Id = added.getId();

		// Add user with ROLE_PAGE3
		user = new UserDto();
		user.setUsername("site2");
		user.setPassword("site2");
		user.setRoles(Arrays.asList(Constants.ROLE_PAGE2));
		given().
			basePath("/rest/users").
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			contentType(ContentType.JSON).
			body(user).
		post().then().using().defaultParser(Parser.JSON);	

		// Add user with ROLE_PAGE3
		user = new UserDto();
		user.setUsername("site3");
		user.setPassword("site3");
		user.setRoles(Arrays.asList(Constants.ROLE_PAGE3));
		given().
			basePath("/rest/users").
			auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
			contentType(ContentType.JSON).
			body(user).
		post().then().using().defaultParser(Parser.JSON);	
}

	@After
	public void stopServer() {
		server.stop();
	}
	
	@Test
	public void testIndexIsPublic() {
		when().get(Constants.INDEX_URL).
			then().statusCode(200).
			contentType(ContentType.HTML);
	}
	
	@Test
	public void testPage1IsRestricted() {
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.LOGIN_URL));
	}
	
	@Test
	public void testInvalidCredentialsFailsLogin() {
		Response response = loginAs("site1", "invalid");
		
		assertThat("User is redirected after login", response.statusCode(), is(302));
		assertThat("User is redirected after to login page", response.header("Location"), endsWith(Constants.LOGIN_URL));
	}
	
	@Test
	public void testNewUser1LogsIn() {
		Response response = loginAs("site1", "site1");
		
		assertThat("User is redirected after login", response.statusCode(), is(302));
		assertThat("User is redirected after to index page", response.header("Location"), endsWith(Constants.INDEX_URL));
	}
	
	@Test
	public void testNewUser2LogsIn() {
		Response response = loginAs("site2", "site2");
		
		assertThat("User is redirected after login", response.statusCode(), is(302));
		assertThat("User is redirected after to index page", response.header("Location"), endsWith(Constants.INDEX_URL));
	}
	
	@Test
	public void testNewUser3LogsIn() {
		Response response = loginAs("site3", "site3");
		
		assertThat("User is redirected after login", response.statusCode(), is(302));
		assertThat("User is redirected after to index page", response.header("Location"), endsWith(Constants.INDEX_URL));
	}
	
	@Test
	public void testPage1RequiresRole1() {
		Response loginSite1 = loginAs("site1", "site1");
		String site1Token = loginSite1.cookie(Constants.SEC_COOKIE_NAME);
		Response loginSite2 = loginAs("site2", "site2");
		String site2Token = loginSite2.cookie(Constants.SEC_COOKIE_NAME);
		Response loginSite3 = loginAs("site3", "site3");
		String site3Token = loginSite3.cookie(Constants.SEC_COOKIE_NAME);
		
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(not(containsString("Oops! You have no permissions!")));
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site2Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site2Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! You have no permissions!"));
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site3Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site3Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! You have no permissions!"));
	}
	
	@Test
	public void testPage2IsRestricted() {
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
		when().get(Constants.PAGE_2_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.LOGIN_URL));
	}
	
	@Test
	public void testPage2RequiresRole2() {
		Response loginSite1 = loginAs("site1", "site1");
		String site1Token = loginSite1.cookie(Constants.SEC_COOKIE_NAME);
		Response loginSite2 = loginAs("site2", "site2");
		String site2Token = loginSite2.cookie(Constants.SEC_COOKIE_NAME);
		Response loginSite3 = loginAs("site3", "site3");
		String site3Token = loginSite3.cookie(Constants.SEC_COOKIE_NAME);

		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_2_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_2_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! You have no permissions!"));
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site2Token).
		when().get(Constants.PAGE_2_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(not(containsString("Oops! You have no permissions!")));
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site3Token).
		when().get(Constants.PAGE_2_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site3Token).
		when().get(Constants.PAGE_2_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! You have no permissions!"));
	}

	@Test
	public void testPage3IsRestricted() {
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.LOGIN_URL));
	}
	
	@Test
	public void testPage3RequiresRole3() {
		Response loginSite1 = loginAs("site1", "site1");
		String site1Token = loginSite1.cookie(Constants.SEC_COOKIE_NAME);
		Response loginSite2 = loginAs("site2", "site2");
		String site2Token = loginSite2.cookie(Constants.SEC_COOKIE_NAME);
		Response loginSite3 = loginAs("site3", "site3");
		String site3Token = loginSite3.cookie(Constants.SEC_COOKIE_NAME);

		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! You have no permissions!"));
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site2Token).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site2Token).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! You have no permissions!"));
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site3Token).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(not(containsString("Oops! You have no permissions!")));
	}
	
	@Test
	public void testRemovingRoleRevokesAccess() {
		// Remove role
		UserDto user = new UserDto();
		user.setRoles(Arrays.asList(Constants.ROLE_PAGE3));
		UserDto updated = given().
				basePath("/rest/users").
				auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
				contentType(ContentType.JSON).
				body(user).
			put("/" + user1Id).as(UserDto.class);
		assertThat("User lost ROLE_PAGE1", updated.getRoles(), not(containsInAnyOrder(Constants.ROLE_PAGE1)));

		// Login and access
		Response loginSite1 = loginAs("site1", "site1");
		String site1Token = loginSite1.cookie(Constants.SEC_COOKIE_NAME);
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(302).
			header("Location", endsWith(Constants.UNAUTHORIZED_URL));
		given().
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_1_URL).
			then().statusCode(200).
			body(containsString("Oops! You have no permissions!"));
	}
	
	@Test
	public void testAddingRoleGrantesAccess() {
		// Remove role
		UserDto user = new UserDto();
		user.setRoles(Arrays.asList(Constants.ROLE_PAGE1, Constants.ROLE_PAGE3));
		UserDto updated = given().
				basePath("/rest/users").
				auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
				contentType(ContentType.JSON).
				body(user).
			put("/" + user1Id).as(UserDto.class);
		assertThat("User maintains ROLE_PAGE1 and gains ROLE_PAGE3", updated.getRoles(), containsInAnyOrder(Constants.ROLE_PAGE1, Constants.ROLE_PAGE3));

		// Login and access
		Response loginSite1 = loginAs("site1", "site1");
		String site1Token = loginSite1.cookie(Constants.SEC_COOKIE_NAME);
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
			cookie(Constants.SEC_COOKIE_NAME, site1Token).
		when().get(Constants.PAGE_3_URL).
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(not(containsString("Oops! You have no permissions!")));
	}
	
	@Test
	public void testRemovingUserRevokesAccess() {
		// Remove user
		Response response = given().
								basePath("/rest/users").
								auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
							delete("/" + user1Id).andReturn();
		assertThat("Remove successful", response.statusCode(), is(200));
		
		response = given().
						basePath("/rest/users").
						auth().basic(UserDAO.MOCK_USERNAME, UserDAO.MOCK_PASSWORD).
					get("/" + user1Id).andReturn();
		assertThat("User no longer exists", response.statusCode(), is(404));

		// Login and access
		Response loginSite1 = loginAs("site1", "site1");
		assertThat("Login fails", loginSite1.statusCode(), is(302));
		assertThat("Login redirects to login page", loginSite1.header("Location"), endsWith(Constants.LOGIN_URL));
	}
	
	@Test
	public void testLogoutRevokesAccess() {
		// Login and access
		Response loginSite1 = loginAs("site1", "site1");
		String site1Token = loginSite1.cookie(Constants.SEC_COOKIE_NAME);
		Response response = given().
								config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
								cookie(Constants.SEC_COOKIE_NAME, site1Token).
							when().get(Constants.PAGE_1_URL).andReturn();
		assertThat("After login it has access to page1.html", response.statusCode(), is(200));

		// Logout
		response = given().
						config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
						cookie(Constants.SEC_COOKIE_NAME, site1Token).
					when().get(Constants.LOGOUT_URL).andReturn();
		assertThat("Logout successful", response.statusCode(), is(302));
		assertThat("Logout removes token", response.cookie(Constants.SEC_COOKIE_NAME), is("deleted"));
		assertThat("Logout redirects to login page", response.header("Location"), endsWith(Constants.LOGIN_URL));
		
		// Access again
		response = given().
						config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
						cookie(Constants.SEC_COOKIE_NAME, site1Token).
					when().get(Constants.PAGE_1_URL).andReturn();
		assertThat("Access with invalid token fails", response.statusCode(), is(302));
		assertThat("Invalid access redirects to login page", response.header("Location"), endsWith(Constants.LOGIN_URL));
	}

	@Test
	public void testPageNotFound() {
		// Redirection
		given().
			config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
		when().get("/invalid-page.html").
			then().statusCode(302).
			header("Location", endsWith(Constants.NOT_FOUND_URL));
		// Content
		given().
		when().get("/invalid-page.html").
			then().statusCode(200).
			contentType(ContentType.HTML).
			body(containsString("Oops! Not found!"));
	}
	
	protected Response loginAs(String username, String password) {
		return given().
					config(RestAssured.config().redirect(redirectConfig().followRedirects(false))).
					formParam(Constants.LOGIN_USERNAME_PARAM, username).
					formParam(Constants.LOGIN_PASSWORD_PARAM, password).
					contentType("application/x-www-form-urlencoded").
				when().post(Constants.LOGIN_URL).andReturn();
	}

}
