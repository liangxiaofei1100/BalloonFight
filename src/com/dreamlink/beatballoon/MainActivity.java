package com.dreamlink.beatballoon;

import com.dreamlink.util.DisplayUtil;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import com.dreamlink.aidl.Communication;
import com.dreamlink.aidl.OnCommunicationListenerExternal;
import com.dreamlink.aidl.User;
import com.dreamlink.beatballoon.GameView.GameViewCallback;
import com.dreamlink.beatballoon.net.BalloonData;
import com.dreamlink.beatballoon.net.PlayerData;
import com.dreamlink.beatballoon.net.ProtocolDecoder;
import com.dreamlink.beatballoon.net.ProtocolEncoder;
import com.dreamlink.role.Balloon;
import com.dreamlink.role.Player;
import com.dreamlink.util.Log;

public class MainActivity extends Activity implements ProtocolDecoder.Callback,
		GameViewCallback {
	private static final String TAG = "MainActivity";
	public int height, width;
	public static final int refreshSpeed = 30;
	public static MainActivity mainActivity;
	public static final int LIFE_NUMBER = 3;
	private Context mContext;

	private ProtocolDecoder mProtocolDecoder;

	private static final int MSG_GAMEOVER = 1;
	private static final int MSG_COMPETITOR_QUIT = 2;
	private static final int MSG_COMPETITOR_REPLAY = 3;
	private static final int MSG_COMPETITOR_JOIN = 4;

	private User mLocalPlayer;
	private Vector<User> mPlayers = new Vector<User>();

	/** The player number of the game. */
	private static final int PLAYER_NUMBER = 2;
	/**
	 * This is host or not. Host will do all game calculations, and send the
	 * result to the other player.
	 * */
	private boolean mIsHost = false;

	private GameView mGameView;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GAMEOVER:
				// mGameOverView.setGameOverResult(msg.arg1);
				// mGameOverView.setVisibility(View.VISIBLE);
				break;

			case MSG_COMPETITOR_QUIT:
				Toast.makeText(mContext, R.string.player_quit,
						Toast.LENGTH_LONG).show();
				finish();
				break;

			case MSG_COMPETITOR_REPLAY:
				// mGameOverView.setVisibility(View.GONE);
				// startGame();
				break;

			case MSG_COMPETITOR_JOIN:
				User user = (User) msg.obj;
				Toast.makeText(
						mContext,
						getString(R.string.player_join, user.getUserName(),
								mPlayers.size()), Toast.LENGTH_LONG).show();

				if (mPlayers.size() == PLAYER_NUMBER) {
					// Start game.
					startGame();
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		height = DisplayUtil.getScreenHeight(this);
		width = DisplayUtil.getScreenWidth(this);
		mainActivity = this;

		mGameView = (GameView) findViewById(R.id.overlay2);
		mGameView.setCallback(this);

		mProtocolDecoder = new ProtocolDecoder(this);

		mAppId = getAppID();
		boolean result = connectCommunicationService();
		if (result) {
			Log.d(TAG, "connectCommunicationService success.");
		} else {
			Toast.makeText(this, "connectCommunicationService fail.",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "bind service fail.");
		}
	}

	@Override
	protected void onDestroy() {
		disconnectService();
		super.onDestroy();
	}

	/**
	 * Reset and start game();
	 */
	private void startGame() {
		if (mPlayers.size() == PLAYER_NUMBER) {
			if (mLocalPlayer.getUserID() > mPlayers.get(1).getUserID()) {
				mIsHost = true;
			} else {
				mIsHost = false;
			}
			Log.d(TAG, "startGame() isHost = " + mIsHost);
			mGameView.startGame(mIsHost);
		} else {
			Log.e(TAG, "startGame() error, player count: " + mPlayers.size());
			mGameView.startGame(true);
		}
	}

	private void addPlayer(User player) {
		Log.d(TAG, "addPlayer(): id = " + player.getUserID() + ", name = "
				+ player.getUserName());
		boolean isAdded = false;
		for (User user : mPlayers) {
			if (user.getUserID() == player.getUserID()) {
				isAdded = true;
			}
		}
		if (!isAdded) {
			mPlayers.add(player);

			if (player != mLocalPlayer) {
				Message message = mHandler.obtainMessage();
				message.what = MSG_COMPETITOR_JOIN;
				message.obj = player;
				mHandler.sendMessage(message);
			}
		}
	}

	private void handleMessage(byte[] data, User sendUser) {
		mProtocolDecoder.decode(data, sendUser);
	}

	// Communication Service begin
	/**
	 * Connected to communication service.
	 */
	private void onCommunicationReady() {
		if (!checkCommunicationConnection()) {
			Toast.makeText(mContext, "无网络连接，请先建立连接后再启动游戏。", Toast.LENGTH_LONG)
					.show();
		}
		// Search other players.
		byte[] searchData = ProtocolEncoder.encodeSearchOtherPlayers();
		sendMessageToAllCompetitor(searchData);
		// Tell other players we join the game
		byte[] joinData = ProtocolEncoder.encodeJoinGame();
		sendMessageToAllCompetitor(joinData);

		mLocalPlayer = getLocalUser();
		if (mLocalPlayer != null) {
			addPlayer(mLocalPlayer);
		} else {
			Log.e(TAG, "onCommunicationReady get local user fail. ");
		}
	}

	private final String INTENT_STRING = "com.dreamlink.communication.ComService";
	private int mAppId;
	private Communication mCommunication;
	private ServiceConnection mServiceConnection;

	private int getAppID() {
		try {
			ActivityInfo info = this.getPackageManager().getActivityInfo(
					getComponentName(), PackageManager.GET_META_DATA);
			return info.metaData.getInt("APPID");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, "Get App ID error!");
		}
		return 0;
	}

	private void sendMessageToSingleCompetitor(byte[] data, User receiver) {
		sendMessage(data, receiver);
	}

	private void sendMessageToAllCompetitor(byte[] data) {
		sendMessage(data, null);
	}

	private void sendMessage(byte[] data, User receiver) {
		if (mCommunication == null) {
			Log.e(TAG, "sendMessageToCompetitor fail, communication is null");
			return;
		}
		try {
			mCommunication.sendMessage(data, mAppId, null);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Service is connected, but communication is not connected.
			Log.e(TAG, "sendMessageToCompetitor fail: " + e);
		}
	}

	private boolean connectCommunicationService() {
		mServiceConnection = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
				try {
					mCommunication
							.unRegistListenr(mCommunicationListenerExternal);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				mCommunication = Communication.Stub.asInterface(service);
				try {
					mCommunication.registListenr(
							mCommunicationListenerExternal, mAppId);
					onCommunicationReady();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

		};
		Intent communicateIntent = new Intent(INTENT_STRING);
		return bindService(communicateIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	private void disconnectService() {
		if (mCommunication != null) {
			try {
				mCommunication.unRegistListenr(mCommunicationListenerExternal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
		}
	}

	private OnCommunicationListenerExternal mCommunicationListenerExternal = new OnCommunicationListenerExternal.Stub() {

		@Override
		public void onUserDisconnected(User user) throws RemoteException {

		}

		@Override
		public void onUserConnected(User user) throws RemoteException {

		}

		@Override
		public void onReceiveMessage(byte[] msg, User sendUser)
				throws RemoteException {
			handleMessage(msg, sendUser);
		}
	};

	private boolean checkCommunicationConnection() {
		boolean result = true;
		if (mCommunication == null) {
			result = false;
		} else {
			List<User> users = null;
			try {
				users = mCommunication.getAllUser();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (users != null && users.size() > 1) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}

	private User getLocalUser() {
		User user = null;
		if (mCommunication != null) {
			try {
				user = mCommunication.getLocalUser();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	// Communication Service end

	// Protocol callback begin.
	@Override
	public void onSyncGame(ArrayList<BalloonData> balloons,
			ArrayList<PlayerData> players) {
		Log.d(TAG, "onSyncGame. BalloonData: " + balloons + ", PlayerData: "
				+ players);
		mGameView.syncGame(balloons, players);
	}

	@Override
	public void onPlayerQuit() {
		Message message = mHandler.obtainMessage();
		message.what = MSG_COMPETITOR_QUIT;
		mHandler.sendMessage(message);
	}

	@Override
	public void onPlayerReplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerJoin(User player) {
		addPlayer(player);
	}

	@Override
	public void onSearchRequest(User sendUser) {
		// Tell the searcher we join the game.
		byte[] data = ProtocolEncoder.encodeJoinGame();
		sendMessageToSingleCompetitor(data, sendUser);
	}
	
	@Override
	public void onPlayerTouch(float x, float y) {
		// TODO Auto-generated method stub
		
	}
	// Protocol callback end.

	// GameView callback begin.

	@Override
	public void onSyncOtherPlayers(Balloon[] balloons, Player[] players,
			int screenWidth, int screenHeight) {
		Log.d(TAG,
				"onSyncOtherPlayers(), balloons: " + Arrays.toString(balloons)
						+ ", players: " + Arrays.toString(players));
		byte[] data = ProtocolEncoder.encodeSyncOtherPlayers(balloons, players,
				screenWidth, screenHeight);
		sendMessageToAllCompetitor(data);

	}

	@Override
	public void onGameOver(int result) {
		// TODO Auto-generated method stub

	}

	// GameView callback end.
}
