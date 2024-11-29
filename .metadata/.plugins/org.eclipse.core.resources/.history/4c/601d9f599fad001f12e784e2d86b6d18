package service;

import java.util.*;

import entity.User;

public class PermissionManager {
	private static PermissionManager instance;
	private Map<String, Set<String>> rolePermissions;

	private PermissionManager() {
		rolePermissions = new HashMap<>();
		initializeDefaultPermissions();
	}

	public static PermissionManager getInstance() {
		if (instance == null) {
			instance = new PermissionManager();
		}
		return instance;
	}

	private void initializeDefaultPermissions() {
		// Admin có tất cả các quyền
		 rolePermissions = new HashMap<>();
	        
	        // Admin permissions
	        Set<String> adminPermissions = new HashSet<>(Arrays.asList(
	            "VIEW_STAFF", "ADD_STAFF", "EDIT_STAFF", "DELETE_STAFF",
	            "VIEW_PERMISSIONS", "MANAGE_PERMISSIONS",
	            "VIEW_ACCOUNTS", "MANAGE_ACCOUNTS",
	            "VIEW_REPORTS", "MANAGE_SYSTEM",
	            "EXPORT_STAFF", "EXPORT_PERMISSIONS", "EXPORT_ACCOUNTS"
	        ));
	        rolePermissions.put("ROLE001", adminPermissions);
	}

	public boolean hasPermission(User user, String permission) {
        if (user == null || permission == null) {
            return false;
        }
        
        String userRole = user.getRole();
        System.out.println("Checking permission for user: " + user.getUsername());
        System.out.println("User role: " + userRole);
        
        // Admin luôn có tất cả quyền
        if (userRole != null && userRole.equals("ROLE001")) {
            System.out.println("User is admin, granting permission: " + permission);
            return true;
        }
        
        Set<String> permissions = rolePermissions.get(userRole);
        boolean hasPermission = permissions != null && permissions.contains(permission);
        System.out.println("Permission " + permission + " result: " + hasPermission);
        
        return hasPermission;
    }

}
