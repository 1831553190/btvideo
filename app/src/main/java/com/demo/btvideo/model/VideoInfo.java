package com.demo.btvideo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoInfo implements Serializable {
	/**
	 * id : 3
	 * title : 国服第一亚索
	 * labels : 紧张刺激
	 * description : 我用双手成就你的梦想
	 * coverImage : C:\Users\Nekumiya\Desktop\videoapp\video\刘柏良\de5249de-0802-4bfd-afdf-7baba9ec22c8\802429e2221802ebe455c652db1e17dd.jpg
	 * videoUrl : C:\Users\Nekumiya\Desktop\videoapp\video\刘柏良\de5249de-0802-4bfd-afdf-7baba9ec22c8\testVideo.mp4
	 * videoType : mp4
	 * updateTime : 2020-10-22 21:53:49
	 * watchNum : 0
	 * collectNum : 0
	 * praiseNum : 0
	 * userAccount : bt413167
	 * categoryId : null
	 * username : 刘柏良
	 */

	private int id;
	private String title;
	private String labels;
	private String description;
	private String coverImage;
	@SerializedName("videoUrl")
	private String videoUrl;
	private String videoType;
	private String updateTime;
	private int watchNum;
	private int collectNum;
	private int praiseNum;
	private String userAccount;
	private Object categoryId;
	User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoType() {
		return videoType;
	}

	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getWatchNum() {
		return watchNum;
	}

	public void setWatchNum(int watchNum) {
		this.watchNum = watchNum;
	}

	public int getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(int collectNum) {
		this.collectNum = collectNum;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public Object getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Object categoryId) {
		this.categoryId = categoryId;
	}


}
