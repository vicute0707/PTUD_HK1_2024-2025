package bus;

import dao.UserDAO;
import dao.UserRoleDAO;
import entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserBUS {
	private UserDAO userDAO;
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
	private static final String PHONE_REGEX = "^[0-9]{10}$";
	private UserRoleDAO userPermissionDAO;

	public UserBUS() {
		userDAO = new UserDAO();
		userPermissionDAO = new UserRoleDAO();
	}

	public List<User> getAllUsers() {
		return userDAO.getAllUsers();
	}
	

	public User getUserById(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}
		return userDAO.getUserById(userID);
	}

	public List<User> searchUsers(String keyword) {
		return userDAO.searchUsers(keyword);
	}

	public boolean addUser(User user) throws Exception {
		validateUserData(user, true);

		// Generate new userID if not provided
		if (user.getUserID() == null || user.getUserID().trim().isEmpty()) {
			user.setUserID(generateNewUserID()); // Use the new method to generate the ID
		}

		// Set default status if not provided
		if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
			user.setStatus("Đang hoạt động");
		}

		// Set default role if not provided
		if (user.getRole() == null || user.getRole().trim().isEmpty()) {
			user.setRole("User");
		}

		return userDAO.addUser(user);
	}

	public boolean updateUser(User user) throws Exception {
		validateUserData(user, false);
		return userDAO.updateUser(user);
	}
	public String generateNewUserID() {
	    String lastUserID = userDAO.getLastUserID();
	    int numericPart = Integer.parseInt(lastUserID.substring(4)) + 1;
	    return String.format("USER%03d", numericPart);
	}

	public boolean deleteUser(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}

		// Check if user exists
		User existingUser = userDAO.getUserById(userID);
		if (existingUser == null) {
			throw new Exception("Không tìm thấy người dùng");
		}

		// Prevent deleting the last active admin
		if ("Admin".equals(existingUser.getRole())) {
			List<User> admins = userDAO.getUsersByRole("Admin");
			if (admins.size() <= 1) {
				throw new Exception("Không thể xóa admin cuối cùng trong hệ thống");
			}
		}

		return userDAO.updateUserStatus(userID, "Ngừng hoạt động");
	}

	public boolean resetPassword(String userID, String newPassword) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}
		if (newPassword == null || newPassword.trim().isEmpty()) {
			throw new Exception("Mật khẩu mới không được để trống");
		}
		if (newPassword.length() < 6) {
			throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
		}

		return userDAO.updatePassword(userID, newPassword);
	}

	public boolean updateUserStatus(String userID, String status) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}
		if (status == null || status.trim().isEmpty()) {
			throw new Exception("Trạng thái không được để trống");
		}
		if (!status.equals("Đang hoạt động") && !status.equals("Ngừng hoạt động")) {
			throw new Exception("Trạng thái không hợp lệ");
		}

		return userDAO.updateUserStatus(userID, status);
	}

	private void validateUserData(User user, boolean isNewUser) throws Exception {
		// Validate required fields for new users
		if (isNewUser) {
			if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
				throw new Exception("Username không được để trống");
			}
			if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
				throw new Exception("Password không được để trống");
			}
			// Check if username already exists
			if (userDAO.isUsernameExists(user.getUsername())) {
				throw new Exception("Username đã tồn tại");
			}
		}

		// Common validations
		if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
			throw new Exception("Họ tên không được để trống");
		}

		// Validate email format
		if (user.getEmail() != null && !user.getEmail().isEmpty()) {
			if (!Pattern.compile(EMAIL_REGEX).matcher(user.getEmail()).matches()) {
				throw new Exception("Email không hợp lệ");
			}
		}

		// Validate phone format
		if (user.getPhone() != null && !user.getPhone().isEmpty()) {
			if (!Pattern.compile(PHONE_REGEX).matcher(user.getPhone()).matches()) {
				throw new Exception("Số điện thoại không hợp lệ");
			}
		}

		// Validate birth date
		if (user.getBirthDate() != null) {
			java.util.Date currentDate = new java.util.Date();
			if (user.getBirthDate().after(currentDate)) {
				throw new Exception("Ngày sinh không hợp lệ");
			}
		}
	}



	public boolean hasPermission(String userID, String permissionID) {
		try {
			return userPermissionDAO.checkPermission(userID, permissionID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean assignPermission(String userID, String permissionID) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}
		if (permissionID == null || permissionID.trim().isEmpty()) {
			throw new Exception("Permission ID không được để trống");
		}

		// Check if user exists
		if (getUserById(userID) == null) {
			throw new Exception("Không tìm thấy người dùng");
		}

		return userPermissionDAO.assignPermission(userID, permissionID);
	}

	public boolean removePermission(String userID, String permissionID) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}
		if (permissionID == null || permissionID.trim().isEmpty()) {
			throw new Exception("Permission ID không được để trống");
		}

		return userPermissionDAO.removePermission(userID, permissionID);
	}

	public List<String> getUserPermissions(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty()) {
			throw new Exception("User ID không được để trống");
		}

		return userPermissionDAO.getPermissionsByUser(userID);
	}
	  public List<User> getUsersByRole(String role) throws Exception {
	        // Input validation
	        if (role == null || role.trim().isEmpty()) {
	            throw new Exception("Vai trò không được để trống");
	        }

	        // Get users by role from DAO
	        List<User> users = userDAO.getUsersByRole(role);

	        // If no users found with the given role, return empty list rather than null
	        if (users == null) {
	            return new ArrayList<>();
	        }

	        return users;
	    }

}
