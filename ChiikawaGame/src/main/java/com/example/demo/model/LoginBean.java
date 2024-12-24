package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "userInfo")
public class LoginBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@Column(name = "userEmail") // 這裡改為 "userAC"，對應資料庫欄位
	private String userEmail; // 改為 userAC，與資料庫欄位名稱匹配

	@Column(name = "userPassword")
	private String userPassword;

	@Column(name = "userName")
	private String userName;

	@Column(name = "userTel")
	private String userTel;

	@Column(name = "userIdNumber")
	private String userIdNumber;

	@Column(name = "userBirthday")
	private String userBirthday;
	
	@Column(name = "userStatus")
	private int userStatus;
	
	@Transient
	private String status;
	
	@Transient
	private String message;
	
	// 無參構造函數（JPA 要求）
	public LoginBean() {
	}

	// Getters and Setters

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserTel() {
		return userTel;
	}

	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

	public String getUserIdNumber() {
		return userIdNumber;
	}

	public void setUserIdNumber(String userIdNumber) {
		this.userIdNumber = userIdNumber;
	}

	public String getUserBirthday() {
		return userBirthday;
	}

	public void setUserBirthday(String userBirthday) {
		this.userBirthday = userBirthday;
	}
	
	public int getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	
	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
	
}
