package com.dreamlink.beatballoon.net;

import java.io.Serializable;

public class TouchEventData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6952185221647846500L;

	private float mX;
	private float mY;

	public TouchEventData(float x, float y) {
		this.mX = x;
		this.mY = y;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return mX;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.mX = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return mY;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.mY = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TouchEventData [x=" + mX + ", y=" + mY + "]";
	}

}
