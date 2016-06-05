package org.company.techtest.api.resources;

import java.util.List;

import org.company.core.jaxrs.annotations.BodyParam;
import org.company.core.jaxrs.annotations.Consumes;
import org.company.core.jaxrs.annotations.DELETE;
import org.company.core.jaxrs.annotations.GET;
import org.company.core.jaxrs.annotations.POST;
import org.company.core.jaxrs.annotations.PUT;
import org.company.core.jaxrs.annotations.Path;
import org.company.core.jaxrs.annotations.PathParam;
import org.company.core.jaxrs.annotations.Permissions;
import org.company.core.jaxrs.annotations.Produces;
import org.company.core.jaxrs.exceptions.ResourceException;
import org.company.core.security.Constants;
import org.company.techtest.api.resources.dto.UserDto;

/**
 * Users' exposed REST interface.
 */
@Path("/users")
public interface UsersResource extends Resource {
	@GET
	@Produces("application/json")
	@Permissions(Constants.ROLE_ADMIN)
	public List<UserDto> list() throws ResourceException;

	@Path("/")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Permissions(Constants.ROLE_ADMIN)
	public UserDto addUser(@BodyParam UserDto userDto) throws ResourceException;

	@Path("/:id")
	@GET
	@Produces("application/json")
	@Permissions({ Constants.ROLE_ADMIN, Constants.ROLE_USER })
	public UserDto getUser(@PathParam("id") String id) throws ResourceException;

	@Path("/:id")
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	@Permissions(Constants.ROLE_ADMIN)
	public UserDto updateUser(@PathParam("id") String id, @BodyParam UserDto userDto) throws ResourceException;

	@Path("/:id")
	@DELETE
	@Produces("application/json")
	@Permissions(Constants.ROLE_ADMIN)
	public void removeUser(@PathParam("id") String id) throws ResourceException;
}
