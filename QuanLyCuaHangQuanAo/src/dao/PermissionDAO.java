package dao;

import entity.Permission;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO {
	private MyConnection myConnection;

	public PermissionDAO() {
		myConnection = new MyConnection();
	}

	public List<Permission> getAllPermissions() {
		List<Permission> permissions = new ArrayList<>();
		String sql = "SELECT * FROM permission";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				Permission permission = new Permission();
				permission.setPermissionID(rs.getString("permissionID"));
				permission.setName(rs.getString("name"));
				permission.setDescription(rs.getString("description"));
				permissions.add(permission);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.closeConnection();
		}
		return permissions;
	}

	public boolean addPermission(Permission permission) {
		String sql = "CALL sp_AddPermission(?, ?, ?)";

		try (Connection conn = myConnection.connect(); CallableStatement cs = conn.prepareCall(sql)) {

			cs.setString(1, permission.getPermissionID());
			cs.setString(2, permission.getName());
			cs.setString(3, permission.getDescription());

			return cs.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean updatePermission(Permission permission) {
		String sql = "UPDATE permission SET name = ?, description = ? WHERE permissionID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, permission.getName());
			pst.setString(2, permission.getDescription());
			pst.setString(3, permission.getPermissionID());

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean deletePermission(String permissionId) {
		String sql = "DELETE FROM permission WHERE permissionID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, permissionId);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}
	// Add to PermissionDAO class:

	public Permission getPermissionById(String id) {
		String sql = "SELECT * FROM permission WHERE permissionID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, id);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				Permission permission = new Permission();
				permission.setPermissionID(rs.getString("permissionID"));
				permission.setName(rs.getString("name"));
				permission.setDescription(rs.getString("description"));
				return permission;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.closeConnection();
		}
		return null;
	}

	public String getLastPermissionId() {
		String sql = "SELECT permissionID FROM permission ORDER BY CAST(SUBSTRING(permissionID, 5) AS UNSIGNED) DESC LIMIT 1";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				return rs.getString("permissionID");
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			myConnection.closeConnection();
		}
	}

	public String getPermissionNameById(String permissionId) {
		String sql = "SELECT name FROM permission WHERE permissionID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, permissionId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getString("name");
			}
			return permissionId; // Return ID if name not found

		} catch (SQLException e) {
			e.printStackTrace();
			return permissionId;
		} finally {
			myConnection.closeConnection();
		}
	}
}