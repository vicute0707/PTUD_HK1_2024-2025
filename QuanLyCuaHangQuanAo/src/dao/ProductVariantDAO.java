package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import connection.MyConnection;
import entity.ProductVariant;

public class ProductVariantDAO {
	private static final Logger LOGGER = Logger.getLogger(ProductVariantDAO.class.getName());
	private MyConnection myConnection;

	public ProductVariantDAO() {
		myConnection = new MyConnection();
	}

	public boolean updateVariantQuantity(String variantID, int quantity) {
		String sql = "UPDATE productvariant SET quantity = quantity + ? WHERE variantID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setInt(1, quantity);
			pst.setString(2, variantID);

			int result = pst.executeUpdate();

			if (result > 0) {
				LOGGER.info(String.format("Updated quantity for variant %s: +%d", variantID, quantity));
				updateProductStockQuantity(variantID, quantity, conn);
				return true;
			}

			return false;
		} catch (SQLException e) {
			LOGGER.severe("Error updating variant quantity: " + e.getMessage());
			return false;
		}
	}

	private void updateProductStockQuantity(String variantID, int quantity, Connection conn) throws SQLException {
		// Lấy productID từ variant
		String getProductSQL = "SELECT productID FROM productvariant WHERE variantID = ?";
		String updateProductSQL = "UPDATE product SET stockQuantity = (SELECT SUM(quantity) FROM productvariant WHERE productID = ?) WHERE productID = ?";

		String productID = null;

		// Lấy productID
		try (PreparedStatement pst = conn.prepareStatement(getProductSQL)) {
			pst.setString(1, variantID);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				productID = rs.getString("productID");
			}
		}

		// Cập nhật tổng số lượng trong bảng product
		if (productID != null) {
			try (PreparedStatement pst = conn.prepareStatement(updateProductSQL)) {
				pst.setString(1, productID);
				pst.setString(2, productID);
				pst.executeUpdate();
				LOGGER.info("Updated stock quantity for product: " + productID);
			}
		}
	}

	public ProductVariant getVariantByID(String variantID) {
		String sql = "SELECT * FROM productvariant WHERE variantID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, variantID);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					ProductVariant variant = new ProductVariant();
					variant.setVariantID(rs.getString("variantID"));
					variant.setProductID(rs.getString("productID"));
					variant.setSize(rs.getString("size"));
					variant.setColor(rs.getString("color"));
					variant.setQuantity(rs.getInt("quantity"));
					return variant;
				}
			}
		} catch (SQLException e) {
			LOGGER.severe("Error getting variant: " + e.getMessage());
		}
		return null;
	}

	public List<ProductVariant> getAllVariantsByProductID(String productID) {
		List<ProductVariant> variants = new ArrayList<>();
		String sql = "SELECT * FROM productvariant WHERE productID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, productID);

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					ProductVariant variant = new ProductVariant();
					variant.setVariantID(rs.getString("variantID"));
					variant.setProductID(rs.getString("productID"));
					variant.setSize(rs.getString("size"));
					variant.setColor(rs.getString("color"));
					variant.setQuantity(rs.getInt("quantity"));
					variants.add(variant);
				}
			}
		} catch (SQLException e) {
			LOGGER.severe("Error getting variants: " + e.getMessage());
		}
		return variants;
	}

	public boolean updateVariant(ProductVariant variant) {
		String sql = "UPDATE productvariant SET quantity = ? WHERE variantID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setInt(1, variant.getQuantity());
			pst.setString(2, variant.getVariantID());

			int result = pst.executeUpdate();
			if (result > 0) {
				LOGGER.info("Successfully updated variant quantity for variantID: " + variant.getVariantID());
				return true;
			}

		} catch (SQLException e) {
			LOGGER.severe("Error updating variant: " + e.getMessage());
		}

		return false;
	}

	public boolean addVariant(ProductVariant variant) {
		String sql = "SELECT productID FROM product WHERE productID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement check = conn.prepareStatement(sql)) {

			check.setString(1, variant.getProductID());
			ResultSet rs = check.executeQuery();

			if (!rs.next()) {
				LOGGER.severe("Product ID not found: " + variant.getProductID());
				return false;
			}

			sql = "INSERT INTO productvariant (variantID, productID, color, size, quantity) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement pst = conn.prepareStatement(sql)) {
				pst.setString(1, variant.getVariantID());
				pst.setString(2, variant.getProductID());
				pst.setString(3, variant.getColor());
				pst.setString(4, variant.getSize());
				pst.setInt(5, variant.getQuantity());

				return pst.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			LOGGER.severe("Error adding variant: " + e.getMessage());
			return false;
		}
	}

	public boolean deleteVariant(String variantID, String productID) {
		String sql = "DELETE FROM ProductVariant WHERE variantID = ? AND productID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, variantID);
			pst.setString(2, productID);

			boolean result = pst.executeUpdate() > 0;
			LOGGER.info("Deleted variant " + variantID + ": " + (result ? "success" : "failed"));
			return result;

		} catch (SQLException e) {
			LOGGER.severe("Error deleting variant: " + e.getMessage());
			return false;
		}
	}

	private ProductVariant createVariantFromResultSet(ResultSet rs) throws SQLException {
		ProductVariant variant = new ProductVariant();
		variant.setVariantID(rs.getString("variantID"));
		variant.setProductID(rs.getString("productID"));
		variant.setSize(rs.getString("size"));
		variant.setColor(rs.getString("color"));
		variant.setQuantity(rs.getInt("quantity"));
		return variant;
	}

	public String generateNewVariantID(String productID) {
		String sql = "SELECT variantID FROM productvariant WHERE productID = ? ORDER BY variantID DESC LIMIT 1";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, productID);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				String lastID = rs.getString("variantID");
				int number = Integer.parseInt(lastID.substring(productID.length())) + 1;
				return String.format("%s%03d", productID, number);
			}
			return productID + "001";

		} catch (SQLException e) {
			LOGGER.severe("Error generating variant ID: " + e.getMessage());
			return productID + "001";
		}
	}

	public String getVariantID(String productID, String color, String size) {
		String sql = "SELECT variantID FROM productvariant WHERE productID = ? AND color = ? AND size = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, productID);
			pst.setString(2, color);
			pst.setString(3, size);

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString("variantID");
			}

			return null;
		} catch (SQLException e) {
			LOGGER.severe("Error getting variantID: " + e.getMessage());
			return null;
		}
	}

	public String getDefaultVariantID(String productID) {
		String sql = "SELECT variantID FROM productvariant WHERE productID = ? AND color = 'Mặc định' AND size = 'Mặc định'";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, productID);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getString("variantID");
			}

			// Nếu không tìm thấy variant mặc định, tạo mới
			return createDefaultVariant(productID);

		} catch (SQLException e) {
			LOGGER.severe("Error getting default variant: " + e.getMessage());
			return null;
		}
	}

	public String createDefaultVariant(String productID) {
		String variantID = generateNewVariantID();
		String sql = "INSERT INTO productvariant (variantID, productID, color, size, quantity) VALUES (?, ?, 'Mặc định', 'Mặc định', 0)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, variantID);
			pst.setString(2, productID);

			if (pst.executeUpdate() > 0) {
				LOGGER.info("Created default variant " + variantID + " for product " + productID);
				return variantID;
			}
			return null;
		} catch (SQLException e) {
			LOGGER.severe("Error creating default variant: " + e.getMessage());
			return null;
		}
	}

	private String generateNewVariantID() {
		String sql = "SELECT variantID FROM productvariant ORDER BY CAST(SUBSTRING(variantID, 4) AS UNSIGNED) DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				String lastID = rs.getString("variantID");
				int number = Integer.parseInt(lastID.substring(3)) + 1;
				return String.format("VAR%03d", number);
			}
			return "VAR001";
		} catch (SQLException e) {
			LOGGER.severe("Error generating new variant ID: " + e.getMessage());
			return "VAR001";
		}
	}

}