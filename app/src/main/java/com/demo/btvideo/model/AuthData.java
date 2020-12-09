package com.demo.btvideo.model;



//令牌(token)数据
public class AuthData {
	private String tokenHead;
	private String token;

	public void setTokenHead(String tokenHead) {
		this.tokenHead = tokenHead;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenHead() {
		return tokenHead;
	}

	public String getToken() {
		return token;
	}
}

