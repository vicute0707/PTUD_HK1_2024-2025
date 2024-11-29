package service;

import entity.User;
import entity.UserRole;
import bus.UserBUS;
import bus.UserRoleBUS;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Manages permission checking and validation for the store management system.
 * Works in conjunction with the UserRole entity class to provide comprehensive
 * permission management capabilities.
 */
public class PermissionChecker {
	private static final Logger LOGGER = Logger.getLogger(PermissionChecker.class.getName());
	private static final UserRoleBUS userRoleBUS = new UserRoleBUS();

	public static final String PERM_USER_MANAGEMENT = "PERM001";  // Quản lý người dùng
	public static final String PERM_PRODUCT_MANAGEMENT = "PERM002"; // Quản lý sản phẩm
	public static final String PERM_ORDER_MANAGEMENT = "PERM003";  // Quản lý đơn hàng 
	public static final String PERM_REPORT = "PERM004";  // Báo cáo

	// Permission descriptions for user interface display
	private static final Map<String, String> PERMISSION_DESCRIPTIONS = new HashMap<String, String>() {
		{
			put(PERM_USER_MANAGEMENT, "Quản lý người dùng");
			put(PERM_PRODUCT_MANAGEMENT, "Quản lý sản phẩm");
			put(PERM_ORDER_MANAGEMENT, "Quản lý đơn hàng");
			put(PERM_REPORT, "Quản lý báo cáo");
		}
	};

	/**
	 * Checks if a user has a specific permission by examining their role's
	 * permission set. Takes advantage of the UserRole class's built-in permission
	 * management.
	 */
	public static boolean hasPermission(String userId, String permissionId) {
	    try {
	        // First get the user's role directly from the business layer
	        UserBUS userBUS = new UserBUS();
	        User user = userBUS.getUserById(userId);
	        
	        if (user == null) {
	            LOGGER.warning("No user found for ID: " + userId);
	            return false;
	        }

	        // Now get the role using the role ID stored in the user object
	        UserRole userRole = userRoleBUS.getRoleById(user.getRole());
	        
	        if (userRole == null) {
	            LOGGER.warning("No role found for user: " + userId + " with role ID: " + user.getRole());
	            return false;
	        }

	        // For admin roles, grant all permissions automatically
	        if (userRole.isAdminRole()) {
	            LOGGER.info("User " + userId + " has admin role - granting permission: " + permissionId);
	            return true;
	        }

	        // Use the UserRole's built-in permission checking
	        boolean hasPermission = userRole.hasPermission(permissionId);
	        LOGGER.info("Permission check for user " + userId + ": " + permissionId + " = " + hasPermission);
	        return hasPermission;
	        
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Error checking permission for user: " + userId, e);
	        return false;
	    }
	}
	/**
	 * Updates the permissions for a role using the UserRole's permission management
	 * methods.
	 */
	public static void updateRolePermissions(String roleId, List<String> newPermissions) {
		try {
			UserRole userRole = userRoleBUS.getRoleById(roleId);
			if (userRole == null) {
				throw new IllegalArgumentException("Role not found: " + roleId);
			}

			// Convert List to Set and update permissions
			Set<String> permissionSet = new HashSet<>(newPermissions);
			userRole.setPermissionSet(permissionSet);

			// Update in database through BUS layer
			userRoleBUS.updatePermissions(roleId, userRole.getPermissions());

			LOGGER.info("Updated permissions for role " + roleId + ": " + userRole.getPermissions());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error updating role permissions: " + roleId, e);
			throw new RuntimeException("Failed to update permissions", e);
		}
	}

	/**
	 * Gets the user-friendly description of a permission.
	 */
	public static String getPermissionDescription(String permissionId) {
		return PERMISSION_DESCRIPTIONS.getOrDefault(permissionId, "Quyền không xác định");
	}

	/**
	 * Checks if a user has multiple permissions.
	 */
	public static boolean hasAllPermissions(String userId, String... permissionIds) {
		try {
			UserRole userRole = userRoleBUS.getRoleById(userId);
			if (userRole == null) {
				return false;
			}

			// Admin roles have all permissions
			if (userRole.isAdminRole()) {
				return true;
			}

			// Check each required permission
			for (String permissionId : permissionIds) {
				if (!userRole.hasPermission(permissionId)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error checking multiple permissions for user: " + userId, e);
			return false;
		}
	}
}