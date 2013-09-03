package com.dreamlink.beatballoon.net;

import java.io.Serializable;

public class LifeData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6876997830369177028L;
	private int mLifeOfPlayer1;
	private int mLifeOfPlayer2;

	public LifeData(int lifeOfPlayer1, int lifeOfPlayer2) {
		mLifeOfPlayer1 = lifeOfPlayer1;
		mLifeOfPlayer2 = lifeOfPlayer2;
	}

	/**
	 * @return the mLifeOfPlayer1
	 */
	public int getLifeOfPlayer1() {
		return mLifeOfPlayer1;
	}

	/**
	 * @param mLifeOfPlayer1
	 *            the mLifeOfPlayer1 to set
	 */
	public void setLifeOfPlayer1(int lifeOfPlayer1) {
		mLifeOfPlayer1 = lifeOfPlayer1;
	}

	/**
	 * @return the mLifeOfPlayer2
	 */
	public int getLifeOfPlayer2() {
		return mLifeOfPlayer2;
	}

	/**
	 * @param mLifeOfPlayer2
	 *            the mLifeOfPlayer2 to set
	 */
	public void setLifeOfPlayer2(int lifeOfPlayer2) {
		mLifeOfPlayer2 = lifeOfPlayer2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LifeData [mLifeOfPlayer1=" + mLifeOfPlayer1
				+ ", mLifeOfPlayer2=" + mLifeOfPlayer2 + "]";
	}

}
