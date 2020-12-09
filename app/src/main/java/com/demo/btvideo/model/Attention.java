package com.demo.btvideo.model;


//关注对象的实体类
public class Attention {
	/**
	 * attentionNum : null
	 * fansAccount : bt963702
	 * attentionTime : 2020-12-08 21:44:36
	 * followAccount : bt638005
	 * id : 3
	 * up : {"password":"$2a$10$XRyT/lHXj0kPzjAhCfz5H.itiPr4Zc.HZ5DeQTHmWdn2aRafjdKFW","fansNum":1,"mail":null,"phone":"13978524587","signature":null,"headImage":null,"attentionStatus":null,"id":6,"type":"1","account":"bt638005","username":"123"}
	 * status : 1
	 * fans : null
	 */
	private String attentionNum;
	private String fansAccount;
	private String attentionTime;
	private String followAccount;
	private int id;
	private User up;
	private String status;
	private String fans;

	public void setAttentionNum(String attentionNum) {
		this.attentionNum = attentionNum;
	}

	public void setFansAccount(String fansAccount) {
		this.fansAccount = fansAccount;
	}

	public void setAttentionTime(String attentionTime) {
		this.attentionTime = attentionTime;
	}

	public void setFollowAccount(String followAccount) {
		this.followAccount = followAccount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUp(User up) {
		this.up = up;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFans(String fans) {
		this.fans = fans;
	}

	public String getAttentionNum() {
		return attentionNum;
	}

	public String getFansAccount() {
		return fansAccount;
	}

	public String getAttentionTime() {
		return attentionTime;
	}

	public String getFollowAccount() {
		return followAccount;
	}

	public int getId() {
		return id;
	}

	public User getUp() {
		return up;
	}

	public String getStatus() {
		return status;
	}

	public String getFans() {
		return fans;
	}

}
