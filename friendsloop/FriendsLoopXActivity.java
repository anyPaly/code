package com.edgelesschat.friendsloop;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager.Request;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat.Field;
import android.media.Image;
import android.net.Uri;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.edgelesschat.org.json.JSONArray;
import com.edgelesschat.org.json.JSONException;
import com.edgelesschat.org.json.JSONObject;
import com.edgelesschat.smallvideo.SmallVideoActivityUp;
import com.edgelesschat.utils.GsonUtil;
import com.edgelesschat.widget.ResizeLayout.OnResizeListener;
import com.emoji.EmojiDatas;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import me.iwf.photopicker.PhotoPicker;

import org.afinal.simplecache.ACache;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.db.converter.LongColumnConverter;
import org.xutils.http.RequestParams;

import com.edgelesschat.BaseActivity;
import com.edgelesschat.ChatMainActivity;
import com.edgelesschat.FriensLoopActivity;
import com.edgelesschat.R;
import com.edgelesschat.RotateImageActivity;
import com.edgelesschat.SendMovingActivity;
import com.edgelesschat.DB.DBHelper;
import com.edgelesschat.DB.SessionTable;
import com.edgelesschat.Entity.CommentUser;
import com.edgelesschat.Entity.FriendsLoop;
import com.edgelesschat.Entity.FriendsLoopItem;
import com.edgelesschat.Entity.Login;
import com.edgelesschat.Entity.MapInfo;
import com.edgelesschat.Entity.MessageType;
import com.edgelesschat.Entity.MorePicture;
import com.edgelesschat.Entity.MovingContent;
import com.edgelesschat.Entity.MovingPic;
import com.edgelesschat.Entity.NotifiyVo;
import com.edgelesschat.Entity.ResearchJiaState;
import com.edgelesschat.Entity.Session;
import com.edgelesschat.Entity.UploadImg;
import com.edgelesschat.R.drawable;
import com.edgelesschat.adapter.EmojiAdapter;
import com.edgelesschat.adapter.ResearchViewPagerAdapter;
import com.edgelesschat.animation.AnimationMyStickers;
import com.edgelesschat.animation.AnimationViewPagerCover;
import com.edgelesschat.append.AnimatePageManager;
import com.edgelesschat.dialog.MMAlert;
import com.edgelesschat.dialog.MMAlert.OnAlertSelectId;
import com.edgelesschat.friendsloop.PullToRefreshLayout.RefreshListener;
import com.edgelesschat.friendsloop.XListView.IXListViewListener;
import com.edgelesschat.global.FeatureFunction;
//import com.edgelesschat.friendsloop.ListViewExpanded.IXListViewListener;
import com.edgelesschat.global.GlobalParam;
import com.edgelesschat.global.ImageLoader;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.map.BMapApiApp;
import com.edgelesschat.net.ResearchException;
import com.edgelesschat.net.ResearchInfo;

//未做收藏
public class FriendsLoopXActivity extends BaseActivity implements
		IXListViewListener, CustomSwipeRefreshLayout.OnRefreshListener, OnPageChangeListener {

	public static final int REQUEST_GET_IMAGE_BY_PHOTO=1001;
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 1000;
	public static final String APPKEY = ResearchInfo.APPKEY;
	public static final String FRIENDSLOOP_PRAISE_CLICKED_NOTIFY = "friendsloop_praise_clicked_notify";
	public static final String FRIENDSLOOP_COMMENT_CLICKED_NOTIFY = "friendsloop_comment_clicked_notify";
	public static final String FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY = "friendsloop_comment_replyother_notify";
	public static final String FRIENDSLOOP_COMMENT_REPLYDElETE_NOTIFY = "friendsloop_comment_replydelete_notify";
	public static final String FRIENDSLOOP_CONTENT_SHAREDELETE_NOTIFY = "friensloop_content_sharedelete_notify";
	public static final String FRIENDSLOOP_CONTENT_FAVORITE_NOTIFY = "friends_content_favorite_notify";
	private static final int REQUEST_FRIENDSLOOP_ACTIVITY = 200;
	private static final int RESULT_FRIENDSLOOP_ACTIVITY = 199;
	private static final int STOP_REFRESH = 198;

	final String URL_PREFIX = "https://www.520219.com/lediao/index.php";
	// private ListViewExpanded mListView;
	private int IMAGE_MAX = 9;
	private int sendType;  //1 长按跳转  2拍摄图片跳转  3视频跳转  4相册选择
	private XListView mListView;
	private View mCommentView;
	private MyAdapter mAdapter;
	private EditText mEtComment;
	private Button mBtnComment,mEmotionBtn;
	private ImageView mImageViewReturn;
	private ImageView mImageViewRight;
	private ImageView mHeaderBg;
	
	private String strMyUid;
	private String mCropImgPath, mToUserId;
	private int mShareId;
	private int screenWidthEdit;
	private int screenHeightEdit;
	private Bitmap mBitmap;
	private FriendsLoop mMyAlbum;
	private ImageLoader mImageLoader;
	private DisplayMetrics mMetric;
	protected AlertDialog mUpgradeNotifyDialog;
	private int ICON_SIZE_WIDTH;
	private static int ICON_SIZE_HEIGHT;

	private List<FriendsLoopItem> mDataList = new ArrayList<FriendsLoopItem>();
	private List<MorePicture> mListpic = new ArrayList<MorePicture>();
	private String mTempFileName = "front_cover.jpg";
	int nLoadTimes = 1;
	private DynamicReceiver dynamicReceiver;
	private XListViewHeaderLayout mHeaderView;
	private int RefreshTime;
	private boolean isLoadData = false;
	private boolean isRefreshData = false;
	private ObjectAnimator mRotationAnimator;// 滚动动画
	private int lastItem;
	private CustomSwipeRefreshLayout mRefreshLayout;
	
	private ViewPager mViewPager;// 表情页
	private RelativeLayout mEmotionLayout;
	private LinearLayout mLayoutCircle;
	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();// 表情页适配器
	private LinkedList<View> mViewList = new LinkedList<View>();// 表情页集合
	private ResearchViewPagerAdapter mEmotionAdapter;// 表情页适配器
	private LinearLayout mTypeAddEmoji;
	public int mPageIndxe = 0;
	
	public ACache mCache;
	private SharedPreferences sharedPreferences;

	void initViews() {
		setTitleContent(R.drawable.ic_return, R.drawable.friendsloop_camera,
				R.string.friends_loop);
		// setTitleContent(R.drawable.ic_return, 0, "");
		// mListView = (ListViewExpanded)findViewById(R.id.listview);
		mListView = (XListView) findViewById(R.id.friendsloop_listview);
		mListView.setPullRefreshEnable(false);
		mRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.refresh_layout);
		CustomProgressDrawable drawable = new CustomProgressDrawable(this,
				mRefreshLayout);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.friendsloop_pulltorefres_loading);
		drawable.setBitmap(bitmap);
		mRefreshLayout.setProgressView(drawable);
		mRefreshLayout.setBackgroundColor(Color.BLACK);// 更改刷新背景颜色 
		mRefreshLayout.setProgressBackgroundColorSchemeColor(Color
				.parseColor("#6bb890"));// 更改刷新图标背景颜色
		// 设置下拉刷新监听
		mRefreshLayout.setProgressViewEndTarget(true, 150);
		mRefreshLayout.setOnRefreshListener(this);
		mRefreshLayout.setEnabled(false);
		mImageViewReturn = (ImageView) findViewById(R.id.left_icon);
		
		mImageViewRight = (ImageView) findViewById(R.id.right_btn);
		
		//
		mHeaderBg = (ImageView) findViewById(R.id.img_background);
		mHeaderBg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListpic != null && mListpic.size() > 0) {
					mListpic.clear();
				}
//				selectImg();
				showLoopImgDialog();
			}
		});

		mImageViewRight.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// 分享
				sendType=1;
				Intent intent = new Intent();
				intent.setClass(FriendsLoopXActivity.this,
						SendMovingActivity.class);
				intent.putExtra("sendType", sendType);
				startActivityForResult(intent, REQUEST_FRIENDSLOOP_ACTIVITY);
				return true;
			}
		});

		mImageViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLoopDialog();
			}
		});

		mImageViewReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		getLoopDataCache();
		if (mAdapter == null) {
			mAdapter = new MyAdapter(this, mDataList);
			mListView.setAdapter(mAdapter);
		}
		// 滑动到底部自动刷新
		mListView.setOnScrollListener(new OnScrollListener() {  
            //AbsListView view 这个view对象就是listview  
            @Override  
            public void onScrollStateChanged(AbsListView view, int scrollState) {  
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    	onLoadMore();
                    }
                }
            }  
            @Override  
            public void onScroll(AbsListView view, int firstVisibleItem,  
                    int visibleItemCount, int totalItemCount) {  
            	lastItem = firstVisibleItem + visibleItemCount - 1;
				// 解决listview与SwipeRefreshLayout滑动冲突问题
				boolean enable = false;
				if (mListView != null && mListView.getChildCount() > 0) {
					// check if the first item of the list is visible
					boolean firstItemVisible = mListView
							.getFirstVisiblePosition() == 0;
					// check if the top of the first item is visible
					boolean topOfFirstItemVisible = mListView.getChildAt(0)
							.getTop() == 0;
					// enabling or disabling the refresh layout
					enable = firstItemVisible && topOfFirstItemVisible;
				}
				mRefreshLayout.setEnabled(enable);
            }  
        });
		mListView.setXListViewListener(this);

		mCommentView = findViewById(R.id.comment_view);
		// 获取id
		strMyUid = ResearchCommon.getUserId(this);
		// mListView.setPullLoadEnable(true);
		// 广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(FRIENDSLOOP_PRAISE_CLICKED_NOTIFY);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter);

		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(FRIENDSLOOP_COMMENT_CLICKED_NOTIFY);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter2);

		IntentFilter filter3 = new IntentFilter();
		filter3.addAction(FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter3);

		IntentFilter filter_replydelete = new IntentFilter();
		filter_replydelete.addAction(FRIENDSLOOP_COMMENT_REPLYDElETE_NOTIFY);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter_replydelete);

		IntentFilter filter_sharedelete = new IntentFilter();
		filter_sharedelete.addAction(FRIENDSLOOP_CONTENT_SHAREDELETE_NOTIFY);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter_sharedelete);

		IntentFilter filter_cotentfavorite = new IntentFilter();
		filter_cotentfavorite.addAction(FRIENDSLOOP_CONTENT_FAVORITE_NOTIFY);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter_cotentfavorite);

		mBtnComment = (Button) findViewById(R.id.friendsloop_btn_comment);
		mEtComment = (EditText) findViewById(R.id.friendsloop_edit_comment);
		mEtComment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				boolean bEnableButton = (mEtComment.getText().length() > 0);
				mBtnComment.setEnabled(bEnableButton);
				if (bEnableButton) {
					mBtnComment.setTextColor(Color.WHITE);
				} else {
					mBtnComment.setTextColor(Color.rgb(0xeb, 0xeb, 0xeb));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		mEtComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEmotionLayout.setVisibility(View.GONE);
			}
		});
		
		//表情
		mEmotionBtn = (Button) findViewById(R.id.chat_box_btn_emoji);
		mEmotionBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("lt", "点击表情按钮.");
				mEmotionLayout.setVisibility(View.VISIBLE);
				mTypeAddEmoji.setVisibility(View.GONE);
				/*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.hideSoftInputFromWindow(mEtComment.getWindowToken(), 0);*/
				
				InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);   
			}
		});
		mEmotionLayout = (RelativeLayout) findViewById(R.id.emotionlayout);
		mEmotionLayout.setVisibility(View.GONE);
		
		// 添加表情包按钮
		mViewPager = (ViewPager) findViewById(R.id.imagepager);
		// mViewPager.setOffscreenPageLimit(9);
		mViewPager.setOnPageChangeListener(this);
		mLayoutCircle = (LinearLayout) findViewById(R.id.circlelayout);
		mEmotionLayout = (RelativeLayout) findViewById(R.id.emotionlayout);
		mEmotionLayout.setVisibility(View.GONE);
		mTypeAddEmoji = (LinearLayout) findViewById(R.id.typelayout);
		mTypeAddEmoji.setVisibility(View.GONE);
		
		mTotalEmotionList = getEmojiList();
		for (int i = 0; i < mTotalEmotionList.size(); i++) {
			addView(i);
		}
		mEmotionAdapter = new ResearchViewPagerAdapter(mViewList);
		mViewPager.setAdapter(mEmotionAdapter);
		mViewPager.setCurrentItem(0);
		showCircle(mViewList.size());
	}

	private void startPhotoPicker(){
		PhotoPicker.builder().setPhotoCount(IMAGE_MAX).setShowCamera(false).setSelected(null).setShowGif(false).setPreviewEnabled(true).start(this,
                PhotoPicker.REQUEST_CODE);
	}
	
//	private File tempFile = null;
//	private String filename = null;
	String basepath = AnimationMyStickers.BASE_PATH+"videocache"+File.separator;
	String filename=null;
	
	// 弹出框
	private void showLoopDialog() {

		View view = ((Activity) mContext).getLayoutInflater().inflate(
				R.layout.loop_alertdialog, null, false);
		Builder builder = new AlertDialog.Builder(mContext,
				R.style.Theme_Transparent);
		final AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		final TextView camera = (TextView) view.findViewById(R.id.camera);
		final TextView photo = (TextView) view.findViewById(R.id.photo);

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(mContext, SmallVideoActivityUp.class);

//				tempFile = new File(Environment.getExternalStorageDirectory(),
//						getPhotoFileName());
//				filename = getPhotoFileName();
				// Log.e("filepath", (tempFile.getAbsolutePath());
//				Log.e("路径", tempFile.getAbsolutePath());
				filename=getPhotoFileName();
				intent.putExtra("tempPath", basepath+filename);
				startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
				dialog.dismiss();
			}
		});

		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
//				Intent intent=new Intent(Intent.ACTION_GET_CONTENT);  
//		        intent.addCategory(Intent.CATEGORY_OPENABLE);  
//		        intent.setType("image/*");  
//		        startActivityForResult(intent, REQUEST_GET_IMAGE_BY_PHOTO);
				startPhotoPicker();
				dialog.dismiss();
			}
		});
	}

	
	
	private void showLoopImgDialog() {

		View view = ((Activity) mContext).getLayoutInflater().inflate(
				R.layout.loop_bgimg_alertdialog, null, false);
		Builder builder = new AlertDialog.Builder(mContext,
				R.style.Theme_Transparent);
		final AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		final TextView camera = (TextView) view.findViewById(R.id.camera);
		final TextView photo = (TextView) view.findViewById(R.id.photo);

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getImageFromGallery();
				dialog.dismiss();
			}
		});

		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				getImageFromCamera();
				dialog.dismiss();
			}
		});
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// switch (requestCode) {
	//
	// case REQUEST_GET_IMAGE_BY_CAMERA:
	// if(data != null && RESULT_OK == resultCode){
	//
	// }
	// break;
	// }
	// }

	/**
	 * 拍一张照片
	 */

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date);
	}

	// 广播
	class DynamicReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == FRIENDSLOOP_PRAISE_CLICKED_NOTIFY) {
				final String strFid = intent.getStringExtra("fid");
				putPraise(strFid);
			} else if (intent.getAction() == FRIENDSLOOP_COMMENT_CLICKED_NOTIFY) {
				final String strFid = intent.getStringExtra("fid");
				final String strToUid = intent.getStringExtra("toUid");
				final String strContent = intent.getStringExtra("content");
				final String strFNickname = intent.getStringExtra("fnickname");
				final boolean bReplyOther = intent.getBooleanExtra("other",
						false);
				putContent(strFid, strToUid, strContent, bReplyOther,
						strFNickname);
			} else if (intent.getAction() == FRIENDSLOOP_COMMENT_REPLYOTHER_NOTIFY) {
				final int position = intent.getIntExtra("position", -1);
				final String strFid = intent.getStringExtra("fid");
				final String strUid = intent.getStringExtra("uid");
				final String strNickName = intent.getStringExtra("nickname");
				mAdapter.showCommentView(position, strUid, strNickName);
			} else if (intent.getAction() == FRIENDSLOOP_COMMENT_REPLYDElETE_NOTIFY) {
				final String strFidReply = Integer.toString(intent.getIntExtra(
						"replyid", 1));
				final String strFid = intent.getStringExtra("fsid");
				putReplyDelete(strFid, strFidReply);
			} else if (intent.getAction() == FRIENDSLOOP_CONTENT_SHAREDELETE_NOTIFY) {
				final String strFid = intent.getStringExtra("fsid");
				putReplyDelete(strFid);
			} else if (intent.getAction() == FRIENDSLOOP_CONTENT_FAVORITE_NOTIFY) {
				final String strFuid = intent.getStringExtra("fuid");
				final String strContent = intent.getStringExtra("content");
				putContentFavorite(strFuid, null, strContent);
				// showFavoriteDialog(1, mfFriendsLoopItem, 2, strFuid);
			}
		}
	}
	
	
	

	// 选择一张图片
	private void selectImg() {
		MMAlert.showAlert(this, "",
				this.getResources().getStringArray(R.array.camer_item), null,
				new OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case 0:
							getImageFromGallery();
							break;
						case 1:
							getImageFromCamera();
							break;
						default:
							break;
						}
					}
				});
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMetric = new DisplayMetrics();
		mMetric = getResources().getDisplayMetrics();
		float density = mMetric.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int screenWidth = (int) (mMetric.widthPixels * density + 0.5f); // 屏幕宽（px，如：480px）

		// getWindowManager().getDefaultDisplay().getMetrics(mMetric);
		mImageLoader = new ImageLoader();
		ICON_SIZE_WIDTH = screenWidth;// mMetric.widthPixels;
		if (ICON_SIZE_WIDTH > 1000) {
			ICON_SIZE_WIDTH = 1000;
		}
		ICON_SIZE_HEIGHT = (ICON_SIZE_WIDTH / 4) * 3;// x:y 3:2
		long time = System.currentTimeMillis();
		final Calendar mCalendar = Calendar.getInstance();
		RefreshTime = mCalendar.get(Calendar.MINUTE);
		
		sharedPreferences = this.getSharedPreferences(ResearchCommon.getUserId(mContext)+"FriendsLoopXActivity", Context.MODE_PRIVATE);
		
		mCache = ACache.get(this, ResearchCommon.getUserId(mContext)+"FriendsLoopXActivity");// 初始化ACache
		setContentView(R.layout.activity_friendsloop_x_layout);

		// 获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidthEdit = dm.widthPixels;
		screenHeightEdit = dm.heightPixels;
		mCropImgPath = Environment.getExternalStorageDirectory()
				+ FeatureFunction.PUB_TEMP_DIRECTORY + "album.jpg";
		File file = new File(mCropImgPath);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		mImageLoader = new ImageLoader();
		initViews();
		x.Ext.init(getApplication());
		// 设置首次进入自动刷新
		if (!ResearchCommon.verifyNetwork(mContext)) {
			Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
			mRefreshLayout.measure(0, 0);
			mRefreshLayout.setRefreshing(true);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
						mHandler.sendEmptyMessage(STOP_REFRESH);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
		} else {
			mRefreshLayout.measure(0, 0);
			mRefreshLayout.setRefreshing(true);
			onRefresh();
		}
		// loadData();
		ResearchCommon.saveReadFriendsLoopTip(mContext, true);
		Intent hideIntent = new Intent(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		hideIntent.putExtra("found_type", 1);
		sendBroadcast(hideIntent);
		sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ResearchCommon.saveReadFriendsLoopTip(mContext, true);
		Intent hideIntent = new Intent(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		hideIntent.putExtra("found_type", 1);
		sendBroadcast(hideIntent);
		sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP));
		super.onDestroy();
		this.finish();
	}

	private void getLoopData() {
		String strURL = URL_PREFIX;
		strURL += "/friend/api/shareList";
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
		params.addBodyParameter("uid", strMyUid);
		if (nLoadTimes > 1) {
			params.addBodyParameter("page", nLoadTimes + "");
		}
		x.http().post(params, new Callback.CacheCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub
				isLoadData = false;
				mListView.setEnabled(true);
				
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub
				isLoadData = false;
				mListView.setEnabled(true);
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				isLoadData = false;
				mListView.setEnabled(true);
			}

			@Override
			public void onSuccess(String strResponse) {
				isLoadData = false;
				mListView.setEnabled(true);
				if (isRefreshData) {
					mDataList.clear();
					isRefreshData = false;
				}
				
				// TODO Auto-generated method stub
				try {
					// JSON解析
					JSONObject parentJson = new JSONObject(strResponse);
					JSONArray array = parentJson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						FriendsLoopItem mfFriendsLoopItem = new FriendsLoopItem(
								object);
						mDataList.add(mfFriendsLoopItem);
					}
					if (array.length() > 0) {
						nLoadTimes++;
					}
					
					saveDatas(mDataList);
					
					// 刷新适配器
					mAdapter.notifyDataSetChanged();
					mRefreshLayout.setRefreshing(false);
					// mListView.stopRefresh();
					mListView.stopLoadMore();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				isLoadData = false;
				mListView.setEnabled(true);
				return false;
			}

		});
	}

	// 点赞
	private void putPraise(final String strFid) {
		String strURL = URL_PREFIX;
		strURL += "/friend/api/sharePraise";
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
		params.addBodyParameter("uid", strMyUid);
		params.addBodyParameter("fsid", strFid);
		Log.v("LT", strMyUid + ":" + strFid + ":");
		x.http().post(params, new Callback.CacheCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				FriendsLoopItemView itemView = new FriendsLoopItemView(
						FriendsLoopXActivity.this);
				itemView.addComment();
			}

			@Override
			public void onSuccess(String strRetval) {
				// TODO Auto-generated method stub
				if (strRetval.contains("{\"code\":0")) {
					// nLoadTimes = 1;
					// mDataList.clear();
					// getLoopData();
					int fid = Integer.parseInt(strFid);
					for (int index = 0; index < mDataList.size(); index++) {
						if (mDataList.get(index).id == fid) {
							FriendsLoopItem item = mDataList.get(index);
							if (item.praiselist == null) {
								item.praiselist = new ArrayList<CommentUser>();
							}
							for (int i = 0; i < item.praiselist.size(); i++) {
								if (item.praiselist.get(i).uid
										.equalsIgnoreCase(strMyUid)) {
									item.praiselist.remove(i);
									// FriendsLoopItemView itemView = new
									// FriendsLoopItemView(FriendsLoopXActivity.this);
									// itemView.setLikeViewVisibility();
									item.ispraise = 0;
									Log.v("LT", strMyUid + ":" + strFid
											+ " isparaise:" + item.ispraise);
									mAdapter.notifyDataSetChanged();
									return;
								}
							}
							Login userInfoVo = ResearchCommon
									.getLoginResult(FriendsLoopXActivity.this);
							CommentUser user = new CommentUser();
							user.uid = strMyUid;
							user.nickname = userInfoVo.nickname;
							item.praiselist.add(user);
							Log.v("LT", strMyUid + ":" + strFid + " isparaise:"
									+ item.ispraise);
							item.ispraise = 1;
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
					Toast.makeText(FriendsLoopXActivity.this, "点赞成功",
							Toast.LENGTH_SHORT).show();
					// mAdapter.notifyDataSetChanged();
				} else {
					mAdapter.notifyDataSetChanged();
					Toast.makeText(FriendsLoopXActivity.this, "点赞失败",
							Toast.LENGTH_SHORT).show();
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	// 评论
	public void putContent(final String strFsid, final String toUid,
			final String content, final boolean bReplyOther,
			final String strReplyNickname) {
		String strURL = URL_PREFIX;
		strURL += "/friend/api/shareReply";
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
		params.addBodyParameter("uid", strMyUid);
		params.addBodyParameter("content", content);
		params.addBodyParameter("fsid", strFsid);
		params.addBodyParameter("fuid", toUid);
		params.addBodyParameter("rflag", bReplyOther ? "1" : "0");
		Log.d("lt", content + " " + strFsid + " " + toUid);
		x.http().post(params, new Callback.CacheCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				// FriendsLoopItemView itemView = new
				// FriendsLoopItemView(FriendsLoopXActivity.this);
				// itemView.addComment();
			}

			@Override
			public void onSuccess(String strRetval) {
				// TODO Auto-generated method stub
				Log.v("cgj", strRetval + ":" + strFsid);
				int fid = Integer.parseInt(strFsid);
				for (int index = 0; index < mDataList.size(); index++) {
					if (mDataList.get(index).id == fid) {
						FriendsLoopItem item = mDataList.get(index);
						Login userInfoVo = ResearchCommon
								.getLoginResult(FriendsLoopXActivity.this);
						CommentUser user = new CommentUser();
						user.uid = strMyUid;
						user.nickname = userInfoVo.nickname;
						user.content = content;
						user.fuid = toUid;
						user.fnickname = strReplyNickname;
						user.replyflag = bReplyOther ? 1 : 0;
						if (item.replylist == null) {
							item.replylist = new ArrayList<CommentUser>();
						}
						item.replylist.add(user);
						item.replys = 1;
						mAdapter.notifyDataSetChanged();
						break;
					}
				}
				Toast.makeText(FriendsLoopXActivity.this, "评论成功",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	// 删除回复
	public void putReplyDelete(final String fsid, final String replyid) {
		String strURL = URL_PREFIX;
		strURL += "/friend/api/deleteReply";
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
		params.addBodyParameter("uid", strMyUid);
		params.addBodyParameter("fsid", fsid);
		if (replyid.equalsIgnoreCase("")) {
			return;
		}
		params.addBodyParameter("replyid", replyid);
//		Log.e("LT", "replyid=" + replyid);
//		Log.e("LT", "uid=" + strMyUid);
		x.http().post(params, new Callback.CacheCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
			}

			// 是否成功查看debug msg:"unicod码"转中文
			@Override
			public void onSuccess(String strRetval) {

				if (strRetval.contains("{\"code\":0")) {
					int nReplyId = Integer.parseInt(replyid);
					int nFsid = Integer.parseInt(fsid);
					for (int index = 0; index < mDataList.size(); index++) {
						if (mDataList.get(index).id == nFsid) {
							FriendsLoopItem item = mDataList.get(index);
							for (int i = 0; i < item.replylist.size(); i++) {
								CommentUser user = item.replylist.get(i);
								if (user.id == nReplyId) {
									item.replylist.remove(i);
									mAdapter.notifyDataSetChanged();
									break;
								}
							}
							break;
						}
					}
					Toast.makeText(FriendsLoopXActivity.this, "删除成功",
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(FriendsLoopXActivity.this, "删除失败",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	// 收藏
	public void putContentFavorite(final String strFuid, final String groupId,
			String content) {
		FriendsLoopItem item = new FriendsLoopItem();
		String strURL = URL_PREFIX;
		strURL += "/user/api/favorite";
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
		params.addBodyParameter("uid", strMyUid);
		params.addBodyParameter("fuid", strFuid);
		if (content != null) {
			params.addBodyParameter("content", content);
		}
		params.addBodyParameter("groupId", groupId);
		x.http().post(params, new Callback.CacheCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
			}

			// 是否成功查看debug msg:"unicod码"转中文
			@Override
			public void onSuccess(String strRetval) {
				Toast.makeText(FriendsLoopXActivity.this, strRetval,
						Toast.LENGTH_SHORT).show();
				Log.v("LT", strRetval);
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	// 删除分享
	public void putReplyDelete(final String strFid) {
		String strURL = URL_PREFIX;
		strURL += "/friend/api/delete";
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
		params.addBodyParameter("uid", strMyUid);
		params.addBodyParameter("fsid", strFid);
//		Log.e("LT", "strFid=" + strFid);
//		Log.e("LT", "uid=" + strMyUid);
		x.http().post(params, new Callback.CacheCallback<String>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String strRetval) {
				if (strRetval.contains("{\"code\":0")) {
					int nFsid = Integer.parseInt(strFid);
					for (int index = 0; index < mDataList.size(); index++) {
						if (mDataList.get(index).id == nFsid) {
							mDataList.remove(index);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
					Toast.makeText(FriendsLoopXActivity.this, "删除成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FriendsLoopXActivity.this, "删除失败",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	// 上传背景图片
	public void putCover(final String strUid, final List<MorePicture> listpic) {

		final int UP_SUCCEED = 200;
		final int UP_FAILED = 400;
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == UP_SUCCEED) {
					Toast.makeText(FriendsLoopXActivity.this, "成功",
							Toast.LENGTH_LONG).show();
				} else if (msg.what == UP_FAILED) {
					Toast.makeText(FriendsLoopXActivity.this, "失败",
							Toast.LENGTH_LONG).show();
				}

			}
		};
		Runnable runnableCover = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ResearchJiaState status = ResearchCommon.getResearchInfo()
							.uploadUserBg(strUid, listpic);
					if (status == null) {
						handler.sendEmptyMessage(UP_FAILED);
						return;
					}
					if (status.code != 0) {
						handler.sendEmptyMessage(UP_FAILED);
						return;
					}
					handler.sendEmptyMessage(UP_SUCCEED);
					// 解决上传了图片，本地显示错误 刷新base64.xml文件
					Login login = ResearchCommon
							.getLoginResult(FriendsLoopXActivity.this);
					login.cover = status.frontCover;
					ResearchCommon.saveLoginResult(FriendsLoopXActivity.this,
							login);
				} catch (ResearchException e) {
					// TODO Auto-generated catch block
					handler.sendEmptyMessage(UP_FAILED);
					e.printStackTrace();
				}
			}
		};
		new Thread(runnableCover).start();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case STOP_REFRESH:
				mRefreshLayout.setRefreshing(false);
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				if ((msg.arg1 == GlobalParam.LIST_LOAD_FIRST || msg.arg1 == GlobalParam.LIST_LOAD_REFERSH)
						&& mDataList != null && mDataList.size() > 0) {
					mDataList.clear();
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}

				List<FriendsLoopItem> tempList = (List<FriendsLoopItem>) msg.obj;
				if (tempList != null && tempList.size() > 0) {
					mDataList.addAll(tempList);
//					Log.e("mDataList", mDataList.toString());
					// saveDatas(mDataList);
//					Log.e("保存成功", "保存成功");
				}
				break;
			case GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG:
				FriendsLoopItem item = (FriendsLoopItem) msg.obj;
				int type = msg.arg1; // 1收藏文本 2收藏图片
				int picIndex = msg.arg2;
				mShareId = item.id;
				mToUserId = item.uid;
				showFavoriteDialog(type, item, picIndex, null);
				break;
			/*case GlobalParam.MSG_CHECK_DEL_SHARE_STATUS:
				int posiTion = msg.arg1;
				ResearchJiaState delStatus = (ResearchJiaState) msg.obj;
				if (delStatus == null) {
					Toast.makeText(mContext, R.string.commit_data_error,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (delStatus.code != 0) {
					Toast.makeText(mContext, delStatus.errorMsg,
							Toast.LENGTH_LONG).show();
					return;
				}
				mDataList.remove(posiTion);
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				List<NotifiyVo> messageList = new ArrayList<NotifiyVo>();
				List<NotifiyVo> tempMsgList = ResearchCommon
						.getMovingResult(mContext);
				if (tempMsgList != null && tempMsgList.size() > 0) {
					for (NotifiyVo notifiyVo : tempMsgList) {
						if (notifiyVo.shareId != mShareId) {
							messageList.add(notifiyVo);
						}
					}

					ResearchCommon.saveMoving(mContext, messageList);
				}
				break;*/
			case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
				ResearchJiaState favoriteResult = (ResearchJiaState) msg.obj;
				if (favoriteResult == null) {
					Toast.makeText(mContext, R.string.commit_dataing,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (favoriteResult.code != 0) {
					Toast.makeText(mContext, favoriteResult.errorMsg,
							Toast.LENGTH_LONG).show();
					return;
				}
				break;
			}
		}
	};

	/**
	 * 
	 * @param FriendsLoopItem
	 * @param type
	 *            1-收藏或取消收藏文本 2-收藏或取消收藏图片
	 */
	private void showFavoriteDialog(final int type, final FriendsLoopItem item,
			final int picIndex, final String strFuid) {
		if (item == null) {
			return;
		}
		String[] items;
		if (item.favorite == 1) {
			items = new String[] { getString(R.string.cancle_favorite) };
		} else {
			items = new String[] { getString(R.string.favorite) };
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				FriendsLoopXActivity.this).setItems(items,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (type) {
						case 1:
							MovingContent movingContent = new MovingContent(
									item.content, MessageType.TEXT + "");
							favoriteMoving(
									MovingContent.getInfo(movingContent),
									strFuid);
							break;
						case 2:
							if (picIndex != -1
									&& (item.listpic != null && item.listpic
											.size() > 0)) {
								MovingPic pic = new MovingPic(item.listpic
										.get(picIndex).originUrl, item.listpic
										.get(picIndex).smallUrl,
										MessageType.PICTURE + "");
								favoriteMoving(MovingPic.getInfo(pic), strFuid);
							}
							break;

						default:
							break;
						}
						if (mUpgradeNotifyDialog != null) {
							mUpgradeNotifyDialog.dismiss();
						}
					}
				});
		mUpgradeNotifyDialog = builder.show();
	}

	// custom adapter

    private class MyAdapter extends BaseAdapter implements FriendsLoopItemView.OnCommentListener {

        private Context context;
        private List<FriendsLoopItem> mData;
        private Map<Integer, FriendsLoopItemView> mCachedViews = new HashMap<Integer, FriendsLoopItemView>();

        public MyAdapter(Context context, List<FriendsLoopItem> mData) {
            this.context = context;
            this.mData = mDataList;
        }
        
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            View view2;

            if (convertView != null) {
                view = convertView;
                view2 = convertView;
            } else {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friendsloop_listview_item, null, false);
            }

            if (view instanceof FriendsLoopItemView) {
                FriendsLoopItem data = (FriendsLoopItem) getItem(position);
                ((FriendsLoopItemView) view).setData(data);
                ((FriendsLoopItemView) view).setPosition(position);
                ((FriendsLoopItemView) view).setCommentListener(this);
                cacheView(position, (FriendsLoopItemView) view);
            }

            return view;
        }

        @Override
        public void onComment(int position) {
            showCommentView(position, "", "");
//            showCommentMenu(position,view[0]);
        }
        
        public void onLike(int position){
        	
        }

        private void cacheView(int position, FriendsLoopItemView view) {
            Iterator<Map.Entry<Integer, FriendsLoopItemView>> entries = mCachedViews.entrySet().iterator();

            while (entries.hasNext()) {

                Map.Entry<Integer, FriendsLoopItemView> entry = entries.next();
                if (entry.getValue() == view && entry.getKey() != position) {
                    mCachedViews.remove(entry.getKey());
                    break;
                }
            }

            mCachedViews.put(position, view);

            Log.d("MainActivity", position + ", " + mCachedViews.size());
        }

        private void showCommentMenu(final int position,final View v){
            final FriendsLoopItem data = (FriendsLoopItem) getItem(position);
            View menu = getLayoutInflater().inflate(R.layout.firendloop_comments_menu, null, false);
            final PopupWindow popupWindow = new PopupWindow(menu,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
            popupWindow.setTouchable(true);
            popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
            popupWindow.setOutsideTouchable(true);
            menu.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            OnClickListener mClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    if(v.getId() == R.id.ic_comment_btn){
                        showCommentView(position,"", "");
                    } else if(v.getId() == R.id.ic_comment_zan) {
                        Intent intent = new Intent();
                        intent.setAction(FRIENDSLOOP_PRAISE_CLICKED_NOTIFY);
                        intent.putExtra("fid", data.id+"");
                        data.praises++;
                        mContext.sendBroadcast(intent);
                    }
                }
            };
            menu.findViewById(R.id.ic_comment_btn).setOnClickListener(mClickListener);
            menu.findViewById(R.id.ic_comment_zan).setOnClickListener(mClickListener);
            int measuredWidth = menu.getMeasuredWidth();
            int measuredHeight = menu.getMeasuredHeight();
            int[] location = new int[2];  
            v.getLocationOnScreen(location);
            if(data.ispraise == 0) {
                ((TextView)menu.findViewById(R.id.tv_comments_zan)).setText("赞");
            }else if(data.ispraise == 1) {
                ((TextView)menu.findViewById(R.id.tv_comments_zan)).setText("取消");
            }
            int px = ResearchCommon.dip2px(mContext, 10);
            popupWindow.showAtLocation(v,Gravity.NO_GRAVITY,location[0]-measuredWidth - px,
                    location[1] + v.getHeight() /2 - measuredHeight/2);
        }

        /**
         * Hide or show input method.
         * 
         * @param show
         */
        public void toggleSoftInput(EditText edit,boolean show) {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (show) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    imm.hideSoftInputFromWindow(edit.getApplicationWindowToken(), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void showCommentView(final int position, final String strUid, final String strNickName) {
            mCommentView.setVisibility(View.VISIBLE);
//            mListView.setSelection(position+2);
            mCommentView.findViewById(R.id.friendsloop_edit_comment).requestFocus(); 
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
            imm.showSoftInput(mCommentView.findViewById(R.id.friendsloop_edit_comment),InputMethodManager.SHOW_FORCED);
            
            boolean bReplayOther = false;
            mEtComment.setHint("");
            if(!strUid.isEmpty())
            {
            	bReplayOther = true;
            	mEtComment.setHint("回复 " + strNickName);
            }
            final boolean bReplayOtherFinal = bReplayOther;
            final String strFNickname = bReplayOther ? strNickName : "";
            final String strFUid = strUid;
            EditText et = (EditText) mCommentView.findViewById(R.id.friendsloop_edit_comment);
            toggleSoftInput(et,true);
            mCommentView.findViewById(R.id.friendsloop_btn_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText et = (EditText) mCommentView.findViewById(R.id.friendsloop_edit_comment);
                    et.setHint("");
                    String s = et.getText().toString();
                    if (!TextUtils.isEmpty(s)) {

                        // update model
                        //Comment comment = new Comment(s);
                       // mData.get(position).getComments().add(comment);

                        // update view maybe
                        FriendsLoopItemView itemView = mCachedViews.get(position);
                        if (itemView != null && position == itemView.getPosition()) {
                            itemView.addComment(s, strFUid, bReplayOtherFinal, strFNickname);
                        }
//                    	FriendsLoopItemView itemView = new FriendsLoopItemView(getApplicationContext());
//                    	itemView.addComment(s);	
                        et.setText("");
                        mCommentView.setVisibility(View.GONE);
                        mEmotionLayout.setVisibility(View.GONE);
                        /*InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);   */
                    }
                }
            });
        }
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//if (id == R.id.action_settings) 
		//{
			//return true;
		//}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			// 获取当前获得当前焦点所在View
			View view = getCurrentFocus();
			if (isClickEt(view, event)) {
				// 如果不是edittext或者Button，则隐藏键盘
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager != null) {
					// 隐藏键盘
					mCommentView.setVisibility(View.GONE);
					EditText et = (EditText) mCommentView
							.findViewById(R.id.friendsloop_edit_comment);
					et.setText("");
					mEmotionLayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(
							view.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(event);
		}
		/**
		 * 看源码可知superDispatchTouchEvent 是个抽象方法，用于自定义的Window
		 * 此处目的是为了继续将事件由dispatchTouchEvent(MotionEvent
		 * event)传递到onTouchEvent(MotionEvent event) 必不可少，否则所有组件都不能触发
		 * onTouchEvent(MotionEvent event)
		 */
		if (getWindow().superDispatchTouchEvent(event)) {
			return true;
		}
		return onTouchEvent(event);
	}

	public boolean isClickEt(View view, MotionEvent event) {
		if (view != null && (view instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			view.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			// 此处根据输入框左上位置和宽高获得右下位置
			int bottom = top + view.getHeight()+screenHeightEdit/2;
			// 加上屏幕的宽度，
			int right = left + view.getWidth() + screenWidthEdit;
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		// onRefresh();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		// nLoadTimes = 1;
		// mDataList.clear();
		// long time=System.currentTimeMillis();
		// final Calendar mCalendar=Calendar.getInstance();
		// mCalendar.setTimeInMillis(time);
		// int mHour=mCalendar.get(Calendar.HOUR);
		// int mMinuts=mCalendar.get(Calendar.MINUTE);
		// mListView.setRefreshTime((mMinuts-RefreshTime)+1+"分钟前");
		// RefreshTime = mMinuts;
		// getLoopData();
		if (!isLoadData) {
			mListView.setEnabled(false);
			nLoadTimes = 1;
			isLoadData = true;
			isRefreshData = true;
			getLoopData();
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (!isLoadData) {
			isLoadData = true;
			mListView.LoadMoreing();
			getLoopData();
		}
		// mListView.stopLoadMore();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 根据上面发送过去的请求码来区别
		// Toast.makeText(this, requestCode+"!!!!!!", Toast.LENGTH_LONG).show();
		switch (requestCode) {

		case PhotoPicker.REQUEST_CODE:
			if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) 
			{
				sendType=4;
				ArrayList<String> photos = null;
				if (data != null) {
	                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
	            }
				Intent intent = new Intent();
				intent.setClass(FriendsLoopXActivity.this,
						SendMovingActivity.class);
				if(photos!=null){
					intent.putStringArrayListExtra("selectPhoto", photos);
				}
				intent.putExtra("sendType", sendType);
				startActivityForResult(intent, REQUEST_FRIENDSLOOP_ACTIVITY);
//				//data.putExtra("path", getRealPathFromURI(data.getData()));
//				doChoose(true, data);
//				//doChoose(true, data);
			}
			break;
		
		case REQUEST_GET_IMAGE_BY_CAMERA:
			sendType=2;
			if (data != null && resultCode == ChatMainActivity.REQUEST_GET_PHOTO) {
				Intent intent = new Intent();
				intent.setClass(FriendsLoopXActivity.this,
						SendMovingActivity.class);
//				String path=new File(Environment.getExternalStorageDirectory(),
//						filename+".jpg").getAbsolutePath();
				intent.putExtra("photoPath", basepath+filename+".jpg");
				intent.putExtra("sendType", sendType);
				startActivityForResult(intent, REQUEST_FRIENDSLOOP_ACTIVITY);
			} else if (data != null && resultCode == ChatMainActivity.REQUEST_GET_VIDEO) {
				sendType=3;
				Intent intent = new Intent();
				intent.setClass(FriendsLoopXActivity.this,
						SendMovingActivity.class);
//				String path=new File(Environment.getExternalStorageDirectory(),
//						filename+".mp4").getAbsolutePath();
				intent.putExtra("videoPath", basepath+filename+".mp4");
				intent.putExtra("sendType", sendType);
				startActivityForResult(intent, REQUEST_FRIENDSLOOP_ACTIVITY);
//				sendFile(MessageType.VIDEO,
//						new File(Environment.getExternalStorageDirectory(),
//								filename + ".mp4").getAbsolutePath());
			}
			break;

		case REQUEST_FRIENDSLOOP_ACTIVITY:
			if (resultCode == RESULT_FRIENDSLOOP_ACTIVITY) {
				mAdapter.notifyDataSetChanged();
				onRefresh();
			}
			break;
		case GlobalParam.REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}
			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				doChoose(false, data);
			}
			break;
		case GlobalParam.REQUEST_GET_BITMAP:
			/* try { */
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				// String path = data.getStringExtra("path");
				if (mCropImgPath != null && !mCropImgPath.equals("")) {
					// if (extras != null) {
					String path = mCropImgPath;
					mHeaderBg.setImageBitmap(null);
					if (mBitmap != null && !mBitmap.isRecycled()) {
						mBitmap.recycle();
						mBitmap = null;
					}

					// mBitmap = extras.getParcelable("data");
					mBitmap = BitmapFactory.decodeFile(path);
					if (mBitmap != null) {
						mHeaderBg.setImageBitmap(mBitmap);
					}
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ FeatureFunction.PUB_TEMP_DIRECTORY
									+ mTempFileName + ".jpg");
					if (file != null && file.exists()) {
						file.delete();
						file = null;
					}

					// mCropImgPath = FeatureFunction.saveTempBitmap(mBitmap,
					// "album.jpg");
					//showModifybgDialog();
					uploadBg();
				}
				// Log.e("FriendsLoopActivity", "REQUEST_GET_BITMAP");

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 处理选择的图片
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if (isGallery) {
			originalImage(data);
		} else {
			if (data != null) {
				originalImage(data);
			} else {
				// Here if we give the uri, we need to read it
				String path = Environment.getExternalStorageDirectory()
						+ FeatureFunction.PUB_TEMP_DIRECTORY + mTempFileName;
				startPhotoZoom(Uri.fromFile(new File(path)));
				// mImageFilePath =
				// FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
				// ShowBitmap(false);
			}
		}
	}

	/*
	 * 显示更换背景提示框
	 */
	private void showModifybgDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				FriendsLoopXActivity.this);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(this.getResources().getString(
				R.string.are_you_change_bg));
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 这里添加点击确定后的逻辑
						// showDialog("你选择了确定");
						// onRefresh();
						uploadBg();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 这里添加点击确定后的逻辑
						// showDialog("你选择了取消");
						if (mMyAlbum != null && mMyAlbum.frontCover != null
								&& !mMyAlbum.frontCover.equals("")) {
							mImageLoader.getBitmap(FriendsLoopXActivity.this,
									mHeaderBg, null, mMyAlbum.frontCover, 0,
									false, false);
						} else {
							mHeaderBg.setImageResource(R.drawable.head_img);
						}
					}
				});
		builder.create().show();

	}

	private void originalImage(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
//		Log.e("LT", uri + "!!!");
		if (uri != null) {
			// Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				Cursor cursor = getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				if (null == cursor) {
					Toast.makeText(this, R.string.no_found, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				cursor.moveToFirst();
				Environment.getExternalStorageDirectory();
				String imageFilePath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				Log.d("may", "path=" + imageFilePath);
				startPhotoZoom(uri);
				// ShowBitmap(false);

			} else {
				Log.d("may", "path=" + uri.getPath());
				//修改后的代码
				startPhotoZoom(uri);
				//修改前的代码
				/*Intent intent = new Intent(this, FriendsLoopXActivity.class);
				intent.putExtra("path", uri.getPath());
				startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);*/
				
				// mImageFilePath = uri.getPath();
				// ShowBitmap(false);
			}
		} else {
			Intent intent = new Intent(this, RotateImageActivity.class);
			intent.putExtra("path", Environment.getExternalStorageDirectory()
					+ FeatureFunction.PUB_TEMP_DIRECTORY + mTempFileName);
			startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
		}
	}

	/*
	 * 裁剪图片
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 2);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", /* ICON_SIZE_WIDTH */720);
		intent.putExtra("outputY", /* ICON_SIZE_HEIGHT */480);
		mCropImgPath = Environment.getExternalStorageDirectory()
				+ FeatureFunction.PUB_TEMP_DIRECTORY + "album.jpg";
		File file = new File(mCropImgPath);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		// Uri imageUri = Uri.parse(mCropImgPath);
		Uri imageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		Log.e("startPhotoZoom", "width:" + ICON_SIZE_WIDTH + " height:"
				+ ICON_SIZE_HEIGHT);
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
	}

	/*
	 * 更换背景
	 */
	private void uploadBg() {
		if (mCropImgPath != null && !mCropImgPath.equals("")) {

		}
		mListpic.add(new MorePicture("picture", mCropImgPath));
		putCover(strMyUid, mListpic);
	}

	/*
	 * 收藏图片
	 */
	private void favoriteMoving(final String favoriteContent,
			final String strFuid) {
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(R.string.send_request));
					ResearchJiaState status = ResearchCommon.getResearchInfo()
							.favoreiteMoving(strFuid, null, favoriteContent);
					// putContentFavorite(strFuid, null, favoriteContent);
					// ResearchCommon.sendMsg(mHandler,
					// GlobalParam.MSG_CHECK_FAVORITE_STATUS, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (ResearchException e) {
					e.printStackTrace();
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_MSG_TIMEOUT_ERROR, mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}
	// 存集合的方法
	private void saveDatas(List<FriendsLoopItem> flilist) {
		String flilistArray = GsonUtil.getGson().toJson(flilist);
		mCache.put("key", flilistArray);
	}

	// 取集合的方法
	private List<FriendsLoopItem> readDatas() {
		org.json.JSONArray result = mCache.getAsJSONArray("key");

		Type mType = new TypeToken<List<FriendsLoopItem>>() {}.getType();
		List<FriendsLoopItem> flilist = new ArrayList<FriendsLoopItem>();
		try {
			flilist = GsonUtil.getGson().fromJson(result.toString(), mType);
		} catch (Exception e) {
			// TODO: handle exception
//			Log.e("空指针AAA", "空指针");
		}
		return flilist;
	}
	
	//获取缓存
	private void getLoopDataCache() {
		try {
//				if (mData != null) {
//					while (mData.size() > 1) {
//						mData.remove(1);
//					}
//				}
			//SharedPreferences保存是否是首次使用朋友圈，首次不取缓存。
			SharedPreferences setting = getSharedPreferences(ResearchCommon.getUserId(mContext)+"FriendsLoopXActivity", 0);  
		    Boolean user_first = setting.getBoolean("FIRST",true);  
		    if(user_first){//第一次  
		        setting.edit().putBoolean("FIRST", false).commit();  
		        Log.e("保存状态成功", "保存状态成功");
		    }else{ 
		    	if(mDataList.size()==0){
		    		Log.v("liaotian", "mDatasize()"+mDataList.size());
		            mDataList = readDatas();
		 			Log.v("liaotian", "mData=!!!" + mDataList.size());
		 			if (mDataList != null && mDataList.size() > 0) {
		 				mAdapter = new MyAdapter(this, mDataList);
		 				mListView.setAdapter(mAdapter);
		 				Log.e("liaotian", "取数据成功");
		 			}
		    	}
		    	saveDatas(mDataList);
		    }  
			}catch (Exception e) {
			e.printStackTrace();
		}

	}
	// 表情
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		mPageIndxe = position;
		showCircle(mViewList.size());
	}
	
	/**
	 * 获取表情key列表
	 * 
	 * @return
	 * @author fighter <br />
	 *         创建时间:2013-6-21<br />
	 *         修改时间:<br />
	 */

	private List<List<String>> getEmojiList() {
		List<String> emojiList = new ArrayList<String>();
		for (int i = 0; i < EmojiDatas.EMOJI_KEY.length; i++) {
			emojiList.add(EmojiDatas.EMOJI_KEY[i]);
		}
		List<List<String>> totalList = new ArrayList<List<String>>();
		int page = emojiList.size() % 21 == 0 ? emojiList.size() / 21
				: emojiList.size() / 21 + 1;
		for (int i = 0; i < page; i++) {
			int startIndex = i * 21;
			List<String> singleList = new ArrayList<String>();
			if (singleList != null) {
				singleList.clear();
			}
			int endIndex = 0;
			if (i < page - 1) {
				endIndex = startIndex + 21;
			} else if (i == page - 1) {
				endIndex = emojiList.size() - 1;
			}
			singleList.addAll(emojiList.subList(startIndex, endIndex));
			totalList.add(singleList);

		}

		return totalList;
	}
	
	/**
	 * 添加表情滑动控件
	 * 
	 * @param i
	 *            添加的位置
	 */
	private void addView(final int i) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.emotion_gridview, null);
		GridView gridView = (GridView) view.findViewById(R.id.emoji_grid);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < mTotalEmotionList.get(i).size() - 1
						|| i == mTotalEmotionList.size() - 1) {
					ImageView imageView = (ImageView) view
							.findViewById(R.id.emotion);
					if (imageView != null) {
						Drawable drawable = imageView.getDrawable();
						if (drawable instanceof BitmapDrawable) {
							Bitmap bitmap = ((BitmapDrawable) drawable)
									.getBitmap();
							String name = mTotalEmotionList.get(i)
									.get(position);
							// Log.e("aaaaaa", name);

							String key = name;
							for (int j = 0; j < EmojiDatas.EMOJI_KEY.length; j++) {
								if (name.equals(EmojiDatas.EMOJI_KEY[j])) {
									key = EmojiDatas.EMOJI_KEY[j];
									// Log.e("bbbbbb",key);
									break;
								}
							}
							// Log.e("ccccc",key);

							Drawable mDrawable = new BitmapDrawable(
									getResources(), bitmap);
							int width = getResources().getDimensionPixelSize(
									R.dimen.pl_emoji);
							int height = width;
							mDrawable.setBounds(0, 0, width > 0 ? width : 0,
									height > 0 ? height : 0);
							ImageSpan span = new ImageSpan(mDrawable);

							SpannableString spannableString = new SpannableString(
									key);
							// 类似于集合中的(start, end)，不包括起始值也不包括结束值。
							// 同理，Spannable.SPAN_INCLUSIVE_EXCLUSIVE类似于
							// [start，end)
							spannableString.setSpan(span, 0,
									spannableString.length(),
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							Editable dEditable = mEtComment.getEditableText();
							int index = mEtComment.getSelectionStart();
							dEditable.insert(index, spannableString);
						}
					}
				} else {
					int index = mEtComment.getSelectionStart();

					String text = mEtComment.getText().toString();
					if (index > 0) {
						String text2 = text.substring(index - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = index;
							mEtComment.getText().delete(start, end);
							return;
						}
						mEtComment.getText().delete(index - 1, index);
					}
				}
			}

		});
		gridView.setAdapter(new EmojiAdapter(mContext,
				mTotalEmotionList.get(i), ResearchCommon.mScreenWidth, i));
		mViewList.add(view);
	}
	
	/**
	 * 显示表情处于第几页标志
	 * 
	 * @param size
	 */
	private void showCircle(int size) {
		mLayoutCircle.removeAllViews();

		for (int i = 0; i < size; i++) {
			ImageView img = new ImageView(mContext);
			img.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction
					.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5)));
			LinearLayout layout = new LinearLayout(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int margin = FeatureFunction.dip2px(mContext, 5);
			params.setMargins(margin, 0, margin, 0);
			layout.setLayoutParams(params);
			layout.addView(img);
			// img.setLayoutParams()
			if (mPageIndxe == i) {
				img.setImageResource(R.drawable.circle_d);
			} else {
				img.setImageResource(R.drawable.circle_n);
			}
			mLayoutCircle.addView(layout);
		}
	}
}