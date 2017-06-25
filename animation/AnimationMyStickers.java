package com.edgelesschat.animation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.http.RequestParams;

import com.edgelesschat.BaseActivity;
import com.edgelesschat.ChatMainActivity;
import com.edgelesschat.R;
import com.edgelesschat.DB.AnimationTable;
import com.edgelesschat.DB.DBHelper;
import com.edgelesschat.animation.ProgressButton.OnStateListener;
import com.edgelesschat.global.ResearchCommon;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AnimationMyStickers extends BaseActivity implements OnClickListener {
	
	public static final String ANIMATION_ACTIVITY_NOTIFYCHANGEDATA = "animation_activity_notifydatachange";

	private ListView mListView;
	private LinearLayout mLinearLayout;
	private List<Animation> mData = new ArrayList<Animation>();
	private LinearLayout left_btn;
	private MyAdapter mAdapter;
	private static final String URL_REMOVEGIF = "https://www.520219.com/lediao/index.php/animation/api/remove";
	public static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator
			+ "EdgelessChat" + File.separator;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animation_my_stickers);
		initView();
		getLoopData();
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.finish();
	}

	private void getLoopData() {
		try {
			SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
			AnimationTable table = new AnimationTable(db);
			if (mData.size() == 0 && table.query().size() != 0) {
				mData = table.query();
				mListView.setVisibility(View.VISIBLE);
				mLinearLayout.setVisibility(View.GONE);
			} else {
				mListView.setVisibility(View.GONE);
				mLinearLayout.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.animation_listview);
		mLinearLayout = (LinearLayout) findViewById(R.id.animation_lin_my_stickers_background);
		setTitleContent(R.drawable.ic_return, 0, R.string.animation_sticker_gallery);
		setTitleOnClick();
	}

	private void setTitleOnClick() {
		// TODO setTitleOnClick
		left_btn = (LinearLayout) findViewById(R.id.left_btn);
		left_btn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn:
			
			Intent intent = new Intent();
			intent.setAction(ANIMATION_ACTIVITY_NOTIFYCHANGEDATA);
			sendBroadcast(intent);
			this.finish();
			break;
			
		default:
			break;
		}

	}

	void removeAnimation(String strUid, String strGifId, String strDownload) {
		SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
		AnimationTable table = new AnimationTable(db);
		table.remove(strGifId, strUid);
		String mFolderName = AnimationActivity.getFolderNameFromUrl(strDownload);
		File file = new File(strDownload);
		Log.e("liaotian", "BASE_PATH+mFolderName=" + BASE_PATH + "/" + mFolderName);
		deleteFile(file);
		
		Intent intent=new Intent(ChatMainActivity.GIF_NOTIFY);
		intent.putExtra("dleGifId", strGifId);
		this.sendBroadcast(intent);
	}

	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
		}
	}

	class MyAdapter extends BaseAdapter {
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

		void registerDownloadButtonClickEvent(Button button, final String strUid, final String strGifId,
				final String strGifName) {
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeAnimation(strUid, strGifId, strGifName);
					removeAnimationFile(strUid, strGifId, strGifName);
				}
			});
		}

		void removeAnimationFile(final String strUid, final String strGifId, final String strDownload) {
			RequestParams params = new RequestParams(URL_REMOVEGIF);
			params.addBodyParameter("uid", strUid);
			params.addBodyParameter("gifid", strGifId);
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
				if(strResponse.contains("{\"result\":200")){
						for (int index = 0; index < mData.size(); index++) {
							if (mData.get(index).id.equals(strGifId)) {
								mData.remove(index);
								mAdapter.notifyDataSetChanged();
								Log.e("liaotian", "strResponse=" + strResponse + " 移除gif成功！");
								break;
							}
						}
						if (mData.size() == 0) {
							mListView.setVisibility(View.GONE);
							mLinearLayout.setVisibility(View.VISIBLE);
						}
						Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public boolean onCache(String arg0) {
					return false;
				}
			});
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (null == view) {
				holder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(R.layout.animation_my_stickers_item, null, true);
				holder.mAnimationCover = (ImageView) view.findViewById(R.id.animation_listview_item_cover);
				holder.mAnimationHeader = (TextView) view.findViewById(R.id.animation_listview_item_header);
				holder.mAnimationDel = (Button) view.findViewById(R.id.animation_listview_item_del);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			Picasso.with(mContext).load(mData.get(position).gif_cover)
					.placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.mAnimationCover);
			holder.mAnimationHeader.setText(mData.get(position).gif_name);
			registerDownloadButtonClickEvent(holder.mAnimationDel, ResearchCommon.getUserId(mContext),
					mData.get(position).getId(), mData.get(position).getGif_folder());
			return view;
		}

		class ViewHolder {
			ImageView mAnimationCover;
			TextView mAnimationHeader;
			Button mAnimationDel;
		}
	}
}
