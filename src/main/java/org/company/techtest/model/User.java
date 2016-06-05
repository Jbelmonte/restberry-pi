package org.company.techtest.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class User implements Serializable {
	private static final long serialVersionUID = 368654930090150288L;

	private static final BeanComparator<User> COMPARATOR = new BeanComparator<User>();

	private String id;
	private String username;
	private String password;
	private List<String> roles = new LinkedList<>();

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User)) {
			return false;
		}
		return COMPARATOR.compare(this, (User) obj) == 0;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(username).append(password).append(roles).toHashCode();
	}
}
