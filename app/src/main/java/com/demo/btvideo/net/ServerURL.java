package com.demo.btvideo.net;

public interface ServerURL {

	String MAIN_URL="http://10.34.70.43:8080/";
//	String MAIN_URL="http://10.34.55.97:8080/";
//	String MAIN_URL="http://39.99.150.199:8080";
	String LOGIN_URL = MAIN_URL+"user/login.do";
	String REGISTER_URL = MAIN_URL+"user/register.do";
	String URL_VIDEO_LIST = MAIN_URL+"user/getVideoListj.do";
	String USER_INFO = MAIN_URL+"user/personal.do";
}
