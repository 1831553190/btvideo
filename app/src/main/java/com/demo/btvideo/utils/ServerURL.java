package com.demo.btvideo.utils;

public interface ServerURL {

	String MAIN_URL="http://10.34.48.194:8080/";
//	String MAIN_URL="http://39.99.150.199:8080";
	String LOGIN_URL = MAIN_URL+"user/login.do";
	String REGISTER_URL = MAIN_URL+"user/register.do";
	String URL_VIDEO_LIST = MAIN_URL+"user/getVideoList.do";
	String USER_INFO = MAIN_URL+"user/personal.do";
}
