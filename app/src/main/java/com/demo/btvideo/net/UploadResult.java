package com.demo.btvideo.net;

import com.demo.btvideo.model.Msg;

public interface UploadResult {
	void failed(Msg<String> msg);
	void success(Msg<String> msg);
}
