package com.dreamlink.beatballoon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.dreamlink.beatballoon.net.BalloonData;
import com.dreamlink.beatballoon.net.PlayerData;
import com.dreamlink.role.Balloon;
import com.dreamlink.role.Human;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder;
	private boolean draw = true;
	private boolean gaming = true;
	private Human human1, human2;
	public static ConcurrentHashMap<Balloon, Integer> balloons;
	public static List<Human> humans;
	public static GameView mGameView;
	private GameViewCallback mCallback;

	private boolean mIsMaster = false;
	private boolean mIsPlayerJoined = false;
	
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mGameView = this;
		holder = this.getHolder();
		holder.addCallback(this);
		setZOrderOnTop(true);
		holder.setFormat(PixelFormat.TRANSLUCENT);
		balloons = new ConcurrentHashMap<Balloon, Integer>();
		humans = new ArrayList<Human>();
		setFocusable(true);
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Point point = new Point();
				point.x = (int) event.getRawX();
				point.y = (int) event.getRawY();
				if (humans.size() == 0) {
					return false;
				}
				if (MainActivity.IsHost && human1 != null)
					human1.moveTo(point);
				return false;
			}
		});

	}

	public void startGame(boolean isMaster) {
		mIsMaster = isMaster;
		mIsPlayerJoined = true;
		resetGame();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		initGame();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		baThread.start();
		drawBa.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		draw = false;
		gaming = false;
		if (human1 != null) {
			human1.setstillAlive(false);
		}
		if (human2 != null) {
			human2.setstillAlive(false);
		}
	}

	private Thread drawBa = new Thread() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (draw) {
				if (!gaming) {
					continue;
				}
				ArrayList<Balloon> temp = new ArrayList<Balloon>();
				for (java.util.Map.Entry<Balloon, Integer> b : balloons
						.entrySet()) {
					if (!b.getKey().isExsit()) {
						temp.add(b.getKey());
					}

				}
				if (temp.size() != 0) {
					synchronized (balloons) {
						for (Balloon b : temp) {
							balloons.remove(b);
						}
					}
				}
				// Sync with other players.
				if (mIsMaster) {
					if (mCallback != null) {
						mCallback.onSyncOtherPlayers((Balloon[]) balloons
								.keySet().toArray(new Balloon[0]),
								new Human[] { human1 }, getWidth(), getHeight());
						drawRole();
					}
				}
				try {
					Thread.sleep(MainActivity.refreshSped);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};

	private void drawRole() {
		if (holder == null) {
			return;
		}
		Canvas canvas = holder.lockCanvas();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ball_final);
		Paint paint = new Paint();
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		for (java.util.Map.Entry<Balloon, Integer> b : balloons.entrySet()) {
			canvas.drawBitmap(bitmap,
					b.getKey().getX() - bitmap.getWidth() / 2, b.getKey()
							.getY() - bitmap.getHeight(), paint);
		}
		bitmap.recycle();
		if (humans.size() == 0) {
			holder.unlockCanvasAndPost(canvas);
			return;
		}
		if (human1 != null) {
			if (human1.hasBalloon)
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.human_define_left);
			else
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.human_over);
			canvas.drawBitmap(bitmap, human1.getX() - bitmap.getWidth() / 2,
					human1.getY() - bitmap.getHeight() / 2, paint);
			bitmap.recycle();
		}

		if (human2 != null) {
			if (human2.hasBalloon)
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.human_define_right);
			else
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.human_over);
			canvas.drawBitmap(bitmap, human2.getX() - bitmap.getWidth() / 2,
					human2.getY() - bitmap.getHeight() / 2, paint);
			bitmap.recycle();
		}
		holder.unlockCanvasAndPost(canvas);
	}

	private Thread baThread = new Thread() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (draw) {
				if (!gaming) {
					continue;
				}
				Balloon b1 = new Balloon(BackgroundView.startPoint.get(0).x,
						BackgroundView.startPoint.get(0).y);
				Balloon b2 = new Balloon(BackgroundView.startPoint.get(1).x,
						BackgroundView.startPoint.get(1).y);
				Balloon b3 = new Balloon(BackgroundView.startPoint.get(2).x,
						BackgroundView.startPoint.get(2).y);
				Balloon b4 = new Balloon(BackgroundView.startPoint.get(3).x,
						BackgroundView.startPoint.get(3).y);
				b1.start();
				b2.start();
				b3.start();
				b4.start();
				balloons.put(b1, b1.getX());
				balloons.put(b2, b2.getX());
				balloons.put(b3, b3.getX());
				balloons.put(b4, b4.getX());
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private void setHumanStatus(int status, int id) {
		switch (id) {
		case 0:
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ball_final);
			Bitmap b1 = BitmapFactory.decodeResource(getResources(),
					R.drawable.human_define_left);
			humans.get(id).width = (bitmap.getWidth() + b1.getWidth()) / 2;
			humans.get(id).height = (bitmap.getHeight() + b1.getHeight()) / 2;
			bitmap.recycle();
			b1.recycle();
			break;
		case 1:
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ball_final);
			b1 = BitmapFactory.decodeResource(getResources(),
					R.drawable.human_define_right);
			humans.get(id).width = (bitmap.getWidth() + b1.getWidth()) / 2;
			humans.get(id).height = (bitmap.getHeight() + b1.getHeight()) / 2;
			bitmap.recycle();
			b1.recycle();
		default:
			break;
		}
	}

	public void resetGame() {
		clearData();
		initGame();
	}

	private void initGame() {
		gaming = true;
		human1 = new Human(0);
		human2 = new Human(1);
		human1.registerCallback(ScoreView.mScoreView);
		human2.registerCallback(ScoreView.mScoreView);
		humans.add(human1);
		humans.add(human2);
		human1.start();
		human2.start();
		setHumanStatus(0, 0);
		setHumanStatus(0, 1);
	}

	public void clearData() {
		gaming = false;
		humans.clear();
		balloons.clear();
	}
	public void setCallback(GameViewCallback callback) {
		mCallback = callback;
	}

	/**
	 * Be careful. These callbacks is not run in UI Thread.
	 * 
	 */
	public interface GameViewCallback {

		/**
		 * Send message to sync all players.
		 * 
		 * @param balloons
		 * @param players
		 * @param screenHeight
		 * @param screenWidth
		 */
		void onSyncOtherPlayers(Balloon[] balloons, Human[] players,
				int screenWidth, int screenHeight);

		/**
		 * The game is over.
		 * 
		 * @param result
		 */
		void onGameOver(int result);
	}

	public void syncGame(ArrayList<BalloonData> balloons2,
			ArrayList<PlayerData> players) {
		int width = getWidth();
		int height = getHeight();
		balloons.clear();
		for (BalloonData balloonData : balloons2) {
			Balloon balloon = new Balloon((int) (balloonData.getX() * width),
					(int) (balloonData.getY() * height));
			balloons.put(balloon, balloon.getX());
		}
		int playerNumber = players.size();
		if (playerNumber == 1) {
			PlayerData playerData = players.get(0);
			human1 = new Human(0);
			human1.registerCallback(ScoreView.mScoreView);
			human1.setX((int) (playerData.getX() * width));
			human1.setY((int) (playerData.getY() * height));
		} else if (playerNumber == 2) {
			PlayerData playerData = players.get(1);
			human2 = new Human(0);
			human1.registerCallback(ScoreView.mScoreView);
			human2.setX((int) (playerData.getX() * width));
			human2.setY((int) (playerData.getY() * height));
		}
		drawRole();
	}

}
