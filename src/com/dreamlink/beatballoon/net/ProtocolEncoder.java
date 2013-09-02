package com.dreamlink.beatballoon.net;

import com.dreamlink.role.Balloon;
import com.dreamlink.role.Player;
import com.dreamlink.util.ArrayUtil;

public class ProtocolEncoder {
	@SuppressWarnings("unused")
	private static final String TAG = "ProtocolEncoder";

	public static byte[] encodeSyncOtherPlayers(Balloon[] balloons,
			Player[] players, int screenWidth, int screenHeight) {
		byte[] typeData = ArrayUtil
				.int2ByteArray(Protocol.TYPE_SYNC_OTHER_PLAYERS);
		GameSyncData gameSyncData = new GameSyncData(balloons, players,
				screenWidth, screenHeight);
		byte[] syncData = ArrayUtil.objectToByteArray(gameSyncData);
		return ArrayUtil.join(typeData, syncData);
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
}
