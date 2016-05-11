package com.rambo.marketposter.widget.xView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.rambo.marketposter.R;


public class XListView extends ListView implements OnScrollListener {
	// private static final String TAG = "XListView";

	private final static int SCROLL_BACK_HEADER = 0;
	private final static int SCROLL_BACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400;

	// when pull up >= 50px
	private final static int PULL_LOAD_MORE_DELTA = 50;

	// support iOS like pull
	private final static float OFFSET_RADIO = 1.8f;

	private float mLastY = -1;

	// used for scroll back
	private Scroller mScroller;
	// user's scroll listener
	private OnScrollListener mScrollListener;
	// for mScroller, scroll back from header or footer.
	private int mScrollBack;

	// the interface to trigger refresh and load more.
	private IXListViewListener mListener;

	private XHeaderView mHeader;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderContent;
	private TextView mHeaderTime;
	private int mHeaderHeight;

	private LinearLayout mFooterLayout;
	private XFooterView mFooterView;
	private boolean mIsFooterReady = false;

	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false;

	private boolean mEnablePullLoad = true;
	private boolean mEnableAutoLoad = false;
	private boolean mPullLoading = false;

	// total list items, used to detect is at the bottom of ListView
	private int mTotalItemCount;

	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}
	
	OnItemClickListener mOnItemClickListener;
	
	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * @return The callback to be invoked with an item in this AdapterView has
     *         been clicked, or null id no callback has been set.
     */
//	@Override
//	public final OnItemClickListener getOnItemClickListener() {
//		return mOnItemClickListener;
//	}
	
	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (mOnItemClickListener != null) {
			playSoundEffect(SoundEffectConstants.CLICK);
			if (view != null) {
				view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
			}
			mOnItemClickListener.onItemClick(this, view, position-2, id-2);
			return true;
		}

		return false;
	}

	LinearLayout emptyHeader;
	View emptyHeaderView;

	public void setEmptyHeadHight(final float h) {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) emptyHeaderView.getLayoutParams();
        lp.height = (int) h;
        emptyHeaderView.setLayoutParams(lp);
		
		postInvalidate();
	}
	
	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		emptyHeader = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.vw_empty_header, null);
		emptyHeaderView = emptyHeader.findViewById(R.id.empty_view);
		addHeaderView(emptyHeader);

		// init header view
		mHeader = new XHeaderView(context);
		mHeaderContent = (RelativeLayout) mHeader
				.findViewById(R.id.header_content);
		mHeaderTime = (TextView) mHeader.findViewById(R.id.header_hint_time);
		addHeaderView(mHeader);

		// init footer view
		mFooterView = new XFooterView(context);
		mFooterLayout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		mFooterLayout.addView(mFooterView, params);

		// init header height
		ViewTreeObserver observer = mHeader.getViewTreeObserver();
		if (null != observer) {
			observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void onGlobalLayout() {
					mHeaderHeight = mHeaderContent.getHeight();
					ViewTreeObserver observer = getViewTreeObserver();

					if (null != observer) {
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							observer.removeGlobalOnLayoutListener(this);
						} else {
							observer.removeOnGlobalLayoutListener(this);
						}
					}
				}
			});
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XFooterView is the last footer view, and only add once.
		if (!mIsFooterReady) {
			mIsFooterReady = true;
			addFooterView(mFooterLayout);
		}

		super.setAdapter(adapter);
	}

	/**
	 * Enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;

		// disable, hide the content
		mHeaderContent.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * Enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		if (mEnablePullLoad == enable)
			return;
		mEnablePullLoad = enable;

		if (!mEnablePullLoad) {
			mFooterView.setBottomMargin(0);
			mFooterView.hide();
			mFooterView.setPadding(0, 0, 0, 0);
			mFooterView.setOnClickListener(null);

		} else {
			mPullLoading = false;
			mFooterView.setPadding(0, 0, 0, 0);
			mFooterView.show();
			mFooterView.setState(XFooterView.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * Enable or disable auto load more feature when scroll to bottom.
	 * 
	 * @param enable
	 */
	public void setAutoLoadEnable(boolean enable) {
		mEnableAutoLoad = enable;
	}

	/**
	 * Stop refresh, reset header view.
	 */
	public void stopRefresh() {
		Log.e("", "xlist, stopRefresh() mPullRefreshing = " + mPullRefreshing);
		// if (mPullRefreshing) {
		mPullRefreshing = false;
		resetHeaderHeight();
		// }
	}

	public boolean isRefreshing() {
		return mPullRefreshing;
	}

	/**
	 * Stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading) {
			mPullLoading = false;
			mFooterView.setState(XFooterView.STATE_NORMAL);
			resetFooterHeight();
		}
	}

	/**
	 * Set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTime.setText(time);
	}

	/**
	 * Set listener.
	 * 
	 * @param listener
	 */
	public void setXListViewListener(IXListViewListener listener) {
		mListener = listener;
	}

	/**
	 * Auto call back refresh.
	 */
	public void autoRefresh() {
		mHeader.setVisibleHeight(mHeaderHeight);

		if (mEnablePullRefresh && !mPullRefreshing) {
			// update the arrow image not refreshing
			if (mHeader.getVisibleHeight() > mHeaderHeight) {
				mHeader.setState(XHeaderView.STATE_READY);
			} else {
				mHeader.setState(XHeaderView.STATE_NORMAL);
			}
		}

		refresh();
		mPullRefreshing = true;
		mHeader.setState(XHeaderView.STATE_REFRESHING);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener listener = (OnXScrollListener) mScrollListener;
			listener.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
//		Log.e("", "xlist, updateHeaderHeight() - mHeader.getVisibleHeight() = "
//				+ mHeader.getVisibleHeight());
//		Log.e("", "xlist, updateHeaderHeight() - mEnablePullRefresh = "
//				+ mEnablePullRefresh + "; mPullRefreshing=" + mPullRefreshing);

		mHeader.setVisibleHeight((int) delta + mHeader.getVisibleHeight());

		if (mEnablePullRefresh && !mPullRefreshing) {
			// update the arrow image unrefreshing
			if (mHeader.getVisibleHeight() > mHeaderHeight) {
				mHeader.setState(XHeaderView.STATE_READY);
				setSelection(0);
			} else {
				mHeader.setState(XHeaderView.STATE_NORMAL);
			}
		}
	}

	private void resetHeaderHeight() {
		int height = mHeader.getVisibleHeight();
//		Log.e("", "xlist, resetHeaderHeight() height = " + height
//							+ ";>=_ok; mHeaderHeight=" + mHeaderHeight);
		if (height == 0)
			return;

		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height < mHeaderHeight)
			return;

		// default: scroll back to dismiss header.
		int finalHeight = 0;
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderHeight) {
			finalHeight = mHeaderHeight;
		}

		mScrollBack = SCROLL_BACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);

		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
				
		int height = mFooterView.getBottomMargin() + (int) delta;

		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				// height enough to invoke load more.
				mFooterView.setState(XFooterView.STATE_READY);
			} else {
				mFooterView.setState(XFooterView.STATE_NORMAL);
			}
		}

		mFooterView.setBottomMargin(height);

		// scroll to bottom
		// setSelection(mTotalItemCount - 1);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();

		if (bottomMargin > 0) {
			mScrollBack = SCROLL_BACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);

			updateFooterHeight(-bottomMargin);
			invalidate();
		}
	}
	
	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XFooterView.STATE_LOADING);
		loadMore();
	}

	public interface InterceptTouchCb {
		public abstract boolean interceptTouchEvent(MotionEvent ev);
	}

	public InterceptTouchCb mInterceptTouchCb;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mInterceptTouchCb != null
				&& mInterceptTouchCb.interceptTouchEvent(ev)) {
			return true;
		}
		
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();

			if (getFirstVisiblePosition() == 0
					&& (mHeader.getVisibleHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();

			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				int tmp = getAdapter().getCount();
				if (!mPullLoading && getAdapter().getCount() > 3) {
					updateFooterHeight(-deltaY / OFFSET_RADIO);
				}
			}
			break;

		default:
			// reset
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeader.getVisibleHeight() > mHeaderHeight) {
					refresh();
					mPullRefreshing = true;
					mHeader.setState(XHeaderView.STATE_REFRESHING);
				}

				resetHeaderHeight();

			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (!mPullLoading && getAdapter().getCount() > 3) {
					if (mEnablePullLoad
							&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
						startLoadMore();
					}
					resetFooterHeight();
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		boolean isFinish = mScroller.computeScrollOffset();
		if (isFinish) {
			if (mScrollBack == SCROLL_BACK_HEADER) {
				mHeader.setVisibleHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}

			postInvalidate();
			invokeOnScrolling();
		}

		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !mPullLoading
				&& getAdapter().getCount() > 3) {
			if (mEnableAutoLoad && getLastVisiblePosition() == getCount() - 1) {
				startLoadMore();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	private void refresh() {
		if (mEnablePullRefresh && null != mListener && !mPullRefreshing) {
			mListener.onRefresh();
		}
	}

	private void loadMore() {
		if (mEnablePullLoad && null != mListener) {
			mListener.onLoadMore();
		}
	}

	/**
	 * You can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * Implements this interface to get refresh/load more event.
	 * 
	 * @author markmjw
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	public boolean isEmpty() {
		if (getAdapter() == null) {
			return true;
		}
		if (getAdapter().getCount() == 0) {
			return true;
		}
		return false;
	}
}
