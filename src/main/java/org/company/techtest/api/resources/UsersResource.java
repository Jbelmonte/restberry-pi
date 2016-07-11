package org.company.techtest.api.resources;

import java.util.List;

import org.company.techtest.api.resources.dto.UserDto;
import org.restberrypi.core.jaxrs.annotations.BodyParam;
import org.restberrypi.core.jaxrs.annotations.Consumes;
import org.restberrypi.core.jaxrs.annotations.DELETE;
import org.restberrypi.core.jaxrs.annotations.GET;
import org.restberrypi.core.jaxrs.annotations.POST;
import org.restberrypi.core.jaxrs.annotations.PUT;
import org.restberrypi.core.jaxrs.annotations.Path;
import org.restberrypi.core.jaxrs.annotations.PathParam;
import org.restberrypi.core.jaxrs.annotations.Permissions;
import org.restberrypi.core.jaxrs.annotations.Produces;
import org.restberrypi.core.jaxrs.exceptions.ResourceException;
import org.restberrypi.core.security.Constants;

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
