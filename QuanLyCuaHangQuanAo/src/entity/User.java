package entity;

import java.util.Date;

public class User {
	private String userID;
	private String username;
	private String password;
	private String fullName;
	private String gender;
	private Date birthDate;
	private String phone;
	private String email;
	private String role;
	private String status;

	// Constructor
	public User() {
	}

	public User(String userID, String username, String fullName, String gender, Date birthDate, String phone,
			String email, String role, String status) {
		this.userID = userID;
		this.username = username;
		this.fullName = fullName;
		this.gender = gender;
		this.birthDate = birthDate;
		this.phone = phone;
		this.email = email;
		this.role = role;
		this.status = status;
	}

	// Getters and Setters
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
