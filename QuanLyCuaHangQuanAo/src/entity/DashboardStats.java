package entity;

public class DashboardStats {
	private int totalActiveStaff;
	private int totalProducts;
	private int totalSuppliers;
	private double monthlyRevenue;

	// Constructor
	public DashboardStats(int totalActiveStaff, int totalProducts, int totalSuppliers, double monthlyRevenue) {
		this.totalActiveStaff = totalActiveStaff;
		this.totalProducts = totalProducts;
		this.totalSuppliers = totalSuppliers;
		this.monthlyRevenue = monthlyRevenue;
	}

	// Getters
	public int getTotalActiveStaff() {
		return totalActiveStaff;
	}

	public int getTotalProducts() {
		return totalProducts;
	}

	public int getTotalSuppliers() {
		return totalSuppliers;
	}

	public double getMonthlyRevenue() {
		return monthlyRevenue;
	}
}