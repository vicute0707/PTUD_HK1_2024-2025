package entity;

public class Permission {
	private String permissionID;
	private String name;
	private String description;

	public Permission() {
	}

	public Permission(String permissionID, String name, String description) {
		this.permissionID = permissionID;
		this.name = name;
		this.description = description;
	}

	// Getters and setters
	public String getPermissionID() {
		return permissionID;
	}

	public void setPermissionID(String permissionID) {
		this.permissionID = permissionID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
