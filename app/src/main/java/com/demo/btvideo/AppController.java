package com.demo.btvideo;

public class AppController {

	private AppController(){}
	private static AppController controller;


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


}
