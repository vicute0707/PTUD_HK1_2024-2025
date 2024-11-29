package bus;

import dao.UserRoleDAO;
import entity.UserRole;
import java.util.List;

public class UserRoleBUS {
	private UserRoleDAO userRoleDAO;

	public UserRoleBUS() {
		userRoleDAO = new UserRoleDAO();
	}

	public List<UserRole> getAllRoles() {
		return userRoleDAO.getAllRoles();
	}

	public UserRole getRoleById(String id) {
		return userRoleDAO.getRoleById(id);
	}

	public boolean addRole(UserRole role) throws Exception {
		validateRole(role);

		if (userRoleDAO.getRoleById(role.getRoleID()) != null) {
			throw new Exception("Mã vai trò đã tồn tại");
		}

		return userRoleDAO.addRole(role);
	}

	public boolean updateRole(UserRole role) throws Exception {
		validateRole(role);
		UserRole existingRole = userRoleDAO.getRoleById(role.getRoleID());
		if (existingRole == null) {
			throw new Exception("Không tìm thấy vai trò");
		}
		return userRoleDAO.updateRolePermissions(role.getRoleID(), role.getPermissions());
	}

	public boolean deleteRole(String roleId) throws Exception {
		if (roleId == null || roleId.trim().isEmpty()) {
			throw new Exception("Mã vai trò không được để trống");
		}
		if (roleId.equals(UserRoleDAO.ADMIN_ROLE_ID)) {
			throw new Exception("Không thể xóa vai trò Admin");
		}
		// Call delete method if added to DAO, for now return false
		return false;
	}

	private void validateRole(UserRole role) throws Exception {
		if (role.getRoleID() == null || role.getRoleID().trim().isEmpty()) {
			throw new Exception("Mã vai trò không được để trống");
		}
		if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
			throw new Exception("Tên vai trò không được để trống");
		}
		if (role.getPermissions() == null || role.getPermissions().trim().isEmpty()) {
			throw new Exception("Phải chọn ít nhất một quyền");
		}
	}

	public String generateNewRoleId() {
		String lastId = userRoleDAO.getLastRoleId();
		if (lastId == null || lastId.equals("ROLE000")) {
			return "ROLE001";
		}
		int currentNum = Integer.parseInt(lastId.substring(4));
		return String.format("ROLE%03d", currentNum + 1);
	}

	public String getPermissionNames(String permissions) {
		return userRoleDAO.getPermissionNames(permissions);
	}

	/**
	 * Cập nhật quyền cho một vai trò trong hệ thống Phương thức này sẽ cập nhật
	 * trường permissions trong bảng userrole
	 * 
	 * @param roleId          Mã vai trò cần cập nhật
	 * @param jsonPermissions Chuỗi JSON chứa danh sách quyền mới
	 * @return true nếu cập nhật thành công, false nếu có lỗi
	 */
	public boolean updatePermissions(String roleId, String jsonPermissions) {
		try {
			// Kiểm tra tham số đầu vào
			if (roleId == null || roleId.trim().isEmpty()) {
				throw new IllegalArgumentException("Mã vai trò không được để trống");
			}

			// Kiểm tra xem vai trò có tồn tại không
			UserRole existingRole = userRoleDAO.getRoleById(roleId);
			if (existingRole == null) {
				throw new IllegalArgumentException("Không tìm thấy vai trò với mã: " + roleId);
			}

			// Cập nhật permissions trong đối tượng UserRole
			existingRole.setPermissions(jsonPermissions);

			// Thực hiện cập nhật vào database
			return userRoleDAO.updateRole(existingRole);

		} catch (Exception e) {
			e.printStackTrace();
			// Log lỗi nếu cần
			return false;
		}
	}

}