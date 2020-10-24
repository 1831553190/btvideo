package com.demo.btvideo;

public class AppController {

	private AppController(){}
	private static AppController controller;
	static boolean login=false;


	public static AppController getInstance(){
		if (controller==null){
			synchronized (AppController.class){
				if (controller==null){
					controller=new AppController();
				}
			}
		}
		return controller;
	}


	public void setLogin(boolean isLogin){
		if (isLogin){
			login=true;
		}else {
			login=false;
		}
	}


	public boolean isLogin(){
		return login;
	}


}
