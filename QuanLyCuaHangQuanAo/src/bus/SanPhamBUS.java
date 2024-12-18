package bus;

import dao.SanPhamDAO;
import entity.SanPham;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SanPhamBUS {
	private static final Logger LOGGER = Logger.getLogger(SanPhamBUS.class.getName());
	private SanPhamDAO sanPhamDAO;
	private ArrayList<SanPham> dsSanPham;

	public SanPhamBUS() {

		sanPhamDAO = new SanPhamDAO();
		dsSanPham = new ArrayList<>();
	}

	public ArrayList<SanPham> getAllSanPham() {
		ArrayList<SanPham> dsSanPham = sanPhamDAO.getAllSanPham();
		// Validate danh sách trước khi trả về
		if (dsSanPham == null) {
			LOGGER.warning("Không thể lấy danh sách sản phẩm từ DAO");
			return new ArrayList<>(); // Trả về list rỗng thay vì null
		}

		LOGGER.info("Đã lấy " + dsSanPham.size() + " sản phẩm từ database");
		return dsSanPham;
	}

	public SanPham getSanPhamById(String maSP) throws Exception {
		if (maSP == null || maSP.trim().isEmpty()) {
			throw new IllegalArgumentException("Product ID cannot be null or empty");
		}

		SanPham sanPham = sanPhamDAO.getSanPhamByID(maSP);
		if (sanPham == null) {
			throw new Exception("Product not found with ID: " + maSP);
		}

		return sanPham;
	}

	public ArrayList<SanPham> searchSanPham(String keyword, String type) {
		try {
			ArrayList<SanPham> allProducts = getAllSanPham();
			ArrayList<SanPham> searchResults = new ArrayList<>();

			keyword = keyword.toLowerCase().trim();

			for (SanPham sp : allProducts) {
				boolean matches = false;
				switch (type) {
				case "Theo Mã":
					matches = sp.getMaSP().toLowerCase().contains(keyword);
					break;
				case "Tên SP":
					matches = sp.getTenSP().toLowerCase().contains(keyword);
					break;
				case "Tên DM":
					matches = sp.getDanhmuc().getTenDM().toLowerCase().contains(keyword);
					break;
				case "Thương Hiệu":
					matches = sp.getThuongHieu().toLowerCase().contains(keyword);
					break;
				}
				if (matches) {
					searchResults.add(sp);
				}
			}

			LOGGER.info("Tìm thấy " + searchResults.size() + " kết quả cho từ khóa: " + keyword);
			return searchResults;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm sản phẩm", e);
			return new ArrayList<>();
		}
	}

	public boolean addSanPham(SanPham sp) {
		try {
			validateSanPham(sp);
			boolean result = sanPhamDAO.addSanPham(sp);
			if (result) {
				LOGGER.info("Thêm sản phẩm thành công: " + sp.getMaSP());
			} else {
				LOGGER.warning("Không thể thêm sản phẩm: " + sp.getMaSP());
			}
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Lỗi khi thêm sản phẩm", e);
			return false;
		}
	}

	public List<SanPham> getSanPhamByCategory(String categoryId) {
		// Get products from database through DAO
		return sanPhamDAO.getSanPhamByCategory(categoryId);
	}

	public boolean updateSanPham(SanPham sp) {
		try {
			validateSanPham(sp);
			boolean result = sanPhamDAO.updateSanPham(sp);
			if (result) {
				LOGGER.info("Cập nhật sản phẩm thành công: " + sp.getMaSP());
			} else {
				LOGGER.warning("Không thể cập nhật sản phẩm: " + sp.getMaSP());
			}
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật sản phẩm", e);
			return false;
		}
	}

	public boolean deleteSanPham(String maSP) {
		try {
			if (maSP == null || maSP.trim().isEmpty()) {
				throw new IllegalArgumentException("Mã sản phẩm không được để trống");
			}
			boolean result = sanPhamDAO.deleteSanPham(maSP);
			if (result) {
				LOGGER.info("Xóa sản phẩm thành công: " + maSP);
			} else {
				LOGGER.warning("Không thể xóa sản phẩm: " + maSP);
			}
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Lỗi khi xóa sản phẩm", e);
			return false;
		}
	}

	private void validateSanPham(SanPham sp) throws IllegalArgumentException {
		if (sp == null) {
			throw new IllegalArgumentException("Sản phẩm không được null");
		}
		if (sp.getMaSP() == null || sp.getMaSP().trim().isEmpty()) {
			throw new IllegalArgumentException("Mã sản phẩm không được để trống");
		}
		if (sp.getTenSP() == null || sp.getTenSP().trim().isEmpty()) {
			throw new IllegalArgumentException("Tên sản phẩm không được để trống");
		}
		
		if (sp.getSoLuongTonKho() < 0) {
			throw new IllegalArgumentException("Số lượng tồn không được âm");
		}
	}

	public String generateNewProductId() {
		// Lấy mã sản phẩm cuối cùng từ database
		String lastId = sanPhamDAO.getLastProductId();

		if (lastId == null || lastId.equals("PRD000")) {
			return "PRD001";
		}

		// Tách lấy phần số và tăng lên 1
		int currentNum = Integer.parseInt(lastId.substring(3));
		return String.format("PRD%03d", currentNum + 1);
	}

	// Tìm kiếm theo mã sản phẩm
	public ArrayList<SanPham> timKiemTheoMa(String ma) {
		ArrayList<SanPham> ketQua = new ArrayList<>();
		ma = ma.toLowerCase();

		for (SanPham sp : dsSanPham) {
			if (sp.getMaSP().toLowerCase().contains(ma)) {
				ketQua.add(sp);
			}
		}
		return ketQua;
	}

	// Tìm kiếm theo tên sản phẩm
	public ArrayList<SanPham> timKiemTheoTen(String ten) {
		ArrayList<SanPham> ketQua = new ArrayList<>();
		ten = ten.toLowerCase();

		for (SanPham sp : dsSanPham) {
			if (sp.getTenSP().toLowerCase().contains(ten)) {
				ketQua.add(sp);
			}
		}
		return ketQua;
	}

	// Tìm kiếm theo danh mục
	public ArrayList<SanPham> timKiemTheoDanhMuc(String tenDM) {
		ArrayList<SanPham> ketQua = new ArrayList<>();
		tenDM = tenDM.toLowerCase();

		for (SanPham sp : dsSanPham) {
			if (sp.getDanhmuc().getTenDM().toLowerCase().contains(tenDM)) {
				ketQua.add(sp);
			}
		}
		return ketQua;
	}

	// Tìm kiếm theo thương hiệu
	public ArrayList<SanPham> timKiemTheoThuongHieu(String thuongHieu) {
		ArrayList<SanPham> ketQua = new ArrayList<>();
		thuongHieu = thuongHieu.toLowerCase();

		for (SanPham sp : dsSanPham) {
			if (sp.getThuongHieu().toLowerCase().contains(thuongHieu)) {
				ketQua.add(sp);
			}
		}
		return ketQua;
	}

	public String getLastProductId() {
		SanPhamDAO dao = new SanPhamDAO();
		String lastId = dao.getLastProductId();

		if (lastId == null || lastId.isEmpty()) {
			return "PRD000";
		}

		try {
			int number = Integer.parseInt(lastId.substring(3));
			return String.format("PRD%03d", number);
		} catch (NumberFormatException e) {
			LOGGER.severe("Error parsing product ID: " + e.getMessage());
			return "PRD000";
		}
	}

	public List<SanPham> searchProductsByName(String keyword) {
		// Input validation with logging
		if (keyword == null || keyword.trim().isEmpty()) {
			LOGGER.warning("Search attempted with empty keyword");
			return new ArrayList<>();
		}

		try {
			// Ensure we have the latest data
			refreshDanhSachSanPham();

			// Create a new list for search results
			ArrayList<SanPham> searchResults = new ArrayList<>();

			// Convert keyword to lowercase for case-insensitive search
			String searchKeyword = keyword.toLowerCase().trim();

			// Search through the in-memory product list
			for (SanPham product : dsSanPham) {
				if (product.getTenSP() != null && product.getTenSP().toLowerCase().contains(searchKeyword)) {
					searchResults.add(product);
				}
			}

			// Log search results
			LOGGER.info(String.format("Found %d products matching keyword '%s'", searchResults.size(), keyword));

			return searchResults;

		} catch (Exception e) {
			LOGGER.severe("Error during product search: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// Helper method to refresh product list
	private void refreshDanhSachSanPham() {
		dsSanPham = sanPhamDAO.getAllSanPham();
	}

	// Helper method to validate product data
	private boolean isValidProduct(SanPham product) {
		return product != null && product.getMaSP() != null && !product.getMaSP().trim().isEmpty()
				&& product.getTenSP() != null && !product.getTenSP().trim().isEmpty()
				&& product.getSoLuongTonKho() >= 0;
	}
}