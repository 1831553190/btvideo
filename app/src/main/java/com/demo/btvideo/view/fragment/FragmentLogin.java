package com.demo.btvideo.view.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.AuthData;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.statement.StateLogin;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Propertys;
import com.demo.btvideo.utils.ServerURL;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentLogin extends Fragment {

	View mainView;
	@BindView(R.id.username)
	EditText edituser;
	@BindView(R.id.password)
	EditText editpasswd;
	@BindView(R.id.remPasswd)
	CheckBox checkBox;


	private String username;
	private String password;
	SharedPreferences preferences;
	Handler handler = new Handler(Looper.getMainLooper());

	ExecutorService executorService = Executors.newCachedThreadPool();
	AppDatabase appDatabase;


	private static class Holder {
		private static FragmentLogin instance = new FragmentLogin();
	}

	public static FragmentLogin getInstance() {
		return FragmentLogin.Holder.instance;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_login,container,false);
		ButterKnife.bind(this,mainView);
		appDatabase = Room.databaseBuilder(getContext(),AppDatabase.class,"user").fallbackToDestructiveMigration().build();

		preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		checkBox.setChecked(preferences.getBoolean("isrem", false));
		if (checkBox.isChecked()) {
			edituser.setText(preferences.getString("username", ""));
			editpasswd.setText(preferences.getString("passwd", ""));
			Log.d("password", "onCreate: " + password);
		}
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			preferences.edit().putBoolean("isrem", isChecked).apply();
			if (!isChecked) {
				preferences.edit().putString("username", "").apply();
				preferences.edit().putString("passwd", "").apply();
			}
		});

		return mainView;
	}

	@OnClick(R.id.btnUserLogin)
	public void login(View view) {
		username = edituser.getText().toString();
		password = editpasswd.getText().toString();
		if (username.equals("") || password.equals("")) {
			Snackbar.make(view.findViewById(android.R.id.content), "请输入完整内容！", Snackbar.LENGTH_SHORT).show();
			return;
		}
		view.setEnabled(false);
		final ProgressDialog progressDialog= new ProgressDialog(getContext());
		progressDialog.setCancelable(true);
		progressDialog.setTitle("登陆中...");
		progressDialog.show();
		HashMap<String,String> data=new HashMap<>();
		data.put(Propertys.USERNAME,username);
		data.put(Propertys.PASSWORD,password);

		Observable.just(data)
				.subscribeOn(Schedulers.io())
				.map(new Function<HashMap<String, String>, Msg<AuthData>>() {
					@Override
					public Msg<AuthData> apply(HashMap<String, String> stringStringHashMap) throws Exception {
						Retrofit retrofit=new Retrofit.Builder()
								.addConverterFactory(GsonConverterFactory.create())
								.baseUrl(ServerURL.MAIN_URL)
								.build();
						NetInterface netInterface=retrofit.create(NetInterface.class);
						Response<Msg<AuthData>> response=netInterface.login(stringStringHashMap).execute();
						if (response.isSuccessful()&&response.body()!=null&&response.body().getCode()==200){
							return response.body();
						}else if (response.body()!=null){
							throw Exceptions.propagate(new Throwable(response.body().getMessage()));
						}else{
							throw Exceptions.propagate(new Throwable("未知错误"));
						}
					}
				}).map(new Function<Msg<AuthData>, User>() {
			@Override
			public User apply(Msg<AuthData> authDataMsg) throws Exception {
				String auth=authDataMsg.getData().getToken();
				preferences.edit().putString("token",auth).putString("tokenHead",authDataMsg.getData().getTokenHead()).apply();
				AppController.getInstance().updateAuth(auth);
				Retrofit retrofit=new Retrofit.Builder()
						.addConverterFactory(GsonConverterFactory.create())
						.baseUrl(ServerURL.MAIN_URL)
						.build();
				NetInterface netInterface=retrofit.create(NetInterface.class);
				Response<Msg<User>> response=netInterface.getUserInfo("Bearer "+auth).execute();
				if (response.isSuccessful()&&response.body()!=null){
					return response.body().getData();
				}else {
					Exceptions.propagate(new Throwable("未知错误"));
				}
				return null;
			}
		}).subscribe(new Observer<User>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(User user) {
				LoginViewModel.getInstance().userLiveData().postValue(user);
			}

			@Override
			public void onError(Throwable e) {
				handler.post(()->{
					progressDialog.dismiss();
					view.setEnabled(true);
					Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
//					Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				});

			}

			@Override
			public void onComplete() {
				if (checkBox.isChecked()) {
					preferences.edit().putString("username", username).apply();
					preferences.edit().putString("passwd", password).apply();
				}
				AppController.getInstance().setLogin(new StateLogin());
				handler.post(()->{
					progressDialog.dismiss();
					view.setEnabled(true);
					Toast.makeText(getContext(),"登录成功!", Toast.LENGTH_SHORT).show();
					getActivity().finish();
				});
			}
		});

//		Retrofit retrofit=NetWorkUtils.getRetrofit();
//		NetInterface netInterface=retrofit.create(NetInterface.class);
//		netInterface.login(data).enqueue(new retrofit2.Callback<Msg<AuthData>>() {
//			@Override
//			public void onResponse(retrofit2.Call<Msg<AuthData>> call, retrofit2.Response<Msg<AuthData>> response) {
//
//				if (response.isSuccessful()){
//					Msg<AuthData> msg = response.body();
//					assert msg != null;
//					if (msg.getCode() == 200) {
//						if (checkBox.isChecked()) {
//							preferences.edit().putString("username", username).apply();
//							preferences.edit().putString("passwd", password).apply();
//						}
////						preferences.edit().putString("token",msg.getData());
//						handler.post(() -> {
//							preferences.edit().putString("token",msg.getData().getToken()).putString("tokenHead",msg.getData().getTokenHead()).apply();
//							AppController.getInstance().setLogin(new StateLogin());
//							AppController.getInstance().updateAuth(msg.getData().getToken());
//							LoginViewModel.getInstance().update().setValue(200);
//							progressDialog.dismiss();
//							view.setEnabled(true);
//							Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();
//							getActivity().finish();
//						});
//					} else {
//						handler.post(()->{
//							progressDialog.dismiss();
//							view.setEnabled(true);
//							Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();
//						});
//					}
//				} else {
//					handler.post(()->{
//						Toast.makeText(getContext(), "服务器响应错误,请稍后重试", Toast.LENGTH_SHORT).show();
//						view.setEnabled(true);
//						progressDialog.dismiss();
//					});
//				}
//
//
//			}
//
//			@Override
//			public void onFailure(retrofit2.Call<Msg<AuthData>> call, Throwable t) {
//				handler.post(() -> {
//					Snackbar.make(view.findViewById(android.R.id.content), "连接失败，请检查网络连接。", Snackbar.LENGTH_SHORT).show();
//					progressDialog.dismiss();
//					view.setEnabled(true);
//				});
//			}
//		});

	}

	@OnClick(R.id.btnUserRegister)
	public void toRegister(){
		ViewModelProviders.of(getActivity()).get(DataViewModel.class).update().setValue(0);
	}
}