package com.edgelesschat.animation;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.afinal.simplecache.ACache;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import com.edgelesschat.BaseActivity;
import com.edgelesschat.ChatMainActivity;
import com.edgelesschat.R;
import com.edgelesschat.DB.AnimationTable;
import com.edgelesschat.DB.DBHelper;
import com.edgelesschat.animation.ProgressButton.OnStateListener;
import com.edgelesschat.global.ResearchCommon;
import com.edgelesschat.org.json.JSONArray;
import com.edgelesschat.org.json.JSONException;
import com.edgelesschat.org.json.JSONObject;
import com.edgelesschat.utils.GsonUtil;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class AnimationActivity extends BaseActivity {
	public static final String APPKEY = "0e93f53b5b02e29ca3eb6f37da3b05b9";
	private ListView mListView;
	private HomeAdapter mAdapter;
	private static final String URL_PREFIX = "https://www.520219.com/lediao/index.php/Animation/api/getlist";
	private static final String URL_SAVEGIF = "https://www.520219.com/lediao/index.php/animation/api/save";
	private static final String URL_VPCGIF = "https://www.520219.com/lediao/index.php/Animation/api/getcoverlist";
	private static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator
			+ "EdgelessChat" + File.separator;
	public static final String ANIMATION_ACTIVITY_NOTIFYCHANGEDATA = "animation_activity_notifydatachange";
	public static final int NOTIFY = 101;
	private List<Animation> mData = new ArrayList<Animation>();
	private List<AnimationViewPagerCover> mDataVPC = new ArrayList<AnimationViewPagerCover>();
	private DynamicReceiver dynamicReceiver;
	private LinearLayout left_btn;
	private ImageView right_btn;
	public ACache mCache, mCacheAnimation;
	private Boolean isFirstIn = false;  
	
	final Handler mHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NOTIFY) {
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animation_main);
		initRegister();
		initView();
		getLoopDataCache();				
		getLoopData();						//网络拉取数据
		getLoopDataVPC();					//轮播图网络数据

		mAdapter = new HomeAdapter();
		//判断是否是第一次进入activity
        SharedPreferences pref = mContext.getSharedPreferences("AnimationActivity"+ResearchCommon.getUserId(mContext), 0);  
        //取得相应的值，如果没有该值，说明还未写入，用true作为默认值  
        isFirstIn = pref.getBoolean("isFirstIn", true);
		if (isFirstIn) {
			Animation mAnimation = new Animation();
			mData.add(mAnimation);
			Log.e("liaotian", "进入对象创建判断!");
		}
		mListView.setAdapter(mAdapter);
	}
	
	//获取缓存
	private void getLoopDataCache() {
		try {
//			if (mData != null) {
//				while (mData.size() > 1) {
//					mData.remove(1);
//				}
//			}
            Log.v("liaotian", "mDatasize()"+mData.size());
			mData = readDatasAnimation();
			mDataVPC = readDatas();
			Log.v("liaotian", "mData=!!!" + mData.size() + " mDataVPC=" + mDataVPC.size());
			if (mData != null && mDataVPC != null) {
				mHandle.sendEmptyMessage(NOTIFY);
			}
			}catch (Exception e) {
			e.printStackTrace();
		}

	}
	//注册广播
	private void initRegister() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ANIMATION_ACTIVITY_NOTIFYCHANGEDATA);
		dynamicReceiver = new DynamicReceiver();
		registerReceiver(dynamicReceiver, filter);
	}

	// 广播
	class DynamicReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == ANIMATION_ACTIVITY_NOTIFYCHANGEDATA) {
				notifyData();
			}
		}
	}

	private void notifyData() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.finish();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	public static String getFolderNameFromUrl(String strDownload) {
		String strFileName = "";
		int nPos = strDownload.lastIndexOf('/');
		int nPos2 = strDownload.lastIndexOf('.');
		if ((nPos != -1) && (nPos2 != -1)) {

			strFileName = strDownload.substring(nPos + 1, nPos2);
		}
		return strFileName;
	}

	boolean isFolderExists(String strGifId) {
		SQLiteDatabase db = DBHelper.getInstance(this).getReadableDatabase();
		AnimationTable table = new AnimationTable(db);
		Animation animationInfo = table.query(strGifId);
		return (animationInfo != null);
	}

	public boolean DeleteFolder(String sPath) {
		Log.e("liaotian", "sPath=" + sPath);
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return false;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return file.delete();
			}
			return true;
		}
	}

	public void saveAnimation(Animation animationInfo) {
		SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
		AnimationTable table = new AnimationTable(db);
		table.insert(animationInfo);
		Intent intent=new Intent(ChatMainActivity.GIF_NOTIFY);
		intent.putExtra("addGifId", animationInfo.id);
		this.sendBroadcast(intent);
	}

	private void initView() {
		setTitleContent(R.drawable.ic_return, R.drawable.animation_setting_selector,
				R.string.animation_sticker_gallery);
		setTitleOnClick();
		mCache = ACache.get(this, ResearchCommon.getUserId(mContext));// 初始化ACache
		mCacheAnimation = ACache.get(this, ResearchCommon.getUserId(mContext)+"Animation");
		mListView = (ListView) findViewById(R.id.animation_recycleview);
	}

	// 存集合的方法
	private void saveDatas(List<AnimationViewPagerCover> flilist) {
		String flilistArray = GsonUtil.getGson().toJson(flilist);
		mCache.put("key", flilistArray);
	}

	// 取集合的方法
	private List<AnimationViewPagerCover> readDatas() {
		org.json.JSONArray result = mCache.getAsJSONArray("key");

		Type mType = new TypeToken<List<AnimationViewPagerCover>>() {}.getType();
		List<AnimationViewPagerCover> flilist = new ArrayList<AnimationViewPagerCover>();
		try {
			flilist = GsonUtil.getGson().fromJson(result.toString(), mType);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("空指针AAA", "空指针");
		}
		return flilist;
	}
	
	// 存集合的方法
		private void saveDatasAnimation(List<Animation> flilist) {
			String flilistArray = GsonUtil.getGson().toJson(flilist);
			mCacheAnimation.put("key", flilistArray);
		}

		// 取集合的方法
		private List<Animation> readDatasAnimation() {
			org.json.JSONArray result = mCacheAnimation.getAsJSONArray("key");

			Type mType = new TypeToken<List<Animation>>() {}.getType();
			List<Animation> flilist = new ArrayList<Animation>();
			try {
				flilist = GsonUtil.getGson().fromJson(result.toString(), mType);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("空指针AAA", "空指针");
			}
			return flilist;
		}

	private void setTitleOnClick() {
		// TODO Auto-generated method stub
		left_btn = (LinearLayout) findViewById(R.id.left_btn);
		right_btn = (ImageView) findViewById(R.id.right_btn);

		left_btn.setOnClickListener(this);
		right_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.right_btn:
			Intent mIntent = new Intent(AnimationActivity.this, AnimationMyStickers.class);
			startActivity(mIntent);
			break;
		default:
			break;
		}

	}

	private void getLoopData() {
		String strURL = URL_PREFIX;
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
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
				// if(strRetval.contains("{\"count\":"))
				// {
				try {
					// JSON解析
					Log.e("liaotian", "strRetval=" + strRetval);
					JSONObject parentJson = new JSONObject(strRetval);
					JSONArray array = parentJson.getJSONArray("data");
					Log.e("liaotian", "mData.size()1=" + mData.size());
					if (mData != null) {
						while (mData.size() > 1) {
							mData.remove(1);
						}
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						Animation mAnimation = new Animation(object);
						mData.add(mAnimation);
						Log.e("liaotian", "mData.size()2=" + mData.size());
					}
					if (mData != null) {
						saveDatasAnimation(mData);
						if (isFirstIn) {
							SharedPreferences pref = mContext.getSharedPreferences("AnimationActivity"+ResearchCommon.getUserId(mContext), 0);  
							Editor editor = pref.edit();  
							editor.putBoolean("isFirstIn", false);  
							editor.commit();  
						}
						Log.e("liaotian", "mDatas.size()3=" + mData.size());
					}
					// 刷新适配器
					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// }
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	private void getLoopDataVPC() {
		String strURL = URL_VPCGIF;
		RequestParams params = new RequestParams(strURL);
		params.addBodyParameter("appkey", APPKEY);
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
				// if(strRetval.contains("{\"count\":"))
				// {
				try {
					// JSON解析
					if (mDataVPC != null) {
						mDataVPC.clear();
					}
					Log.e("liaotian", "strRetval=" + strRetval);
					JSONObject parentJson = new JSONObject(strRetval);
					JSONArray array = parentJson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						AnimationViewPagerCover mAnimationVPC = new AnimationViewPagerCover(object);
						mDataVPC.add(mAnimationVPC);
						saveDatas(mDataVPC);
					}
					if (mDataVPC != null) {
						Log.e("liaotian", "mDataVPC.size()=" + mDataVPC.size());
					}
					// 刷新适配器
					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// }
			}

			@Override
			public boolean onCache(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

		});
	}

	class HomeAdapter extends BaseAdapter {

		public static final int FIRST_LETTER_ITEM = 0;
		public static final int CONTENT_ITEM = 1;
		private int nTypeCount = 2;

		String getFileNameFromUrl(String strDownload) {
			String strFileName = "";
			int nPos = strDownload.lastIndexOf('/');
			if (nPos != -1) {
				strFileName = strDownload.substring(nPos + 1, strDownload.length());
			}
			return strFileName;
		}

		void downloadFile(final ProgressButton button, final String strDownload, final Animation animationInfo) {
			final String strFileName = getFileNameFromUrl(strDownload);
			if (strFileName.length() < 4) {
				button.setText("失 败");
				return;
			}
			final String path = BASE_PATH + strFileName;
			RequestParams requestParams = new RequestParams(strDownload);
			requestParams.setSaveFilePath(path);
			x.http().get(requestParams, new Callback.ProgressCallback<File>() {
				@Override
				public void onWaiting() {
				}

				@Override
				public void onStarted() {
				}

				@Override
				public void onLoading(long total, long current, boolean isDownloading) {
					int nPercent = (int) (((double) current / total) * 100);
					button.setProgress(nPercent);
				}

				@Override
				public void onSuccess(File result) {
//					Toast.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show();
					// path
					if (registerDownloadZip(path)) {
						DeleteFolder(path);
						String mFolderName = AnimationActivity.getFolderNameFromUrl(strDownload);
						String strFolderPath = BASE_PATH + mFolderName;
						animationInfo.setGif_folder(strFolderPath);
						saveAnimation(animationInfo);
						saveAnimationFile(ResearchCommon.getUserId(mContext), animationInfo.id);
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
					ex.printStackTrace();
					Toast.makeText(mContext, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
					// progressDialog.dismiss();
				}

				@Override
				public void onCancelled(CancelledException cex) {
				}

				@Override
				public void onFinished() {
				}
			});
		}

		void saveAnimationFile(final String strUid, final String gifId) {
			RequestParams params = new RequestParams(URL_SAVEGIF);
			params.addBodyParameter("uid", strUid);
			params.addBodyParameter("gifid", gifId);
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
				public void onSuccess(String strResponse) {
					// TODO Auto-generated method stub
					Log.e("liaotian", "strResponse=" + strResponse + " 保存成功！");
				}

				@Override
				public boolean onCache(String arg0) {
					return false;
					// TODO Auto-generated method stub
				}
			});
		}

		void registerDownloadButtonClickEvent(final ProgressButton button, final String strDownload,
				final Animation animationInfo) {
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Integer) button.getTag() == 0) {
						button.setTag(Integer.parseInt("1"));
						button.setProgress(0);
						downloadFile(button, strDownload, animationInfo);
					}
					if (!button.isFinish()) {
						button.toggle();
					}
				}
			});

			button.setOnStateListener(new OnStateListener() {

				@Override
				public void onStop() {
					button.setText("继 续");
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					button.setText("完 成");

				}

				@Override
				public void onContinue() {
					// TODO Auto-generated method stub

				}
			});
		}

		public boolean registerDownloadZip(String strDownload) {
			Log.e("liaotian", "BASE_PATH=" + BASE_PATH + " path=" + strDownload);
			String mFolderName = AnimationActivity.getFolderNameFromUrl(strDownload);
			try {
				ZipFile zipFile = new ZipFile(strDownload);
				zipFile.extractAll(BASE_PATH + "/" + mFolderName);
				return true;
			} catch (ZipException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return (position == 0 ? FIRST_LETTER_ITEM : CONTENT_ITEM);
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return nTypeCount;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			final MyViewHolder holder;

			if (view == null) {
				holder = new MyViewHolder();
				if (getItemViewType(position) == FIRST_LETTER_ITEM) {
					view = LayoutInflater.from(mContext).inflate(R.layout.animation_viewpager_item_layout, null, true);
					holder.mViewPager = (ViewPager) view.findViewById(R.id.pager);
					View view1 = (View) LayoutInflater.from(mContext).inflate(R.layout.animation_viewpager_image_layout,
							null);
					View view2 = (View) LayoutInflater.from(mContext)
							.inflate(R.layout.animation_viewpager_image_layout_two, null);
					View view3 = (View) LayoutInflater.from(mContext).inflate(R.layout.animation_viewpager_image_layout,
							null);
					ArrayList<ImageView> views = new ArrayList<ImageView>();
					holder.viewPageOne = (ImageView) view1.findViewById(R.id.image_subpage);
					holder.viewPageTwo = (ImageView) view2.findViewById(R.id.image_subpage);
					holder.viewPageThree = (ImageView) view3.findViewById(R.id.image_subpage);
					views.add(holder.viewPageOne);
					views.add(holder.viewPageTwo);
					views.add(holder.viewPageThree);
					holder.mViewPager.setAdapter(new ImageAdapter(views));
				} else {
					view = LayoutInflater.from(mContext).inflate(R.layout.animation_item, null, true);
					holder.animationTextViewHeader = (TextView) view.findViewById(R.id.animation_header);
					holder.animationTextViewContent = (TextView) view.findViewById(R.id.animation_content);
					holder.animationImageView = (ImageView) view.findViewById(R.id.animation_avater);
					holder.mButtonDownLoad = (ProgressButton) view.findViewById(R.id.animation_download);
				}
				view.setTag(holder);
			} else {
				holder = (MyViewHolder) view.getTag();
			}
			if (getItemViewType(position) == CONTENT_ITEM) {
				Log.e("liaotian", "mData(adapter)="+mData.size());
				saveDatasAnimation(mData);
				holder.animationTextViewHeader.setText(mData.get(position).gif_name);
				holder.animationTextViewContent.setText(mData.get(position).gif_desc);
				Picasso.with(mContext).load(mData.get(position).gif_cover)
						.placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.animationImageView);
				holder.mButtonDownLoad.setTag(Integer.parseInt("0"));
				if (!isFolderExists(mData.get(position).getId())) {
					Animation animationInfo = mData.get(position);
					registerDownloadButtonClickEvent(holder.mButtonDownLoad, mData.get(position).gif_location,
							animationInfo);
					holder.mButtonDownLoad.setClickable(true);
					holder.mButtonDownLoad.setText("添 加");
					holder.mButtonDownLoad.initState();
				} else {
					holder.mButtonDownLoad.setClickable(false);
					holder.mButtonDownLoad.iniStateFinish();
					holder.mButtonDownLoad.setText("已下载");
				}
			} else if (getItemViewType(position) == FIRST_LETTER_ITEM) {
				if (mDataVPC.size() > 0) {
					Picasso.with(mContext).load(mDataVPC.get(0).gif_cover).error(R.drawable.animation_page_default)
							.into(holder.viewPageOne);
					Picasso.with(mContext).load(mDataVPC.get(1).gif_cover).error(R.drawable.animation_page_default)
							.into(holder.viewPageTwo);
					Log.v("liaotian", "mDataVPC.get(0).gif_cover=" + mDataVPC.get(0).gif_cover
							+ " mDataVPC.get(1).gif_cover" + mDataVPC.get(1).gif_cover);
				}
			}
			return view;
		}

		class MyViewHolder {

			TextView animationTextViewHeader;
			TextView animationTextViewContent;
			ImageView animationImageView;
			ViewPager mViewPager;
			ProgressButton mButtonDownLoad;
			ImageView viewPageOne, viewPageTwo, viewPageThree;
		}
	}

	private class ImageAdapter extends PagerAdapter {

		private ArrayList<ImageView> viewlist;

		public ImageAdapter(ArrayList<ImageView> viewlist) {
			this.viewlist = viewlist;
		}

		@Override
		public int getCount() {
			// 设置成最大，使用户看不到边界
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// Warning：不要在这里调用removeView
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 对ViewPager页号求模取出View列表中要显示的项
			position %= viewlist.size();
			if (position < 0) {
				position = viewlist.size() + position;
			}
			ImageView view = viewlist.get(position);
			// 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
			ViewParent vp = view.getParent();
			if (vp != null) {
				ViewGroup parent = (ViewGroup) vp;
				parent.removeView(view);
			}
			container.addView(view);
			// add listeners here if necessary
			return view;
		}
	}
}
