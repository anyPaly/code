package com.edgelesschat.friendsloop;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.edgelesschat.BaseActivity;
import com.edgelesschat.R;
import com.edgelesschat.global.FeatureFunction;
import com.edgelesschat.global.GlobalParam;

public class FriendsLoopBgActivity extends BaseActivity implements OnClickListener{
	
	
	 private TextView malbum,mshot; 
	 protected Context mContext;
	 private String mTempFileName = "front_cover.jpg";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_friends_loop_bg);
		initComponent();
	}
	/**
	 * 实例化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.ic_return,0,"更换相册封面");
		mLeftBtn.setOnClickListener(this);
		malbum = (TextView) findViewById(R.id.album);
		malbum.setOnClickListener(this);
		mshot = (TextView) findViewById(R.id.shot);
		mshot.setOnClickListener(this);
		
	}
	
	/*
	 * 从相册中选取
	 */
	private void getImageFromGallery() {
		// 解决的放法有很多，比较简单的就是Intent.ACTION_GET_CONTENT换成Intent.ACTION_PICK
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setType("image/*");
		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}

	/*
	 * 拍一张
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory()
				+ FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory()
					+ FeatureFunction.PUB_TEMP_DIRECTORY, mTempFileName);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent,
					GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn://返回按钮
			FriendsLoopBgActivity.this.finish();
			break;
		case R.id.album://从手机相册选择
			getImageFromGallery();
			break;
		case R.id.shot://拍一张
			getImageFromCamera();
			break;
		   
		default:
			break;
		}
	}
}
