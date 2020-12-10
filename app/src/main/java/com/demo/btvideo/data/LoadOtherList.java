package com.demo.btvideo.data;

import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.LoadDataInterface;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;


//从网络获取上传记录数据
public class LoadOtherList implements LoadDataInterface<VideoInfo> {

    ListeningExecutorService executorService;
    String text;
    String type;

    public LoadOtherList(ListeningExecutorService executorService, String account,String type) {
        this.executorService=executorService;
        this.text =account;
        this.type=type;
    }

    @Override
    public ListenableFuture<List<VideoInfo>> load(int pageNum) {
        return executorService.submit(() -> {
            Retrofit retrofit = NetWorkUtils.getRetrofit();
            NetInterface netInterface = retrofit.create(NetInterface.class);
            Response<Msg<PageInfo<VideoInfo>>> infos = null;
            if (type.equals("title")){
                infos = netInterface.getUploadListByTitle(text,pageNum,20).execute();
            }else if (type.equals("labels")){
                infos = netInterface.getUploadListByLabel(text,pageNum,20).execute();
            }else{
                infos=netInterface.getUploadListByDescription(text,pageNum,20).execute();
            }
            if (infos.body() != null&&infos.body().getCode()==200) {
                PageInfo<VideoInfo> datas=infos.body().getData();
//                    JsonElement element=infos.body().getData();
//                    PageInfo<Comment> data=new Gson().fromJson(element,new TypeToken<PageInfo<Comment>>(){}.getType());
                return datas.getList();
            } else {
                return new ArrayList<>();
            }
        });
    }




}
