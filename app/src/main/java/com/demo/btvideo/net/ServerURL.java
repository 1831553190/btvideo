package com.demo.btvideo.net;


//服务器地址
public interface ServerURL {

	String MAIN_URL="http://192.168.43.20:8080/";
//	String MAIN_URL="http://10.34.40.55:8080/";
//	String MAIN_URL="http://39.99.150.199:8080";
	String LOGIN_URL = MAIN_URL+"user/login.do";
	String REGISTER_URL = MAIN_URL+"user/register.do";
	String URL_VIDEO_LIST = MAIN_URL+"user/getVideoListj.do";
	String USER_INFO = MAIN_URL+"user/personal.do";
}
