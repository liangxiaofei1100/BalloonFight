package com.dreamlink.beatballoon.net;

import java.io.Serializable;

public class ScoreData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2015721497483233755L;
	private int mScoreOfPlayer1;
	private int mScoreOfPlayer2;

	public ScoreData(int scoreOfPlayer1, int scoreOfPlayer2) {
		this.mScoreOfPlayer1 = scoreOfPlayer1;
		this.mScoreOfPlayer2 = scoreOfPlayer2;
	}

	/**
	 * @return the scoreOfPlayer1
	 */
	public int getScoreOfPlayer1() {
		return mScoreOfPlayer1;
	}

	/**
	 * @param scoreOfPlayer1
	 *            the scoreOfPlayer1 to set
	 */
	public void setScoreOfPlayer1(int scoreOfPlayer1) {
		this.mScoreOfPlayer1 = scoreOfPlayer1;
	}

	/**
	 * @return the scoreOfPlayer2
	 */
	public int getScoreOfPlayer2() {
		return mScoreOfPlayer2;
	}

	/**
	 * @param scoreOfPlayer2
	 *            the scoreOfPlayer2 to set
	 */
	public void setScoreOfPlayer2(int scoreOfPlayer2) {
		this.mScoreOfPlayer2 = scoreOfPlayer2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScoreData [mScoreOfPlayer1=" + mScoreOfPlayer1
				+ ", mScoreOfPlayer2=" + mScoreOfPlayer2 + "]";
	}

}
