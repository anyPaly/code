package com.edgelesschat.friendsloop;

import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ClipData.Item;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.speech.tts.Voice;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.edgelesschat.DB.DBHelper;
import com.edgelesschat.DB.UserTable;
import com.edgelesschat.Entity.CommentUser;
import com.edgelesschat.Entity.FriendsLoopItem;
import com.edgelesschat.Entity.Login;
import com.edgelesschat.Entity.MessageType;
import com.edgelesschat.Entity.MovingContent;
import com.edgelesschat.Entity.MovingPic;
import com.edgelesschat.Entity.NotifiyVo;
import com.edgelesschat.Entity.ResearchJiaState;
import com.edgelesschat.fragment.FoundFragment;
import com.edgelesschat.global.FeatureFunction;
import com.edgelesschat.global.GlobalParam;
import com.edgelesschat.global.ImageLoader;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.net.ResearchException;
import com.edgelesschat.smallvideo.SmallVideoPalyActivity;
import com.hp.hpl.sparta.Text;
import com.edgelesschat.ChatMainActivity;
import com.edgelesschat.FriensLoopActivity;
import com.edgelesschat.R;
import com.edgelesschat.UserInfoActivity;
import com.edgelesschat.R.drawable;
import com.edgelesschat.adapter.EmojiUtil;
import com.edgelesschat.ShowMultiImageActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;








import java.net.URLDecoder;
import java.security.PublicKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.http.RequestParams;
import org.xutils.image.GifDrawable;

public class FriendsLoopItemView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener{
	private int mPosition;
	private FriendsLoopItem mData;
	private XListViewHeaderLayout xListViewHeaderLayout;
	
	private Context mContext;
	public static final String FRIENDSLOOP_PRAISE_CLICKED_NOTIFY = "friendsloop_praise_clicked_notify";
	public static final String FRIENDSLOOP_COMMENT_CLICKED_NOTIFY = "friendsloop_comment_clicked_notify";
	public static final String FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY = "friendsloop_comment_replyother_notify";
	public static final String FRIENDSLOOP_COMMENT_REPLYDElETE_NOTIFY = "friendsloop_comment_replydelete_notify";
	public static final String FRIENDSLOOP_CONTENT_SHAREDELETE_NOTIFY = "friensloop_content_sharedelete_notify";
	public static final String FRIENDSLOOP_CONTENT_FAVORITE_NOTIFY = "friends_content_favorite_notify";
	
	public static final String EMOJIREX = "emoji_[\\d]{0,3}";// emoji_[\\d]{0,3}
	private CustomImageView mPortraitView;
	private TextView mUserNameView;
	private TextView mContentView;
	private TextView mContentViewAll;
	private TextView mContentViewPut;
	private TextView mCreatedAtView;
	private TextView mDeleteView;
	private LinearLayout mCommentLayout;
	private LinearLayout mLikeLayout;
	private NoScrollGridView mGridView;
	private ImageView mVideoView;
	private ImageView mImageView;
	
	private ImageView mImageViewAcatar;
	
	//private TextView mLikeTxt;
	private TextView mLike;
	private TextView mComment;
	private View mMoreView;
	private TextView mTvDivideLine;
	private String strShareId;
	private String strToUid;
	private String strContent;
	private String strMyUid;
	private String mToUserId;
	private RelativeLayout mExtraArrowLayout;
//	private PopupWindow mMorePopupWindow;
//	private ImageButton mPraise;
//	private ImageButton mContent;
	
	private ImageView mPraise;
	private ImageView mContent;
	private int mShowMorePopupWindowWidth;
	private int mShowMorePopupWindowHeight;
	private int mPraiseClicks;
	private boolean mClicksContent = false;
	private boolean onLongClicked,onLongClickedHead;

	final ArrayList<String> imageUrls = new ArrayList<String>();
	final ArrayList<String> originUrls = new ArrayList<String>();
	final ArrayList<String> heights = new ArrayList<String>();
	final ArrayList<String> widths = new ArrayList<String>();
	  
	private OnCommentListener mCommentListener;
	private OnLikeListener mLikeListener;
	protected AlertDialog mUpgradeNotifyDialog;
	private ChatMainActivity mChatMainActivity = new ChatMainActivity();
	
	public FriendsLoopItemView(Context context) {
		super(context);
		mContext = context;
	}

	public FriendsLoopItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public interface OnCommentListener {
//		void onComment(int position,View...views);
		void onComment(int position);
	}
	
	public interface OnLikeListener{
		void onLike(int position);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mPortraitView = (CustomImageView) findViewById(R.id.portrait);
		mUserNameView = (TextView) findViewById(R.id.nick_name);
		mContentView = (TextView) findViewById(R.id.content);
		mContentViewAll = (TextView) findViewById(R.id.content_all);
		mContentViewPut = (TextView) findViewById(R.id.content_put);
		mCreatedAtView = (TextView) findViewById(R.id.created_at);
		mDeleteView = (TextView) findViewById(R.id.content_delete);
		mCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
		mLikeLayout = (LinearLayout) findViewById(R.id.like_layout);
		mGridView = (NoScrollGridView) findViewById(R.id.gridview);
		mVideoView=(ImageView)findViewById(R.id.video_image);
		mImageView = (CustomImageView) findViewById(R.id.iv_single);
//		mMoreView = findViewById(R.id.more_btn);
//		mPraise = (ImageButton) findViewById(R.id.comment_praise_btn);
//		mContent = (ImageButton) findViewById(R.id.comment_content_btn);
		mPraise = (ImageView) findViewById(R.id.comment_praise_btn);
		mContent = (ImageView) findViewById(R.id.comment_content_btn);
		mTvDivideLine = (TextView)findViewById(R.id.tv_divide_line);
		mExtraArrowLayout = (RelativeLayout)findViewById(R.id.extra_arrow_layout);
	}

	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public void setCommentListener(OnCommentListener l) {
		this.mCommentListener = l;
	}
	
	public void setLikeListener(OnLikeListener li){
		this.mLikeListener = li;
	}

	public void setData(FriendsLoopItem data) {

		mData = data;
		mContentView.setVisibility(View.GONE);
		mContentViewAll.setVisibility(View.GONE);
		mVideoView.setVisibility(View.GONE);
		mImageView.setVisibility(View.GONE);
		mClicksContent =  false;
		mContentViewPut.setText("全文");
		mContentViewPut.setVisibility(View.GONE);
		mDeleteView.setVisibility(View.GONE);
		strShareId = mData.id+"";
		strToUid = mData.uid;
		strContent = mData.content;
		strMyUid = ResearchCommon.getUserId(mContext);
		if (strToUid.equals(strMyUid)) {
			mDeleteView.setVisibility(View.VISIBLE);
		}else{
			mDeleteView.setVisibility(View.GONE);
		}
		mPortraitView.setImageResource(R.drawable.contact_default_header);
		
		if((data.headsmall != null) && !data.headsmall.isEmpty())
		{
			Picasso.with(getContext()).load(data.headsmall).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(mPortraitView);
		}
		
		if(mData.ispraise != 0)
		{
			mPraise.setImageResource(drawable.btn_friendsloop_dianzan_color);
		}
		else
		{
			mPraise.setImageResource(drawable.btn_friendsloop_dianzan3);
		}
		
		mUserNameView.setText(data.nickname);
		//6行TextView和全文TextView的切换
		//设置内容
		if (!strContent.isEmpty()) {
			mContentView.setVisibility(View.VISIBLE);
			mContentView.setText(EmojiUtil.getExpressionString(getContext(), data.content, EMOJIREX));
			
		}else{
			mContentView.setVisibility(View.GONE);
		}
		mContentViewAll.setText(EmojiUtil.getExpressionString(getContext(), data.content, EMOJIREX));
		mContentView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					
					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						//Log.e(TAG, "行数"+mTextView.getLineCount());
						int mContentViewHeight = mContentView.getLineCount();
						if(mContentViewHeight > 6)
						{
							Log.e("LT", mContentView.getLineCount()+"");
							mContentViewPut.setVisibility(View.VISIBLE);
							
							mContentViewPut.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									if (mClicksContent ==  false) {
										mContentViewAll.setVisibility(View.VISIBLE);
										mContentView.setVisibility(View.GONE);
										mContentViewPut.setText("收起");
										mClicksContent = true;
									}else{
										mContentViewAll.setVisibility(View.GONE);
										mContentView.setVisibility(View.VISIBLE);
										mContentViewPut.setText("全文");
										mClicksContent = false;
									}
								}
							});
							mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}else{
							mContentViewAll.setVisibility(View.GONE);
							mContentViewPut.setVisibility(View.GONE);
							mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
					}
				});
		imageUrls.clear();
		originUrls.clear();
		heights.clear();
		widths.clear();
		
		if(mData.listvid!=null && mData.listpic == null ){
			//视频
			showVideo(mData.listvid.get(0).videoUrl);
		}
		
		if(mData.listvid == null && mData.listpic != null)
		{
			// 图片
			mVideoView.setVisibility(View.GONE);
			for (int i = 0; i < mData.listpic.size(); i++) {
				imageUrls.add(mData.listpic.get(i).smallUrl);
				originUrls.add(mData.listpic.get(i).originUrl);
				heights.add(mData.listpic.get(i).height+"");
				widths.add(mData.listpic.get(i).width+"");
			}
		}

			// 单图显示
			singelDisplay();
			// 设置4格gridView的显示效果
			if (imageUrls.size() == 4) {
				mGridView.setNumColumns(2);
				mGridView.setPadding(0, 5, 0, 0);
				int nWidth = FeatureFunction.dip2px(mContext, 180);
				LinearLayout.LayoutParams linearParams = new LayoutParams(
						nWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
				linearParams.setMargins(0, 10, 0, 0);
				mGridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
			} else {
				mGridView.setNumColumns(3);
				mGridView.setPadding(0, 5, 0, 0);
				int nWidth = FeatureFunction.dip2px(mContext, 270);
				LinearLayout.LayoutParams linearParams = new LayoutParams(
						nWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
				linearParams.setMargins(0, 10, 0, 0);
				mGridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
			}
		
		// 数据
		updateComment();
		
		//事件监听
		mContentView.setOnLongClickListener(this);
		mContentViewAll.setOnLongClickListener(this);
		mPortraitView.setOnClickListener(this);
		mUserNameView.setOnClickListener(this);
		mDeleteView.setOnClickListener(this);
//		mMoreView.setOnClickListener(this);
		mContent.setOnClickListener(this);
		mPraise.setOnClickListener(this);
		// 照片查看器
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));//点击背景为transparent
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				imageBrower(position);
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				showAlertDialogFavorite(imageUrls,originUrls,position,mData.uid);
				return true;
			}
		});
	}

	//视频显示
	private void showVideo(final String videoUrl){
		mGridView.setVisibility(View.GONE);
		mVideoView.setVisibility(View.VISIBLE);
		mVideoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SmallVideoPalyActivity.class);
				if(videoUrl==null||videoUrl.equals(""))
				{
					return;
				}
//				intent.putExtra("sendstate", messageInfo.sendState);
				intent.putExtra("type", 1);
				intent.putExtra("videoUrl", videoUrl);
				mContext.startActivity(intent);
			}
		});
	}
	
	// 单图显示
	private void singelDisplay() {
		// 设置单独显示
		if (imageUrls.size() == 1) {
			
			mImageView.setVisibility(View.VISIBLE);
			mGridView.setVisibility(View.GONE);
			String url = originUrls.get(0);
			mImageView.setClickable(true);
//			int height = Integer.parseInt(heights.get(0));
//			int width = Integer.parseInt(widths.get(0));
			int height = Integer.parseInt(heights.get(0));
			//限制大小
//			if(height>960){
//				height=height/2;
//			}
			int width = Integer.parseInt(widths.get(0));
			if(width>960){
				height=height*960/width;
				width=width*960/width;
			}
//			if(width>960){
//				width=width/2;
//			}
			setImageViewParams(mImageView, height, width);
			// 第三方框架Picasso缩放
			//Picasso.with(getContext()).load(url).placeholder(/*new ColorDrawable(Color.parseColor("#f5f5f5"))*/Color.rgb(235, 235, 235))
			//.transform(new CropSquareTransformation()).noFade().into(mImageView);
			Picasso.with(getContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#ebebeb"))).into(mImageView);
			Log.d("lt", "图片url="+url);
			//mImageLoader.getBitmap(getContext(), mImageView, null, url, 0, false,true);
			
			mImageView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					imageBrower(0);
				}
			});
			mImageView.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					showAlertDialogFavorite(imageUrls,originUrls,0,mData.uid);
					return true;
				}
			});
		} else {// 多图显示
			mImageView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			mGridView.setAdapter(new NoScrollGridAdapter(getContext(), imageUrls));
		}
	}
	
	// 设置单图尺寸
	private void setImageViewParams(ImageView imageView,int height, int width){
		ViewGroup.LayoutParams vgl = imageView.getLayoutParams();
		vgl.height = height/2;
		vgl.width = width/2;
		imageView.setLayoutParams(vgl);
	}

	// 解决网络图片宽高适配问题
	private class CropSquareTransformation implements Transformation {
		@Override
		public Bitmap transform(Bitmap source) 
		{

			int targetWidth = 600;
			int targetHeight = 600;
			if (source.getWidth() == 0 || source.getHeight() == 0) 
			{
				return source;
			}
			if (source.getWidth() > source.getHeight()) {// 横向长图
				if (source.getHeight() < targetHeight && source.getWidth() <= 600) {
					return source;
				} else {
					// 如果图片大小大于等于设置的高度，则按照设置的高度比例来缩放
					double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
					int width = (int) (targetHeight * aspectRatio);
					if (width > 600) { // 对横向长图的宽度 进行二次限制
						width = 600;
						targetHeight = (int) (width / aspectRatio);// 根据二次限制的宽度，计算最终高度
					}
					if (width != 0 && targetHeight != 0) {
						Bitmap result = Bitmap.createScaledBitmap(source, width, targetHeight, false);
						if (result != source) {
							// Same bitmap is returned if sizes are the same
							source.recycle();
						}
						return result;
					} else {
						return source;
					}
				}
			} else {// 竖向长图
				// 如果图片小于设置的宽度，则返回原图
				if (source.getWidth() < targetWidth && source.getHeight() <= 700) {
					return source;
				} else {
					// 如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
					double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
					int height = (int) (targetWidth * aspectRatio);
					if (height > 700) {// 对横向长图的高度进行二次限制
						height = 700;
						targetWidth = (int) (height / aspectRatio);// 根据二次限制的高度，计算最终宽度
					}
					if (height != 0 && targetWidth != 0) {
						Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, height, false);
						if (result != source) {
							// Same bitmap is returned if sizes are the same
							source.recycle();
						}
						return result;
					} else {
						return source;
					}
				}
			}
		}

		@Override
		public String key() {
			return "desiredWidth" + " desiredHeight";
		}
	}

	// 查看图片
	protected void imageBrower(int position) {
			Intent intent = new Intent(mContext, ShowMultiImageActivity.class);
	        intent.putExtra("share", mData);
	        intent.putExtra("hide", 1);
	        intent.putExtra("pos", position);
	        mContext.startActivity(intent);
//	       弃用
//		Intent intent = new Intent(getContext(), ImagePagerActivity.class);
//		图片url,为了演示这里使用常量，一般从数据库中或网络中获取
//		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
//		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
//		((Activity) getContext()).startActivityForResult(intent, 1);
//		getContext().startActivity(intent);
	}
	
	

	/**
	 * 弹出点赞和评论框
	 *
	 * @param moreBtnView
	 */
	
	/*未用
	private void showMore(View moreBtnView) {

		if (mMorePopupWindow == null) {

			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View content = li.inflate(R.layout.layout_friendsloop_x_more, null, false);

			mMorePopupWindow = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mMorePopupWindow.setOutsideTouchable(true);
			mMorePopupWindow.setTouchable(true);

			content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			mShowMorePopupWindowWidth = content.getMeasuredWidth();
			mShowMorePopupWindowHeight = content.getMeasuredHeight();

			View parent = mMorePopupWindow.getContentView();
			mLike = (TextView) parent.findViewById(R.id.like);
			mComment = (TextView) parent.findViewById(R.id.comment);
			mLike.setOnClickListener(this);
			mComment.setOnClickListener(this);
		}

		if (mMorePopupWindow.isShowing()) {
			mMorePopupWindow.dismiss();
		} else {
			int heightMoreBtnView = moreBtnView.getHeight();

			mMorePopupWindow.showAsDropDown(moreBtnView, -mShowMorePopupWindowWidth,
					-(mShowMorePopupWindowHeight + heightMoreBtnView) / 2);
		}
	}*/
	
	// 评论2
	private void updateComment() {
		
		mLikeLayout.setVisibility(View.VISIBLE);
		mCommentLayout.setVisibility(View.VISIBLE);
		mTvDivideLine.setVisibility(View.GONE);
		mExtraArrowLayout.setVisibility(View.GONE);
		boolean bShowExtraArrow = false;
		//Log.v("TAG", mData.praiselist.size()+"");
		if((mData.praiselist != null) && mData.praiselist.size() >= 1 )
		{
			bShowExtraArrow = true;
			mLikeLayout.removeAllViews();
			mLikeLayout.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
			LinearLayout  horizLL = new LinearLayout(mContext);
			horizLL.setOrientation(LinearLayout.HORIZONTAL);
			horizLL.setGravity(Gravity.CENTER_VERTICAL);
			horizLL.setLayoutParams(layoutParams);
			mLikeLayout.addView(horizLL);
			int nItem = 0;
			
			ImageView iv = new ImageView(getContext());
			iv.setLayoutParams(new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
					FeatureFunction.dip2px(mContext, 20),ViewGroup.LayoutParams.MATCH_PARENT)));
			iv.setImageResource(drawable.friendsloop_praiselist_background);
			iv.setPadding(5, 0, 5, 0);
			horizLL.addView(iv);
			
			for (CommentUser c : mData.praiselist) 
			{
				//for(int i = 0; i < 10; i++)
				{
					nItem++;
					
					TextView t2 = new TextView(getContext());
					t2.setLayoutParams(new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
					t2.setTextColor(Color.rgb(51, 75, 127));
					t2.setTextSize(14);
					t2.setPadding(3, 0, 0, 3);
					t2.setText(c.nickname);
					t2.setBackgroundResource(drawable.friendsloop_bg_textview);
					t2.setGravity(Gravity.CENTER_VERTICAL);
					horizLL.addView(t2);
					final String strUserId =  c.uid;
					

					
					t2.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View view) {
			                 SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			                 UserTable table = new UserTable(db);
			                 Login login = table.query(strUserId);
		                   
			                 if(login==null){
			                	 login=new Login();
			                	 login.uid=mData.uid;
			                	 login.nickname=mData.nickname;
			                	 login.headsmall=mData.headsmall;
			                	 login.sign = "";
			                 }
			                 
			                 if (login != null) {
			                	 Intent userInfoIntent = new Intent();
			                	 userInfoIntent.setClass(mContext, UserInfoActivity.class);
			                	 userInfoIntent.putExtra("user",login);
			                	 userInfoIntent.putExtra("type",2);
			                	 userInfoIntent.putExtra("uid", strUserId);
			                	 mContext.startActivity(userInfoIntent);
			                	 Log.v("cgj", strUserId);
			                 } else {
			                	 Log.v("cgj", strUserId + " search from UserInfoActivity return null.");
			                 }

						}
						
					});
					//nTextSpace += t2.getWidth();
					if(nItem < mData.praiselist.size())
					{
						//nTextSpace = 0;
						TextView t3 = new TextView(getContext());
						t3.setLayoutParams(new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
						t3.setTextColor(Color.rgb(51, 75, 127));
						t3.setTextSize(14);
						t3.setPadding(3, 0, 0, 3);
						t3.setLineSpacing(3, (float) 0.8);
						t3.setText(",");
						horizLL.addView(t3);
						//nTextSpace += t3.getWidth();
					}
					/*if(nTextSpace >= containerWidth)
					{
						horizLL = new LinearLayout(mContext);
						horizLL.setOrientation(LinearLayout.HORIZONTAL);
						horizLL.setLayoutParams(layoutParams);
						mLikeLayout.addView(horizLL);
					}*/
				}
			}
		}
		else{
			mLikeLayout.setVisibility(View.GONE);
		}
		
		if ((mData.replylist != null) && (mData.replylist.size() >= 1)) {
			
			bShowExtraArrow = true; 
			if((mData.praiselist != null) && mData.praiselist.size() >= 1 )
			{
				mTvDivideLine.setVisibility(View.VISIBLE);
			}
			else
			{
				mTvDivideLine.setVisibility(View.GONE);
			}
			mCreatedAtView.setText(FeatureFunction.calculaterFriendsLoopTime(mContext, new Date((mData.createtime*1000)),mData.createtime*1000));
			mCommentLayout.removeAllViews();
			mCommentLayout.setVisibility(View.VISIBLE);
			
			for (CommentUser c : mData.replylist) {
				final String strFid = Integer.toString(mData.id);
				final String strShareUid = mData.uid;
				final int intFidReply = c.id;
				final String strUserId = c.uid;
				final String strNickname = c.nickname;
				final String tString = EmojiUtil.getExpressionString(getContext(), c.content, EMOJIREX)+"";//复制的内容
				TextView t = new TextView(getContext());
				t.setLayoutParams(new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			
				t.setTextSize(14);
				t.setPadding(3, 4, 3, 4);
				//行间距导致文字垂直不对齐
//				t.setLineSpacing(3, (float) 0.8);
				//设置选择器色selector
				t.setBackgroundResource(drawable.friendsloop_bg_textview);//设置背景选择器
				t.setGravity(Gravity.CENTER_VERTICAL);//文字居中
				//t.setTextIsSelectable(true);//双击复制文字
				String strReplyInfo = c.nickname+":"+c.content/*EmojiUtil.getExpressionString(getContext(), c.content, EMOJIREX)*/;
				//strReplyInfo = EmojiUtil.getExpressionString(mContext, strReplyInfo, EMOJIREX);
				int nColorReply = 0;
				int nColorReplyText = 0;
				onLongClicked = false;
				onLongClickedHead = false;
				if(!c.fnickname.isEmpty() && c.replyflag != 0)
				{
					strReplyInfo = c.nickname+"回复"+c.fnickname+":"+EmojiUtil.getExpressionString(getContext(), c.content, EMOJIREX);
					String strTrash = c.nickname + "回复";
					nColorReply = strTrash.length();
				}
				
				
				
				SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(strReplyInfo/*c.nickname+":"+c.content*/);
//				ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(51, 75, 127));
				if (nColorReply==0) {
					spannableStringBuilder.setSpan(new ClickableSpan() {
						
						@Override
						public void onClick(View v) 
						{
							// TODO Auto-generated method stub
							SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
							UserTable table = new UserTable(db);
							Login login = table.query(strUserId);
							if(login==null){
								login=new Login();
								login.uid=mData.uid;
								login.nickname=mData.nickname;
								login.headsmall=mData.headsmall;
								login.sign = "";
							}
							
							Intent userInfoIntent = new Intent();
							userInfoIntent.setClass(mContext, UserInfoActivity.class);
							userInfoIntent.putExtra("user",login);
							userInfoIntent.putExtra("type",2);
							userInfoIntent.putExtra("uid", strUserId);
							mContext.startActivity(userInfoIntent);
							Log.e("LT", "名字点击！！！"+mData.nickname);
							Log.v("LT", "点击测试！！！");
						}
							  
					    @Override  
					    public void updateDrawState(TextPaint ds) {  
					        ds.setColor(Color.rgb(51, 75, 127));//文本颜色  
					        ds.setUnderlineText(false);//是否有下划线  
					        ds.bgColor = Color.TRANSPARENT;//背景颜色  
					    }  
					}, 0, c.nickname.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					spannableStringBuilder.setSpan(new ClickableSpan() {
						
						@Override
						public void onClick(View v) {
							if (onLongClickedHead) {
								onLongClickedHead = false;
								return;
							}else{
								if (strUserId.equals(ResearchCommon.getUserId(mContext))) {
									showAlertDialogCopyDelete(tString, intFidReply, strUserId, strFid);
									Log.e("LT", "intFidReply"+intFidReply+" strUserId="+strUserId+" getUserId="+ResearchCommon.getUserId(mContext)+" strShareUid="+strShareUid+" strFid"+strFid);
								}else{
									Log.e("LT", "intFidReply"+intFidReply+" strUserId="+strUserId+" getUserId="+ResearchCommon.getUserId(mContext)+" strShareUid="+strShareUid+" strFid"+strFid);
									Intent intent = new Intent();
									intent.setAction(FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY);
									intent.putExtra("position", mPosition);
									intent.putExtra("fid", strFid);
									intent.putExtra("uid", strUserId);
									intent.putExtra("nickname", strNickname);
									mContext.sendBroadcast(intent);
								}
								Log.v("cgj", strFid+":"+strUserId);
								}
						}
						    @Override  
						    public void updateDrawState(TextPaint ds) {  
						       ds.setColor(Color.rgb(0, 0, 0));//文本颜色  
						       ds.setUnderlineText(false);//是否有下划线  
						       ds.bgColor = Color.TRANSPARENT;//背景颜色  
						   } 
					}, c.nickname.length(), strReplyInfo.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}
				if(nColorReply > 0)
				{
					spannableStringBuilder.setSpan(new ClickableSpan() {
						
						@Override
						public void onClick(View v) 
						{
							// TODO Auto-generated method stub
							
							SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
							UserTable table = new UserTable(db);
							Login login = table.query(strUserId);
							if(login==null){
								login=new Login();
								login.uid=mData.uid;
								login.nickname=mData.nickname;
								login.headsmall=mData.headsmall;
								login.sign = "";
							}
							Intent userInfoIntent = new Intent();
							userInfoIntent.setClass(mContext, UserInfoActivity.class);
							userInfoIntent.putExtra("user",login);
							userInfoIntent.putExtra("type",2);
							userInfoIntent.putExtra("uid", strUserId);
							mContext.startActivity(userInfoIntent);
							Log.e("LT", "名字点击！！！"+mData.nickname);
							Log.v("LT", "点击测试！！！");
						}
							  
					    @Override  
					    public void updateDrawState(TextPaint ds) {  
					        ds.setColor(Color.rgb(51, 75, 127));//文本颜色  
					        ds.setUnderlineText(false);//是否有下划线  
					        ds.bgColor = Color.TRANSPARENT;//背景颜色  
					    }  
					}, 0, c.nickname.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//					ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(Color.rgb(51, 75, 127));
					spannableStringBuilder.setSpan(new ClickableSpan() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
							UserTable table = new UserTable(db);
							Login login = table.query(strUserId);
							if(login==null){
								login=new Login();
								login.uid=mData.uid;
								login.nickname=mData.nickname;
								login.headsmall=mData.headsmall;
								login.sign = "";
							}
							Intent userInfoIntent = new Intent();
							userInfoIntent.setClass(mContext, UserInfoActivity.class);
							userInfoIntent.putExtra("user",login);
							userInfoIntent.putExtra("type",2);
							userInfoIntent.putExtra("uid", mData.uid);
							mContext.startActivity(userInfoIntent);
							Log.e("LT", "名字点击！！！"+mData.nickname);
							Log.v("LT", "点击测试！！！");
						}
							  
					    @Override  
					    public void updateDrawState(TextPaint ds) {  
					        ds.setColor(Color.rgb(51, 75, 127));//文本颜色  
					        ds.setUnderlineText(false);//是否有下划线  
					        ds.bgColor = Color.TRANSPARENT;//背景颜色  
					    }  
					}, nColorReply, nColorReply + c.fnickname.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					
					nColorReplyText = nColorReply+c.fnickname.length();
					spannableStringBuilder.setSpan(new ClickableSpan() {
						@Override
						public void onClick(View v) {
							if (onLongClicked) {
								onLongClicked = false;
								return;
							}else{
								if (strUserId.equals(ResearchCommon.getUserId(mContext))) {
									showAlertDialogCopyDelete(tString, intFidReply, strUserId, strFid);
									Log.e("LT", "intFidReply"+intFidReply+" strUserId="+strUserId+" getUserId="+ResearchCommon.getUserId(mContext)+" strShareUid="+strShareUid+" strFid"+strFid);
								}else{
									Log.e("LT", "intFidReply"+intFidReply+" strUserId="+strUserId+" getUserId="+ResearchCommon.getUserId(mContext)+" strShareUid="+strShareUid+" strFid"+strFid);
									Intent intent = new Intent();
									intent.setAction(FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY);
									intent.putExtra("position", mPosition);
									intent.putExtra("fid", strFid);
									intent.putExtra("uid", strUserId);
									intent.putExtra("nickname", strNickname);
									mContext.sendBroadcast(intent);
								}
								Log.v("cgj", strFid+":"+strUserId);
							}
						}
						@Override  
					    public void updateDrawState(TextPaint ds) {  
					       ds.setColor(Color.rgb(0, 0, 0));//文本颜色  
					       ds.setUnderlineText(false);//是否有下划线  
					       ds.bgColor = Color.TRANSPARENT;//背景颜色  
					    } 
					}, nColorReplyText, strReplyInfo.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}
				
				t.setText(spannableStringBuilder);
				//EmojiUtil.getExpressionString(getContext(), spannableStringBuilder, EMOJIREX);
				//设置点击不变色
				t.setHighlightColor(getResources().getColor(R.color.transparent));
				t.setMovementMethod(LinkMovementMethod.getInstance());
				mCommentLayout.addView(t);
				
			    //长按事件
				t.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						onLongClicked = true;
						onLongClickedHead = true;
						if (strUserId.equals(ResearchCommon.getUserId(mContext))||strShareUid.equals(ResearchCommon.getUserId(mContext))) {
							showAlertDialogCopyDelete(tString, intFidReply, strUserId, strFid);
						}else {
							showAlertDialogCopy(tString);
						}
						return true;
					}
				});
//				//点击事件
//				t.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View view) {
//						if (strUserId.equals(ResearchCommon.getUserId(mContext))) {
//							showAlertDialogCopyDelete(tString, intFidReply, strUserId, strFid);
//							Log.e("LT", "intFidReply"+intFidReply+" strUserId="+strUserId+" getUserId="+ResearchCommon.getUserId(mContext)+" strShareUid="+strShareUid+" strFid"+strFid);
//						}else{
//							Log.e("LT", "intFidReply"+intFidReply+" strUserId="+strUserId+" getUserId="+ResearchCommon.getUserId(mContext)+" strShareUid="+strShareUid+" strFid"+strFid);
//							Intent intent = new Intent();
//							intent.setAction(FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY);
//							intent.putExtra("position", mPosition);
//							intent.putExtra("fid", strFid);
//							intent.putExtra("uid", strUserId);
//							intent.putExtra("nickname", strNickname);
//							mContext.sendBroadcast(intent);
//						}
//						Log.v("cgj", strFid+":"+strUserId);
//					}
//					
//				});
			}
		} else {
			mCreatedAtView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mCreatedAtView.setText(FeatureFunction.calculaterFriendsLoopTime(mContext, new Date((mData.createtime*1000)),mData.createtime*1000));
			mCommentLayout.setVisibility(View.GONE);
		}
		if(bShowExtraArrow)
		{
			mExtraArrowLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void setLikeViewVisibility(){
		mLikeLayout.removeViewAt(getPosition());
		mLikeLayout.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.content:
			final String copyTest = mContentView.getText().toString();
			final String ownerId = mData.uid;
			showAlerDialogCopyFavoite(copyTest,ownerId);
			break;
		case R.id.content_all:
			final String copyTest2 = mContentViewAll.getText().toString();
			final String ownerId2 = mData.uid;
			showAlerDialogCopyFavoite(copyTest2,ownerId2);
			break;

		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();

		/*if (id == R.id.more_btn) 
		{
			showMore(v);
		} */
		//未用
		/*else if (id == R.id.comment) 
		{
			
			if (mCommentListener != null) {
				mCommentListener.onComment(mPosition);
				if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
					mMorePopupWindow.dismiss();
				}
			}
		} */
		if (id == R.id.like) 
		{
			if (mLikeListener != null) {
				mLikeListener.onLike(mPosition);
			}
			/*未用
			 * //隐藏弹出
			if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) 
			{
				mMorePopupWindow.dismiss();
			}*/
			//广播
			Intent intent = new Intent();
			intent.setAction(FRIENDSLOOP_PRAISE_CLICKED_NOTIFY);
			intent.putExtra("fid", strShareId);
			mData.praises++;
			mContext.sendBroadcast(intent);
		}else if (id == R.id.comment_praise_btn) {
			if (mData.ispraise !=0 ) {
				mPraise.setImageResource(drawable.btn_friendsloop_dianzan3);
			}
			else {
				mPraise.setImageResource(drawable.btn_friendsloop_dianzan_color);
			}
			Intent intent = new Intent();
			intent.setAction(FRIENDSLOOP_PRAISE_CLICKED_NOTIFY);
			intent.putExtra("fid", strShareId);
			mData.praises++;
			mContext.sendBroadcast(intent);
		}else if (id == R.id.comment_content_btn) {
			if (mCommentListener != null) {
//				mCommentListener.onComment(mPosition,v);
				mCommentListener.onComment(mPosition);
			}
		}else if (id == R.id.portrait) {
			//跳转到详细资料
			
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			UserTable table = new UserTable(db);
			Login login = table.query(mData.uid);
			if(login==null){
				login=new Login();
				login.uid=mData.uid;
				login.nickname=mData.nickname;
				login.headsmall=mData.headsmall;
				login.sign = "";
			}
			
			Intent userInfoIntent = new Intent();
			userInfoIntent.setClass(mContext, UserInfoActivity.class);
			userInfoIntent.putExtra("user",login);
			userInfoIntent.putExtra("type",2);
			userInfoIntent.putExtra("uid", mData.uid);
			mContext.startActivity(userInfoIntent);
			Log.e("LT", "头像点击！！！"+getPosition());
		}else if (id == R.id.nick_name) {
//			跳转到详细资料
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			UserTable table = new UserTable(db);
			Login login = table.query(mData.uid);
			if(login==null){
				login=new Login();
				login.uid=mData.uid;
				login.nickname=mData.nickname;
				login.headsmall=mData.headsmall;
				login.sign = "";
			}
			
			Intent userInfoIntent = new Intent();
			userInfoIntent.setClass(mContext, UserInfoActivity.class);
			userInfoIntent.putExtra("type",2);
			userInfoIntent.putExtra("user",login);
			userInfoIntent.putExtra("uid", mData.uid);
			mContext.startActivity(userInfoIntent);
			Log.e("LT", "名字点击！！！"+mData.nickname);
		}else if (id == R.id.content_delete) {
			showAlerDialogDeleteNotify();
		}
	}

	public int getPosition() {
		return mPosition;
	}

	// 评论1
	public void addComment(String s, String strReplyUid, boolean bReplyOther, String strFNickname) {
		Intent intent = new Intent();
		intent.setAction(FRIENDSLOOP_COMMENT_CLICKED_NOTIFY);
		strContent = s;
		intent.putExtra("fid", strShareId);
		if(strReplyUid.isEmpty())
		{
			intent.putExtra("toUid", strToUid);
		}
		else
		{
			intent.putExtra("toUid", strReplyUid);
		}
		
		intent.putExtra("content", strContent);
		intent.putExtra("other", bReplyOther);
		intent.putExtra("fnickname", strFNickname);
		mContext.sendBroadcast(intent);
		updateComment();
	}
	
	public void addComment() {
		updateComment();
	}
	
	//AlertDialog-复制
	public void showAlertDialogCopy(String copyText){
		final AlertDialog mAlertDialogCopy = new AlertDialog.Builder(mContext, R.style.Theme_Transparent).create();
		LayoutInflater inflaterCopy = LayoutInflater.from(mContext);
		View mLayoutCopy = inflaterCopy.inflate(R.layout.friendsloop_comment_alerdialog_custom_copy, null);
		mAlertDialogCopy.setView(mLayoutCopy);
		final String tString = copyText;
		mLayoutCopy.findViewById(R.id.friendsloop_alerdialog_txt_cpoy).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager cManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				cManager.setText(tString);
				Log.e("LT", "点击复制！！！");
				mAlertDialogCopy.dismiss();
			}
		});
		mAlertDialogCopy.show();
	}
	//Alertdialog-复制与删除回复
	public void showAlertDialogCopyDelete(String copyText,final int intFidReply, final String strUserId, final String strFid){
		final AlertDialog mAlertDialogCopyDelete = new AlertDialog.Builder(mContext, R.style.Theme_Transparent).create();
		LayoutInflater inflaterCopyDelete = LayoutInflater.from(mContext);
		View mLayoutCopyDelete = inflaterCopyDelete.inflate(R.layout.friendsloop_comment_alerdialog_custom_copy_delete, null);
		mAlertDialogCopyDelete.setView(mLayoutCopyDelete);
		final String tString = copyText;
		mLayoutCopyDelete.findViewById(R.id.friendsloop_alerdialog_txt_cpoy).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager cManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				cManager.setText(tString);
				Log.e("LT", "点击复制！！！");
				mAlertDialogCopyDelete.dismiss();
			}
		});
		mLayoutCopyDelete.findViewById(R.id.friendsloop_alerdialog_txt_delete).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("LT", "点击删除！！！"+intFidReply);
				if((Integer.parseInt(strUserId) == Integer.parseInt(ResearchCommon.getUserId(mContext)))
						|| (Integer.parseInt(mData.uid) == Integer.parseInt(ResearchCommon.getUserId(mContext))))
				{
					Intent intent = new Intent();
					intent.setAction(FRIENDSLOOP_COMMENT_REPLYDElETE_NOTIFY);
					intent.putExtra("replyid", intFidReply);
					intent.putExtra("fsid", strFid);
					mContext.sendBroadcast(intent);
				}
				mAlertDialogCopyDelete.dismiss();
			}
		});
		mAlertDialogCopyDelete.show();
	}
	//AlertDialog-收藏
	public void showAlertDialogFavorite(final ArrayList<String> imageUrls,final ArrayList<String> originUrls,
			final int i,final String uid){
		final AlertDialog mAlertDialog = new AlertDialog.Builder(mContext, R.style.Theme_Transparent).create();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View mLayout = inflater.inflate(R.layout.friendsloop_comment_alerdialog_custom_favorite,null);
		mAlertDialog.setView(mLayout);
		final String tString = mContentView.getText().toString();
		mLayout.findViewById(R.id.friendsloop_alerdialog_txt_favorite).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//文字的话就是这个content
//				MovingContent movingContent = new MovingContent(
//						mData.content, MessageType.TEXT + "");
//				MovingContent.getInfo(movingContent);
				//图片的话就是这个content
//				if(mData.listpic != null && mData.listpic.size() > 0){
//					MovingPic pic = new MovingPic(mData.listpic
//							.get(1).originUrl, mData.listpic
//							.get(1).smallUrl,
//							MessageType.PICTURE + "");
//					strContent = MovingPic.getInfo(pic);
//				}
//				//收藏未完成
//				Intent intent = new Intent();
//				intent.setAction(FRIENDSLOOP_CONTENT_FAVORITE_NOTIFY);
//				mToUserId = mData.uid;
//				intent.putExtra("fuid", mToUserId);
//				intent.putExtra("content", strContent);
//				mContext.sendBroadcast(intent);
//				Log.e("LT", "点击收藏！！！");
				String groupId = "";
				String ownerId = uid;
				MovingPic movingPic = new MovingPic(originUrls.get(i),
						imageUrls.get(i), MessageType.PICTURE + "");
				mChatMainActivity.favoriteMoving(MovingPic.getInfo(movingPic),ownerId,groupId,mContext);
				Log.e("ownerId1", ownerId);
				mAlertDialog.dismiss();
			}
		});
		mAlertDialog.show();
	}
	// AlertDialog-复制与收藏
	public void showAlerDialogCopyFavoite(final String copyTest,final String uid){
		final AlertDialog mAlertDialog = new AlertDialog.Builder(mContext, R.style.Theme_Transparent).create();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View mLayout = inflater.inflate(R.layout.friendsloop_comment_alerdialog_custom_copy_favorite,null);
		mAlertDialog.setView(mLayout);
		mLayout.findViewById(R.id.friendsloop_alerdialog_txt_cpoy).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager cManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				cManager.setText(copyTest);
				Log.e("LT", "点击复制！！！");
				mAlertDialog.dismiss();
			}
		});
		mLayout.findViewById(R.id.friendsloop_alerdialog_txt_favorite).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("LT", "点击收藏！！！");
				String groupId = "";
				String ownerId = uid;
				MovingContent movingContent = new MovingContent(
						URLDecoder.decode(copyTest),MessageType.TEXT + "");
				mChatMainActivity.favoriteMoving(MovingContent.getInfo(movingContent),ownerId, groupId,mContext);
				Log.e("copyTest", copyTest);
				Log.e("ownerId", ownerId);
//				Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
				mAlertDialog.dismiss();
			}
		});
		mAlertDialog.show();
	}
	
	//AlertDialog-删除消息提示框
	public void showAlerDialogDeleteNotify(){
		final AlertDialog mAlertDialog = new AlertDialog.Builder(mContext, R.style.Theme_Transparent).create();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View mLayout = inflater.inflate(R.layout.friendsloop_comment_alerdialog_custom_write,null);
		mAlertDialog.setView(mLayout);
		mLayout.findViewById(R.id.friendsloop_alerdialog_txt_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("LT", "点击取消！！！");
				mAlertDialog.dismiss();
			}
		});
		mLayout.findViewById(R.id.friendsloop_alerdialog_txt_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(FRIENDSLOOP_CONTENT_SHAREDELETE_NOTIFY);
				intent.putExtra("fsid", strShareId);
				mContext.sendBroadcast(intent);
				Log.e("LT", "点击确定！！！");
				mAlertDialog.dismiss();
			}
		});
		
		WindowManager m = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);   
		Display d = m.getDefaultDisplay();  //为获取屏幕宽、高     
		android.view.WindowManager.LayoutParams p = mAlertDialog.getWindow().getAttributes();  //获取对话框当前的参数值     
		p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3   
		p.width = (int) (d.getWidth() * 0.5);    //宽度设置为屏幕的0.5    
		mAlertDialog.getWindow().setAttributes(p);     //设置生效  
		mAlertDialog.show();
	}
}
