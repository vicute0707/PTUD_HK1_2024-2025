package dao;

import entity.User;
import entity.UserRole;
import connection.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	private MyConnection myConnection;

	public UserDAO() {
		myConnection = new MyConnection();
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM user";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				User user = new User();
				user.setUserID(rs.getString("userID"));
				user.setUsername(rs.getString("username"));
				user.setFullName(rs.getString("fullName"));
				user.setGender(rs.getString("gender"));
				user.setBirthDate(rs.getDate("birthDate"));
				user.setPhone(rs.getString("phone"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				users.add(user);
			}
			return users;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean addUser(User user) {
		String sql = "INSERT INTO user (userID, username, password, fullName, gender, birthDate, phone, email, role, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, user.getUserID());
			pst.setString(2, user.getUsername());
			pst.setString(3, user.getPassword());
			pst.setString(4, user.getFullName());
			pst.setString(5, user.getGender());
			pst.setDate(6, new java.sql.Date(user.getBirthDate().getTime()));
			pst.setString(7, user.getPhone());
			pst.setString(8, user.getEmail());
			pst.setString(9, user.getRole());
			pst.setString(10, "active");

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean updateUser(User user) {
		String sql = "UPDATE user SET fullName=?, gender=?, birthDate=?, phone=?, email=?, role=?, status=? WHERE userID=?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, user.getFullName());
			pst.setString(2, user.getGender());
			pst.setDate(3, new java.sql.Date(user.getBirthDate().getTime()));
			pst.setString(4, user.getPhone());
			pst.setString(5, user.getEmail());
			pst.setString(6, user.getRole());
			pst.setString(7, user.getStatus());
			pst.setString(8, user.getUserID());

			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean deleteUser(String userID) {
		String sql = "UPDATE user SET status='inactive' WHERE userID=?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, userID);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public User getUserById(String id) {
		String sql = "SELECT * FROM user WHERE userID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, id);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				User user = new User();
				user.setUserID(rs.getString("userID"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setFullName(rs.getString("fullName"));
				user.setGender(rs.getString("gender"));
				user.setBirthDate(rs.getDate("birthDate"));
				user.setPhone(rs.getString("phone"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				return user;
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			myConnection.closeConnection();
		}
	}

	public User getUserByUsername(String username) {
		String sql = "SELECT * FROM user WHERE username = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				User user = new User();
				user.setUserID(rs.getString("userID"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setFullName(rs.getString("fullName"));
				user.setGender(rs.getString("gender"));
				user.setBirthDate(rs.getDate("birthDate"));
				user.setPhone(rs.getString("phone"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				return user;
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			myConnection.closeConnection();
		}
	}

	public List<String> getAllUserIDs() {
		List<String> userIDs = new ArrayList<>();
		String sql = "SELECT userID FROM user WHERE status = 'active'";

		try (Connection conn = myConnection.connect();
				PreparedStatement pst = conn.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				userIDs.add(rs.getString("userID"));
			}
			return userIDs;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean isUsernameExists(String username) {
		String sql = "SELECT COUNT(*) FROM user WHERE username = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, username);
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

	public boolean updateUserStatus(String userID, String status) {
		String sql = "UPDATE user SET status = ? WHERE userID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, status);
			pst.setString(2, userID);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean updatePassword(String userID, String newPassword) {
		String sql = "UPDATE user SET password = ? WHERE userID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, newPassword);
			pst.setString(2, userID);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean updateUserRole(String userID, String newRole) {
		String sql = "UPDATE user SET role = ? WHERE userID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, newRole);
			pst.setString(2, userID);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}
	// Add these methods to your existing UserDAO class:

	public List<User> getUsersByRole(String role) {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE role = ? AND status = 'active'";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, role);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setUserID(rs.getString("userID"));
				user.setUsername(rs.getString("username"));
				user.setFullName(rs.getString("fullName"));
				user.setGender(rs.getString("gender"));
				user.setBirthDate(rs.getDate("birthDate"));
				user.setPhone(rs.getString("phone"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				users.add(user);
			}
			return users;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public List<User> searchUsers(String keyword) {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE status = 'active' AND "
				+ "(userID LIKE ? OR username LIKE ? OR fullName LIKE ? OR " + "phone LIKE ? OR email LIKE ?)";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			String searchPattern = "%" + keyword + "%";
			pst.setString(1, searchPattern);
			pst.setString(2, searchPattern);
			pst.setString(3, searchPattern);
			pst.setString(4, searchPattern);
			pst.setString(5, searchPattern);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setUserID(rs.getString("userID"));
				user.setUsername(rs.getString("username"));
				user.setFullName(rs.getString("fullName"));
				user.setGender(rs.getString("gender"));
				user.setBirthDate(rs.getDate("birthDate"));
				user.setPhone(rs.getString("phone"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				users.add(user);
			}
			return users;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public boolean updateUsername(String userID, String newUsername) {
		String sql = "UPDATE user SET username = ? WHERE userID = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, newUsername);
			pst.setString(2, userID);
			return pst.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			myConnection.closeConnection();
		}
	}

	public List<User> getUsersByStatus(String status) {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE status = ?";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {

			pst.setString(1, status);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setUserID(rs.getString("userID"));
				user.setUsername(rs.getString("username"));
				user.setFullName(rs.getString("fullName"));
				user.setGender(rs.getString("gender"));
				user.setBirthDate(rs.getDate("birthDate"));
				user.setPhone(rs.getString("phone"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				users.add(user);
			}
			return users;

		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			myConnection.closeConnection();
		}
	}

	public String getLastUserID() {
		String sql = "SELECT userID FROM user ORDER BY CAST(SUBSTRING(userID, 5) AS UNSIGNED) DESC LIMIT 1";

		try (Connection conn = myConnection.connect(); PreparedStatement pst = conn.prepareStatement(sql)) {
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString("userID");
			}
			return "USER000"; // Default starting value
		} catch (SQLException e) {
			e.printStackTrace();
			return "USER000";
		} finally {
			myConnection.closeConnection();
		}
	}

	
}