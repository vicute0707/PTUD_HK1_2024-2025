package entity;

import entity.User;
import java.util.Date;

public class UserSession {
	private static UserSession instance;
	private User currentUser;
	private Date loginTime;
	private boolean isLoggedIn;

	// Constructor private để đảm bảo Singleton pattern
	private UserSession() {
		this.isLoggedIn = false;
		this.loginTime = null;
		this.currentUser = null;
	}

	// Singleton pattern
	public static UserSession getInstance() {
		if (instance == null) {
			instance = new UserSession();
		}
		return instance;
	}

	// Thiết lập thông tin người dùng khi đăng nhập
	public void setCurrentUser(User user) {
		this.currentUser = user;
		this.loginTime = new Date();
		this.isLoggedIn = true;
	}

	// Xóa session khi đăng xuất
	public void clearSession() {
		this.currentUser = null;
		this.loginTime = null;
		this.isLoggedIn = false;
	}

	// Kiểm tra đăng nhập
	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	// Kiểm tra quyền
	public boolean hasRole(String roleId) {
		if (!isLoggedIn || currentUser == null)
			return false;
		return currentUser.getRole().equals(roleId);
	}

	// Kiểm tra admin
	public boolean isAdmin() {
		return hasRole("ROLE001");
	}

	// Kiểm tra nhân viên kho
	public boolean isKhoRole() {
		return hasRole("ROLE002");
	}

	// Kiểm tra nhân viên bán hàng
	public boolean isBanHangRole() {
		return hasRole("ROLE003");
	}

	// Getters cho thông tin người dùng
	public String getUserId() {
		return currentUser != null ? currentUser.getUserID() : null;
	}

	public String getUsername() {
		return currentUser != null ? currentUser.getUsername() : null;
	}

	public String getFullName() {
		return currentUser != null ? currentUser.getFullName() : null;
	}

	public String getRole() {
		return currentUser != null ? currentUser.getRole() : null;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	// Lấy toàn bộ thông tin user
	public User getCurrentUser() {
		return currentUser;
	}
}