package com.edgelesschat.friendsloop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.edgelesschat.MyAlbumActivity;
import com.edgelesschat.R;
import com.edgelesschat.UserInfoActivity;
import com.edgelesschat.Entity.CommentUser;
import com.edgelesschat.Entity.FriendsLoopItem;
import com.edgelesschat.Entity.Login;
import com.edgelesschat.Entity.MorePicture;
import com.edgelesschat.dialog.MMAlert;
import com.edgelesschat.dialog.MMAlert.OnAlertSelectId;
import com.edgelesschat.global.FeatureFunction;
import com.edgelesschat.global.GlobalParam;
import com.edgelesschat.global.ResearchCommon;
import com.hp.hpl.sparta.Text;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class XListViewHeaderLayout extends LinearLayout implements View.OnClickListener{
	private LinearLayout mContainer;
	private ImageView mImageView;
	private ImageView mImageViewAcatar;
	private TextView mTextViewName;
	private Context mContext;
	private String headSmall;
	private String nickName;
	private String strMyUid;
	private CommentUser user;
	private String headsmall;
	private String nikename;
	private String cover;
	private String mTempFileName = "front_cover.jpg";
	
	private List<MorePicture> mListpic = new ArrayList<MorePicture>();
	

	public XListViewHeaderLayout(Context context) {
		super(context);
		this.mContext = context;
		initView(context);
	}
	
	public XListViewHeaderLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        initView(context);  
    }  

	private void initView(Context context) {
		WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(  
                LayoutParams.MATCH_PARENT, height/2-45); 
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(  
                R.layout.friendsloop_header_layout, null); 
		addView(mContainer, lp);
		strMyUid = ResearchCommon.getUserId(context);
		mImageView = (ImageView) findViewById(R.id.img_background);
		mImageViewAcatar = (ImageView) findViewById(R.id.friendsloop_header_avatar);
		mTextViewName = (TextView) findViewById(R.id.friendsloop_header_name);
		//获取头像和ID
		Login userInfoVo = ResearchCommon.getLoginResult(context);
		user = new CommentUser();
		user.uid = strMyUid;
		nikename = user.nickname = userInfoVo.nickname;
		headsmall = user.headsmall = userInfoVo.headsmall;
		cover = userInfoVo.cover;
		
		mTextViewName.setText(user.nickname);
		mTextViewName.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		
		if((cover != null) && !cover.isEmpty())
		{
			Picasso.with(getContext()).load(cover).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(mImageView);
		}
		
		mImageViewAcatar.setImageResource(R.drawable.contact_default_header);
		if((user.headsmall != null) && !user.headsmall.isEmpty())
		{
			Picasso.with(getContext()).load(user.headsmall).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(mImageViewAcatar);
		}
		//mImageView.setOnClickListener(this);
		mImageViewAcatar.setOnClickListener(this);	
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_background:
			Log.e("LT", "点击背景！！！"+cover);
			break;
		case R.id.friendsloop_header_avatar:
			//朋友圈 个人头像
			Intent albumIntent = new Intent();
            albumIntent.setClass(mContext, MyAlbumActivity.class);
            mContext.startActivity(albumIntent);
//			Login userInfoVo = ResearchCommon.getLoginResult(getContext());
//			
//			Intent userInfoIntent = new Intent();
//			userInfoIntent.setClass(mContext, UserInfoActivity.class);
//			userInfoIntent.putExtra("user",userInfoVo);
//			userInfoIntent.putExtra("type",2);
//			userInfoIntent.putExtra("uid", strMyUid);
//			mContext.startActivity(userInfoIntent);
//			Log.e("LT", "点击头像！！！"+headsmall);
			break;
		default:
			break;
		}
	}
}
