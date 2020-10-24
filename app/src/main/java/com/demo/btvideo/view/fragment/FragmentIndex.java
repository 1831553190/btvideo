package com.demo.btvideo.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.IndexAdapter;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Propertys;
import com.demo.btvideo.view.activity.ServerURL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentIndex extends Fragment {

	private static FragmentIndex fragmentIndex;

	public static FragmentIndex getInstance(){
		if (fragmentIndex==null){
			synchronized (FragmentIndex.class){
				if (fragmentIndex==null){
					fragmentIndex=new FragmentIndex();
				}
			}
		}
		return fragmentIndex;
	}

	View mainView;
	@BindView(R.id.index_recycler)
	RecyclerView recyclerView;
	IndexAdapter indexAdapter;
	List<VideoInfo> videoInfos;
	Handler handler=new Handler(Looper.getMainLooper());



	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_main,container,false);
		ButterKnife.bind(this,mainView);
		videoInfos=new ArrayList<>();
		indexAdapter=new IndexAdapter(videoInfos,getContext());
		recyclerView.setAdapter(indexAdapter);
		NetWorkUtils.httpPost(ServerURL.URL_VIDEO_LIST, "", new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				String respString=response.body().string();
				handler.post(()->{
					if (NetWorkUtils.isJson(respString)){
						Msg msg=new Gson().fromJson(respString,Msg.class);
						if (msg.getCode()==200){
							Gson gson=new Gson();
							Type type=new TypeToken<Msg<PageInfo>>(){}.getType();
							Msg<PageInfo> pageInfoMsg= gson.fromJson(respString,type);
							videoInfos.addAll(pageInfoMsg.getData().getList());
							indexAdapter.notifyDataSetChanged();
							Log.d("TAG", "size: "+videoInfos.size());
							Log.d("TAG", "rspSize: "+pageInfoMsg.getData().getList().size());
						}else{
							Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();
						}



					}else{
						Toast.makeText(getContext(), "请求错误", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
		return mainView;
	}
}
