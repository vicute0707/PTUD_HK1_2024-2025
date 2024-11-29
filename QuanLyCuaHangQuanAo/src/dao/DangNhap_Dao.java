package dao;

import entity.User;
import service.EmailService;
import service.PasswordGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.MyConnection;

public class DangNhap_Dao {
	private MyConnection myConnection;

	public DangNhap_Dao() {
		myConnection = new MyConnection();
	}

	public User kiemTraDangNhap(String username, String password) {
	    String sql = "SELECT * FROM user WHERE username = ?";

	    try (Connection conn = myConnection.connect(); 
	         PreparedStatement pst = conn.prepareStatement(sql)) {

	        pst.setString(1, username);
	        ResultSet rs = pst.executeQuery();

	        if (rs.next()) {
	            String dbPassword = rs.getString("password");

	            if (dbPassword.equals(password)) {
	                User user = new User();
	                user.setUserID(rs.getString("userID"));
	                user.setUsername(rs.getString("username"));
	                user.setFullName(rs.getString("fullName"));
	                user.setGender(rs.getString("gender"));
	                user.setBirthDate(rs.getDate("birthDate"));
	                user.setPhone(rs.getString("phone"));
	                user.setEmail(rs.getString("email"));
	                
	                // Kiểm tra và set role
	                String role = rs.getString("role");
	                if (role == null || role.trim().isEmpty()) {
	                    // Set role mặc định nếu cần
	                    role = "ROLE003"; // hoặc role phù hợp với hệ thống của bạn
	                }
	                user.setRole(role);
	                
	                user.setStatus(rs.getString("status"));

	                if (!"active".equals(user.getStatus())) {
	                    return null;
	                }

	                return user;
	            }
	        }
	        return null;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        myConnection.closeConnection();
	    }
	}

	 public String layLaiMatKhau(String username, String email, String phone) {
	        Connection conn = null;
	        PreparedStatement pst = null;
	        ResultSet rs = null;
	        
	        try {
	            conn = myConnection.connect();
	            
	            // Kiểm tra thông tin user
	            String sql = "SELECT * FROM user WHERE username = ? AND email = ? AND phone = ? AND status = 'active'";
	            pst = conn.prepareStatement(sql);
	            pst.setString(1, username);
	            pst.setString(2, email);
	            pst.setString(3, phone);
	            
	            rs = pst.executeQuery();
	            
	            if (rs.next()) {
	                // Tạo mật khẩu mới đơn giản
	                String newPassword = "password"; // Mật khẩu mặc định
	                
	                // Cập nhật mật khẩu mới vào DB
	                String updateSql = "UPDATE user SET password = ? WHERE username = ?";
	                try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
	                    updatePst.setString(1, newPassword);
	                    updatePst.setString(2, username);
	                    updatePst.executeUpdate();
	                }
	                
	                // Gửi email
	                boolean emailSent = EmailService.sendPasswordResetEmail(
	                    email, 
	                    rs.getString("fullName"), // Sử dụng tên đầy đủ từ DB
	                    newPassword
	                );
	                
	                if (emailSent) {
	                    // Ghi log thành công
	                    ghiLogDoiMatKhau(conn, username, "SUCCESS");
	                    return newPassword;
	                } else {
	                    // Ghi log thất bại
	                    ghiLogDoiMatKhau(conn, username, "EMAIL_FAILED");
	                    return null;
	                }
	            }
	            return null;
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	            try {
	                if (conn != null) {
	                    ghiLogDoiMatKhau(conn, username, "ERROR: " + e.getMessage());
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	            return null;
	        } finally {
	            try {
	                if (rs != null) rs.close();
	                if (pst != null) pst.close();
	                if (conn != null) conn.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    private void ghiLogDoiMatKhau(Connection conn, String username, String status) throws SQLException {
	        String sql = "INSERT INTO password_reset_log (username, reset_time, status) VALUES (?, NOW(), ?)";
	        try (PreparedStatement pst = conn.prepareStatement(sql)) {
	            pst.setString(1, username);
	            pst.setString(2, status);
	            pst.executeUpdate();
	        }
	    }
}