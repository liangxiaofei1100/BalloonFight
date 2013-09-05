package com.dreamlink.beatballoon.net;

import java.util.ArrayList;
import java.util.Arrays;

import com.dreamlink.aidl.User;
import com.dreamlink.util.ArrayUtil;
import com.dreamlink.util.Log;

public class ProtocolDecoder {
	private static final String TAG = "ProtocolDecoder";
	private Callback mCallback;

	public ProtocolDecoder(Callback callback) {
		mCallback = callback;
	}

	public void decode(byte[] data, User sendUser) {
		if (data.length < Protocol.TYPE_LENGTH) {
			Log.e(TAG, "decode() data length error." + data.length);
			return;
		}
		int msgType = ArrayUtil.byteArray2Int(Arrays.copyOfRange(data, 0,
				Protocol.TYPE_LENGTH));
		data = Arrays.copyOfRange(data, Protocol.TYPE_LENGTH, data.length);

		switch (msgType) {
		case Protocol.TYPE_SYNC_OTHER_PLAYERS:
			Log.d(TAG, "TYPE_SYNC_OTHER_PLAYERS");
			handleMessageSyncOtherPlayers(data);
			break;
		case Protocol.TYPE_INPUT_TOUCH_EVENT:
			Log.d(TAG, "TYPE_INPUT_TOUCH_EVENT");
			handleMessageInputTouchEvent(data, sendUser);
			break;
		case Protocol.TYPE_THE_PLAYER_QUIT:
			Log.d(TAG, "TYPE_THE_PLAYER_QUIT");
			handleMessageQuit(data);
			break;
		case Protocol.TYPE_THE_PLAYER_REPLAY:
			Log.d(TAG, "TYPE_THE_PLAYER_REPLAY");
			handMessageReplay(data);
		case Protocol.TYPE_JOIN_GAME:
			Log.d(TAG, "TYPE_JOIN_GAME");
			handMessageJoin(data, sendUser);
			break;
		case Protocol.TYPE_SEARCH_OTHER_PLAYERS:
			Log.d(TAG, "TYPE_SEARCH_OTHER_PLAYERS");
			handMessageSearchPlayers(data, sendUser);
			break;
		case Protocol.TYPE_SYNC_SCORE:
			Log.d(TAG, "TYPE_SYNC_SCORE");
			handMessageSyncScore(data, sendUser);
			break;
		case Protocol.TYPE_SYNC_LIFE:
			Log.d(TAG, "TYPE_SYNC_LIFE");
			handMessageSyncLife(data, sendUser);
			break;
		default:
			Log.d(TAG, "Unkown message type: " + msgType);
			break;
		}

	}

	private void handMessageSyncLife(byte[] data, User sendUser) {
		LifeData lifeData = (LifeData) ArrayUtil.byteArrayToObject(data);
		if (mCallback != null) {
			mCallback.onSyncLife(lifeData.getLifeOfPlayer1(),
					lifeData.getLifeOfPlayer2());
		}

	}

	private void handMessageSyncScore(byte[] data, User sendUser) {
		ScoreData scoreData = (ScoreData) ArrayUtil.byteArrayToObject(data);
		if (mCallback != null) {
			mCallback.onSyncSore(scoreData.getScoreOfPlayer1(),
					scoreData.getScoreOfPlayer2());
		}
	}

	private void handleMessageInputTouchEvent(byte[] data, User sendUser) {
		TouchEventData touchEventData = (TouchEventData) ArrayUtil
				.byteArrayToObject(data);

		if (mCallback != null) {
			mCallback.onPlayerTouch(touchEventData.getX(),
					touchEventData.getY(), touchEventData.ismTouched());
		}
	}

	private void handMessageSearchPlayers(byte[] data, User sendUser) {
		if (mCallback != null) {
			mCallback.onSearchRequest(sendUser);
		}
	}

	private void handMessageJoin(byte[] data, User sendUser) {
		if (mCallback != null) {
			mCallback.onPlayerJoin(sendUser);
		}
	}

	private void handMessageReplay(byte[] data) {
		if (mCallback != null) {
			mCallback.onPlayerReplay();
		}
	}

	private void handleMessageQuit(byte[] data) {
		if (mCallback != null) {
			mCallback.onPlayerQuit();
		}
	}

	private void handleMessageSyncOtherPlayers(byte[] data) {
		GameSyncData gameSyncData = (GameSyncData) ArrayUtil
				.byteArrayToObject(data);

		if (mCallback != null) {
			mCallback.onSyncGame(gameSyncData.getmBalloonDatas(),
					gameSyncData.getmPlayerDatas());
		}
	}

	public interface Callback {
		void onSyncGame(ArrayList<BalloonData> balloons,
				ArrayList<PlayerData> players);

		void onPlayerQuit();

		void onPlayerReplay();

		void onPlayerJoin(User player);

		void onSearchRequest(User sendUser);

		void onPlayerTouch(float x, float y, boolean touched);

		void onSyncSore(int scoreOfPlayer1, int scoreOfPlayer2);

		void onSyncLife(int lifeOfPlayer1, int lifeOfPlayer2);
	}
}
