package com.demo.btvideo.utils;

import android.content.Context;

import com.demo.btvideo.model.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tool {



	public static File saveCover(Context context,User user, File of) {
		String storePath = context.getFilesDir().getAbsolutePath();
		File appdir = new File(storePath);
		if (!appdir.exists()) {
			appdir.mkdir();
		}
		String userHead=null;
		if (user!=null){
			userHead = user.getHeadImage();
		}
//		String fileName = user.getAccount() + userHead.substring(userHead.lastIndexOf("."), user.getHeadImage().length());
		String fileName = user.getAccount() +".png";
		File file = new File(appdir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			FileInputStream fis = new FileInputStream(of);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buffer = new byte[1024];
			int byteRead;
			while (-1 != (byteRead = bis.read(buffer))) {
				fos.write(buffer, 0, byteRead);
			}
			bis.close();
			fos.flush();
			fos.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static File getPictureFile(Context context, InputStream in) {
		String storePath = context.getFilesDir().getAbsolutePath();
		File appDir = new File(storePath);
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
//			FileInputStream fis = new FileInputStream(fileDescriptor.getFileDescriptor());
			BufferedInputStream bis = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			int byteRead;
			while (-1 != (byteRead = bis.read(buffer))) {
				fos.write(buffer, 0, byteRead);
			}
			bis.close();
			fos.flush();
			fos.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
