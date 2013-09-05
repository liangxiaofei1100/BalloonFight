package com.dreamlink.beatballoon.net;

import java.io.Serializable;

import com.dreamlink.role.Balloon;
import com.dreamlink.role.Player;
import com.dreamlink.util.ArrayUtil;

public class ProtocolEncoder {
	@SuppressWarnings("unused")
	private static final String TAG = "ProtocolEncoder";

	public static byte[] encodeSyncLife(int lifeOfPlayer1, int lifeOfPlayer2) {
		LifeData lifeData = new LifeData(lifeOfPlayer1, lifeOfPlayer2);
		return encode(Protocol.TYPE_SYNC_LIFE, lifeData);
	}

	public static byte[] encodeSyncScore(int scoreOfPlayer1, int scoreOfPlayer2) {
		ScoreData scoreData = new ScoreData(scoreOfPlayer1, scoreOfPlayer2);
		return encode(Protocol.TYPE_SYNC_SCORE, scoreData);
	}

	public static byte[] encodeSyncOtherPlayers(Balloon[] balloons,
			Player[] players, int screenWidth, int screenHeight) {
		GameSyncData gameSyncData = new GameSyncData(balloons, players,
				screenWidth, screenHeight);
		return encode(Protocol.TYPE_SYNC_OTHER_PLAYERS, gameSyncData);
	}

	public static byte[] encodeInputTouchEvent(float x, float y,
			int screenWidth, int screenHeight,boolean touched) {
		TouchEventData touchEventData = new TouchEventData(x / screenWidth, y
				/ screenHeight,touched);
		return encode(Protocol.TYPE_INPUT_TOUCH_EVENT, touchEventData);
	}

	public static byte[] encodeQuitGame() {
		return ArrayUtil.int2ByteArray(Protocol.TYPE_THE_PLAYER_QUIT);
	}

	public static byte[] encodeReplayGame() {
		return ArrayUtil.int2ByteArray(Protocol.TYPE_THE_PLAYER_REPLAY);
	}

	public static byte[] encodeJoinGame() {
		return ArrayUtil.int2ByteArray(Protocol.TYPE_JOIN_GAME);
	}

	public static byte[] encodeSearchOtherPlayers() {
		return ArrayUtil.int2ByteArray(Protocol.TYPE_SEARCH_OTHER_PLAYERS);
	}

	private static byte[] encode(int type, Serializable data) {
		byte[] typteData = ArrayUtil.int2ByteArray(type);
		byte[] dataData = ArrayUtil.objectToByteArray(data);
		return ArrayUtil.join(typteData, dataData);
	}
}
