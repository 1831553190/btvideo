package com.demo.btvideo.net;

import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Funs;
import com.demo.btvideo.model.User;
import com.demo.btvideo.model.AuthData;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.UserMessage;
import com.demo.btvideo.model.VideoInfo;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;



//Retrofit的网络接口
public interface NetInterface {
	//登录
	@POST("user/login.do")
	Call<Msg<AuthData>> login(@Body HashMap<String,String> data,@Query("test") String test);
	//获取用户信息
	@POST("user/personal.do")
	Call<Msg<User>> getUserInfo(@Header("Authorization") String auth);

	//获取用户信息
	@POST("{path}")
	Observable<ResponseBody> downloadFile(String puth);

	//获取用户信息
	@POST("user/personal.do")
	Call<Msg<JsonElement>> getUserInfo();
	//获取用户信息
	@POST("user/personal.do")
	Observable<Msg<JsonElement>> getUserInfoOb();
	//修改用户信息
	@POST("user/modify.do")
	Call<Msg<JsonElement>> modifyUserInfo(@Body HashMap<String,String> body);

	//注册
	@POST("user/register.do")
	Call<Msg<Object>> register(@Body HashMap<String,String> data);

	//获取视频列表
	@POST("user/getVideoList.do")
	Call<Msg<PageInfo<VideoInfo>>> getPageInfo(@Query("pageNum") int pageNum,@Query("pageSize") int pageSize);

	//获取评论列表
	@POST("user/getMessages.do")
	Call<Msg<JsonElement>> getCommentList(@Query("pageNum") int pageNum, @Query("objectId") int objectId);

	//获取消息列表
	@POST("user/getMessageList.do")
	Call<Msg<PageInfo<UserMessage>>> getMessageList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

	//获取上传作品列表
	@POST("user/getVideoList.do")
	Call<Msg<PageInfo<VideoInfo>>> getUploadList(@Query("userAccount") String account,@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);


//	上传用户头像
	@Multipart
	@POST("user/uploadHeadImage.do")
	Call<Msg<String>> uploadHeadImage(@Part MultipartBody.Part body);

	//上传视频
	@Multipart
	@POST("user/uploadVideo.do")
	Call<Msg<String>> uploadVideo(@Part List<MultipartBody.Part> videofile,@QueryMap HashMap<String,String> data);


	@POST("user/praiseVideo.do")
	Call<Msg<JsonElement>> praiseVideo(@Body HashMap<String,String> body);

	//关注
	@POST("user/followUser.do")
	Call<Msg<JsonElement>> followUser(@Body HashMap<String,String> body);
	//收藏
	@POST("user/collectVideo.do")
	Call<Msg<JsonElement>> collectVideo(@Body HashMap<String,String> body);


	@POST("user/publish.do")
	Call<Msg> sendComment(@Body HashMap<String,String> data);

	@POST("user/playVideo.do")
	Call<Msg<VideoInfo>> getVideoInfo(@Query("id") String id,@Query("pageSize") int pageSize);

	@POST("user/playVideo.do")
	Call<Msg<VideoInfo>> getVideoInfo(@Query("id") String id,@Body HashMap<String,String> body,@Query("pageSize") int pageSize);


	//收藏列表
	@POST("user/getCollectionList.do")
	Call<Msg<PageInfo<Collection>>> getCollectionList(@Query("pageNum")int pageNum, @Query("pageSize") int pageSize);

	//关注列表
	@POST("user/getAttentionList.do")
	Call<Msg<PageInfo<Attention>>> getAttentionList(@Query("pageNum")int pageNum, @Query("pageSize") int pageSize);


	//获取消息列表
	@POST("user/getMessageList.do")
	Observable<Msg<PageInfo<UserMessage>>> getMessageList(@Query("pageSize") String pageSize);

	//关注列表
	@POST("user/getAttentionList.do")
	Observable<Msg<PageInfo<Attention>>> getAttentionList(@Query("pageSize") String pageSize);
	//收藏列表
	@POST("user/getCollectionList.do")
	Observable<Msg<PageInfo<Collection>>> getCollectionList(@Query("pageSize") String pageSize);


	//粉丝列表
	@POST("user/getFansList.do")
	Call<Msg<PageInfo<Funs>>> getFunsList(@Query("pageNum")int pageNum,@Query("pageSize") String pageSize);

}
