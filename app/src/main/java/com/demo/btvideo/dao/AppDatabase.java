package com.demo.btvideo.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.demo.btvideo.model.User;
import com.demo.btvideo.model.VideoInfo;


//Room抽象类
@Database(entities = {User.class, VideoInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract UserDao userDao();
}