package com.quanle.healthmanager.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.R.color;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.widget.ScrollOverListView.OnScrollOverListener;

/**
 * ����ˢ�¿ؼ�</br> ����ʵ������ˢ�µ�������ؼ��� ScrollOverListViewֻ���ṩ�������¼���
 * 
 * @author Solo Email:kjsoloho@gmail.com
 */
public class PullDownView extends LinearLayout implements OnScrollOverListener,
		OnScrollListener {
	private static final String TAG = "PullDownView";
	private static final boolean DEBUG = false;

	private static final int AUTO_INCREMENTAL = 10; // �����������ڻص�

	private static final int WHAT_SET_HEADER_HEIGHT = 1;// Handler what ���ø߶�

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"MM-dd HH:mm");

	private View mHeaderView;
	private LayoutParams mHeaderViewParams;
	private TextView mHeaderViewDateView;
	private TextView mHeaderTextView;
	private ImageView mHeaderArrowView;
	private View mHeaderLoadingView;
	private View mFooterView;
	private TextView mFooterTextView;
	private View mFooterLoadingView;
	private ScrollOverListView mListView;

	private OnPullDownListener mOnPullDownListener;
	private RotateAnimation mRotateOTo180Animation;
	private RotateAnimation mRotate180To0Animation;

	private Context mContext;
	private Field overScrollModeField;

	private int mMoveDeviation; // �ƶ����
	private int mHeaderIncremental; // ͷ���ļ�����������
	private int mDefaultHeaderViewHeight; // ͷ���ļ�ԭ���ĸ߶�
	private int mStartIndex; // ��ǰList����ʾ��һ��
	private int mEndIndex; // ��ǰList����ʾ���һ��

	private float mMotionDownLastY; // ����ʱ���Y������

	private boolean mEnablePullDown; // �Ƿ��������
	private boolean mIsPullUpDone; // �Ƿ�������
	private boolean mEnableLoadMore; // �Ƿ����ø���
	private boolean mEnableAutoFetchMore; // �Ƿ������Զ���ȡ����
	private boolean mIsNoMoreData; // û�и����������
	private boolean mIsDidLoad; // �Ƿ����������

	// ͷ���ļ���״̬
	private static final int HEADER_VIEW_STATE_IDLE = 0; // ����
	private static final int HEADER_VIEW_STATE_NOT_OVER_HEIGHT = 1; // û�г���Ĭ�ϸ߶�
	private static final int HEADER_VIEW_STATE_OVER_HEIGHT = 2; // ����Ĭ�ϸ߶�
	private int mHeaderViewState = HEADER_VIEW_STATE_IDLE;

	private static final int STATE_NONE = 0;
	private static final int STATE_REFRESHING = 1; // ˢ����
	private static final int STATE_LOADING_MORE = 2; // ���ظ�����
	private static final int STATE_DRAGING = 4; // �϶���
	private static final int STATE_MOTION_DOWN = 8; // ����
	private int state = STATE_NONE;

	public PullDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderViewAndFooterViewAndListView(context);
	}

	public PullDownView(Context context) {
		super(context);
		initHeaderViewAndFooterViewAndListView(context);
	}

	/*
	 * ================================== Public method �ⲿʹ�ã�����������⼸���Ϳ�����
	 * 
	 * ==================================
	 */

	/**
	 * ˢ���¼��ӿ�
	 * 
	 * @author Solo Email:kjsoloho@gmail.com
	 */
	public interface OnPullDownListener {
		void onRefresh();

		void onLoadMore();
	}

	/**
	 * ֪ͨ�����������ݣ�Ҫ����Adapter.notifyDataSetChanged���� ������������ݵ�ʱ�򣬵������notifyDidLoad()
	 * �Ż�����ͷ��������ʼ�����ݵ�
	 */
	public void notifyDidDataLoad(boolean isNoMoreData) {
		mIsDidLoad = true;
		System.out.println("mIsDidLoad" + mIsDidLoad);
		mIsNoMoreData = isNoMoreData;
		mFooterView.setVisibility(View.VISIBLE);
		updateFooter();
		mListView.setFooterDividersEnabled(true);

		mHeaderViewParams.height = 0;
		mHeaderView.setLayoutParams(mHeaderViewParams);
		updateHeader();

		doListViewIdleActionOnDataDidLoad();
	}

	/**
	 * ֪ͨ�Ѿ�ˢ�����ˣ�Ҫ����Adapter.notifyDataSetChanged����
	 * ����ִ����ˢ������֮�󣬵������notifyDidRefresh() �Ż����ص�ͷ���ļ��Ȳ���
	 */
	public void notifyDidRefresh(boolean isNoMoreData) {
		mIsNoMoreData = isNoMoreData;
		updateFooter();

		state &= ~STATE_REFRESHING;
		mHeaderViewState = HEADER_VIEW_STATE_IDLE;
		setHeaderHeight(0);
		updateHeader();

		doListViewIdleActionOnDataDidLoad();
	}

	/**
	 * ֪ͨ�Ѿ���ȡ������ˣ�Ҫ����Adapter.notifyDataSetChanged����
	 * ����ִ�����������֮�󣬵������notyfyDidMore() �Ż����ؼ���Ȧ�Ȳ���
	 */
	public void notifyDidLoadMore(boolean isNoMoreData) {
		mIsNoMoreData = isNoMoreData;
		state &= ~STATE_LOADING_MORE;
		updateFooter();
	}

	/**
	 * ���ü�����
	 * 
	 * @param listener
	 */
	public void setOnPullDownListener(OnPullDownListener listener) {
		mOnPullDownListener = listener;
	}

	/**
	 * ��ȡ��Ƕ��listview
	 * 
	 * @return ScrollOverListView
	 */
	public ScrollOverListView getListView() {
		return mListView;
	}

	/**
	 * �Ƿ����Զ���ȡ����</br> �Զ���ȡ���࣬���ڴ�������ײ���ʱ���Զ�ˢ��
	 * 
	 * @param index
	 *            �����ڼ�������
	 */
	public void enableAutoFetchMore(boolean enable, int index) {
		if (!mEnableLoadMore)
			return;
		mEnableAutoFetchMore = enable;
		if (enable) {
			mListView.setBottomPosition(index);
		} else {
			updateFooter();
		}
	}

	/**
	 * �Ƿ����ü��ظ���</br> ֻ���ڳ�ʼ����ʱ�����
	 */
	public void enableLoadMore(boolean enable) {
		mEnableLoadMore = enable;
		if (!enable) {
			// TODO ��ʱʹ�����ִ��ķ���
			// ���õİ취��д����������
			mUIHandler.post(new Runnable() {
				public void run() {
					removeFooter();
				}
			});
		}
	}

	/**
	 * �Ƿ���������ˢ��
	 */
	public void enablePullDown(boolean enable) {
		mEnablePullDown = enable;
	}

	/*
	 * ================================== Private method ����ʵ������ˢ�µȲ���
	 * 
	 * ==================================
	 */

	/**
	 * ��ʼ������
	 */
	private void initHeaderViewAndFooterViewAndListView(Context context) {
		setOrientation(LinearLayout.VERTICAL);

		mContext = context;
		this.setBackgroundColor(color.white);
		/*
		 * �Զ���ͷ���ļ� ������������Ϊ���ǵ��ܶ���涼��Ҫʹ�� ���Ҫ�޸ģ�������ص����ö�Ҫ����
		 */
		mEnablePullDown = true;
		mHeaderView = LayoutInflater.from(context).inflate(
				R.layout.pulldown_header, null);
		mHeaderViewParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		mHeaderIncremental = mDefaultHeaderViewHeight;
		addView(mHeaderView, 0, mHeaderViewParams);

		mDefaultHeaderViewHeight = DisplayUtil.dip2px(context, 60);
		mMoveDeviation = DisplayUtil.dip2px(context, 20);
		mHeaderTextView = (TextView) mHeaderView
				.findViewById(R.id.pulldown_header_text);
		mHeaderArrowView = (ImageView) mHeaderView
				.findViewById(R.id.pulldown_header_arrow);
		mHeaderViewDateView = (TextView) mHeaderView
				.findViewById(R.id.pulldown_header_date);
		mHeaderLoadingView = mHeaderView
				.findViewById(R.id.pulldown_header_loading);

		// ע�⣬ͼƬ��ת֮����ִ����ת����������¿�ʼ����
		mRotateOTo180Animation = new RotateAnimation(0, 180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateOTo180Animation.setDuration(250);
		mRotateOTo180Animation.setFillAfter(true);

		mRotate180To0Animation = new RotateAnimation(180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotate180To0Animation.setDuration(250);
		mRotate180To0Animation.setFillAfter(true);

		/*
		 * �Զ���Ų��ļ�
		 */
		mEnableLoadMore = true;
		mFooterView = LayoutInflater.from(mContext).inflate(
				R.layout.pulldown_footer, null);
		mFooterView.setVisibility(View.VISIBLE);
		mFooterTextView = (TextView) mFooterView
				.findViewById(R.id.pulldown_footer_text);
		mFooterLoadingView = mFooterView
				.findViewById(R.id.pulldown_footer_loading);
		mFooterView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mIsNoMoreData || !mIsDidLoad
						|| (state & STATE_LOADING_MORE) == STATE_LOADING_MORE)
					return;
				ScrollOverListView listView = mListView;
				if (listView.getCount() - listView.getHeaderViewsCount()
						- listView.getFooterViewsCount() > 0) {
					state |= STATE_LOADING_MORE;
					updateFooter();
					mOnPullDownListener.onLoadMore();
				}
			}
		});

		/*
		 * ScrollOverListView ͬ���ǿ��ǵ�������Ҫʹ�ã����Է������� ͬʱ��Ϊ����Ҫ���ļ����¼�
		 */
		mListView = new ScrollOverListView(context);
		mListView.setFooterDividersEnabled(true);
		mListView.setId(android.R.id.list);
		// mListView.addFooterView(mFooterView);
		mListView.setOnScrollOverListener(this);
		mListView.setOnScrollListener(this);
		this.setFocusable(true);
		// ��Ϊ2.3֮���ĳЩListView�ؼ��Լ�ʵ����pull��Ӱ����Ч��
		// ����������������������
		try {
			Method method = AbsListView.class.getDeclaredMethod(
					"setOverScrollMode", int.class);
			method.setAccessible(true);
			method.invoke(mListView, 2);// View.OVER_SCROLL_NEVER
		} catch (Exception e) {
			e.printStackTrace();
		}
		addView(mListView, android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);

		// set ListView animation

		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		mListView.setLayoutAnimation(controller);

		// �յ�listener
		mOnPullDownListener = new OnPullDownListener() {
			public void onRefresh() {
			}

			public void onLoadMore() {
			}
		};
	}

	/**
	 * ȥ���ײ��ļ�
	 */
	private void removeFooter() {
		if (mListView.getFooterViewsCount() > 0 && mListView != null
				&& mFooterView != null) {
			mListView.removeFooterView(mFooterView);
		}
	}

	// public boolean isDraging(){
	// return mHeaderIncremental>5||(state & STATE_DRAGING) == STATE_DRAGING;
	// }
	/**
	 * ����ͷ���ļ�</br> ���ˢ������ʾLoading�� ����϶��У���ʾ�϶�Ч���� ������ʾ����ʱ�䡣
	 */
	private void updateHeader() {
		if ((state & STATE_REFRESHING) == STATE_REFRESHING) {
			mHeaderArrowView.clearAnimation();
			mHeaderArrowView.setVisibility(View.GONE);
			mHeaderLoadingView.setVisibility(View.VISIBLE);
			mHeaderTextView.setText("正在刷新...");
		} else if ((state & STATE_DRAGING) == STATE_DRAGING) {
			if (mHeaderViewParams.height >= mDefaultHeaderViewHeight) {
				if (mHeaderViewState == HEADER_VIEW_STATE_OVER_HEIGHT)
					return;

				mHeaderArrowView.setVisibility(View.VISIBLE);
				mHeaderLoadingView.setVisibility(View.GONE);
				mHeaderViewDateView.setVisibility(View.VISIBLE);
				mHeaderViewState = HEADER_VIEW_STATE_OVER_HEIGHT;
				mHeaderTextView.setText("松开刷新");
				mHeaderArrowView.startAnimation(mRotateOTo180Animation);
			} else {
				if (mHeaderViewState == HEADER_VIEW_STATE_NOT_OVER_HEIGHT
						|| mHeaderViewState == HEADER_VIEW_STATE_IDLE)
					return;

				mHeaderArrowView.setVisibility(View.VISIBLE);
				mHeaderLoadingView.setVisibility(View.GONE);
				mHeaderViewDateView.setVisibility(View.VISIBLE);
				mHeaderViewState = HEADER_VIEW_STATE_NOT_OVER_HEIGHT;
				mHeaderTextView.setText("下拉刷新");
				mHeaderArrowView.startAnimation(mRotate180To0Animation);

			}
		} else {
			mHeaderLoadingView.setVisibility(View.GONE);
			mHeaderViewDateView.setVisibility(View.VISIBLE);
			mHeaderArrowView.setVisibility(View.VISIBLE);
			mHeaderTextView.setText("下拉刷新");
			mHeaderViewDateView.setText("最后刷新"
					+ dateFormat.format(new Date(System.currentTimeMillis())));
		}
	}

	/**
	 * ���½Ų��ļ�</br> ������ʾ"����"�� ���û�и�������ݾ���ʾ"�Ѽ�����ȫ��"�� ��������о���ʾ"������..."��
	 */
	private void updateFooter() {
		// if (!mEnableLoadMore)
		// return;

		if (mIsNoMoreData) {
			mFooterTextView.setText("�Ѽ�����ȫ��");
			mFooterLoadingView.setVisibility(View.GONE);
			enableLoadMore(false);
			// mListView.removeFooterView(mFooterView);
		} else if ((state & STATE_LOADING_MORE) == STATE_LOADING_MORE) {
			mFooterTextView.setText("���ظ�����...");
			mFooterLoadingView.setVisibility(View.VISIBLE);
		} else {
			mFooterTextView.setText("����...");

			mFooterLoadingView.setVisibility(View.GONE);
		}
	}

	private void setHeaderHeight(final int height) {
		mHeaderIncremental = height;
		mHeaderViewParams.height = height;
		mHeaderView.setLayoutParams(mHeaderViewParams);
	}

	/**
	 * �Զ����ض���
	 */
	class HideHeaderViewTask extends TimerTask {

		public void run() {
			if ((state & STATE_MOTION_DOWN) == STATE_MOTION_DOWN) {
				cancel();
				return;
			}
			mHeaderIncremental -= AUTO_INCREMENTAL;
			if (mHeaderIncremental > 0) {
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
			} else {
				mHeaderIncremental = 0;
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
				cancel();
			}
		}
	}

	/**
	 * �Զ���ʾ����
	 */
	class ShowHeaderViewTask extends TimerTask {

		public void run() {
			if ((state & STATE_MOTION_DOWN) == STATE_MOTION_DOWN) {
				cancel();
				return;
			}
			mHeaderIncremental -= AUTO_INCREMENTAL;
			if (mHeaderIncremental > mDefaultHeaderViewHeight) {
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
			} else {
				mHeaderIncremental = mDefaultHeaderViewHeight;
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
				if ((state & STATE_REFRESHING) != STATE_REFRESHING) {
					state |= STATE_REFRESHING;
					mUIHandler.post(new Runnable() {

						public void run() {
							// Ҫ����������������޷�����
							updateHeader();
							mHeaderArrowView.clearAnimation();
							mHeaderArrowView.setVisibility(View.INVISIBLE);
							mHeaderLoadingView.setVisibility(View.VISIBLE);
							mOnPullDownListener.onRefresh();
						}
					});
				}
				cancel();
			}
		}
	}

	private Handler mUIHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SET_HEADER_HEIGHT: {
				setHeaderHeight(mHeaderIncremental);
				return;
			}
			}
		}

	};

	/**
	 * ��ˢ��������ʱ���ֶ�����onIdle�¼�
	 */
	private void doListViewIdleActionOnDataDidLoad() {
		new Handler().postDelayed(new Runnable() {

			public void run() {
				if (mOnListViewIdleListener != null) {
					// ����Ҫ����FooterView
					final int firstVisiblePosition = mListView
							.getFirstVisiblePosition();
					final int childCount = mListView.getChildCount();
					// Log.d(TAG,
					// "[doListViewIdleActionOnDataDidLoad] firstVisiblePosition:"
					// + firstVisiblePosition + " childCount:" + childCount);
					mOnListViewIdleListener.onIdle(firstVisiblePosition,
							childCount);
				}
			}
		}, 0);
	}

	/**
	 * ��Ŀ�Ƿ�����������Ļ
	 */
	private boolean isFillScreenItem() {
		final int firstVisiblePosition = mListView.getFirstVisiblePosition();
		final int lastVisiblePosition = mListView.getLastVisiblePosition()
				- mListView.getFooterViewsCount();
		final int visibleItemCount = lastVisiblePosition - firstVisiblePosition
				+ 1;
		final int totalItemCount = mListView.getCount()
				- mListView.getFooterViewsCount();

		if (visibleItemCount < totalItemCount)
			return true;
		return false;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			state |= STATE_MOTION_DOWN;
			mIsPullUpDone = false;
			mMotionDownLastY = ev.getRawY();
			Log.d(TAG, "pulldownview.onIntercept:" + mMotionDownLastY);
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*
	 * ================================== ʵ�� OnScrollOverListener�ӿ�
	 * 
	 * 
	 * ==================================
	 */

	public boolean onListViewTopAndPullDown(MotionEvent event, int delta) {
		// ��ΪĳЩListView�ؼ��Լ��Ѿ����������Ч��
		// �������Ǳ��뷵��true������ListView����������ﵽ���ε�Ч��
		System.out.println("mIsDidLoad" + mIsDidLoad);
		if ((state & STATE_REFRESHING) == STATE_REFRESHING || !mEnablePullDown)
			// || !mIsDidLoad)
			return true;

//		if (mListView.getCount() - mListView.getFooterViewsCount() == 0) {
//			return true;
//		}

		// �����ʼ���µ��������벻�������ֵ���򲻻���
		if (mHeaderViewParams.height <= 0) {
			final int absMotionY = (int) Math.abs(event.getRawY()
					- mMotionDownLastY);
			if (absMotionY < mMoveDeviation) {
				return true;
			}
		}

		int absDelta = Math.abs(delta);
		final int i = (int) Math.ceil((double) absDelta / 2);
		mHeaderIncremental += i;
		if (mHeaderIncremental >= 0) { // && mIncremental <= mMaxHeight
			setHeaderHeight(mHeaderIncremental);
			updateHeader();
		}
		return true;
	}

	public boolean onListViewBottomAndPullUp(MotionEvent event, int delta) {
		System.out.println("mIsDidLoad" + mIsDidLoad);
		if ((state & STATE_LOADING_MORE) == STATE_LOADING_MORE || !mIsDidLoad
				|| !mEnableAutoFetchMore)// || mIsNoMoreData)
			return false;
		ScrollOverListView listView = mListView;
		if (listView.getCount() - listView.getHeaderViewsCount()
				- listView.getFooterViewsCount() > 0) {
			state |= STATE_LOADING_MORE;
			updateFooter();
			mOnPullDownListener.onLoadMore();
		}
		return true;
	}

	public boolean onMotionDown(MotionEvent ev) {
		return false;
	}

	public boolean onMotionMove(MotionEvent ev, int delta) {
		state |= STATE_DRAGING;
		// ��ͷ���ļ�������ʧ��ʱ�򣬲��������
		if (mIsPullUpDone)
			return true;

		// onTopDown�ڶ����������ϻ��ƺ�onTopUp���
		if (mHeaderViewParams.height > 0 && delta < 0) {
			final int absDelta = Math.abs(delta);
			final int i = (int) Math.ceil((double) absDelta / 2);

			mHeaderIncremental -= i;
			if (mHeaderIncremental > 0) {
				setHeaderHeight(mHeaderIncremental);
				updateHeader();
			} else {
				mHeaderViewState = HEADER_VIEW_STATE_IDLE;
				mHeaderIncremental = 0;
				setHeaderHeight(mHeaderIncremental);
				mIsPullUpDone = true;
			}
			return true;
		}
		return false;
	}

	public boolean onMotionUp(MotionEvent ev) {
		state &= ~STATE_DRAGING;
		state &= ~STATE_MOTION_DOWN;
		// ����͵���¼���ͻ
		if (mHeaderViewParams.height > 0 || mIsPullUpDone) {

			// �ж�ͷ�ļ������ľ������趨�ĸ߶ȣ�С�˾����أ����˾͹̶��߶�
			int x = mHeaderIncremental - mDefaultHeaderViewHeight;
			Timer timer = new Timer(true);
			if (x < 0) {
				timer.scheduleAtFixedRate(new HideHeaderViewTask(), 0, 10);
			} else {
				timer.scheduleAtFixedRate(new ShowHeaderViewTask(), 0, 10);
			}
			return true;
		}
		return false;
	}

	public void refresh() {
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new ShowHeaderViewTask(), 0, 10);
	}

	/*
	 * ===================================== ����ListViewֹͣ�¼�
	 * =====================================
	 */

	private OnListViewIdleListener mOnListViewIdleListener;

	public interface OnListViewIdleListener {
		void onIdle(int startIndex, int count);
	}

	public void setOnListViewIdleListener(OnListViewIdleListener listener) {
		if (mListView != null) {
			mOnListViewIdleListener = listener;
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			if (DEBUG)
				Log.d(TAG, "IDLE");
			if (mOnListViewIdleListener != null) {
				int count = mListView.getChildCount();
				final int childEndIndex = mStartIndex
						+ mListView.getChildCount() - 1;
				final int listEndIndex = mListView.getCount() - 1;
				if (childEndIndex == listEndIndex) {
					count -= mListView.getFooterViewsCount();
				}
				mOnListViewIdleListener.onIdle(mStartIndex, count);
			}
		}
		switch (scrollState) {
		case SCROLL_STATE_FLING:
			if (DEBUG)
				Log.d(TAG, "FLING");
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			if (DEBUG)
				Log.d(TAG, "SCROLL");
		default:
			break;
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (DEBUG)
			Log.d(TAG, "onScroll");
		mStartIndex = firstVisibleItem;
		mEndIndex = firstVisibleItem + visibleItemCount - 1;
		// ��Ϊ����I91002.3�汾�������over_scroll_never����ֻص����⣬���������ﴦ��һ��
		// ����12-9-9 2:08����������������2.3.3�汾�������ʹ����Щ���Իᱨ��
		/*
		 * final ScrollOverListView localListView = this.mListView; final
		 * boolean hasItem = localListView.getCount() > 0;
		 * 
		 * try { if (overScrollModeField == null) return; final Integer mode =
		 * (Integer) overScrollModeField.get(localListView);
		 * 
		 * if (firstVisibleItem <= 0 && hasItem) { if (mode !=
		 * View.OVER_SCROLL_NEVER) { if (DEBUG) Log.w(TAG,
		 * "set over scroll never"); overScrollModeField.set(localListView,
		 * View.OVER_SCROLL_NEVER); } } else if (firstVisibleItem +
		 * visibleItemCount >= totalItemCount && hasItem) { if (mode !=
		 * View.OVER_SCROLL_ALWAYS) { if (DEBUG) Log.w(TAG,
		 * "set over scroll always"); overScrollModeField.set(localListView,
		 * View.OVER_SCROLL_ALWAYS); } } } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

}
