package entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.JsonArray;


public class Import {
	private String importID; // Primary key
	private Date importDate; // Date of import
	private String supplier; // Supplier reference
	private String staff; // Staff who handled the import
	private double totalAmount; // Total cost of import
	private String details; // Additional notes/details
	private List<ImportDetail> importDetails; // One-to-many relationship with details

	public Import() {
		this.importDetails = new ArrayList<>();
	}

	// Constructor with fields
	public Import(String importID, Date importDate, String supplier, String staff, double totalAmount, String details) {
		this.importID = importID;
		this.importDate = importDate;
		this.supplier = supplier;
		this.staff = staff;
		this.totalAmount = totalAmount;
		this.details = details;
		this.importDetails = new ArrayList<>();
	}



	// Getters and setters
	public String getImportID() {
		return importID;
	}

	public void setImportID(String importID) {
		this.importID = importID;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<ImportDetail> getImportDetails() {
		return importDetails;
	}

	public void setImportDetails(List<ImportDetail> importDetails) {
		this.importDetails = importDetails;
	}

	// Helper method to add import detail
	public void addImportDetail(ImportDetail detail) {
		this.importDetails.add(detail);
		// Recalculate total amount
		this.calculateTotalAmount();
	}

	// Helper method to calculate total amount based on details
	private void calculateTotalAmount() {
		this.totalAmount = importDetails.stream().mapToDouble(detail -> detail.getQuantity() * detail.getPrice()).sum();
	}
}