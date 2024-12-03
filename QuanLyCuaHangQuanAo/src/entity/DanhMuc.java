package entity;

public class DanhMuc {
	private String categoryID; // Changed from maDM to match database
	private String name; // Changed from tenDM to match database
	private String description; // Changed from GhiChu to match database column

	public DanhMuc(String categoryID, String name, String description) {
		this.categoryID = categoryID != null ? categoryID.trim() : null;
		this.name = name != null ? name.trim() : null;
		this.description = description != null ? description.trim() : "";
	}

	public DanhMuc(String name) {
		this.name = name != null ? name.trim() : null;
		this.description = "";
	}

	public DanhMuc() {
		// Initialize with empty strings rather than null
		this.categoryID = "";
		this.name = "";
		this.description = "";
	}

	// Getters and setters with proper validation and backwards compatibility
	public String getMaDM() {
		return categoryID;
	}

	public void setMaDM(String categoryID) {
		this.categoryID = categoryID != null ? categoryID.trim() : "";
	}

	public String getTenDM() {
		return name;
	}

	public void setTenDM(String name) {
		this.name = name != null ? name.trim() : "";
	}

	public String getGhiChu() {
		return description;
	}

	public void setGhiChu(String description) {
		this.description = description != null ? description.trim() : "";
	}

	// Add validation method
	public boolean isValid() {
		return categoryID != null && !categoryID.trim().isEmpty() && name != null && !name.trim().isEmpty();
	}

	@Override
	public String toString() {
		return "DanhMuc[categoryID=" + categoryID + ", name=" + name + ", description=" + description + "]";
	}
}
