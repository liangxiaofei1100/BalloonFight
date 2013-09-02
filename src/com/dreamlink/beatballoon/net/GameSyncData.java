package com.dreamlink.beatballoon.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.dreamlink.role.Balloon;
import com.dreamlink.role.Human;
import com.dreamlink.util.Log;

public class GameSyncData implements Serializable {
	private static final String TAG = "GameSyncData";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8098410840301305846L;

	private ArrayList<BalloonData> mBalloonDatas = new ArrayList<BalloonData>();
	private ArrayList<PlayerData> mPlayerDatas = new ArrayList<PlayerData>();

	public GameSyncData(Balloon[] balloons, Human[] players, int screenWidth,
			int screenHeight) {
		for (Balloon balloon : balloons) {
			BalloonData balloonData = new BalloonData(balloon.getX()
					/ (float) screenWidth, balloon.getY()
					/ (float) screenHeight);
			mBalloonDatas.add(balloonData);
		}

		for (Human player : players) {
			PlayerData playerData = new PlayerData(player.getX()
					/ (float) screenWidth, player.getY() / (float) screenHeight);
			mPlayerDatas.add(playerData);
		}
	}

	public ArrayList<BalloonData> getmBalloonDatas() {
		return mBalloonDatas;
	}

	public ArrayList<PlayerData> getmPlayerDatas() {
		return mPlayerDatas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GameSyncData [mBalloonDatas=" + mBalloonDatas
				+ ", mPlayerDatas=" + mPlayerDatas + "]";
	}

}
