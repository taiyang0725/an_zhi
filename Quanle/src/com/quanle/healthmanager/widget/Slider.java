package com.quanle.healthmanager.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.quanle.healthmanager.R;

public class Slider extends LinearLayout {
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private Context context;

	private LinearLayout layout;
	private boolean isFirst = true;
	private int mCurScreen = 0;
	private int duration = 300;
	private static final int SNAP_VELOCITY = 600;
	private float mLastMotionX = 0;

	private OnViewChangeListener mOnViewChangeListener;

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_slide, this);

		this.context = context;
		layout = (LinearLayout) getChildAt(0);
		mScroller = new Scroller(context);

		ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (isFirst) {
					int width = getMeasuredWidth();
					isFirst = false;

					LayoutParams params = (LayoutParams) layout
							.getLayoutParams();
					params.width = layout.getChildCount() * width;
					layout.setLayoutParams(params);

					for (int i = 0; i < layout.getChildCount(); i++) {
						ImageView imageView = (ImageView) layout.getChildAt(i);
						params = (LayoutParams) imageView.getLayoutParams();
						params.width = width;
						params.height = getMeasuredHeight();
						imageView.setLayoutParams(params);
					}
				}
				return true;
			}
		});
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
			}
			mLastMotionX = x;
			scrollBy(deltaX, 0);
			break;

		case MotionEvent.ACTION_UP:
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0)
				snapToScreen(mCurScreen - 1);
			else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < layout.getChildCount() - 1)
				snapToScreen(mCurScreen + 1);
			else
				snapToDestination();

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		}
		return true;
	}

	public void snapToDestination() {
		int screenWidth = getWidth();
		int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		if (destScreen > layout.getChildCount() - 1)
			destScreen = layout.getChildCount() - 1;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		if (getScrollX() != (whichScreen * getWidth())) {
			int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
			invalidate();
			mCurScreen = whichScreen;
		}

		if (mOnViewChangeListener != null)
			mOnViewChangeListener.OnViewChange(mCurScreen);

	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	public interface OnViewChangeListener {
		public void OnViewChange(int view);
	}

	public void SetImages(Bitmap[] obj) {
		for (int i = 0; i < obj.length; i++) {
			if (i < layout.getChildCount()) {
				ImageView imageView = (ImageView) layout.getChildAt(i);
				imageView.setImageBitmap(obj[i]);
			} else {
				AddImages(obj[i]);
			}

		}
	}

	private void AddImages(Bitmap bitmap) {
		ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(new LayoutParams(getWidth(), getHeight()));
		imageView.setImageBitmap(bitmap);
		imageView.setVisibility(View.VISIBLE);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		layout.addView(imageView);

		LayoutParams params = (LayoutParams) layout.getLayoutParams();
		params.width = layout.getChildCount() * getWidth();
		layout.setLayoutParams(params);
	}

	public void SetImages(int[] sliderInt) {
		for (int i = 0; i < sliderInt.length; i++) {
			if (i < layout.getChildCount()) {
				ImageView imageView = (ImageView) layout.getChildAt(i);
				imageView.setImageResource(sliderInt[i]);
			} else {
				AddImages(sliderInt[i]);
			}

		}
	}

	public void AddImages(int resId) {
		ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(new LayoutParams(getWidth(), getHeight()));
		imageView.setImageResource(resId);
		imageView.setVisibility(View.VISIBLE);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		layout.addView(imageView);

		LayoutParams params = (LayoutParams) layout.getLayoutParams();
		params.width = layout.getChildCount() * getWidth();
		layout.setLayoutParams(params);
	}
}
