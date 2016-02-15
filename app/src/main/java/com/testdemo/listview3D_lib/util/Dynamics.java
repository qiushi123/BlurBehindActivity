package com.testdemo.listview3D_lib.util;

/**
 * FlingЧ��������
 */
public abstract class Dynamics {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int MAX_TIMESTEP = 50;
	
	// ===========================================================
	// Fields
	// ===========================================================

	/** ��ǰλ�� **/
	protected float mPosition;
	/** ��ǰ�ٶ� **/
    protected float mVelocity;
    /** ���ƶ����λ�� **/
    protected float mMaxPosition = Float.MAX_VALUE;
    /** ���ƶ���Сλ�� **/
    protected float mMinPosition = -Float.MAX_VALUE;
    /** ��¼��һ�θ���ʱ��ʱ��ֵ **/
    protected long mLastTime = 0;

    
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Setter
	// ===========================================================
    
    /**
     * ���ó�ʼ״̬
     * 
     * @param position ��ǰλ��
     * @param velocity ��ǰ�ٶ�
     * @param now      ����ʱ��
     */
    public void setState(final float position, final float velocity, final long now) {
        mVelocity = velocity;
        mPosition = position;
        mLastTime = now;
    }

    /**
     * ���������ƶ�����λ��
     * 
     * @param maxPosition
     */
    public void setMaxPosition(final float maxPosition) {
        mMaxPosition = maxPosition;
    }

    
    /**
     * ������С���ƶ�����λ��
     * 
     * @param minPosition
     */
    public void setMinPosition(final float minPosition) {
        mMinPosition = minPosition;
    }
    
	// ===========================================================
	// Getter
	// ===========================================================
    
    /**
     * ��ȡ��ǰλ��
     * 
     * @return
     */
    public float getPosition() {
        return mPosition;
    }

    /**
     * ��ȡ��ǰ�ٶ�
     * 
     * @return
     */
    public float getVelocity() {
        return mVelocity;
    }

    
    /**
     * �Ƿ��ڻָ�״̬
     * 
     * @param velocityTolerance
     * @param positionTolerance
     * @return
     */
    public boolean isAtRest(final float velocityTolerance, final float positionTolerance) {
        final boolean standingStill = Math.abs(mVelocity) < velocityTolerance;
        final boolean withinLimits = mPosition - positionTolerance < mMaxPosition
                && mPosition + positionTolerance > mMinPosition;
        return standingStill && withinLimits;
    }


    /**
     * ����
     * 
     * @param now
     */
    public void update(final long now) {
        int dt = (int)(now - mLastTime);
        if (dt > MAX_TIMESTEP) {
            dt = MAX_TIMESTEP;
        }

        onUpdate(dt);

        mLastTime = now;
    }

    
    /**
     * ��ȡ�����ƺ�Ŀ��ƶ�����
     * 
     * @return
     */
    protected float getDistanceToLimit() {
        float distanceToLimit = 0;

        if (mPosition > mMaxPosition) {
            distanceToLimit = mMaxPosition - mPosition;
        } else if (mPosition < mMinPosition) {
            distanceToLimit = mMinPosition - mPosition;
        }

        return distanceToLimit;
    }
    
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

    /**
     * @param dt
     */
    abstract protected void onUpdate(int dt);
    
	// ===========================================================
	// Private Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

 
    

}
