package com.quanle.healthmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.quanle.healthmanager.R;

/**
 * <p>
 * һ�����Լ���ListView�Ƿ�������������ײ����Զ���ؼ�
 * </p>
 * ֻ�ܼ����ɴ�������ģ������ListView����Flying���µģ����ܼ���</br>
 * �����ԸĽ����ʵ�ּ���scroll�����ľ���λ�õ�
 * 
 * @author solo ho</br> Email:kjsoloho@gmail.com
 */

public class ScrollOverListView extends ListView {

	private static final String TAG = "ScrollOverListView";
	private static final boolean DEBUG = false;
	private int mLastY;
	private int mTopPosition;
	private int mBottomPosition;

	public ScrollOverListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ScrollOverListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScrollOverListView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mTopPosition = 0;
		mBottomPosition = 0;
		setCacheColorHint(0x00000000);
		setVerticalScrollBarEnabled(false);
		setFadingEdgeLength(0);

		// setDivider(getResources().getDrawable(R.drawable.shape_line));
		// setDividerHeight(1);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (DEBUG)
				Log.d(TAG, "onInterceptTouchEvent Action down");
			mLastY = (int) ev.getRawY();
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int y = (int) ev.getRawY();

		boolean isHandled = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			if (DEBUG)
				Log.d(TAG, "action down");
			mLastY = y;
			isHandled = mOnScrollOverListener.onMotionDown(ev);
			if (isHandled) {
				break;
			}
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int childCount = getChildCount();
			if (childCount == 0) {
				break;
			}

			final int itemCount = getAdapter().getCount() - mBottomPosition;

			final int deltaY = y - mLastY;
			final int firstTop = getChildAt(0).getTop();
			final int listPadding = getListPaddingTop();

			final int lastBottom = getChildAt(childCount - 1).getBottom();
			final int end = getHeight() - getPaddingBottom();

			final int firstVisiblePosition = getFirstVisiblePosition();

			isHandled = mOnScrollOverListener.onMotionMove(ev, deltaY);

			if (isHandled) {
				break;
			}

			// DLog.d("firstVisiblePosition=%d firstTop=%d listPaddingTop=%d deltaY=%d",
			// firstVisiblePosition, firstTop, listPadding, deltaY);
			if (firstVisiblePosition <= mTopPosition && firstTop >= listPadding
					&& deltaY > 0) {
				isHandled = mOnScrollOverListener.onListViewTopAndPullDown(ev,
						deltaY);
				if (isHandled) {
					break;
				}
			}

			// DLog.d("lastBottom=%d end=%d deltaY=%d", lastBottom, end,
			// deltaY);
			if (firstVisiblePosition + childCount >= itemCount
					&& lastBottom <= end && deltaY < 0) {
				isHandled = mOnScrollOverListener.onListViewBottomAndPullUp(ev,
						deltaY);
				if (isHandled) {
					break;
				}
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {

			if (DEBUG)
				Log.d(TAG, "action move pull up");
			isHandled = mOnScrollOverListener.onMotionUp(ev);
			if (isHandled) {
				break;
			}
			break;
		}
		}

		mLastY = y;
		if (isHandled) {
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/** �յ� */
	private OnScrollOverListener mOnScrollOverListener = new OnScrollOverListener() {

		public boolean onListViewTopAndPullDown(MotionEvent event, int delta) {
			return false;
		}

		public boolean onListViewBottomAndPullUp(MotionEvent event, int delta) {
			return false;
		}

		public boolean onMotionDown(MotionEvent ev) {
			return false;
		}

		public boolean onMotionMove(MotionEvent ev, int delta) {
			return false;
		}

		public boolean onMotionUp(MotionEvent ev) {
			return false;
		}
	};

	// =============================== public method
	// ===============================

	/**
	 * �����Զ�������һ����ĿΪͷ����ͷ���������¼��������Ϊ׼��Ĭ��Ϊ��һ��
	 * 
	 * @param index
	 *            ����ڼ�������������Ŀ��Χ֮��
	 */
	public void setTopPosition(int index) {
		if (index < 0)
			throw new IllegalArgumentException("Top position must > 0");

		mTopPosition = index;
	}

	/**
	 * �����Զ�������һ����ĿΪβ����β���������¼��������Ϊ׼��Ĭ��Ϊ���һ��
	 * 
	 * @param index
	 *            ����ڼ�������������Ŀ��Χ֮��
	 */
	public void setBottomPosition(int index) {
		if (index < 0)
			throw new IllegalArgumentException("Bottom position must > 0");

		mBottomPosition = index;
	}

	/**
	 * �������Listener���Լ����Ƿ񵽴ﶥ�ˣ������Ƿ񵽴�Ͷ˵��¼�</br>
	 * 
	 * @see OnScrollOverListener
	 */
	public void setOnScrollOverListener(
			OnScrollOverListener onScrollOverListener) {
		mOnScrollOverListener = onScrollOverListener;
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		super.setOnItemSelectedListener(listener);
	}

	/**
	 * ��������ӿ�</br>
	 * 
	 * @see ScrollOverListView#setOnScrollOverListener(OnScrollOverListener)
	 * 
	 * @author solo ho</br> Email:kjsoloho@gmail.com
	 */
	public interface OnScrollOverListener {

		/**
		 * �����������
		 * 
		 * @param delta
		 *            ��ָ����ƶ������ƫ����
		 * @return
		 */
		boolean onListViewTopAndPullDown(MotionEvent event, int delta);

		/**
		 * ������ײ�����
		 * 
		 * @param delta
		 *            ��ָ����ƶ������ƫ����
		 * @return
		 */
		boolean onListViewBottomAndPullUp(MotionEvent event, int delta);

		/**
		 * ��ָ�������´������൱��{@link MotionEvent#ACTION_DOWN}
		 * 
		 * @return ����true��ʾ�Լ�����
		 * @see View#onTouchEvent(MotionEvent)
		 */
		boolean onMotionDown(MotionEvent ev);

		/**
		 * ��ָ�����ƶ��������൱��{@link MotionEvent#ACTION_MOVE}
		 * 
		 * @return ����true��ʾ�Լ�����
		 * @see View#onTouchEvent(MotionEvent)
		 */
		boolean onMotionMove(MotionEvent ev, int delta);

		/**
		 * ��ָ���������𴥷����൱��{@link MotionEvent#ACTION_UP}
		 * 
		 * @return ����true��ʾ�Լ�����
		 * @see View#onTouchEvent(MotionEvent)
		 */
		boolean onMotionUp(MotionEvent ev);

	}

}
