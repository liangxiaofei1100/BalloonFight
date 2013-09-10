package com.dreamlink.beatballoon;

import com.dreamlink.util.DisplayUtil;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.Toast;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.CommunicationManager;
import com.dreamlink.communication.lib.CommunicationManager.OnCommunicationListener;
import com.dreamlink.communication.lib.CommunicationManager.OnConnectionChangeListener;
import com.dreamlink.communication.lib.util.AppUtil;
import com.dreamlink.beatballoon.GameView.GameViewCallback;
import com.dreamlink.beatballoon.ScoreView.OnGameOverListener;
import com.dreamlink.beatballoon.ScoreView.OnSoreChangedListener;
import com.dreamlink.beatballoon.net.BalloonData;
import com.dreamlink.beatballoon.net.PlayerData;
import com.dreamlink.beatballoon.net.ProtocolDecoder;
import com.dreamlink.beatballoon.net.ProtocolEncoder;
import com.dreamlink.role.Balloon;
import com.dreamlink.role.Player;
import com.dreamlink.util.Log;

public class MainActivity extends Activity implements ProtocolDecoder.Callback,
		GameViewCallback, OnSoreChangedListener, OnGameOverListener,
		OnConnectionChangeListener, OnCommunicationListener {
	private static final String TAG = "MainActivity";
	public int height, width;
	public static final int refreshSpeed = 30;
	public static MainActivity mainActivity;
	public static final int LIFE_NUMBER = 3;
	private Context mContext;

	private ProtocolDecoder mProtocolDecoder;

	private static final int MSG_COMPETITOR_QUIT = 1;
	private static final int MSG_COMPETITOR_REPLAY = 2;
	private static final int MSG_COMPETITOR_JOIN = 3;

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

	private ScoreView mScoreView;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case MSG_COMPETITOR_QUIT:
				Toast.makeText(mContext, R.string.player_quit,
						Toast.LENGTH_LONG).show();
				finish();
				break;

			case MSG_COMPETITOR_REPLAY:
				mGameView.resetGame();
				mScoreView.reset();
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

		mGameView = (GameView) findViewById(R.id.gameView);
		mGameView.setCallback(this);

		mScoreView = (ScoreView) findViewById(R.id.scoreView);
		mScoreView.setOnSoreChangedListener(this);
		mScoreView.setOnGameOverListener(this);

		mProtocolDecoder = new ProtocolDecoder(this);

		mAppId = AppUtil.getAppID(this);
		mCommunicationManager = new CommunicationManager(mContext);
		boolean result = mCommunicationManager.connectCommunicatonService(this,
				this, mAppId);
		if (result) {
			Log.d(TAG, "connectCommunicationService success.");
		} else {
			Toast.makeText(this, "connectCommunicationService fail.",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "bind service fail.");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		startGame();
	}

	private void quitGame() {
		Log.d(TAG, "quitGame");
		// Send message to competitor.
		byte[] data = ProtocolEncoder.encodeQuitGame();
		sendMessageToAllCompetitor(data);
	}

	@Override
	protected void onDestroy() {
		quitGame();
		mCommunicationManager.disconnectCommunicationService();
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
			mScoreView.setmSingelPlay(false);
			mGameView.setmSingelPlay(false);
			mScoreView.setmIsHost(mIsHost);
			mGameView.startGame(mIsHost);
		} else {
			Log.e(TAG, "startGame() error, player count: " + mPlayers.size());
			mGameView.setmSingelPlay(true);
			mScoreView.setmIsHost(true);
			mScoreView.setmSingelPlay(true);
			mGameView.startGame(false);
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
	private int mAppId;
	private CommunicationManager mCommunicationManager;

	private void sendMessageToSingleCompetitor(byte[] data, User receiver) {
		mCommunicationManager.sendMessage(data, receiver);
	}

	private void sendMessageToAllCompetitor(byte[] data) {
		mCommunicationManager.sendMessageToAll(data);
	}

	@Override
	public void onReceiveMessage(byte[] msg, User sendUser) {
		handleMessage(msg, sendUser);
	}

	@Override
	public void onUserConnected(User user) {
		Log.d(TAG, "onUserConnected() " + user);
	}

	@Override
	public void onUserDisconnected(User user) {
		Log.d(TAG, "onUserDisconnected() " + user);
	}

	@Override
	public void onCommunicationConnected() {
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

		mLocalPlayer = mCommunicationManager.getLocalUser();
		if (mLocalPlayer != null) {
			addPlayer(mLocalPlayer);
		} else {
			Log.e(TAG, "onCommunicationReady get local user fail. ");
		}
	}

	@Override
	public void onCommunicationDisconnected() {
		Log.d(TAG, "onCommunicationDisconnected");
	}

	private boolean checkCommunicationConnection() {
		boolean result = true;
		List<User> users = mCommunicationManager.getAllUser();
		if (users != null && users.size() > 1) {
			result = true;
		} else {
			result = false;
		}
		return result;
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
		Message message = mHandler.obtainMessage();
		message.what = MSG_COMPETITOR_REPLAY;
		mHandler.sendMessage(message);
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
	public void onPlayerTouch(float x, float y, boolean touched) {
		Log.d(TAG, "onPlayerTouch x = " + x + ", y = " + y);
		mGameView.onPlayerTouch(x, y, touched);
	}

	@Override
	public void onSyncLife(int lifeOfPlayer1, int lifeOfPlayer2) {
		Log.d(TAG, "onSyncLife lifeOfPlayer1 = " + lifeOfPlayer1
				+ ", lifeOfPlayer2 = " + lifeOfPlayer2);
		mScoreView.setLife(lifeOfPlayer1, lifeOfPlayer2);
	}

	@Override
	public void onSyncSore(int scoreOfPlayer1, int scoreOfPlayer2) {
		Log.d(TAG, "onSyncSore scoreOfPlayer1 = " + scoreOfPlayer1
				+ ", scoreOfPlayer2 = " + scoreOfPlayer2);
		mScoreView.setScore(scoreOfPlayer1, scoreOfPlayer2);
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
	public void onInputTouchEvent(MotionEvent motionEvent, int screenWidth,
			int screenHeight, boolean touched) {
		Log.d(TAG, "onInputTouchEvent: x = " + motionEvent.getX() + ", y = "
				+ motionEvent.getY());
		byte[] data = ProtocolEncoder.encodeInputTouchEvent(motionEvent.getX(),
				motionEvent.getY(), screenWidth, screenHeight, touched);
		sendMessageToAllCompetitor(data);
	}

	// GameView callback end.

	// Score view callback begin
	@Override
	public void onLifeChanged(int lifeOfPlayer1, int lifeOfPlayer2) {
		Log.d(TAG, "onLifeChanged lifeOfPlayer1 = " + lifeOfPlayer1
				+ ", lifeOfPlayer2 = " + lifeOfPlayer2);
		byte[] data = ProtocolEncoder.encodeSyncLife(lifeOfPlayer1,
				lifeOfPlayer2);
		sendMessageToAllCompetitor(data);
	}

	@Override
	public void onScoreChanged(int scoreOfPlayer1, int scoreOfPlayer2) {
		Log.d(TAG, "onScoreChanged scoreOfPlayer1 = " + scoreOfPlayer1
				+ ", scoreOfPlayer2 = " + scoreOfPlayer2);
		byte[] data = ProtocolEncoder.encodeSyncScore(scoreOfPlayer1,
				scoreOfPlayer2);
		sendMessageToAllCompetitor(data);
	}

	@Override
	public void onGameOverQuit() {
		Log.d(TAG, "onGameOverQuit");
		byte[] data = ProtocolEncoder.encodeQuitGame();
		sendMessageToAllCompetitor(data);
		finish();
	}

	@Override
	public void onGameOverPlayAgain() {
		Log.d(TAG, "onGameOverPlayAgain");
		byte[] data = ProtocolEncoder.encodeReplayGame();
		sendMessageToAllCompetitor(data);
		mGameView.resetGame();
	}
	// Score view callback end
}
