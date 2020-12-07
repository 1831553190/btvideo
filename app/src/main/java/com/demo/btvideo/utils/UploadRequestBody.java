package com.demo.btvideo.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class UploadRequestBody extends RequestBody {
	Long size=null;
	InputStream inputStream;
	MediaType conntentType=null;
	Context context;
	String name;
	UploadProgress uploadProgress;

	int id;

	public interface UploadProgress{
		void getPercent(int id,int percent);
		void getProgress(int id,long progress);
	}

	public long getMaxProgress(){
		if (size!=null){
			return size;
		}
		return -1;
	}


	public UploadRequestBody(int id,Uri uri, MediaType conntentType, Context context,UploadProgress uploadProgress) {
		this.size = size;
		this.id=id;
		this.conntentType = conntentType;
		this.context = context;
		this.uploadProgress=uploadProgress;
		Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
		if (cursor.moveToFirst()){
			name=cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
			String mimeType=cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
			if (mimeType==null){
				mimeType=mimeType.split("/")[1];
				if (!name.equals(mimeType)){
//					name = "$name.$mimeType";
				}
			}
			size=cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
			cursor.close();
		}
		try {
			inputStream=context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Nullable
	@Override
	public MediaType contentType() {
		return conntentType;
	}


	@Override
	public long contentLength() throws IOException {
		return size==null?-1l:size;
	}

	@Override
	public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
		BufferedSink bufferSink = Okio.buffer(wrapSink(bufferedSink));
		bufferSink.writeAll(Okio.source(inputStream));
		bufferSink.flush();
	}

	private Sink wrapSink(Sink sink){
		return new Forware(sink);
	}

	class Forware extends ForwardingSink{
		long contentLength=0l;
		long byteWritten=0l;
		public Forware(@NotNull Sink delegate) {
			super(delegate);
			if (contentLength<=0l){
				contentLength=size==null?-1l:size;
			}
		}

		@Override
		public void write(@NotNull Buffer source, long byteCount) throws IOException {
			super.write(source, byteCount);
			if (contentLength <= 0L) {
				contentLength = size==null?-1L:size;
			}
			byteWritten+=byteCount;
			if (uploadProgress!=null){
				long uploadSize=size==null?-1L :size;
				uploadProgress.getPercent(id,(int) ((byteWritten*1.0f/uploadSize)*100));
				uploadProgress.getProgress(id,byteWritten);
			}
		}
	}

}
