package com.demo.btvideo.model;

public class Collection {

	/**
	 * collectTime : 2020-12-02 16:43:30
	 * userAccount : bt413167
	 * collectNum : null
	 * videoId : 9
	 * id : 8
	 * collectStatus : 1
	 */
	private String collectTime;
	private String userAccount;
	private String collectNum;
	private int videoId;
	private int id;
	private String collectStatus;
	private VideoInfo video;

	public VideoInfo getData() {
		return video;
	}

	public void setData(VideoInfo video) {
		this.video = video;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public void setCollectNum(String collectNum) {
		this.collectNum = collectNum;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCollectStatus(String collectStatus) {
		this.collectStatus = collectStatus;
	}

	public String getCollectTime() {
		return collectTime;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public String getCollectNum() {
		return collectNum;
	}

	public int getVideoId() {
		return videoId;
	}

	public int getId() {
		return id;
	}

	public String getCollectStatus() {
		return collectStatus;
	}
}
