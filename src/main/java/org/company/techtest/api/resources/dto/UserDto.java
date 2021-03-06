package org.company.techtest.api.resources.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User data transfer object.
 */
@JsonInclude(Include.NON_NULL)
@XmlRootElement
public class UserDto implements Serializable, Comparable<UserDto> {
	private static final long serialVersionUID = -8795238393651104480L;

	@JsonProperty
	private String id;
	@JsonProperty
	private String username;
	@JsonProperty
	private String password;
	@JsonProperty
	private List<String> roles;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int compareTo(UserDto o) {
		return id.compareTo(o.id) + username.compareTo(o.username) + (roles.equals(o.roles) ? 0 : 1);
	}

}
