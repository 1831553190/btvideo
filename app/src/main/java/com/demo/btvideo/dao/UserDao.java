package com.demo.btvideo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.btvideo.model.User;

import java.util.List;

@Dao
public interface UserDao {
	@Query("SELECT * FROM user")
	List<User> getAll();

	@Query("SELECT * FROM user")
	User getUser();

	@Query("SELECT * FROM user where account=:account")
	User getUser(String account);

	@Query("SELECT * FROM user WHERE id IN (:userIds)")
	List<User> loadAllByIds(int[] userIds);

//	@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//			"last_name LIKE :last LIMIT 1")
//	User findByName(String first, String last);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertAll(User...users);


	@Delete
	void delete(User user);
}
