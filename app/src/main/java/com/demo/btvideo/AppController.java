package com.demo.btvideo;

import android.content.Context;

import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.statement.StateLogin;
import com.demo.btvideo.statement.StateUser;
import com.demo.btvideo.utils.NetWorkUtils;


//用户状态控制类
public class AppController {

	private AppController(){}
//	private static AppController controller;
	StateUser stateUser=new StateGuest();
	String authString="";
	public String getAuthString() {
		return authString;
	}

	public void updateAuth(String token){
		NetWorkUtils.updateAuth(token);
		authString=token;
	}


	private static class Holder {
		private static AppController instance = new AppController();
	}

	public static AppController getInstance() {
		return AppController.Holder.instance;
	}


	public void setLogin(StateUser stateUser){
		this.stateUser=stateUser;

	}

	public boolean isLogin(){
		return !authString.equals("");
	}


	public void uploadVideo(Context context){
		stateUser.uploadVideo(context);
	}






}
