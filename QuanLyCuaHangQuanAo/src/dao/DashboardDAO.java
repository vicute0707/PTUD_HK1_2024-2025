package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.MyConnection;
import entity.DashboardStats;

public class DashboardDAO {
    private MyConnection myConnection;
    
    public DashboardDAO() {
        myConnection = new MyConnection();
    }
    
    public DashboardStats getDashboardStats() {
        int totalActiveStaff = 0;
        int totalProducts = 0;
        int totalSuppliers = 0;
        double monthlyRevenue = 0.0;
        
        try(Connection conn = myConnection.connect();
            ) {
            // Get total active staff
            String staffQuery = "SELECT COUNT(*) FROM user WHERE status = 'active'";
            PreparedStatement staffStmt = conn.prepareStatement(staffQuery);
            ResultSet staffRs = staffStmt.executeQuery();
            if (staffRs.next()) {
                totalActiveStaff = staffRs.getInt(1);
            }
            
            // Get total products in stock
            String productQuery = "SELECT SUM(stockQuantity) FROM product WHERE status = 'Đang kinh doanh'";
            PreparedStatement productStmt = conn.prepareStatement(productQuery);
            ResultSet productRs = productStmt.executeQuery();
            if (productRs.next()) {
                totalProducts = productRs.getInt(1);
            }
            
            // Get total active suppliers
            String supplierQuery = "SELECT COUNT(*) FROM supplier";
            PreparedStatement supplierStmt = conn.prepareStatement(supplierQuery);
            ResultSet supplierRs = supplierStmt.executeQuery();
            if (supplierRs.next()) {
                totalSuppliers = supplierRs.getInt(1);
            }
            
            // Get monthly revenue
            String revenueQuery = "SELECT SUM(totalAmount) FROM `order` " +
                                "WHERE MONTH(orderDate) = MONTH(CURRENT_DATE()) " +
                                "AND YEAR(orderDate) = YEAR(CURRENT_DATE())";
            PreparedStatement revenueStmt = conn.prepareStatement(revenueQuery);
            ResultSet revenueRs = revenueStmt.executeQuery();
            if (revenueRs.next()) {
                monthlyRevenue = revenueRs.getDouble(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new DashboardStats(totalActiveStaff, totalProducts, totalSuppliers, monthlyRevenue);
    }
}
