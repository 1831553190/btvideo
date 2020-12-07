package com.demo.btvideo.model;

public class Comment {
	/**
	 * announcerAccount : bt538933
	 * msgType : 1
	 * subscriber : {"password":"$2a$10$MB3UaZvX6iRLT3YsM7DFJuO.PX5wj2s/ll44bOMJfcRkH8DmQyX.q","mail":"","phone":"","signature":null,"headImage":null,"id":5,"type":"1","account":"bt538933","username":"123"}
	 * subscriberAccount : bt538933
	 * id : 12
	 * video : null
	 * title : null
	 * msgStatus : 2
	 * objectId : 14
	 * content : ja
	 * sendTime : 2020-11-06 20:31:36
	 */
	private String announcerAccount;
	private String msgType;
	private SubscriberEntity subscriber;
	private String subscriberAccount;
	private int id;
	private String video;
	private String title;
	private String msgStatus;
	private int objectId;
	private String content;
	private String sendTime;

	public void setAnnouncerAccount(String announcerAccount) {
		this.announcerAccount = announcerAccount;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public void setSubscriber(SubscriberEntity subscriber) {
		this.subscriber = subscriber;
	}

	public void setSubscriberAccount(String subscriberAccount) {
		this.subscriberAccount = subscriberAccount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setVideo(String video) {
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

	public SubscriberEntity getSubscriber() {
		return subscriber;
	}

	public String getSubscriberAccount() {
		return subscriberAccount;
	}

	public int getId() {
		return id;
	}

	public String getVideo() {
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

	public class SubscriberEntity {
		/**
		 * password : $2a$10$MB3UaZvX6iRLT3YsM7DFJuO.PX5wj2s/ll44bOMJfcRkH8DmQyX.q
		 * mail :
		 * phone :
		 * signature : null
		 * headImage : null
		 * id : 5
		 * type : 1
		 * account : bt538933
		 * username : 123
		 */
		private String password;
		private String mail;
		private String phone;
		private String signature;
		private String headImage;
		private int id;
		private String type;
		private String account;
		private String username;

		public void setPassword(String password) {
			this.password = password;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public void setHeadImage(String headImage) {
			this.headImage = headImage;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public String getMail() {
			return mail;
		}

		public String getPhone() {
			return phone;
		}

		public String getSignature() {
			return signature;
		}

		public String getHeadImage() {
			return headImage;
		}

		public int getId() {
			return id;
		}

		public String getType() {
			return type;
		}

		public String getAccount() {
			return account;
		}

		public String getUsername() {
			return username;
		}
	}
}
