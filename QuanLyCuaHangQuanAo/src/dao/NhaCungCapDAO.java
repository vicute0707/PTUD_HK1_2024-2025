package dao;

import entity.NhaCC;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {
	private MyConnection myConnection;

	public NhaCungCapDAO() {
		myConnection = new MyConnection();
	}

	/**
	 * Lấy tất cả nhà cung cấp từ database
	 */
	public List<NhaCC> getAllNhaCC() {
		List<NhaCC> danhSachNCC = new ArrayList<>();
		String sql = "SELECT * FROM supplier";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				NhaCC ncc = createNhaCCFromResultSet(rs);
				danhSachNCC.add(ncc);
			}
			return danhSachNCC;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	/**
	 * Thêm nhà cung cấp mới sử dụng stored procedure
	 */
	public boolean addNhaCC(NhaCC ncc) {
		String sql = "CALL sp_AddSupplier(?, ?, ?, ?, ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, ncc.getMaNCC());
			pst.setString(2, ncc.getTenNCC());
			pst.setString(3, ncc.getDiaChi());
			pst.setString(4, ncc.getEmail());
			pst.setString(5, ncc.getSdt());

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	/**
	 * Cập nhật thông tin nhà cung cấp
	 */
	public boolean updateNhaCC(NhaCC ncc) {
		String sql = "CALL sp_UpdateSupplier(?, ?, ?, ?, ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, ncc.getMaNCC());
			pst.setString(2, ncc.getTenNCC());
			pst.setString(3, ncc.getDiaChi());
			pst.setString(4, ncc.getEmail());
			pst.setString(5, ncc.getSdt());

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	/**
	 * Xóa nhà cung cấp
	 */
	public boolean deleteNhaCC(String maNCC) {
		String sql = "CALL sp_DeleteSupplier(?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, maNCC);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	/**
	 * Tìm kiếm nhà cung cấp theo nhiều tiêu chí
	 */
	public List<NhaCC> searchNhaCC(String keyword, String searchType) {
		List<NhaCC> ketQua = new ArrayList<>();
		String sql = "";

		// Xác định câu truy vấn dựa trên loại tìm kiếm
		switch (searchType) {
		case "Theo Mã":
			sql = "SELECT * FROM supplier WHERE supplierID LIKE ?";
			break;
		case "Theo Tên":
			sql = "SELECT * FROM supplier WHERE name LIKE ?";
			break;
		case "Theo SĐT":
			sql = "SELECT * FROM supplier WHERE phone LIKE ?";
			break;
		case "Theo Địa Chỉ":
			sql = "SELECT * FROM supplier WHERE address LIKE ?";
			break;
		case "Theo Email":
			sql = "SELECT * FROM supplier WHERE email LIKE ?";
			break;
		default:
			sql = "SELECT * FROM supplier WHERE supplierID LIKE ? OR name LIKE ? OR phone LIKE ? OR address LIKE ? OR email LIKE ?";
		}

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			String searchPattern = "%" + keyword + "%";
			if (searchType.equals("Tất cả")) {
				for (int i = 1; i <= 5; i++) {
					pst.setString(i, searchPattern);
				}
			} else {
				pst.setString(1, searchPattern);
			}

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				NhaCC ncc = createNhaCCFromResultSet(rs);
				ketQua.add(ncc);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.closeConnection();
		}
		return ketQua;
	}

	/**
	 * Lấy mã nhà cung cấp cuối cùng để tạo mã mới
	 */
	public String getLastSupplierID() {
		String sql = "SELECT supplierID FROM supplier ORDER BY CAST(SUBSTRING(supplierID, 4) AS UNSIGNED) DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getString("supplierID");
			}
			return "SUP000"; // Giá trị mặc định

		} catch (SQLException e) {
			e.printStackTrace();
			return "SUP000";
		} finally {
			myConnection.closeConnection();
		}
	}

	private NhaCC createNhaCCFromResultSet(ResultSet rs) throws SQLException {
		return new NhaCC(rs.getString("supplierID"), rs.getString("name"), rs.getString("address"),
				rs.getString("email"), rs.getString("phone"));
	}
}