package com.edgelesschat.friendsloop;



import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.edgelesschat.R;

/**
 * 底部footView加载更多
 * 直接使用无须修改
 * @author max
 *
 */
public class RefreshFooter extends LinearLayout {
	// 原始状�??
	public final static int STATE_NORMAL = 0;
	// 正在下拉
	public final static int STATE_READY = 1;
	// 正在刷新
	public final static int STATE_LOADING = 2;
	// 加载完毕
	public final static int STATE_FINISH = 3;
	
	private Context mContext;
	
	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;

	public RefreshFooter(Context context) {
		super(context);
		initView(context);
	}
	
	
	
	public RefreshFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}



	//初始化控�?
	private void initView(Context context) {
		mContext = context;
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.friendsloop_refresh_footer, null);
		addView(moreView);
		//设定宽高
		moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		mContentView = moreView.findViewById(R.id.refresh_footer_content);
		mProgressBar = moreView.findViewById(R.id.refresh_footer_progressbar);
		mHintView = (TextView) moreView.findViewById(R.id.refresh_footer_hint_textview);
	}
	
	//不同状�?�下的显示和隐藏
	public void setState(int state){
		//全部消失
		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		//准备上拉
		if (state == STATE_READY) {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText("松开加载更多");
		} 
		//正在加载
		else if (state == STATE_LOADING) {
			//显示进度�?
			mProgressBar.setVisibility(View.VISIBLE);
		}else if (state == STATE_NORMAL) {
			//原始状�??,就显示查看更�?
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText("查看更多");
		}else if (state == STATE_FINISH) {
			//原始状�??,就显示查看更�?
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText("暂无更多的数据");
		}
	}
	
	/**
	 * 设置底部的距�?
	 * @param height
	 */
	public void setBottomMargin(int height){
		if (height < 0) {
			return;
		}
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = height;
		
		mContentView.setLayoutParams(lp);
	}
	
	/**
	 * 获取到底部的距离
	 */
	public int getBottomMargin(){
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		return lp.bottomMargin;
	}
	
	/**
	 * 初始状�?�显示文�?
	 */
	public void normal(){
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}
	
	/**
	 * 加载状�??,显示进度�?
	 */
	public void loading(){
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏底部
	 */
	public void hide(){
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = 0;
		mHintView.setLayoutParams(lp);
	}
	
	/**
	 * 显示底部
	 */
	public void show(){
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}
}
