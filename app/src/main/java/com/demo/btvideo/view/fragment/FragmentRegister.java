package com.demo.btvideo.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.demo.btvideo.R;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Propertys;
import com.demo.btvideo.view.activity.ServerURL;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentRegister extends Fragment {

	View mainView;
	private static class Holder {
		private static FragmentRegister instance=new FragmentRegister();
	}

	@BindView(R.id.reg_username)
	EditText regUserName;
	@BindView(R.id.reg_password)
	EditText regPassWord;
	@BindView(R.id.repeatPassword)
	EditText regPassWordRep;
	@BindView(R.id.reg_editphone)
	EditText regEditPhone;
	@BindView(R.id.reg_editmail)
	EditText regEditMail;

	Handler handler=new Handler(Looper.getMainLooper());
	ProgressDialog progressDialog;


	public static FragmentRegister getInstance(){
		return Holder.instance;
	}
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_register,container,false);
		ButterKnife.bind(this,mainView);
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setCancelable(true);
		return mainView;
	}

	@OnClick(R.id.btnUserRegister)
	public void onRegisterClick(View view){
		String username=regUserName.getText().toString();
		String password=regUserName.getText().toString();
		String pwdrep=regPassWordRep.getText().toString();
		String mail=regEditMail.getText().toString();
		String phone=regEditPhone.getText().toString();

		if (username.equals("")){
			Toast.makeText(getContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
		}else if (password.equals("")){
			Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
		}else if(!password.equals(pwdrep)){
			Toast.makeText(getContext(), "两次输入的密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
		} else{
			HashMap<String,String> data=new HashMap<>();
			data.put(Propertys.USERNAME,username);
			data.put(Propertys.PASSWORD,password);
			data.put(Propertys.MAIL,mail);
			data.put(Propertys.PHONE,phone);
			String dataString=new Gson().toJson(data);
			view.setEnabled(false);
			progressDialog.setTitle("登陆中...");
			progressDialog.show();
			NetWorkUtils.httpPost(ServerURL.REGISTER_URL, dataString, new Callback() {
				@Override
				public void onFailure(@NotNull Call call, @NotNull IOException e) {
					e.printStackTrace();
					handler.post(()->{
						Toast.makeText(getContext(), "注册失败,请检查网络连接", Toast.LENGTH_SHORT).show();
						progressDialog.dismiss();
					});
				}

				@Override
				public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
					String respContent=response.body().string();
					if (NetWorkUtils.isJson(respContent)) {
						Msg msg = new Gson().fromJson(respContent, Msg.class);
						if (msg.getCode() == 200) {
							handler.post(() -> {
								progressDialog.dismiss();
								view.setEnabled(true);
								ViewModelProviders.of(getActivity()).get(DataViewModel.class).update().setValue(1);
								Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();
							});
						} else {
							handler.post(()->{
								progressDialog.dismiss();
								view.setEnabled(true);
								Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();
//								Snackbar.make(view.findViewById(android.R.id.content), msg.getMessage(), Snackbar.LENGTH_LONG).show();
							});
						}
					} else {
						handler.post(()->{
							Toast.makeText(getContext(), "服务器响应错误,请稍后重试", Toast.LENGTH_SHORT).show();

//							Snackbar.make(view.findViewById(android.R.id.content), "服务器响应错误,请稍后重试", Snackbar.LENGTH_LONG).show();
							view.setEnabled(true);
							progressDialog.dismiss();
						});
					}

				}
			});
		}
	}

	@OnClick(R.id.btnBack)
	public void backToLogin(){
		ViewModelProviders.of(getActivity()).get(DataViewModel.class).update().setValue(1);
	}
}
