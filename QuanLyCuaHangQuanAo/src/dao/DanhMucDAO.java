package dao;

import entity.DanhMuc;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DanhMucDAO {
    private MyConnection myConnection;

    public DanhMucDAO() {
        myConnection = new MyConnection();
    }

    /**
     * Lấy tất cả danh mục từ cơ sở dữ liệu
     * @return Danh sách các danh mục hoặc danh sách rỗng nếu có lỗi
     */
    public List<DanhMuc> getAllDanhMuc() {
        List<DanhMuc> danhSachDanhMuc = new ArrayList<>();
        String sql = "SELECT * FROM category";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                DanhMuc danhMuc = createDanhMucFromResultSet(rs);
                danhSachDanhMuc.add(danhMuc);
            }
            return danhSachDanhMuc;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Tìm danh mục theo mã
     * @param maDM Mã danh mục cần tìm
     * @return DanhMuc nếu tìm thấy, null nếu không tồn tại hoặc có lỗi
     */
    public DanhMuc getDanhMucById(String maDM) {
        String sql = "SELECT * FROM category WHERE categoryID = ?";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maDM);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return createDanhMucFromResultSet(rs);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Thêm danh mục mới sử dụng stored procedure
     * @param danhMuc Đối tượng DanhMuc cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addDanhMuc(DanhMuc danhMuc) {
        String sql = "CALL sp_AddCategory(?, ?, ?)";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, danhMuc.getMaDM());
            pst.setString(2, danhMuc.getTenDM());
            pst.setString(3, danhMuc.getGhiChu());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Cập nhật thông tin danh mục sử dụng stored procedure
     * @param danhMuc Đối tượng DanhMuc cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateDanhMuc(DanhMuc danhMuc) {
        String sql = "CALL sp_UpdateCategory(?, ?, ?)";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, danhMuc.getMaDM());
            pst.setString(2, danhMuc.getTenDM());
            pst.setString(3, danhMuc.getGhiChu());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Xóa danh mục sử dụng stored procedure
     * @param maDM Mã danh mục cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteDanhMuc(String maDM) {
        String sql = "CALL sp_DeleteCategory(?)";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maDM);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Tìm kiếm danh mục theo tên
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách các danh mục phù hợp hoặc danh sách rỗng nếu không tìm thấy
     */
    public List<DanhMuc> searchDanhMuc(String keyword) {
        List<DanhMuc> ketQua = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE name LIKE ? OR description LIKE ?";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                DanhMuc danhMuc = createDanhMucFromResultSet(rs);
                ketQua.add(danhMuc);
            }
            return ketQua;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Lấy mã danh mục cuối cùng để tạo mã mới
     * @return Mã danh mục cuối cùng hoặc giá trị mặc định nếu không có dữ liệu
     */
    public String getLastDanhMucID() {
        String sql = "SELECT categoryID FROM category ORDER BY CAST(SUBSTRING(categoryID, 4) AS UNSIGNED) DESC LIMIT 1";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getString("categoryID");
            }
            return "CAT000"; // Giá trị mặc định

        } catch (SQLException e) {
            e.printStackTrace();
            return "CAT000";
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Kiểm tra xem danh mục có đang được sử dụng không
     * @param maDM Mã danh mục cần kiểm tra
     * @return true nếu đang được sử dụng, false nếu không
     */
    public boolean isDanhMucInUse(String maDM) {
        String sql = "SELECT COUNT(*) FROM product WHERE category = ?";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maDM);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Lấy tên danh mục theo mã
     * @param maDM Mã danh mục
     * @return Tên danh mục hoặc null nếu không tìm thấy
     */
    public String getTenDanhMucById(String maDM) {
        String sql = "SELECT name FROM category WHERE categoryID = ?";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maDM);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            myConnection.closeConnection();
        }
    }

    /**
     * Helper method để tạo đối tượng DanhMuc từ ResultSet
     * @param rs ResultSet chứa dữ liệu danh mục
     * @return Đối tượng DanhMuc
     * @throws SQLException nếu có lỗi khi đọc dữ liệu
     */
    private DanhMuc createDanhMucFromResultSet(ResultSet rs) throws SQLException {
        DanhMuc danhMuc = new DanhMuc();
        danhMuc.setMaDM(rs.getString("categoryID"));
        danhMuc.setTenDM(rs.getString("name"));
        danhMuc.setGhiChu(rs.getString("description"));
        return danhMuc;
    }

    /**
     * Kiểm tra xem tên danh mục đã tồn tại chưa
     * @param tenDM Tên danh mục cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean isTenDanhMucExists(String tenDM) {
        String sql = "SELECT COUNT(*) FROM category WHERE name = ?";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, tenDM);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            myConnection.closeConnection();
        }
    }
    public DanhMuc getDanhMucByName(String tenDM) {
        String sql = "SELECT * FROM category WHERE name = ?";

        try (Connection conn = myConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, tenDM);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return createDanhMucFromResultSet(rs);
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            myConnection.closeConnection();
        }
    }

}