package com.edgelesschat.friendsloop;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.edgelesschat.R;
import com.edgelesschat.global.FeatureFunction;
import com.edgelesschat.global.ImageLoader;
import com.squareup.picasso.Picasso;

public class NoScrollGridAdapter extends BaseAdapter {

	/** 上下文 */
	private Context ctx;
	/** 图片Url集合 */
	private ArrayList<String> imageUrls;
	private ImageView ImageViewFront;

	private ImageLoader imageLoader;
	private int clickTemp = -1;

	public void setSeclection(int position) {
		clickTemp = position;
	}

	public NoScrollGridAdapter(Context ctx, ArrayList<String> urls) {
		this.ctx = ctx;
		this.imageUrls = urls;
		imageLoader = new ImageLoader();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageUrls == null ? 0 : imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return imageUrls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(ctx).inflate(R.layout.friendsloop_item_gridview, null);
		ImageViewFront = (ImageView)view.findViewById(R.id.iv_image_background);
		ImageView mImageView = (ImageView) view.findViewById(R.id.iv_image);
		android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mImageView.getLayoutParams();
		mImageView.setLayoutParams(params);
        ImageViewFront.setLayoutParams(params);
//		imageLoader.getBitmap(this.ctx, mImageView, null, imageUrls.get(position), 0, true, false);
		/*
		 * DisplayImageOptions options = new DisplayImageOptions.Builder()//设置
		 * .cacheInMemory(true)//内存 .cacheOnDisk(true)//硬盘
		 * .bitmapConfig(Config.RGB_565)//最低配置 .build();
		 * ImageLoader.getInstance().displayImage(imageUrls.get(position),
		 * imageView, options);
		 */
		imageLoader.getBitmap(this.ctx, mImageView, null, imageUrls.get(position), 0, true, false);
//		view.setOnTouchListener(onTouchListener);
		return view;
	}
}
