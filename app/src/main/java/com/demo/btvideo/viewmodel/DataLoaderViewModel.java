package com.demo.btvideo.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.demo.btvideo.data.AttentionDataSource;
import com.demo.btvideo.data.BaseDataSource;
import com.demo.btvideo.data.CollectionDataSource;
import com.demo.btvideo.data.FunsDataSource;
import com.demo.btvideo.data.HistoryPlayList;
import com.demo.btvideo.data.LoadMessageList;
import com.demo.btvideo.data.LoadOtherList;
import com.demo.btvideo.data.LoadUploadList;
import com.demo.btvideo.data.LoadVideoInfo;
import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Funs;
import com.demo.btvideo.model.UserMessage;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.data.ExecutorBacken;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.Executors;

import kotlinx.coroutines.CoroutineScope;

//加载分页数据的类
public class DataLoaderViewModel extends ViewModel {



	MutableLiveData<PagingData<VideoInfo>> dataMutableLiveData=new MutableLiveData<>();
	PagingConfig pagingConfig=new PagingConfig(20,20,false,20);//初始化配置
	public ListeningExecutorService executorService= MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());


	public LiveData<PagingData<VideoInfo>> getPaging(){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, VideoInfo> pager = new Pager<Integer,VideoInfo>(pagingConfig, ()->new BaseDataSource<>(new LoadVideoInfo(executorService)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}


	public LiveData<PagingData<Comment>> getCommnet(int objId){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, Comment> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new ExecutorBacken(executorService, objId)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);

	}


	public LiveData<PagingData<UserMessage>> getMessage(){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, UserMessage> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new LoadMessageList(executorService)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}

	public LiveData<PagingData<Collection>> getCollections(){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, Collection> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new CollectionDataSource(executorService)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
	public LiveData<PagingData<Attention>> getAttention(){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, Attention> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new AttentionDataSource(executorService)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
	//获取粉丝数据
	public LiveData<PagingData<Funs>> loadFunsData(){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, Funs> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new FunsDataSource(executorService)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
	public LiveData<PagingData<VideoInfo>> getUploadVideo(String userAcccount){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, VideoInfo> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new LoadUploadList(executorService,userAcccount)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
	public LiveData<PagingData<VideoInfo>> getUploadVideoT(String title){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, VideoInfo> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new LoadOtherList(executorService,title,"title")));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
	public LiveData<PagingData<VideoInfo>> getUploadVideoL(String labels){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, VideoInfo> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new LoadOtherList(executorService,labels,"labels")));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
	public LiveData<PagingData<VideoInfo>> getUploadVideoD(String descri){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, VideoInfo> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new LoadOtherList(executorService,descri,"descri")));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}

	public LiveData<PagingData<VideoInfo>> getPlayHistory(Context context){
		CoroutineScope viewmodelScope= ViewModelKt.getViewModelScope(this);
		Pager<Integer, VideoInfo> pager = new Pager<>(pagingConfig, () ->
				new BaseDataSource<>(new HistoryPlayList(executorService,context)));
		return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewmodelScope);
	}
}
