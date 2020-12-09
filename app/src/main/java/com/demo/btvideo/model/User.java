package com.demo.btvideo.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "user")
public class User implements Serializable {

	private String id;
	private String username;
	private String password;
	@NonNull
	@PrimaryKey
	private String account;
	private String type;
	private String phone;
	private String signature;
	private String headImage;
	private String mail;
	int fansNum;
	String attentionStatus;



	public int getFansNum() {
		return fansNum;
	}
	public void setFansNum(int fansNum) {
		this.fansNum = fansNum;
	}

	public String getAttentionStatus() {
		return attentionStatus;
	}

	public void setAttentionStatus(String attentionStatus) {
		this.attentionStatus = attentionStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
//	public User(String username, String password) {
//		this.username = username;
//		this.password = password;
//	}

		/**
		 * id : null
		 * username : 123
		 * account : bt505882
		 * password : $2a$10$AhRX1eqUx6s4ksZ3lsU0Q.QTqirzwYVXZn4nvQu7fag/iKW6wLRgS
		 * type : 1
		 * phone : 13978524587
		 * signature : null
		 * headImage : null
		 * mail : null
		 */
}
