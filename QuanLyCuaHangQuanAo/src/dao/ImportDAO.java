package dao;

import entity.Import;
import entity.ImportDetail;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ImportDAO {
	private static final Logger LOGGER = Logger.getLogger(ImportDAO.class.getName());
	private MyConnection myConnection;
	private ImportDetailDAO importDetailDAO;

	public ImportDAO() {
		myConnection = new MyConnection();
		importDetailDAO = new ImportDetailDAO();
	}

	public boolean addImport(Import importObj) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean success = false;

		try {
			conn = myConnection.connect();
			conn.setAutoCommit(false); // Bắt đầu transaction

			// Thêm bản ghi import chính
			String sql = "INSERT INTO `import` (importID, importDate, supplier, staff, totalAmount, details) VALUES (?, ?, ?, ?, ?, ?)";
			pst = conn.prepareStatement(sql);
			pst.setString(1, importObj.getImportID());
			pst.setTimestamp(2, new Timestamp(importObj.getImportDate().getTime()));
			pst.setString(3, importObj.getSupplier());
			pst.setString(4, importObj.getStaff());
			pst.setDouble(5, importObj.getTotalAmount());
			pst.setString(6, "[]");

			int result = pst.executeUpdate();

			if (result > 0) {
				// Thêm chi tiết đơn nhập
				sql = "INSERT INTO importdetail (importID, variantID, quantity, price) VALUES (?, ?, ?, ?)";
				pst = conn.prepareStatement(sql);

				// Xử lý từng chi tiết một
				for (ImportDetail detail : importObj.getImportDetails()) {
					try {
						pst.setString(1, importObj.getImportID());
						pst.setString(2, detail.getVariantID());
						pst.setInt(3, detail.getQuantity());
						pst.setDouble(4, detail.getPrice());

						int detailResult = pst.executeUpdate();

						if (detailResult <= 0) {
							throw new SQLException(
									"Không thể thêm chi tiết đơn nhập cho variant: " + detail.getVariantID());
						}

					} catch (SQLException e) {
						LOGGER.warning(
								"Lỗi khi thêm chi tiết variant " + detail.getVariantID() + ": " + e.getMessage());
						throw e; // Ném lại ngoại lệ để rollback
					}
				}

				conn.commit();
				success = true;
				LOGGER.info("Đã thêm thành công đơn nhập: " + importObj.getImportID());

			} else {
				conn.rollback();
				LOGGER.warning("Không thể tạo đơn nhập chính");
			}

		} catch (SQLException e) {
			LOGGER.severe("Lỗi khi thêm đơn nhập: " + e.getMessage());
			try {
				if (conn != null) {
					conn.rollback();
				}
			} catch (SQLException ex) {
				LOGGER.severe("Lỗi khi rollback: " + ex.getMessage());
			}
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}
			} catch (SQLException e) {
				LOGGER.severe("Lỗi khi đóng kết nối: " + e.getMessage());
			}
		}

		return success;
	}

	public String generateNewImportID() {
		String lastID = "IMP000";
		String sql = "SELECT importID FROM `import` ORDER BY importID DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				lastID = rs.getString("importID");
			}

			// Extract number and increment
			int number = Integer.parseInt(lastID.substring(3)) + 1;
			return String.format("IMP%03d", number);

		} catch (SQLException e) {
			LOGGER.severe("Error generating import ID: " + e.getMessage());
			return "IMP001";
		}
	}

	public Import getImportById(String importId) {
		String sql = """
				    SELECT i.*, u.fullName as staff_name, s.name as supplier_name
				    FROM import i
				    LEFT JOIN user u ON i.staff = u.userID
				    LEFT JOIN supplier s ON i.supplier = s.supplierID
				    WHERE i.importID = ?
				""";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, importId);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					Import importObj = new Import();
					importObj.setImportID(rs.getString("importID"));
					importObj.setImportDate(rs.getTimestamp("importDate"));
					importObj.setSupplier(rs.getString("supplier_name"));
					importObj.setStaff(rs.getString("staff_name"));
					importObj.setTotalAmount(rs.getDouble("totalAmount"));
					importObj.setDetails(rs.getString("details"));

					LOGGER.info("Successfully retrieved import: " + importId);
					return importObj;
				}
			}
		} catch (SQLException e) {
			LOGGER.severe("Database error retrieving import " + importId + ": " + e.getMessage());
		}
		return null;
	}

	public boolean deleteImport(String importId) {
		Connection conn = null;
		try {
			conn = myConnection.connect();
			// Start transaction
			conn.setAutoCommit(false);

			// First, delete import details
			String deleteDetailsSQL = "DELETE FROM importdetail WHERE importID = ?";
			try (PreparedStatement pstDetails = conn.prepareStatement(deleteDetailsSQL)) {
				pstDetails.setString(1, importId);
				pstDetails.executeUpdate();
			}

			// Then, delete the import order
			String deleteImportSQL = "DELETE FROM import WHERE importID = ?";
			try (PreparedStatement pstImport = conn.prepareStatement(deleteImportSQL)) {
				pstImport.setString(1, importId);
				int rowsAffected = pstImport.executeUpdate();

				// Commit transaction
				conn.commit();

				return rowsAffected > 0;
			}
		} catch (SQLException e) {
			// Rollback transaction in case of error
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					LOGGER.severe("Error during rollback: " + rollbackEx.getMessage());
				}
			}
			LOGGER.severe("Error deleting import order: " + e.getMessage());
			return false;
		} finally {
			// Reset auto-commit and close connection
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					LOGGER.severe("Error closing connection: " + e.getMessage());
				}
			}
		}
	}

	public List<Import> getAllImports() {
		List<Import> imports = new ArrayList<>();
		String sql = """
				    SELECT i.*, u.fullName as staff_name, s.name as supplier_name
				    FROM import i
				    LEFT JOIN user u ON i.staff = u.userID
				    LEFT JOIN supplier s ON i.supplier = s.supplierID
				    ORDER BY i.importDate DESC
				""";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				Import importObj = new Import();
				importObj.setImportID(rs.getString("importID"));
				importObj.setImportDate(rs.getTimestamp("importDate"));
				importObj.setSupplier(rs.getString("supplier_name"));
				importObj.setStaff(rs.getString("staff_name"));
				importObj.setTotalAmount(rs.getDouble("totalAmount"));
				importObj.setDetails(rs.getString("details"));
				imports.add(importObj);
			}
		} catch (SQLException e) {
			LOGGER.severe("Error getting imports: " + e.getMessage());
		}
		return imports;
	}

}
