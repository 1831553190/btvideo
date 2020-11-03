package com.demo.btvideo.net;

import com.demo.btvideo.model.User;
import com.demo.btvideo.model.AuthData;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface NetInterface {
	//登陆
	@POST("user/login.do")
	Call<Msg<AuthData>> login(@Body HashMap<String,String> data);
	//获取用户信息
	@POST("user/personal.do")
	Call<Msg<User>> getUserInfo(@Header("Authorization") String auth);

	//注册
	@POST("user/register.do")
	Call<Msg<Object>> register(@Body HashMap<String,String> data);

	//获取视频列表
	@POST("user/getVideoList.do")
	Call<Msg<PageInfo>> getPageInfo(@Query("pageNum") int pageNum);

	//获取评论列表
	@POST("user/getCommentList.do")
	Call<Msg<List<Comment>>> getCommentList(@Query("pageNum") int pageNum,@Query("objectId") int objectId);

//	上传用户头像
	@Multipart
	@POST("user/uploadHeadImage.do")
	Call<Msg<String>> uploadHeadImage(@Part MultipartBody.Part body);

	//上传视频
	@Multipart
	@POST("user/uploadVideo.do")
	Call<Msg<PageInfo>> uploadVideo(@Part MultipartBody.Part file);


	@POST("user/praiseVideo.do")
	Call<Msg> praiseVideo();




}
