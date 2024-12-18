package dao;

import entity.UserRole;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRoleDAO {
	private MyConnection myConnection;
    private static final Logger LOGGER = Logger.getLogger(UserRoleDAO.class.getName());


	public UserRoleDAO() {
		myConnection = new MyConnection();
	}


	public UserRole getRoleById(String roleId) {
	    String sql = "SELECT * FROM userrole WHERE roleID = ?";
	    
	    try (Connection conn = myConnection.connect();
	         PreparedStatement pst = conn.prepareStatement(sql)) {
	        
	        pst.setString(1, roleId);
	        ResultSet rs = pst.executeQuery();

	        if (rs.next()) {
	            UserRole role = new UserRole();
	            role.setRoleID(rs.getString("roleID"));
	            role.setRoleName(rs.getString("roleName"));
	            role.setPermissions(rs.getString("permissions")); // Get permissions string

	            LOGGER.info("Found role: " + role.getRoleID() + 
	                       ", Name: " + role.getRoleName() + 
	                       ", Permissions: " + role.getPermissions());
	            return role;
	        }
	        
	        LOGGER.warning("No role found for ID: " + roleId);
	        return null;

	    } catch (SQLException e) {
	        LOGGER.severe("Database error: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}

	public boolean updateRolePermissions(String roleID, String permissions) {
		String sql = "UPDATE userrole SET permissions = ? WHERE roleID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, permissions);
			pst.setString(2, roleID);

			// Ghi log thay đổi quyền
			boolean success = pst.executeUpdate() > 0;
			if (success) {
				ghiLogThayDoiQuyen(conn, roleID, permissions);
			}
			return success;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	private void ghiLogThayDoiQuyen(Connection conn, String roleID, String permissions) throws SQLException {
		String sql = "INSERT INTO permission_change_log (roleID, change_time, new_permissions) VALUES (?, NOW(), ?)";
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setString(1, roleID);
			pst.setString(2, permissions);
			pst.executeUpdate();
		}
	}

	public UserRole getCurrentUserRole(String username) {
		String sql = """
				SELECT r.*
				FROM userrole r
				JOIN user u ON u.role = r.roleID
				WHERE u.username = ? AND u.status = 'active'
				""";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				UserRole role = new UserRole();
				role.setRoleID(rs.getString("roleID"));
				role.setRoleName(rs.getString("roleName"));
				role.setPermissions(rs.getString("permissions")); // Tự động parse permissions string
				return role;
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			myConnection.closeConnection();
		}
	}

	public static final String ADMIN_ROLE_ID = "ROLE001";
	public static final String MANAGER_ROLE_ID = "ROLE002";
	public static final String STAFF_ROLE_ID = "ROLE003";

	// Method kiểm tra quyền dựa trên roleID
	public boolean hasPermission(String roleID, String permissionName) {
		// Nếu là Admin role, luôn có tất cả quyền
		if (ADMIN_ROLE_ID.equals(roleID)) {
			return true;
		}

		String sql = "SELECT permissions FROM userrole WHERE roleID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, roleID);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				String permissions = rs.getString("permissions");
				if (permissions != null && !permissions.isEmpty()) {
					String[] permissionArray = permissions.split(",");
					for (String permission : permissionArray) {
						if (permission.trim().equals(permissionName)) {
							return true;
						}
					}
				}
			}
			return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	// Method hỗ trợ kiểm tra roleID có phải admin không
	public boolean isAdminRole(String roleID) {
		return ADMIN_ROLE_ID.equals(roleID);
	}

	// Method lấy roleID từ username
	public String getRoleIDByUsername(String username) {
		String sql = "SELECT role FROM user WHERE username = ? AND status = 'active'";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getString("role");
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			myConnection.closeConnection();
		}
	}

	// Method kiểm tra nhiều quyền cùng lúc
	public boolean hasAllPermissions(String roleID, String... permissions) {
		// Nếu là Admin role, luôn trả về true
		if (ADMIN_ROLE_ID.equals(roleID)) {
			return true;
		}

		for (String permission : permissions) {
			if (!hasPermission(roleID, permission)) {
				return false;
			}
		}
		return true;
	}

	// Method kiểm tra có ít nhất một trong các quyền
	public boolean hasAnyPermission(String roleID, String... permissions) {
		// Nếu là Admin role, luôn trả về true
		if (ADMIN_ROLE_ID.equals(roleID)) {
			return true;
		}

		for (String permission : permissions) {
			if (hasPermission(roleID, permission)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkPermission(String userID, String permissionID) {
		String sql = "SELECT COUNT(*) FROM user_permission WHERE userID = ? AND permissionID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, userID);
			pst.setString(2, permissionID);

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

	public boolean assignPermission(String userID, String permissionID) {
		String sql = "INSERT INTO user_permission (userID, permissionID) VALUES (?, ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, userID);
			pst.setString(2, permissionID);

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean removePermission(String userID, String permissionID) {
		String sql = "DELETE FROM user_permission WHERE userID = ? AND permissionID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, userID);
			pst.setString(2, permissionID);

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public List<String> getPermissionsByUser(String userID) {
		List<String> permissions = new ArrayList<>();
		String sql = "SELECT permissionID FROM user_permission WHERE userID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, userID);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				permissions.add(rs.getString("permissionID"));
			}
			return permissions;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public List<UserRole> getAllRoles() {
		List<UserRole> roles = new ArrayList<>();
		String sql = "SELECT r.*, GROUP_CONCAT(p.name) as permissionNames " + "FROM userrole r "
				+ "LEFT JOIN permission p ON FIND_IN_SET(p.permissionID, REPLACE(REPLACE(r.permissions, '[', ''), ']', '')) "
				+ "GROUP BY r.roleID";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				UserRole role = new UserRole();
				role.setRoleID(rs.getString("roleID"));
				role.setRoleName(rs.getString("roleName"));
				role.setPermissions(rs.getString("permissions"));
				roles.add(role);
			}
			return roles;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public String getLastRoleId() {
		String sql = "SELECT roleID FROM userrole ORDER BY roleID DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getString("roleID");
			}
			return "ROLE000";

		} catch (SQLException e) {
			e.printStackTrace();
			return "ROLE000";
		} finally {
			myConnection.closeConnection();
		}
	}

	public String getPermissionNames(String permissions) {
		String sql = "SELECT GROUP_CONCAT(name) as names FROM permission WHERE permissionID IN ("
				+ permissions.replace("[", "").replace("]", "").replace("\"", "'") + ")";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getString("names");
			}
			return "";
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean addRole(UserRole role) {
		String sql = "INSERT INTO userrole (roleID, roleName, permissions) VALUES (?, ?, ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, role.getRoleID());
			pst.setString(2, role.getRoleName());

			// Format permissions as JSON array
			String[] permArray = role.getPermissions().split(",");
			StringBuilder jsonPerms = new StringBuilder("[");
			for (int i = 0; i < permArray.length; i++) {
				if (i > 0)
					jsonPerms.append(",");
				jsonPerms.append("\"").append(permArray[i].trim()).append("\"");
			}
			jsonPerms.append("]");

			pst.setString(3, jsonPerms.toString());

			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	/**
	 * Cập nhật thông tin vai trò trong database, bao gồm danh sách quyền Sử dụng
	 * MyConnection thay vì ConnectDB để phù hợp với cấu trúc hiện tại
	 * 
	 * @param role Đối tượng UserRole chứa thông tin cần cập nhật
	 * @return true nếu cập nhật thành công, false nếu có lỗi
	 */
	public boolean updateRole(UserRole role) {
		String sql = "UPDATE userrole SET permissions = ? WHERE roleID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			// Bắt đầu transaction để đảm bảo tính nhất quán của dữ liệu
			conn.setAutoCommit(false);

			try {
				// Thiết lập các tham số cho câu lệnh SQL
				pst.setString(1, role.getPermissions());
				pst.setString(2, role.getRoleID());

				// Thực thi câu lệnh UPDATE
				int rowsAffected = pst.executeUpdate();

				// Nếu cập nhật thành công, commit transaction
				if (rowsAffected > 0) {
					conn.commit();
					return true;
				} else {
					// Nếu không có dòng nào được cập nhật, rollback
					conn.rollback();
					return false;
				}

			} catch (SQLException e) {
				// Nếu có lỗi, rollback transaction
				conn.rollback();
				throw e;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			// Đảm bảo đóng kết nối trong mọi trường hợp
			myConnection.closeConnection();
		}
	}

}