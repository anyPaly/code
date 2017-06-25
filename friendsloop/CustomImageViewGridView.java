package com.edgelesschat.friendsloop;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

//���Ч����ImgView
public class CustomImageViewGridView extends ImageView {
	private String url;
	private boolean isAttachedToWindow = false;

	public CustomImageViewGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomImageViewGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomImageViewGridView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Drawable drawable = getDrawable();
			if (drawable != null) {
				drawable.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			Drawable drawableUp = getDrawable();
			if (drawableUp != null) {
				drawableUp.mutate().clearColorFilter();
			}
			break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	public void onAttachedToWindow() {
		isAttachedToWindow = true;
		if (!TextUtils.isEmpty(url)) {
			if (isAttachedToWindow) {
				Picasso.with(getContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5")))
						.into(this);
			}
		}
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		Picasso.with(getContext()).cancelRequest(this);
		isAttachedToWindow = false;
		setImageBitmap(null);
		super.onDetachedFromWindow();
	}

	public void setImageUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			this.url = url;
		}
	}

	public String getUrl() {
		return url;
	}
}
