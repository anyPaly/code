package com.edgelesschat.friendsloop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.edgelesschat.R;

public class ListViewExpanded extends ListView implements OnScrollListener {
	/**
	 * ��󻬶�Y��ľ���
	 */
	private float mLastY = -1;
	private Scroller mScroller;
	/**
	 * �û���������
	 */
	private OnScrollListener mScrollListener;
	/**
	 * ����ˢ�ºͼ��صĽӿ�
	 */
	private IXListViewListener mListViewListener;

	/**
	 * �ӿ�ˢ��\����
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	/**
	 * ������view
	 */
	private RefreshHeader mHeaderView;
	private View mHeaderView2;
	// ����ؼ�
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	// �ؼ��߶�
	private int mHeaderViewHeight;
	// �Ѿ�������
	private boolean mEnablePullRefresh = true;
	// ����ˢ��
	private boolean mPullRefreshing = false;

	/**
	 * �ײ���view
	 */
	private RefreshFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	// ���list��item����
	private int mTotalItemCount;
	private int mScrollBack;
	// header
	private final static int SCROLLBACK_HEADER = 0;
	// footer
	private final static int SCROLLBACK_FOOTER = 1;
	// ��תʱ��
	private final static int SCROLL_DURATION = 400;
	// �ײ����ȴ���50px,����
	private final static int PULL_LOAD_MORE_DELTA = 50;
	private final static float OFFSET_RADIO = 1.8f;

	public ListViewExpanded(Context context) {
		super(context);
		initWithContext(context);
	}

	public ListViewExpanded(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
		setDividerHeight(0);
	}

	public ListViewExpanded(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initWithContext(context);
	}

	// ��ʼ��
	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		setOnScrollListener(this);
		LayoutInflater inflater = LayoutInflater.from(context);
		// ������View
		mHeaderView = new RefreshHeader(context);
		mHeaderView2 = inflater.inflate(R.layout.friendsloop_header_layout, null);
		
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.refresh_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.refresh_header_time);
		addHeaderView(mHeaderView);
		addHeaderView(mHeaderView2);
		
		// �ײ���View
		mFooterView = new RefreshFooter(context);
		// ��ȡheadView����ͼ�߶�
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						// ��ȡ���relativilayout�߶�
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						// �Ƴ��������
						getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
					}
				});
	}

	// ȷ���ײ��������µ�,����ֻ����һ��
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
	}

	// ����ʱ,�趨��ʾˢ�»�������
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) {
			// ��ˢ��,��ʾ����
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			// ˢ��,����ʾ����
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	// �������ص��趨
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			// ���صײ���view
			mFooterView.hide();
			// ȡ��ײ��ĵ���¼�
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			// ���õ�ǰ״̬Ϊԭʼ״̬
			mFooterView.setState(RefreshFooter.STATE_NORMAL);
			// ������ظ��
			mFooterView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	// ���ü��ظ������
	public void setLoadHide() {
		// ���صײ���view
		mFooterView.setState(RefreshFooter.STATE_FINISH);
		// ȡ��ײ��ĵ���¼�
		mFooterView.setOnClickListener(null);
	}

	// ���ü��ظ�๤��
	public void setLoadWork() {
		// չ������¼��ײ��ĵ���¼�
		mFooterView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startLoadMore();
			}
		});
	}

	// ֹͣˢ��
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			// ���赱ǰView�ĸ߶�
			resetHeaderHeight();
		}
	}

	// ֹͣ����
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(RefreshFooter.STATE_NORMAL);
		}
	}

	/**
	 * ֤����ǰ���ڹ���
	 */
	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	// OnXScrollListener�ӿڼ���OnScrollListener
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * ������ʾ��ˢ��ʱ��
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	/**
	 * ����header�߶�
	 */
	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) (delta + mHeaderView
				.getVisiableHeight()));
		// δ����ˢ��״̬.���¼�ͷ
		if (mEnablePullRefresh && !mPullRefreshing) {
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				// �ɼ�߶ȴ��ڿؼ��߶�,�ı�״̬Ϊ׼��ˢ��
				mHeaderView.setState(RefreshHeader.STATE_READY);
			} else {
				// ����Ϊԭʼ״̬
				mHeaderView.setState(RefreshHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	// ���趥��view�ĸ߶�
	private void resetHeaderHeight() {
		// ��ȡ��ǰview�ɼ�ĸ߶�
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) {
			return;
		}
		// ��������viewû����ָ��ˢ�µĸ߶�,ʲôҲ����
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		// ���չ��غ���ʧ�ĸ߶�
		int finalHeight = 0;
		// ����ˢ�µ�ʱ��,�Ϳ������е�headerView
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// �������view
		invalidate();
	}

	/**
	 * ���µײ��ĸ߶�
	 */
	private void updateFooterHeight(float delta) {
		int height = (int) (mFooterView.getBottomMargin() + delta);
		if (mEnablePullLoad && mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				// ��������߶Ⱦͼ��ظ��
				mFooterView.setState(RefreshFooter.STATE_READY);
			} else {
				mFooterView.setState(RefreshFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	/**
	 * ���趥���ĸ߶�
	 */
	private void resetFooterHeight() {
		// ��ȡ�ײ��߶ȵľ���
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	// ����list���item
	protected void startLoadMore() {
		mPullLoading = true;
		// ���õ�ǰ��״̬Ϊ���ڼ���
		mFooterView.setState(RefreshFooter.STATE_LOADING);
		// ����ˢ�ºͼ��صĽӿڲ�Ϊ��
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	/**
	 * �����û��ĵ���¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			// ��ȡ�����Ļ�ĸ߶�
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// �ڶ�����ʱ��,������ˢ��
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// �ڵײ���ʱ����������ظ��
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			// Ĭ��״̬��,����
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				// ֤����ˢ��
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(RefreshHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				// ���ø߶�
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// ֤�����ڼ��ظ��
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
						&& !mPullLoading) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * ��ȡ�������,����������Ӧ
	 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				// ������ȡ�����ĸ߶�
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	// ����ListView������
	// ���listviewֻ��ʾһ�е�����
//	@Override
//	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//		// super.onMeasure(widthMeasureSpec,
//		// MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//		// MeasureSpec.AT_MOST));
//		super.onMeasure(widthMeasureSpec, expandSpec);
//	}

}
