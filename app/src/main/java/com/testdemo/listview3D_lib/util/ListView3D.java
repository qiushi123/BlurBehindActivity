package com.testdemo.listview3D_lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;

import java.util.LinkedList;

public class ListView3D extends AdapterView<Adapter> {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ����ӵ���������ͼ�ڵ�ǰ�ǰ���һ������ͼ����ӵĲ���ģ��
	private static final int LAYOUT_MODE_BELOW = 0;
	// ��LAYOUT_MODE_BELOW�෴������ӵĲ���ģ��
    private static final int LAYOUT_MODE_ABOVE = 1;
	
    
    // ��ʼģʽ���û���δ�Ӵ�ListView
    private static final int TOUCH_MODE_REST = -1;
    // ����Down�¼�ģʽ
    private static final int TOUCH_MODE_DOWN = 0;
    // ����ģʽ
    private static final int TOUCH_MODE_SCROLL = 1;
    
    private static final int INVALID_INDEX = -1;
    
    
    
    /** 3D Ч�� **/
    // Item��List�Ŀ�ȱ�
    private static final float ITEM_WIDTH = 0.85f;
    // Item�߶���Ϊ��ҪԤ���������ռ䣬����ĸ߶���Itemʵ�ʸ߶ȵı���
    private static final float ITEM_VERTICAL_SPACE = 1.45f;
    private static final int AMBIENT_LIGHT = 55;
    private static final int DIFFUSE_LIGHT = 200;
    private static final float SPECULAR_LIGHT = 70;
    private static final float SHININESS = 200;
    private static final int MAX_INTENSITY = 0xFF;
    private static final float SCALE_DOWN_FACTOR = 0.15f;
    private static final int DEGREES_PER_SCREEN = 270;
    
    
    /** Fling �� ����Ч�� **/
    private static final int PIXELS_PER_SECOND = 1000;
    private static final float POSITION_TOLERANCE = 0.4f;
    private static final float VELOCITY_TOLERANCE = 0.5f;
    private static final float WAVELENGTH = 0.9f;
    private static final float AMPLITUDE = 0.0f;
    
    
    
	// ===========================================================
	// Fields
	// ===========================================================

	// ��ͼ����������
	private Adapter mAdapter;
	// ��ǰ��ʾ���һ��Item��Adapter��λ��
	private int mLastItemPosition = -1;
	// ��ǰ��ʾ��һ��Item��Adapter��λ��
	private int mFirstItemPosition;
	
	
	// ��ǰ������һ��item
	private int mListTop;
	// ��ǰ��һ����ʾ��item��ײ���һ��item�Ķ���ƫ����
	private int mListTopOffset;
	// ����Down�¼�ʱ���м�¼ 
	private int mListTopStart;
	
	// ��¼ListView��ǰ��������ģʽ
	private int mTouchMode = TOUCH_MODE_REST;

	// ��¼��һ�δ���X��
	private int mTouchStartX;
	// ��¼��һ�δ���Y��
	private int mTouchStartY;
	// ����¼Down�¼�ʱY��ֵ 
	private int mMotionY;
	
	// ������������С�ƶ�����
	private int mTouchSlop;
	
	// �ɷ���ʹ�õ�Rect
	private Rect mRect;
	
	// ���ڼ�����ֳ�������
	private Runnable mLongPressRunnable;
	
	// View���õ�ǰ��֧��һ������Item��ͼ����
	// ������˽�ListView��ͼ��θ��ÿ��Կ�AbsListView�ڲ���RecycleBin
	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();
	
	
	
	/** 3D Ч�� **/
    private int mListRotation;
    private Camera mCamera;
    private Matrix mMatrix;
    private Paint mPaint;
    private boolean mRotationEnabled = true;
    private boolean mLightEnabled = true;
	
    
    
    /** Fling �� ����Ч�� **/
    private VelocityTracker mVelocityTracker;
    private Dynamics mDynamics;
    private Runnable mDynamicsRunnable;
    private int mLastSnapPos = Integer.MIN_VALUE;
    
	// ===========================================================
	// Constructors
	// ===========================================================

	public ListView3D(Context context) {
		super(context);
		initListView(context);
	}

	public ListView3D(Context context, AttributeSet attrs) {
		super(context, attrs);
		initListView(context);
	}

	public ListView3D(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initListView(context);
	}
	
	// ===========================================================
	// Setter
	// ===========================================================

    public void setDynamics(final Dynamics dynamics) {
        if (mDynamics != null) {
            dynamics.setState(mDynamics.getPosition(), mDynamics.getVelocity(), AnimationUtils
                    .currentAnimationTimeMillis());
        }
        mDynamics = dynamics;
        if (!mRotationEnabled) {
            mDynamics.setMaxPosition(0);
        }
    }
	
    
    private void setSnapPoint() {
        if (mRotationEnabled) {
            final int rotation = mListRotation % 90;
            int snapPosition = 0;

            if (rotation < 45) {
                snapPosition = (-(mListRotation - rotation) * getHeight()) / DEGREES_PER_SCREEN;
            } else {
                snapPosition = (-(mListRotation + 90 - rotation) * getHeight())
                        / DEGREES_PER_SCREEN;
            }

            if (mLastSnapPos == Integer.MIN_VALUE && mLastItemPosition == mAdapter.getCount() - 1
                    && getChildBottom(getChildAt(getChildCount() - 1)) < getHeight()) {
                mLastSnapPos = snapPosition;
            }

            if (snapPosition > 0) {
                snapPosition = 0;
            } else if (snapPosition < mLastSnapPos) {
                snapPosition = mLastSnapPos;
            }
            mDynamics.setMaxPosition(snapPosition);
            mDynamics.setMinPosition(snapPosition);

        } else {
            if (mLastSnapPos == Integer.MIN_VALUE && mLastItemPosition == mAdapter.getCount() - 1
                    && getChildBottom(getChildAt(getChildCount() - 1)) < getHeight()) {
                mLastSnapPos = mListTop;
                mDynamics.setMinPosition(mLastSnapPos);
            }
        }
    }
    
	// ===========================================================
	// Getter
	// ===========================================================
	
	private int getChildMargin(View child) {
	    return (int)(child.getMeasuredHeight() * (ITEM_VERTICAL_SPACE - 1) / 2);
	}
	
	private int getChildTop(View child) {
	    return child.getTop() - getChildMargin(child);
	}
	
	private int getChildBottom(View child) {
	    return child.getBottom() + getChildMargin(child);
	}
	
	private int getChildHeight(View child) {
	    return child.getMeasuredHeight() + 2 * getChildMargin(child);
	}

	
    public void enableRotation(final boolean enable) {
        mRotationEnabled = enable;
        mDynamics.setMaxPosition(Float.MAX_VALUE);
        mDynamics.setMinPosition(-Float.MAX_VALUE);
        mLastSnapPos = Integer.MIN_VALUE;
        if (!mRotationEnabled) {
            mListRotation = 0;
            mDynamics.setMaxPosition(0);
        } else {
            mListRotation = -(DEGREES_PER_SCREEN * mListTop) / getHeight();
            setSnapPoint();
            if (mDynamics != null) {
                mDynamics.setState(mListTop, mDynamics.getVelocity(), AnimationUtils
                        .currentAnimationTimeMillis());
                post(mDynamicsRunnable);
            }
        }    
        invalidate();
    }

    public boolean isRotationEnabled() {
        return mRotationEnabled;
    }

    public void enableLight(final boolean enable) {
        mLightEnabled = enable;
        if (!mLightEnabled) {
            mPaint.setColorFilter(null);
        } else {
            mPaint.setAlpha(0xFF);
        }
        invalidate();
    }

    public boolean isLightEnabled() {
        return mLightEnabled;
    }
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		// �쳣����
		if (mAdapter == null) {
			return;
		}
		
		// ��ǰListViewû���κ�����ͼ(Item)�����������ڴ��������������ͼ
		if (getChildCount() == 0) {
			mLastItemPosition = -1;
			// add and measure
			fillListDown(mListTop, 0);
		} else {
			final int offset = mListTop + mListTopOffset - getChildTop(getChildAt(0));
			// �Ƴ���������Ķ��ɵ�
			removeNonVisibleViews(offset);
			fillList(offset);
		}

		// layout����Ӳ�����󣬻�ȡ��ͼ�ڷ�λ��
		positioinItems();
		
		// draw�� ��������ͼ��������ˣ��ػ沼�ְ�����ͼ���Ƴ�����
		invalidate();
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(ev);
			return false;

		case MotionEvent.ACTION_HOVER_MOVE:
			return startScrollIfNeeded((int)ev.getY());
			
		default:
			endTouch(0);
			return false;
		}
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getChildCount() == 0) {
			return false;
		}
		
		final int y = (int) event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(event);
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchMode == TOUCH_MODE_DOWN) {
				startScrollIfNeeded(y);
			} else if (mTouchMode == TOUCH_MODE_SCROLL) {
                mVelocityTracker.addMovement(event);
				scrollList(y - mTouchStartY);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			float velocity = 0;
			// �����ǰ����û�д���������״̬��Ȼ��DOWN
			// ˵���ǵ��ĳһ��Item
			if (mTouchMode == TOUCH_MODE_DOWN) {
				clickChildAt((int)event.getX(), y);
			} else if (mTouchMode == TOUCH_MODE_SCROLL) {
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND);
                velocity = mVelocityTracker.getYVelocity();
            }
			
			endTouch(velocity);
			break;
		
		default:
			endTouch(0);
			break;
		}
		
		return true;
	}
	


	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		final Bitmap bitmap = child.getDrawingCache();
		if (bitmap == null) {
			return super.drawChild(canvas, child, drawingTime);
		}
		
		// ��ǰItem��ඥ������ֵ
		int left = child.getLeft();
		int top = child.getTop();
		
		// ��ǰItem�в�ƫ��
		int centerX = child.getWidth() / 2;
		int centerY = child.getHeight() / 2;
		
		
		// �������м�λ�õľ���
		float centerScreen = getHeight() / 2;
		
		// ��������
		// ?
		float distFromCenter = (top + centerY - centerScreen) / centerScreen;
        float scale = (float)(1 - SCALE_DOWN_FACTOR * (1 - Math.cos(distFromCenter)));
        
        // ������ת
        float childRotation = mListRotation - 20 * distFromCenter;
        childRotation %= 90;
        if (childRotation < 0) {
			childRotation += 90;
		}
		
        
        // ���Ƶ�ǰItem
        if (childRotation < 45) {
        	// ��ǳ���ʱ - �²�3D
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation - 90);
			// ��������ʾ��Item
			drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation);
		} else {
        	// ��������ʾ��Item
            drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation);
            // ��ǳ���ʱ - �ϲ�3D
            drawFace(canvas, bitmap, top, left, centerX, centerY, scale, childRotation - 90);
		}
		
		return false;
	}




	// ===========================================================
	// Private Methods
	// ===========================================================


	/**
	 * ����3D�����
	 * 
	 * @param canvas  drawChild�ص��ṩ��Canvas����
	 * @param view    
	 * @param top
	 * @param left
	 * @param centerX
	 * @param centerY
	 * @param scale
	 * @param rotation
	 */
	private void drawFace(final Canvas canvas, final Bitmap view, final int top, final int left,
			final int centerX, final int centerY, final float scale, final float rotation) {
		
		// ���֮ǰû�д����¶���
		if (mCamera == null) {
			mCamera = new Camera();
		}
		
		// ���棬�������²�����֮��ϵͳʹ�õ�Canvas���Ӱ��
		mCamera.save();
		
		// ƽ�ƺ���תCamera
		mCamera.translate(0, 0, centerY);
		mCamera.rotateX(rotation);
		mCamera.translate(0, 0, -centerY);
		
		// ���֮ǰû��Matrix�����¶���
		if (mMatrix == null) {
			mMatrix = new Matrix();
		}
		
		mCamera.getMatrix(mMatrix);
		mCamera.restore();
		
		// ƽ�ƺ�����Matrix
		mMatrix.preTranslate(-centerX, -centerY);
		mMatrix.postScale(scale, scale);
		mMatrix.postTranslate(left + centerX, top + centerY);
		
		// �����ͳ�ʼ��
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
		}
		
		// 
		if (mLightEnabled) {
			mPaint.setColorFilter(calculateLight(rotation));
		} else {
			// 
			mPaint.setAlpha(0xFF - (int)(2 * Math.abs(rotation)));
		}
		
		
		// ����Bitmap
		canvas.drawBitmap(view, mMatrix, mPaint);
		
	}
	
	
    private LightingColorFilter calculateLight(final float rotation) {
        final double cosRotation = Math.cos(Math.PI * rotation / 180);
        int intensity = AMBIENT_LIGHT + (int)(DIFFUSE_LIGHT * cosRotation);
        int highlightIntensity = (int)(SPECULAR_LIGHT * Math.pow(cosRotation, SHININESS));

        if (intensity > MAX_INTENSITY) {
            intensity = MAX_INTENSITY;
        }
        if (highlightIntensity > MAX_INTENSITY) {
            highlightIntensity = MAX_INTENSITY;
        }

        final int light = Color.rgb(intensity, intensity, intensity);
        final int highlight = Color.rgb(highlightIntensity, highlightIntensity, highlightIntensity);

        return new LightingColorFilter(light, highlight);
    }
	
	
	
	/**
	 * ListView��ʼ��
	 */
	private void initListView(Context context) {
		
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
	}

	/**
	 * ��ǰListView�������ͼ������Measure����ͼ����
	 * 
	 * @param child  ��Ҫ��ӵ�ListView����ͼ(Item)  
	 * @param layoutMode  �ڶ������������ӻ����ڵײ������������ͼ �� LAYOUT_MODE_ABOVE �� LAYOUT_MODE_BELOW
	 */
	private void addAndMeasureChild(View child, int layoutMode) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		
		// addViewInLayout  index��
		final int index = layoutMode == LAYOUT_MODE_ABOVE ? 0: -1;
		child.setDrawingCacheEnabled(true);
		addViewInLayout(child, index, params, true);
	
		final int itemWidth = (int) (getWidth() * ITEM_WIDTH);
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	/**
	 * ����������ͼ����layout������ȡ����������ͼ��ȷ��λ��
	 */
	private void positioinItems() {
		int top = mListTop + mListTopOffset;
        final float amplitude = getWidth() * AMPLITUDE;
        final float frequency = 1 / (getHeight() * WAVELENGTH);
		
		
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			
			final int offset = (int)(amplitude * Math.sin(2 * Math.PI * frequency * top));
			// ��ǰ��ͼδ��Ȼ��ӵ�ViewGroup���ǻ�δ���½���measure, layout, draw����
			// ֱ��ͨ��child.getWidth();��ȡ�������
			final int width = child.getMeasuredWidth();
			final int height = child.getMeasuredHeight();
			final int left = offset + (getWidth() - width) / 2;
            final int margin = getChildMargin(child);
            final int childTop = top + margin;
			
			child.layout(left, childTop, left + width, childTop + height);
			// ���¶���ҪMargin
			top += height + 2 * margin;
		}
	}

	/**
	 * ��ʼ������֮�����¼��жϴ���Ĳ���
	 * 
	 * @param event 
	 */
	private void startTouch(MotionEvent event) {
		removeCallbacks(mDynamicsRunnable);
		
		mTouchStartX = (int) event.getX();
		mMotionY = mTouchStartY = (int) event.getY();
		
		mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;
		
		startLongPressCheck();
		
        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
		
		mTouchMode = TOUCH_MODE_DOWN;
	}

	/**
	 * �Ƿ������������
	 * 
	 * @param y     ��ǰ������Y���ֵ
	 * @return true ���Թ���
	 */
	private boolean startScrollIfNeeded(int y) {
		// ��ͬ���˴�ģ��AbsListViewʵ��
		
		final int deltaY = y - mMotionY;
		final int distance = Math.abs(deltaY);
		
		// ֻ���ƶ�һ������֮�����ΪĿ��������ListView����
		if (distance > mTouchSlop) {
		
			
			// ��¼��ǰ���ڹ���״̬
			mTouchMode = TOUCH_MODE_SCROLL;
			return true;
		}
		
		return false;
	}

	/**
	 * ͨ��������X,Y�������ȡ�ǵ����һ��Item
	 * 
	 * @param x ������X��ֵ
	 * @param y ������Y��ֵ
	 * @return
	 */
	private int getContainingChildIndex(int x, int y) {
		if (mRect == null) {
			mRect = new Rect();
		}
		
		// ������ǰListView����Item
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).getHitRect(mRect);
			// x,y�Ƿ��ڵ�ǰ��ͼ������
			if (mRect.contains(x, y)) {
				return i;
			}
		}
		
		return INVALID_POSITION;
	}
	
	/**
	 * ����ListView���й���
	 * 
	 * @param scrolledDistance ��ǰ��ָ����λ����մ�������Ļ֮��ľ���,
	 * 								Ҳ���ǵ�ǰ��ָ����Ļ��Y�����ƶ�λ��
	 */
	private void scrollList(final int scrolledDistance) { // scrollIfNeeded
		// �ı䵱ǰ��¼��ListView����λ��
		mListTop = mListTopStart + scrolledDistance;
		
        if (mRotationEnabled) {
            mListRotation = -(DEGREES_PER_SCREEN * mListTop) / getHeight();
        }
		
        setSnapPoint();
        
		// �ؼ���Ҫ��ʹ����ļ�����Ч�����������󲼾�
		// �ᴥ����ǰonLayout������ָ��Itemλ��������ȹػ�����onLayout��
		requestLayout();
	}
	
	/**
	 * ListView���ϻ��������ƶ�����Ҫ�򶥲����ߵײ������ͼ
	 * 
	 * @param offset
	 */
	private void fillList(final int offset) {
		// ���һ��item���±߽�ֵ���ǵ�ǰListView���±߽�ֵ
		final int bottomEdge = getChildBottom(getChildAt(getChildCount() - 1));
		fillListDown(bottomEdge, offset);
		
		// ��һ��Item���ϱ߽�ֵ����ListVie���ϱ߽�ֵ
		final int topEdge = getChildTop(getChildAt(0));
		fillListUp(topEdge, offset);
	}
	
	
	/**
	 * ��fillListDown�෴�������
	 * 
	 * @param topEdge ��ǰ��һ������ͼ�����߽�ֵ
	 * @param offset ��ʾ����ƫ����
	 */
	private void fillListUp(int topEdge, int offset) {
		while (topEdge + offset > 0 && mFirstItemPosition > 0) {
			// ������ӵ���ͼʱ��ǰ����ͼǰ�棬����λ��-1
			mFirstItemPosition--;
			
			View newTopChild = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
			addAndMeasureChild(newTopChild, LAYOUT_MODE_ABOVE);
			int childHeight = getChildHeight(newTopChild);
			topEdge -= childHeight;
			
			// �ڶ��������ͼ�󣬸��¶���ƫ��
			mListTopOffset -= childHeight;
		}
	}
	
	
	/**
	 * ��ǰ���һ������ͼ������ӣ���䵽��ǰListView�ײ����ٿ��������Ϊֹ
	 * 
	 * @param bottomEdge ��ǰ���һ������ͼ�ײ��߽�ֵ
	 * @param offset ��ʾ����ƫ����
	 */
	private void fillListDown(int bottomEdge, int offset) {
		while (bottomEdge + offset < getHeight() && mLastItemPosition < mAdapter.getCount() - 1) {
			// ������ӵ���ͼʱ��ǰ����ͼ���棬����λ��+1
			mLastItemPosition++;
			
			// ���ݺ���ͼͨ��Adapter���䣬�˴���Adapter��ȡ��ͼ��
			// �ڶ����������븴�õ�View�����ȳ���null��֮�������View�����û���
			View newBottomChild = mAdapter.getView(mLastItemPosition, getCachedView(), this);
			// **���������ͼ����
			addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW);
			// ���һ������ͼ(Item)����֮�ײ��߽�Ҳ�����ı�
			bottomEdge += getChildHeight(newBottomChild);
		}
	}

	
	/**
	 * ������Ļ�����������������
	 */
	private void endTouch(final float velocity) {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
		
		// �������ˣ������Ƿ�ִ���ˣ��ɵ��������߳�
		removeCallbacks(mLongPressRunnable);
		
        if (mDynamicsRunnable == null) {
            mDynamicsRunnable = new Runnable() {
                public void run() {
                    if (mDynamics == null) {
                        return;
                    }
                    mListTopStart = getChildTop(getChildAt(0)) - mListTopOffset;
                    mDynamics.update(AnimationUtils.currentAnimationTimeMillis());

                    scrollList((int)mDynamics.getPosition() - mListTopStart);

                    if (!mDynamics.isAtRest(VELOCITY_TOLERANCE, POSITION_TOLERANCE)) {
                        postDelayed(this, 16);
                    }

                }
            };
        }

        if (mDynamics != null) {
            // update the dynamics with the correct position and start the
            // runnable
            mDynamics.setState(mListTop, velocity, AnimationUtils.currentAnimationTimeMillis());
            post(mDynamicsRunnable);
        }
		
		mTouchMode = TOUCH_MODE_REST;
	}

	/**
	 * ����ItemClickListener�ṩ��ǰ���λ��
	 * 
	 * @param x ������X��ֵ
	 * @param y ������Y��ֵ
	 */
	private void clickChildAt(int x, int y) {
		// �������ڵ�ǰ��ʾ����Item����һ��
		final int itemIndex = getContainingChildIndex(x, y);
		
		if (itemIndex != INVALID_INDEX) {
			final View itemView = getChildAt(itemIndex);
			// ��ǰItem��ListView����Item�е�λ��
			final int position = mFirstItemPosition + itemIndex;
			final long id = mAdapter.getItemId(position);
			
			// ���ø��෽�����ᴥ��ListView ItemClickListener
			performItemClick(itemView, position, id);
		}
	}

	/**
	 * �����첽�̣߳���������ʱ����LongClickListener
	 */
	private void startLongPressCheck() {
		// �������߳�
		if (mLongPressRunnable == null) {
			mLongPressRunnable = new Runnable() {
				
				@Override
				public void run() {
					if (mTouchMode == TOUCH_MODE_DOWN) {
						final int index = getContainingChildIndex(
								mTouchStartX, mTouchStartY);
						if (index != INVALID_INDEX) {
							longClickChild(index);
						}
					}
				}
			};
		}
		
		// ViewConfiguration.getLongPressTimeout() ��ȡϵͳ���õĳ�����ʱ����
		// �������Ѿ���������Ҫ��ʱ�䣬�ſ�ʼִ�д��߳�
		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	
	
	/**
	 * ����ItemLongClickListener�ṩ���λ�õ���Ϣ
	 * 
	 * @param index Item����ֵ
	 */
	private void longClickChild(final int index) {
		final View itemView = getChildAt(index);
		final int position = mFirstItemPosition + index;
		final long id = mAdapter.getItemId(position);
		// �Ӹ����ȡ�󶨵�OnItemLongClickListener
		OnItemLongClickListener listener = getOnItemLongClickListener();
		
		if (listener != null) {
			listener.onItemLongClick(this, itemView, position, id);
		}
	}
	
	
	
	/**
	 * ɾ����ǰ�Ѿ��Ƴ����ӷ�Χ��Item View
	 * 
	 * @param offset ��������ƫ����
	 */
	private void removeNonVisibleViews(final int offset) {
		int childCount = getChildCount();
		
		/**  ListView���Ϲ�����ɾ�������Ƴ����������������ͼ  **/
		
		// ����ListView�ײ�������ͼ����1
		if (mLastItemPosition != mAdapter.getCount() -1 && childCount > 1) {
			View firstChild = getChildAt(0);
			// ͨ���ڶ������жϵ�ǰ���������ͼ�Ƿ��Ƴ���������
			while (firstChild != null && getChildBottom(firstChild) + offset < 0) {
				// ��Ȼ������һ����ͼ�Ѿ��Ƴ���������ӵ�ǰViewGroup��ɾ����
				removeViewInLayout(firstChild);
				// �����´��жϣ��Ƿ�ǰ����������Ҫ�Ƴ�����ͼ
				childCount--;
				// View������գ�Ŀ����Ϊ�˸���
				mCachedItemViews.addLast(firstChild);
				// ��Ȼ���������ͼ���ɵ��ˣ���ǰListView��һ����ʾ��ͼҲ��Ҫ+1
				mFirstItemPosition++;
				// ͬ�ϸ���
				mListTopOffset += getChildHeight(firstChild);
				
				// Ϊ��һ��while������ȡ����
				if (childCount > 1) {
					// ��ǰ�Ѿ�ɾ����һ�����ٽ���ȥ��ɾ����ʣ��ĵ�һ��
					firstChild = getChildAt(0);
				} else {
					// û��
					firstChild = null;
				}
			}
		}
		
		
		/**  ListView���¹�����ɾ���ײ��Ƴ����������������ͼ  **/
		// ���������һ����ֻ�Ƿ����෴һ����������һ���ײ�����
		if (mFirstItemPosition != 0 && childCount > 1) {
			View lastChild = getChildAt(childCount - 1);
			while (lastChild != null && getChildTop(lastChild) + offset > getHeight()) {
				removeViewInLayout(lastChild);
				childCount--;
				mCachedItemViews.addLast(lastChild);
				mLastItemPosition--;
				
				if (childCount > 1) {
					lastChild = getChildAt(childCount - 1);
				} else {
					 lastChild = null;
				}
			}
		}
		
	}
	
	
	/**
	 * ��ȡһ�����Ը��õ�Item View
	 * 
	 * @return view ���Ը��õ���ͼ����null
	 */
	private View getCachedView() {
		
		if (mCachedItemViews.size() != 0) {
			return mCachedItemViews.removeFirst();
		}
		
		return null;
	}
	
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
