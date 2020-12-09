package com.demo.btvideo.model;


//粉丝对象的实体类
public class Funs {

	/**
	 * attentionNum : null
	 * fansAccount : bt638005
	 * attentionTime : 2020-12-09 11:46:16
	 * followAccount : bt963702
	 * id : 21
	 * up : null
	 * status : 1
	 * fans : {"password":"$2a$10$XRyT/lHXj0kPzjAhCfz5H.itiPr4Zc.HZ5DeQTHmWdn2aRafjdKFW","fansNum":1,"mail":null,"phone":"13978524587","signature":null,"headImage":"head_image/bt638005.png","attentionStatus":null,"id":6,"type":"1","account":"bt638005","username":"123"}
	 */
	private String attentionNum;
	private String fansAccount;
	private String attentionTime;
	private String followAccount;
	private int id;
	private String up;
	private String status;
	private User fans;

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

	public void setUp(String up) {
		this.up = up;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFans(User fans) {
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

	public String getUp() {
		return up;
	}

	public String getStatus() {
		return status;
	}

	public User getFans() {
		return fans;
	}
}
