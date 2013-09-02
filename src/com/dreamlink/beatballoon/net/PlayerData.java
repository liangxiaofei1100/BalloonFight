package com.dreamlink.beatballoon.net;

import java.io.Serializable;

public class PlayerData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5983728843795679242L;
	private float mX;
	private float mY;

	public PlayerData(float x, float y) {
		mX = x;
		mY = y;
	}

	public float getX() {
		return mX;
	}

	public void setX(float x) {
		this.mX = x;
	}

	public float getY() {
		return mY;
	}

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
		return "PlayerData [mX=" + mX + ", mY=" + mY + "]";
	}

}
