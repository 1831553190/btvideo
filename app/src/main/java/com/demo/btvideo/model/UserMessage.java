package com.demo.btvideo.model;

public class UserMessage {

	/**
	 * announcerAccount : bt413167
	 * msgType : 1
	 * subscriber : null
	 * subscriberAccount : bt658384
	 * id : 79
	 * video : {"videoType":"mp4","description":"视频描述:这是测试上传功能","watchNum":0,"collectNum":0,"praiseNum":0,"updateTime":"2020-11-22 21:01:20","praiseStatus":null,"title":"上传测试","labels":"测试标签","videoPath":"/home/eswd/video/video/bt413167/e959795d-0f3f-48c7-b411-dac1fd54fa9a/video.mp4","videoUrl":"video/bt413167/e959795d-0f3f-48c7-b411-dac1fd54fa9a/video.mp4","coverImage":"video/bt413167/e959795d-0f3f-48c7-b411-dac1fd54fa9a/head.png","userAccount":"bt413167","id":24,"user":null,"categoryId":null,"collectStatus":null}
	 * title : null
	 * msgStatus : 2
	 * objectId : 24
	 * content : jjnn
	 * sendTime : 2020-11-22 21:07:24
	 */
	private String announcerAccount;
	private String msgType;
	private String subscriber;
	private String subscriberAccount;
	private int id;
	private String title;
	private String msgStatus;
	private int objectId;
	private String content;
	private String sendTime;
	private VideoInfo video;

	public void setAnnouncerAccount(String announcerAccount) {
		this.announcerAccount = announcerAccount;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public void setSubscriberAccount(String subscriberAccount) {
		this.subscriberAccount = subscriberAccount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setVideo(VideoInfo video) {
		this.video = video;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getAnnouncerAccount() {
		return announcerAccount;
	}

	public String getMsgType() {
		return msgType;
	}

	public String getSubscriber() {
		return subscriber;
	}

	public String getSubscriberAccount() {
		return subscriberAccount;
	}

	public int getId() {
		return id;
	}

	public VideoInfo getVideo() {
		return video;
	}

	public String getTitle() {
		return title;
	}

	public String getMsgStatus() {
		return msgStatus;
	}

	public int getObjectId() {
		return objectId;
	}

	public String getContent() {
		return content;
	}

	public String getSendTime() {
		return sendTime;
	}
}
