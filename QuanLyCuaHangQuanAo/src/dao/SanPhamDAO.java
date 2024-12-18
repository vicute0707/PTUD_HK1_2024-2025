package dao;

import entity.SanPham;
import entity.DanhMuc;
import entity.ProductVariant;
import connection.MyConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import component.ImageUploadUtil;

public class SanPhamDAO {
	private static final Logger LOGGER = Logger.getLogger(SanPhamDAO.class.getName());
	private MyConnection myConnection;
	private static final String DEFAULT_IMAGE = "default-product.jpg";
	private static final String IMAGE_DIRECTORY = "src/img_product";
	
	public SanPhamDAO() {
		myConnection = new MyConnection();
	}



	public boolean addSanPham(SanPham sp) {
		String sql = "INSERT INTO product (productID, name, category, brand, imagePath, stockQuantity, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
			String newImagePath = sp.getHinhAnh();

			// Handle image upload - now stores just the filename
			String imageName = DEFAULT_IMAGE;
			if (sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
				File imageFile = new File(sp.getHinhAnh());
				imageName = ImageUploadUtil.updateProductImage(newImagePath, sp.getMaSP(), imageName);
			}

			pst.setString(1, sp.getMaSP());
			pst.setString(2, sp.getTenSP());
			pst.setString(3, sp.getDanhmuc().getMaDM());
			pst.setString(4, sp.getThuongHieu());
			pst.setString(5, imageName); // Store just the filename
			pst.setInt(6, sp.getSoLuongTonKho());
			pst.setString(7, sp.getTinhTrang());

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			LOGGER.severe("Error adding product: " + e.getMessage());
			return false;
		}
	}
	

	public boolean updateSanPham(SanPham sp) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean success = false;

		try {
			// Get connection and start transaction
			conn = myConnection.connect();
			conn.setAutoCommit(false);

			// Get current product data
			String selectSql = "SELECT imagePath FROM product WHERE productID = ?";
			pst = conn.prepareStatement(selectSql);
			pst.setString(1, sp.getMaSP());
			rs = pst.executeQuery();

			String oldImageName = null;
			if (rs.next()) {
				oldImageName = rs.getString("imagePath");
			} else {
				throw new SQLException("Product not found: " + sp.getMaSP());
			}

			// Close first PreparedStatement and ResultSet
			if (rs != null)
				rs.close();
			if (pst != null)
				pst.close();

			// Handle image update - this now handles all image file operations
			String finalImageName = ImageUploadUtil.updateProductImage(sp.getHinhAnh(), // New image path (can be null)
					sp.getMaSP(), // Product ID
					oldImageName // Current image name
			);

			// Update product data
			String updateSql = """
					UPDATE Product
					SET name = ?,
					    category = ?,
					    stockQuantity = ?,
					    brand = ?,
					    imagePath = ?,
					    status = ?
					WHERE productID = ?
					""";

			pst = conn.prepareStatement(updateSql);
			pst.setString(1, sp.getTenSP());
			pst.setString(2, sp.getDanhmuc().getMaDM());
			pst.setInt(3, sp.getSoLuongTonKho());
			pst.setString(4, sp.getThuongHieu());
			pst.setString(5, finalImageName);
			pst.setString(6, determineTinhTrang(sp.getSoLuongTonKho()));
			pst.setString(7, sp.getMaSP());

			int rowsAffected = pst.executeUpdate();

			if (rowsAffected > 0) {
				conn.commit();
				success = true;
				LOGGER.info("Successfully updated product: " + sp.getMaSP());
			} else {
				conn.rollback();
				LOGGER.warning("No rows affected when updating product: " + sp.getMaSP());
			}

		} catch (SQLException e) {
			LOGGER.severe("Database error while updating product: " + e.getMessage());
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException rollbackEx) {
				LOGGER.severe("Error during transaction rollback: " + rollbackEx.getMessage());
			}
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pst != null)
					pst.close();
				if (conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}
			} catch (SQLException e) {
				LOGGER.severe("Error closing database resources: " + e.getMessage());
			}
		}

		return success;
	}

	// Helper method to validate product data
	private boolean validateProductData(SanPham sp) {
		if (sp == null) {
			LOGGER.severe("Product object is null");
			return false;
		}

		if (sp.getMaSP() == null || sp.getMaSP().trim().isEmpty()) {
			LOGGER.severe("Product ID is null or empty");
			return false;
		}

		if (sp.getTenSP() == null || sp.getTenSP().trim().isEmpty()) {
			LOGGER.severe("Product name is null or empty");
			return false;
		}

		if (sp.getDanhmuc() == null || sp.getDanhmuc().getMaDM() == null) {
			LOGGER.severe("Category information is missing");
			return false;
		}

		if (sp.getSoLuongTonKho() < 0) {
			LOGGER.severe("Invalid stock quantity");
			return false;
		}

		return true;
	}

	private String handleImageUpdate(SanPham newProduct, SanPham existingProduct) {
		// If no new image is being set, retain the existing one
		if (newProduct.getHinhAnh() == null) {
			return existingProduct.getHinhAnh();
		}

		try {
			// Create target directory if it doesn't exist
			Files.createDirectories(Paths.get(IMAGE_DIRECTORY));

			// Only process if the image is actually changing
			if (!newProduct.getHinhAnh().equals(existingProduct.getHinhAnh())) {
				String selectedImagePath = newProduct.getHinhAnh();
				File sourceFile = new File(selectedImagePath);

				// Verify source file exists
				if (!sourceFile.exists()) {
					LOGGER.warning("Source image file not found: " + selectedImagePath);
					return existingProduct.getHinhAnh();
				}

				// Generate new filename using product ID and original extension
				String extension = getFileExtension(sourceFile.getName());
				String newFileName = newProduct.getMaSP() + extension;
				Path targetPath = Paths.get(IMAGE_DIRECTORY, newFileName);

				// Delete old image if it exists and isn't the default
				if (existingProduct.getHinhAnh() != null && !existingProduct.getHinhAnh().equals(DEFAULT_IMAGE)) {
					Path oldImagePath = Paths.get(IMAGE_DIRECTORY, existingProduct.getHinhAnh());
					Files.deleteIfExists(oldImagePath);
				}

				// Copy new image with overwrite if exists
				Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("Successfully updated image for product " + newProduct.getMaSP());

				return newFileName;
			}

			return existingProduct.getHinhAnh();

		} catch (Exception e) {
			LOGGER.severe("Error handling image update: " + e.getMessage());
			return existingProduct.getHinhAnh(); // Keep existing image on failure
		}
	}

	private String getFileExtension(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		return lastDot > 0 ? fileName.substring(lastDot) : "";
	}

	private void closeResources(Connection conn, Statement stmt, Statement stmt2, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (stmt2 != null)
				stmt2.close();
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.severe("Error closing resources: " + e.getMessage());
		}
	}

	// Helper method to determine product status based on stock
	private String determineTinhTrang(int soLuong) {
		if (soLuong <= 0)
			return "Chưa nhập về";
		if (soLuong <= 10)
			return "Sắp hết";
		return "Đang kinh doanh";
	}

	public String getLastProductID() {
		String sql = "SELECT productID FROM product ORDER BY CAST(SUBSTRING(productID, 3) AS UNSIGNED) DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getString("productID");
			}
			return "SP000";
		} catch (SQLException e) {
			LOGGER.severe("Lỗi lấy mã sản phẩm cuối: " + e.getMessage());
			return "SP000";
		}
	}

	public boolean deleteSanPham(String maSP) {
		Connection conn = null;
		CallableStatement cs = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			conn = myConnection.connect();
			conn.setAutoCommit(false);

			// Get current image name
			pst = conn.prepareStatement("SELECT imagePath FROM product WHERE productID = ?");
			pst.setString(1, maSP);
			rs = pst.executeQuery();

			String imageName = null;
			if (rs.next()) {
				imageName = rs.getString("imagePath");
			}

			// Close first statement
			if (pst != null) {
				pst.close();
				pst = null;
			}

			// Execute delete procedure
			cs = conn.prepareCall("CALL sp_DeleteProduct(?)");
			cs.setString(1, maSP);
			boolean deleted = cs.executeUpdate() > 0;

			if (deleted) {
				// Delete image file if it exists and isn't default
				if (imageName != null && !imageName.equals(DEFAULT_IMAGE)) {
					Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);
					if (Files.exists(imagePath)) {
						Files.delete(imagePath);
						LOGGER.info("Deleted image file: " + imagePath);
					}
				}

				conn.commit();
				LOGGER.info("Successfully deleted product and image: " + maSP);
				return true;
			} else {
				conn.rollback();
				LOGGER.warning("Failed to delete product: " + maSP);
				return false;
			}

		} catch (Exception e) {
			LOGGER.severe("Error during product deletion: " + e.getMessage());
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ex) {
				LOGGER.severe("Error during rollback: " + ex.getMessage());
			}
			return false;
		} finally {
			closeResources(conn, cs, pst, rs);
		}
	}


	private SanPham createSanPhamFromResultSet(ResultSet rs) throws SQLException {
		SanPham sp = new SanPham();
		sp.setMaSP(rs.getString("productID"));
		sp.setTenSP(rs.getString("name"));
		sp.setSoLuongTonKho(rs.getInt("stockQuantity"));
		sp.setThuongHieu(rs.getString("brand"));
		sp.setHinhAnh(rs.getString("imagePath"));

		DanhMuc dm = new DanhMuc();
		dm.setMaDM(rs.getString("category"));
		dm.setTenDM(rs.getString("category_name"));
		sp.setDanhmuc(dm);

		sp.setTinhTrang(getTinhTrangText(rs.getInt("stockQuantity")));

		return sp;
	}

	private String getTinhTrangText(int soLuong) {
		if (soLuong <= 0)
			return "Hết hàng";
		if (soLuong <= 10)
			return "Sắp hết";
		return "Còn hàng";
	}

	public String getLastProductId() {
		String sql = "SELECT productID FROM product ORDER BY productID DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getString("productID");
			}
			return "PRD000";

		} catch (SQLException e) {
			LOGGER.severe("Lỗi lấy mã sản phẩm cuối cùng: " + e.getMessage());
			return "PRD000";
		}
	}

	public List<SanPham> getSanPhamByCategory(String categoryId) {
		List<SanPham> products = new ArrayList<>();
		String sql = """
				SELECT p.*, c.name as category_name
				FROM product p
				LEFT JOIN category c ON p.category = c.categoryID
				WHERE p.category = ?
				""";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, categoryId);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				SanPham product = new SanPham();
				product.setMaSP(rs.getString("productID"));
				product.setTenSP(rs.getString("name"));
				product.setSoLuongTonKho(rs.getInt("stockQuantity"));
				product.setThuongHieu(rs.getString("brand"));
				product.setHinhAnh(rs.getString("imagePath"));

				// Create and set DanhMuc
				DanhMuc dm = new DanhMuc();
				dm.setMaDM(rs.getString("category"));
				dm.setTenDM(rs.getString("category_name"));
				product.setDanhmuc(dm);
				String dbImagePath = rs.getString("imagePath");
				if (dbImagePath != null && !dbImagePath.trim().isEmpty()) {
					// Ensure the path uses correct separators and format
					String formattedPath = "/images/" + dbImagePath.replace("\\", "/");
					product.setHinhAnh(formattedPath);
				} else {
					// Set default image path if no image in database
					product.setHinhAnh("/images/default-product.png");
				}

				products.add(product);

			}

			LOGGER.info("Retrieved " + products.size() + " products for category: " + categoryId);

		} catch (SQLException e) {
			LOGGER.severe("Error retrieving products for category " + categoryId + ": " + e.getMessage());
		}

		return products;
	}

	public List<SanPham> searchProductsByName(String keyword) {
		List<SanPham> searchResults = new ArrayList<>();

		// Enhanced SQL query with JOIN to get category information
		String sql = """
				SELECT p.*, c.name as category_name
				FROM product p
				LEFT JOIN category c ON p.category = c.categoryID
				WHERE LOWER(p.name) LIKE LOWER(?)
				""";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			String searchPattern = "%" + keyword.trim() + "%";
			pst.setString(1, searchPattern);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				// Using the existing helper method for consistent object creation
				SanPham product = createSanPhamFromResultSet(rs);
				searchResults.add(product);
			}

			LOGGER.info("Found " + searchResults.size() + " products matching: " + keyword);

		} catch (SQLException e) {
			LOGGER.severe("Error searching products with keyword '" + keyword + "': " + e.getMessage());
		} finally {
			myConnection.closeConnection();
		}

		return searchResults;
	}
	public ArrayList<String> getProductColors(String productId) {
        ArrayList<String> colors = new ArrayList<>();
        String sql = "SELECT DISTINCT color FROM productvariant WHERE productID = ? ORDER BY color";
        
        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, productId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String color = rs.getString("color");
                if (color != null && !color.isEmpty()) {
                    colors.add(color);
                }
            }
            
            LOGGER.info("Loaded " + colors.size() + " colors for product " + productId);
            
        } catch (SQLException e) {
            LOGGER.severe("Error loading product colors: " + e.getMessage());
        }
        
        return colors;
    }
    
    // Get unique sizes for a product and color combination
    public ArrayList<String> getProductSizes(String productId, String color) {
        ArrayList<String> sizes = new ArrayList<>();
        String sql = "SELECT DISTINCT size FROM productvariant WHERE productID = ? AND color = ? ORDER BY size";
        
        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, productId);
            pst.setString(2, color);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String size = rs.getString("size");
                if (size != null && !size.isEmpty()) {
                    sizes.add(size);
                }
            }
            
            LOGGER.info("Loaded " + sizes.size() + " sizes for product " + productId + " color " + color);
            
        } catch (SQLException e) {
            LOGGER.severe("Error loading product sizes: " + e.getMessage());
        }
        
        return sizes;
    }
    
    // Get specific variant details
    public ProductVariant getVariant(String productId, String color, String size) {
        String sql = "SELECT * FROM productvariant WHERE productID = ? AND color = ? AND size = ?";
        
        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, productId);
            pst.setString(2, color);
            pst.setString(3, size);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                ProductVariant variant = new ProductVariant();
                variant.setVariantID(rs.getString("variantID"));
                variant.setColor(color);
                variant.setSize(size);
                variant.setQuantity(rs.getInt("quantity"));
                return variant;
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Error loading variant details: " + e.getMessage());
        }
        
        return null;
    }
    public void updateStockQuantity(String productID) {
        String sql = """
            UPDATE product p 
            SET stockQuantity = (
                SELECT COALESCE(SUM(quantity), 0) 
                FROM productvariant 
                WHERE productID = ?
            )
            WHERE p.productID = ?
        """;

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, productID);
            pst.setString(2, productID);
            pst.executeUpdate();
            
            LOGGER.info("Updated stock quantity for product: " + productID);
            
        } catch (SQLException e) {
            LOGGER.severe("Error updating stock quantity: " + e.getMessage());
        }
    }

    // Sửa lại phương thức getAllSanPham() để lấy tổng số lượng từ variants
    public ArrayList<SanPham> getAllSanPham() {
        ArrayList<SanPham> dsSanPham = new ArrayList<>();
        String sql = """
            SELECT p.*, c.name as category_name,
                   COALESCE((SELECT SUM(quantity) 
                            FROM productvariant 
                            WHERE productID = p.productID), 0) as total_quantity
            FROM product p
            LEFT JOIN category c ON p.category = c.categoryID
            ORDER BY p.productID
            """;

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                SanPham sp = new SanPham();

                // Set thông tin cơ bản
                sp.setMaSP(rs.getString("productID"));
                sp.setTenSP(rs.getString("name"));
                sp.setSoLuongTonKho(rs.getInt("total_quantity")); // Lấy tổng từ variants
                sp.setThuongHieu(rs.getString("brand"));
                sp.setHinhAnh(rs.getString("imagePath"));
                sp.setTinhTrang(rs.getString("status"));

                // Set danh mục
                DanhMuc dm = new DanhMuc();
                dm.setMaDM(rs.getString("category"));
                dm.setTenDM(rs.getString("category_name"));
                sp.setDanhmuc(dm);

                dsSanPham.add(sp);
            }
            LOGGER.info("Đã lấy " + dsSanPham.size() + " sản phẩm từ database");
            return dsSanPham;

        } catch (SQLException e) {
            LOGGER.severe("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Sửa lại phương thức getSanPhamByID để lấy tổng số lượng từ variants
    public SanPham getSanPhamByID(String productID) {
        SanPham product = null;
        String sql = """
            SELECT p.*, c.name as category_name,
                   COALESCE((SELECT SUM(quantity) 
                            FROM productvariant 
                            WHERE productID = p.productID), 0) as total_quantity
            FROM product p
            LEFT JOIN category c ON p.category = c.categoryID
            WHERE p.productID = ?
            """;

        try (Connection conn = myConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, productID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new SanPham();
                product.setMaSP(rs.getString("productID"));
                product.setTenSP(rs.getString("name"));
                
                DanhMuc danhMuc = new DanhMuc();
                danhMuc.setMaDM(rs.getString("category"));
                danhMuc.setTenDM(rs.getString("category_name"));
                product.setDanhmuc(danhMuc);
                
                product.setSoLuongTonKho(rs.getInt("total_quantity")); // Lấy tổng từ variants
                product.setThuongHieu(rs.getString("brand"));
                product.setTinhTrang(rs.getString("status"));
                product.setHinhAnh(rs.getString("imagePath"));
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
        }
        return product;
    }
}