package service;

import entity.User;
import java.util.logging.Logger;

public class SessionManager {
	private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());
	private static SessionManager instance;
	private User currentUser;

	// Private constructor for singleton pattern
	private SessionManager() {
		LOGGER.info("Creating new SessionManager instance");
	}

	// Get singleton instance with thread safety
	public static synchronized SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	// Set the current user when logging in
	public void setCurrentUser(User user) {
		if (user == null) {
			LOGGER.warning("Attempted to set null user");
			throw new IllegalArgumentException("User cannot be null");
		}

		if (!"active".equals(user.getStatus())) {
			LOGGER.warning("Attempted to log in inactive user: " + user.getUsername());
			throw new IllegalStateException("User account is not active");
		}

		this.currentUser = user;
		LOGGER.info("User logged in: " + user.getUsername());
	}

	// Get the current logged-in user
	public User getCurrentUser() {
		if (currentUser == null) {
			LOGGER.warning("Attempted to get current user when no user is logged in");
		}
		return currentUser;
	}

	// Check if a user is logged in
	public boolean isAuthenticated() {
		return currentUser != null && "active".equals(currentUser.getStatus());
	}

	// Log out the current user
	public void logout() {
		LOGGER.info("User logged out: " + (currentUser != null ? currentUser.getUsername() : "null"));
		currentUser = null;
	}

	// Convenience method to get user's full name
	public String getCurrentUserFullName() {
		return currentUser != null ? currentUser.getFullName() : "";
	}

	// Check if current user has a specific role
	public boolean hasRole(String role) {
		return currentUser != null && currentUser.getRole() != null && currentUser.getRole().equals(role);
	}
}