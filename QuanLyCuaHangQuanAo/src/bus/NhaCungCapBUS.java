package bus;

import dao.NhaCungCapDAO;
import entity.NhaCC;
import java.util.List;
import java.util.regex.Pattern;

public class NhaCungCapBUS {
	private NhaCungCapDAO nhaCungCapDAO;

	public NhaCungCapBUS() {
		nhaCungCapDAO = new NhaCungCapDAO();
	}

	/**
	 * Lấy danh sách tất cả nhà cung cấp
	 */
	public List<NhaCC> getAllNhaCC() {
		return nhaCungCapDAO.getAllNhaCC();
	}

	/**
	 * Thêm nhà cung cấp mới với validation
	 */
	public boolean addNhaCC(NhaCC ncc) {
		// Validate dữ liệu trước khi thêm
		if (!validateNhaCC(ncc)) {
			return false;
		}
		return nhaCungCapDAO.addNhaCC(ncc);
	}

	/**
	 * Cập nhật thông tin nhà cung cấp với validation
	 */
	public boolean updateNhaCC(NhaCC ncc) {
		if (!validateNhaCC(ncc)) {
			return false;
		}
		return nhaCungCapDAO.updateNhaCC(ncc);
	}

	/**
	 * Xóa nhà cung cấp
	 */
	public boolean deleteNhaCC(String maNCC) {
		if (maNCC == null || maNCC.trim().isEmpty()) {
			return false;
		}
		return nhaCungCapDAO.deleteNhaCC(maNCC);
	}

	/**
	 * Tìm kiếm nhà cung cấp
	 */
	public List<NhaCC> searchNhaCC(String keyword, String searchType) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAllNhaCC();
		}
		return nhaCungCapDAO.searchNhaCC(keyword, searchType);
	}

	/**
	 * Tạo mã nhà cung cấp mới
	 */
	public String generateNewSupplierID() {
		String lastID = nhaCungCapDAO.getLastSupplierID();
		int number = Integer.parseInt(lastID.substring(3)) + 1;
		return String.format("SUP%03d", number);
	}

	/**
	 * Validate thông tin nhà cung cấp
	 */
	private boolean validateNhaCC(NhaCC ncc) {
		// Validate tên
		if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty()) {
			throw new IllegalArgumentException("Tên nhà cung cấp không được để trống");
		}

		// Validate email format
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		if (!Pattern.matches(emailRegex, ncc.getEmail())) {
			throw new IllegalArgumentException("Email không hợp lệ");
		}

		// Validate số điện thoại
		String phoneRegex = "\\d{10}";
		if (!Pattern.matches(phoneRegex, ncc.getSdt())) {
			throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải có 10 chữ số)");
		}

		// Validate địa chỉ
		if (ncc.getDiaChi() == null || ncc.getDiaChi().trim().isEmpty()) {
			throw new IllegalArgumentException("Địa chỉ không được để trống");
		}

		return true;
	}
}