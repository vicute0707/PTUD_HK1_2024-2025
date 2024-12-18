package bus;

import dao.DanhMucDAO;
import entity.DanhMuc;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DanhMucBUS {
	private DanhMucDAO danhMucDAO;
	private static final Logger LOGGER = Logger.getLogger(DanhMucBUS.class.getName());
	private List<DanhMuc> danhSachDanhMuc;

	public DanhMucBUS() {
		danhMucDAO = new DanhMucDAO();
		danhSachDanhMuc = new ArrayList<>();
		loadDanhMuc();
	}

	private void loadDanhMuc() {
		danhSachDanhMuc = danhMucDAO.getAllDanhMuc();
	}

	public void hienThiDanhMuc(DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for (DanhMuc dm : danhSachDanhMuc) {
			Object[] row = { dm.getMaDM(), dm.getTenDM(), dm.getGhiChu() };
			tableModel.addRow(row);
		}
	}

	public boolean themDanhMuc(String tenDM, String ghiChu) {
		try {
			// Validate input
			if (tenDM == null || tenDM.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Tên danh mục không được để trống!");
				return false;
			}

			// Check if category name already exists
			if (danhMucDAO.isTenDanhMucExists(tenDM)) {
				JOptionPane.showMessageDialog(null, "Tên danh mục đã tồn tại!");
				return false;
			}

			// Generate new category ID
			String lastID = danhMucDAO.getLastDanhMucID();
			int nextNumber = Integer.parseInt(lastID.substring(3)) + 1;
			String maDM = String.format("CAT%03d", nextNumber);

			// Create new category
			DanhMuc danhMuc = new DanhMuc(maDM, tenDM, ghiChu);

			// Add to database
			boolean result = danhMucDAO.addDanhMuc(danhMuc);
			if (result) {
				danhSachDanhMuc.add(danhMuc);
				LOGGER.info("Thêm danh mục thành công: " + maDM);
				return true;
			}
			return false;

		} catch (Exception e) {
			LOGGER.severe("Lỗi thêm danh mục: " + e.getMessage());
			return false;
		}
	}

	public boolean suaDanhMuc(String maDM, String tenDM, String ghiChu) {
		try {
			// Validate input
			if (tenDM == null || tenDM.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Tên danh mục không được để trống!");
				return false;
			}

			// Check if category exists
			DanhMuc existing = danhMucDAO.getDanhMucById(maDM);
			if (existing == null) {
				JOptionPane.showMessageDialog(null, "Không tìm thấy danh mục!");
				return false;
			}

			// Update category
			DanhMuc danhMuc = new DanhMuc(maDM, tenDM, ghiChu);
			boolean result = danhMucDAO.updateDanhMuc(danhMuc);

			if (result) {
				// Update local list
				for (int i = 0; i < danhSachDanhMuc.size(); i++) {
					if (danhSachDanhMuc.get(i).getMaDM().equals(maDM)) {
						danhSachDanhMuc.set(i, danhMuc);
						break;
					}
				}
				LOGGER.info("Cập nhật danh mục thành công: " + maDM);
				return true;
			}
			return false;

		} catch (Exception e) {
			LOGGER.severe("Lỗi cập nhật danh mục: " + e.getMessage());
			return false;
		}
	}

	public boolean xoaDanhMuc(String maDM) {
		try {
			// Check if category is in use
			if (danhMucDAO.isDanhMucInUse(maDM)) {
				JOptionPane.showMessageDialog(null, "Không thể xóa danh mục này vì đang có sản phẩm thuộc danh mục!",
						"Lỗi", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			// Delete category
			boolean result = danhMucDAO.deleteDanhMuc(maDM);
			if (result) {
				// Remove from local list
				danhSachDanhMuc.removeIf(dm -> dm.getMaDM().equals(maDM));
				LOGGER.info("Xóa danh mục thành công: " + maDM);
				return true;
			}
			return false;

		} catch (Exception e) {
			LOGGER.severe("Lỗi xóa danh mục: " + e.getMessage());
			return false;
		}
	}

	public List<DanhMuc> timKiemDanhMuc(String keyword) {
		try {
			if (keyword == null || keyword.trim().isEmpty()) {
				return new ArrayList<>(danhSachDanhMuc);
			}

			List<DanhMuc> ketQua = danhMucDAO.searchDanhMuc(keyword);
			LOGGER.info("Tìm thấy " + ketQua.size() + " danh mục với từ khóa: " + keyword);
			return ketQua;

		} catch (Exception e) {
			LOGGER.severe("Lỗi tìm kiếm danh mục: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public void refreshData() {
		loadDanhMuc();
	}

	public List<DanhMuc> getDanhSachDanhMuc() {
		return danhSachDanhMuc;
	}

	public DanhMuc getDanhMucById(String maDM) {
		return danhMucDAO.getDanhMucById(maDM);
	}
	public DanhMuc getDanhMucByName(String tenDM) {
	    return danhMucDAO.getDanhMucByName(tenDM);
	}
}