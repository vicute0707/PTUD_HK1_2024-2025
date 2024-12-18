// UserRole.java
package entity;

import java.util.*;
import java.util.logging.Logger;

import gui.Form_SanPham;

public class UserRole {

	private String roleID;
	private String roleName;
	private String permissions; // Stored as comma-separated string in DB
	private Set<String> permissionSet; // For easier permission management in memory
	private static final Logger LOGGER = Logger.getLogger(UserRole.class.getName());

	public UserRole() {
		this.permissionSet = new HashSet<>();
	}

	public UserRole(String roleID, String roleName, String permissions) {
		this.roleID = roleID;
		this.roleName = roleName;
		this.permissions = permissions;
		this.permissionSet = new HashSet<>();
		if (permissions != null && !permissions.isEmpty()) {
			Collections.addAll(this.permissionSet, permissions.split(","));
		}
	}

	// Basic getters and setters
	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
		this.permissionSet.clear();
		if (permissions != null && !permissions.isEmpty()) {
			Collections.addAll(this.permissionSet, permissions.split(","));
		}
	}

	// Permission management methods
	public Set<String> getPermissionSet() {
		return new HashSet<>(permissionSet);
	}

	public void setPermissionSet(Set<String> permissions) {
		this.permissionSet = new HashSet<>(permissions);
		this.permissions = String.join(",", this.permissionSet);
	}

	public void addPermission(String permission) {
		if (permission != null && !permission.isEmpty()) {
			this.permissionSet.add(permission);
			updatePermissionsString();
		}
	}

	public void removePermission(String permission) {
		if (this.permissionSet.remove(permission)) {
			updatePermissionsString();
		}
	}

	public boolean hasPermission(String permissionId) {
		if (this.permissions == null || this.permissions.isEmpty()) {
			return false;
		}

		try {
			// Parse JSON array string
			String cleanPerm = this.permissions.replace("[", "").replace("]", "").replace("\"", "").replace(" ", "");

			String[] permArray = cleanPerm.split(",");

			// Check if permissionId exists in array
			for (String perm : permArray) {
				if (perm.equals(permissionId)) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			LOGGER.severe("Error parsing permissions: " + e.getMessage());
			return false;
		}
	}

	public void clearPermissions() {
		this.permissionSet.clear();
		this.permissions = "";
	}

	private void updatePermissionsString() {
		this.permissions = String.join(",", this.permissionSet);
	}

	// Utility methods for role management
	public boolean isAdminRole() {
		return "ADMIN".equalsIgnoreCase(this.roleName);
	}

	public boolean isManagerRole() {
		return "MANAGER".equalsIgnoreCase(this.roleName);
	}

	public boolean isStaffRole() {
		return "STAFF".equalsIgnoreCase(this.roleName);
	}

	// Override methods for proper object comparison and display
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserRole userRole = (UserRole) o;
		return Objects.equals(roleID, userRole.roleID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleID);
	}

	@Override
	public String toString() {
		return "UserRole{" + "roleID='" + roleID + '\'' + ", roleName='" + roleName + '\'' + ", permissions='"
				+ permissions + '\'' + '}';
	}

}