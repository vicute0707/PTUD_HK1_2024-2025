package dao;

import entity.ImportDetail;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportDetailDAO {
	private static final Logger LOGGER = Logger.getLogger(ImportDetailDAO.class.getName());
	private MyConnection myConnection;

	public ImportDetailDAO() {
		myConnection = new MyConnection();
	}

	 public List<ImportDetail> getImportDetailsByImportID(String importID) {
	        List<ImportDetail> details = new ArrayList<>();
	        // Updated query to use stored product information
	        String sql = "SELECT * FROM importdetail WHERE importID = ?";

	        try (Connection conn = myConnection.connect();
	             PreparedStatement pst = conn.prepareStatement(sql)) {
	            
	            pst.setString(1, importID);
	            try (ResultSet rs = pst.executeQuery()) {
	                while (rs.next()) {
	                    ImportDetail detail = new ImportDetail();
	                    detail.setImportID(rs.getString("importID"));
	                    detail.setVariantID(rs.getString("variantID"));
	                    detail.setQuantity(rs.getInt("quantity"));
	                    detail.setPrice(rs.getDouble("price"));
	                    detail.setProductName(rs.getString("productName"));
	                    detail.setVariantName(rs.getString("variantInfo"));
	                    
	                    details.add(detail);
	                }
	            }
	            
	            LOGGER.info("Retrieved " + details.size() + " details for import " + importID);
	            
	        } catch (SQLException e) {
	            LOGGER.severe("Error getting import details for " + importID + ": " + e.getMessage());
	        }
	        return details;
	    }

	    public boolean addImportDetail(ImportDetail detail) {
	        String sql = "INSERT INTO importdetail (importID, variantID, quantity, price) VALUES (?, ?, ?, ?)";
	        
	        try (Connection conn = myConnection.connect();
	             PreparedStatement pst = conn.prepareStatement(sql)) {
	            
	            pst.setString(1, detail.getImportID());
	            pst.setString(2, detail.getVariantID());
	            pst.setInt(3, detail.getQuantity());
	            pst.setDouble(4, detail.getPrice());
	            
	            int result = pst.executeUpdate();
	            LOGGER.info("Added import detail for variant " + detail.getVariantID());
	            return result > 0;
	            
	        } catch (SQLException e) {
	            LOGGER.severe("Error adding import detail: " + e.getMessage());
	            return false;
	        }
	    }
	    public boolean updateImportDetail(ImportDetail detail) {
	        // We'll use a transaction since we might need to update related records
	        Connection conn = null;
	        PreparedStatement pst = null;
	        boolean success = false;

	        try {
	            conn = myConnection.connect();
	            // Start transaction to ensure data consistency
	            conn.setAutoCommit(false);

	            // Prepare the update statement with all relevant fields
	            String sql = """
	                UPDATE importdetail 
	                SET quantity = ?,
	                    price = ?
	                WHERE importID = ? 
	                AND variantID = ?
	            """;

	            pst = conn.prepareStatement(sql);
	            
	            // Set parameters with null checks and validation
	            pst.setInt(1, detail.getQuantity());
	            pst.setDouble(2, detail.getPrice());
	            pst.setString(3, detail.getImportID());
	            pst.setString(4, detail.getVariantID());

	            // Execute the update
	            int rowsAffected = pst.executeUpdate();

	            if (rowsAffected > 0) {
	                // If update successful, we also need to update the total amount in the import table
	                success = updateImportTotalAmount(conn, detail.getImportID());
	                
	                if (success) {
	                    conn.commit();
	                    LOGGER.info("Successfully updated import detail for Import ID: " + 
	                              detail.getImportID() + ", Variant ID: " + detail.getVariantID());
	                } else {
	                    conn.rollback();
	                    LOGGER.warning("Failed to update import total amount. Rolling back detail update.");
	                }
	            } else {
	                conn.rollback();
	                LOGGER.warning("No rows affected when updating import detail. Possible invalid IDs.");
	            }

	        } catch (SQLException e) {
	            LOGGER.log(Level.SEVERE, "Error updating import detail", e);
	            try {
	                if (conn != null) {
	                    conn.rollback();
	                }
	            } catch (SQLException rollbackEx) {
	                LOGGER.log(Level.SEVERE, "Error during rollback", rollbackEx);
	            }
	        } finally {
	            // Clean up resources and restore auto-commit
	            try {
	                if (pst != null) {
	                    pst.close();
	                }
	                if (conn != null) {
	                    conn.setAutoCommit(true);
	                    conn.close();
	                }
	            } catch (SQLException e) {
	                LOGGER.log(Level.SEVERE, "Error closing database resources", e);
	            }
	        }

	        return success;
	    }

	    /**
	     * Updates the total amount in the import table based on all its details.
	     * This is called after updating an import detail to ensure the total stays accurate.
	     * 
	     * @param conn The active database connection (must be in a transaction)
	     * @param importID The ID of the import to update
	     * @return boolean indicating whether the update was successful
	     */
	    private boolean updateImportTotalAmount(Connection conn, String importID) throws SQLException {
	        // Calculate new total from all details
	        String calculateSql = """
	            SELECT SUM(quantity * price) as total 
	            FROM importdetail 
	            WHERE importID = ?
	        """;
	        
	        try (PreparedStatement calcStmt = conn.prepareStatement(calculateSql)) {
	            calcStmt.setString(1, importID);
	            
	            try (ResultSet rs = calcStmt.executeQuery()) {
	                if (rs.next()) {
	                    double newTotal = rs.getDouble("total");
	                    
	                    // Update the import table with new total
	                    String updateSql = "UPDATE import SET totalAmount = ? WHERE importID = ?";
	                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                        updateStmt.setDouble(1, newTotal);
	                        updateStmt.setString(2, importID);
	                        
	                        int rowsAffected = updateStmt.executeUpdate();
	                        return rowsAffected > 0;
	                    }
	                }
	            }
	        }
	        
	        return false;
	    }


}