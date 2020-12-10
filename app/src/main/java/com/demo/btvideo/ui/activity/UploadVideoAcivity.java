package com.demo.btvideo.ui.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.UploadService;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.UploadRequestBody;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerManager;
import tcking.github.com.giraffeplayer2.VideoView;


//上传视频的界面
public class UploadVideoAcivity extends AppCompatActivity {


	@BindView(R.id.text_upload_videotitle)
	TextView title;
	@BindView(R.id.upload_video_label)
	TextView label;
	@BindView(R.id.upload_video_description)
	TextView description;
	@BindView(R.id.upload_img_video)
	ImageView videoImage;


	@BindView(R.id.video_preview)
	VideoView videoView;

//	boolean selectImg = false;
//	boolean selectVideo = false;


	@BindView(R.id.upload_text_video_file)
	TextView textVideoChoose;


	String strTitle, strLabel, strDescription;

	Uri imgFile=null;
	Uri videoFile=null;

	Handler handler = new Handler(Looper.getMainLooper());
	UploadService.Binder binder;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_video);
		ButterKnife.bind(this);

		Intent intent=new Intent(this,UploadService.class);
		startService(intent);
		bindService(intent,serviceConnection,BIND_AUTO_CREATE);
	}


	@OnClick(R.id.upload_btn_choosefile)
	public void chooseCover() {
		Intent intent = new Intent(Intent.ACTION_CHOOSER);
//创建相机Intent
		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		captureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//将相机Intent以数组形式放入Intent.EXTRA_INITIAL_INTENTS
		intent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent(captureIntent));
//创建相册Intent
		Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
		albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//将相册Intent放入Intent.EXTRA_INTENT
		intent.putExtra(Intent.EXTRA_INTENT, albumIntent);
		startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 70);
	}


	@OnClick(R.id.upload_btn_videochoose)
	public void chooseVideo() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");  // 选择文件类型
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(Intent.createChooser(intent, "选择视频文件"), 80);
	}

	@OnClick(R.id.btn_upload_video)
	public void uploadVideo() {
		strTitle = title.getText().toString();
		strLabel = label.getText().toString();
		strDescription = description.getText().toString();


		if (strTitle.isEmpty()) {
			Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
		} else if (strLabel.isEmpty()) {
			Toast.makeText(this, "请输入标签", Toast.LENGTH_SHORT).show();
		} else if (strDescription.isEmpty()) {
			Toast.makeText(this, "请输入视频描述", Toast.LENGTH_SHORT).show();
		} else if (imgFile==null) {
			Toast.makeText(this, "请选择视频封面", Toast.LENGTH_SHORT).show();
		} else if (videoFile==null) {
			Toast.makeText(this, "请选择视频文件", Toast.LENGTH_SHORT).show();
		} else {
//			final ProgressDialog progressDialog=new ProgressDialog(this);
//			progressDialog.setTitle("上传中");
//			progressDialog.setMessage("正在上传");
//			progressDialog.setMax(100);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			progressDialog.setIndeterminate(false);
//			progressDialog.show();
//
			HashMap<String, String> dataBody = new HashMap<>();
			dataBody.put("title", strTitle);
			dataBody.put("labels", strLabel);
			dataBody.put("description", strDescription);
			if (binder!=null){
				binder.uploadVideo(imgFile,videoFile,dataBody);
			}
			Toast.makeText(this, "视频上传中,你现在可以在通知栏中查看上传进度", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	ServiceConnection serviceConnection=new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder= (UploadService.Binder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null&&data.getData()!=null){
			if(requestCode==70){
				imgFile=data.getData();
				Glide.with(this)
						.load(imgFile)
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(videoImage);
			}else if (requestCode==80){
				videoFile=data.getData();
				textVideoChoose.setText("预览");
//				textVideoChoose.setText(data.getData().toString());
				videoView.setVisibility(View.VISIBLE);
				videoView.setVideoPath(data.getData());
			}
		}
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		PlayerManager.getInstance().onConfigurationChanged(newConfig);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onBackPressed() {
		if (PlayerManager.getInstance().getCurrentPlayer()!=null&&
				PlayerManager.getInstance().getCurrentPlayer().getDisplayModel() == GiraffePlayer.DISPLAY_FLOAT) {
			PlayerManager.getInstance().onBackPressed();
			finish();
		} else {
			if (!PlayerManager.getInstance().onBackPressed()) {
				try {
					if (videoView.getPlayer().isPlaying()) {
						videoView.getPlayer().release();
					}
				}catch (Exception e){
				}
				super.onBackPressed();
			}else {
				super.onBackPressed();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
}
