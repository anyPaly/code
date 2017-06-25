package com.edgelesschat.friendsloop;



import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.edgelesschat.R;

/**
 * 刷新headview
 * 直接使用无须修改
 * @author max
 *
 */
public class RefreshHeader extends LinearLayout {
	//原始状�??
	public final static int STATE_NORMAL = 0;
	//正在下拉
	public final static int STATE_READY = 1;
	//正在刷新
	public final static int STATE_REFRESHING = 2;
	
	//refresh_footer.xml里面的控�?
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	
	/**
	 * 当前头部的状�?
	 * @param context
	 */
	private int mState = STATE_NORMAL;
	//加载动画
	private Animation mRotateAnim;
	private Animation mRotateDownAnim;
	
	private final int ROATE_ANIM_DURATION = 180;
	
	

	public RefreshHeader(Context context) {
		super(context);
		initView(context);
	}


	/**
	 * 初始�?
	 * @param context
	 */
	private void initView(Context context) {
		//初始化情�?,下拉刷新的高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LinearLayout.inflate(context, R.layout.friendsloop_refresh_header, null);
		//为linearlayout添加子view
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);
		//初始化控�?
		mArrowImageView = (ImageView) findViewById(R.id.refresh_header_arrow);
		mProgressBar = (ProgressBar) findViewById(R.id.refresh_header_progressbar);
		mHintTextView = (TextView) findViewById(R.id.refresh_header_hint_textview);
		
		//旋转动画,顺时针旋�?180�?
		mRotateAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnim.setDuration(ROATE_ANIM_DURATION);
		mRotateAnim.setFillAfter(true);
		
		//到底部的旋转动画
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROATE_ANIM_DURATION);
		mRotateAnim.setFillAfter(true);
	}
	
	public void setState(int state){
		if (state == mState) {
			return;
		}
		//正在刷新的时�?,显示进度�?
		if (state == STATE_REFRESHING) {
			//清除动画
			mArrowImageView.clearAnimation();
			//箭头消失
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			//进度条消�?,显示箭头
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		
		switch (state) {
		//原始状�?�下
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				//箭头向下
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText("正在刷新");
			break;
		//正在下拉
		case STATE_READY:
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				//箭头向上
				mArrowImageView.setAnimation(mRotateAnim);
				mHintTextView.setText("松开刷新数据");
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText("正在努力加载...");
			break;

		default:
			break;
		}
		mState = state;
	}

	//设定可视高度
	public void setVisiableHeight(int height){
		if (height < 0) {
			height = 0;
		}
		//获取当前的高�?
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}
	
	//获得可视高度
	public int getVisiableHeight() {
		return mContainer.getHeight();
	}
}
