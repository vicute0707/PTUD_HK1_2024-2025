package bus;

import dao.PermissionDAO;
import entity.Permission;
import java.util.List;

public class PermissionBUS {
	private PermissionDAO permissionDAO;

	public PermissionBUS() {
		permissionDAO = new PermissionDAO();
	}

	public List<Permission> getAllPermissions() {
		return permissionDAO.getAllPermissions();
	}

	public Permission getPermissionById(String id) {
		return permissionDAO.getPermissionById(id);
	}

	public boolean addPermission(Permission permission) throws Exception {
		validatePermission(permission);
		return permissionDAO.addPermission(permission);
	}

	public boolean updatePermission(Permission permission) throws Exception {
		validatePermission(permission);
		return permissionDAO.updatePermission(permission);
	}

	public boolean deletePermission(String permissionId) throws Exception {
		if (permissionId == null || permissionId.trim().isEmpty()) {
			throw new Exception("Mã quyền không được để trống");
		}
		return permissionDAO.deletePermission(permissionId);
	}

	private void validatePermission(Permission permission) throws Exception {
		if (permission.getPermissionID() == null || permission.getPermissionID().trim().isEmpty()) {
			throw new Exception("Mã quyền không được để trống");
		}
		if (permission.getName() == null || permission.getName().trim().isEmpty()) {
			throw new Exception("Tên quyền không được để trống");
		}
		// Add additional validation as needed
		if (permission.getPermissionID().length() > 36) {
			throw new Exception("Mã quyền không được vượt quá 36 ký tự");
		}
		if (permission.getName().length() > 50) {
			throw new Exception("Tên quyền không được vượt quá 50 ký tự");
		}
	}
	public String generateNewPermissionId() {
	    String lastId = permissionDAO.getLastPermissionId();
	    
	    if (lastId == null) {
	        return "PERM001";
	    }
	    
	    String numStr = lastId.substring(4);
	    int newNum = Integer.parseInt(numStr) + 1;
	    return String.format("PERM%03d", newNum);
	}
}