package com.dreamlink.beatballoon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.dreamlink.beatballoon.net.BalloonData;
import com.dreamlink.beatballoon.net.PlayerData;
import com.dreamlink.role.Balloon;
import com.dreamlink.role.Player;
import com.dreamlink.role.Point;
import com.dreamlink.util.DisplayUtil;
import com.dreamlink.util.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder;
	private boolean draw = true;
	private boolean gaming = true;
	private Player human1, human2;
	public static ConcurrentHashMap<Balloon, Float> balloons;
	public static List<Player> humans;
	public static GameView mGameView;
	private GameViewCallback mCallback;
	private Context mContext;
	private Bitmap ballBitman, p1Bitmap, p2Bitmap;
	private Paint paint;
	private Canvas canvas;
	private BalloonThread balloonThread;
	private boolean mSingelPlay = true;

	public void setmSingelPlay(boolean mSingelPlay) {
		this.mSingelPlay = mSingelPlay;
	}

	private boolean mIsHost = false;
	private boolean mIsPlayerJoined = false;

	public GameView(Context context) {
		super(context);
		init(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mGameView = this;
		holder = this.getHolder();
		holder.addCallback(this);
		setZOrderOnTop(true);
		ballBitman = BitmapFactory.decodeResource(getResources(),
				R.drawable.ball_final);
		p1Bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.human_define_left2);
		p2Bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.human_define_right);
		paint = new Paint();
		holder.setFormat(PixelFormat.TRANSLUCENT);
		balloons = new ConcurrentHashMap<Balloon, Float>();
		humans = new ArrayList<Player>();
		setFocusable(true);
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Point point = new Point(event.getRawX(), event.getRawY());
				if (!gaming) {
					return false;
				}
				if ((mIsHost || mSingelPlay) && human1 != null) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						human1.moveTo(point, true);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						human1.moveTo(point, false);
					} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						human1.moveTo(point, true);
					}
				} else if (!mIsHost) {
					if (mCallback != null) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							mCallback.onInputTouchEvent(event, getWidth(),
									getHeight(), true);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							mCallback.onInputTouchEvent(event, getWidth(),
									getHeight(), false);
						} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
							mCallback.onInputTouchEvent(event, getWidth(),
									getHeight(), true);
						}
					}
				}
				return true;
			}
		});

	}

	public void startGame(boolean isMaster) {
		mIsHost = isMaster;
		mIsPlayerJoined = true;
		resetGame();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		initGame();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawBa.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		draw = false;
		gaming = false;
		if (human1 != null) {
			human1.setstillAlive(false);
		}
		if (human2 != null) {
			human2.setstillAlive(false);
		}
		clearData();
		ballBitman.recycle();
		p1Bitmap.recycle();
		p2Bitmap.recycle();
	}

	private Thread drawBa = new Thread() {

		@Override
		public void run() {
			super.run();
			while (draw) {
				if (!gaming) {
					continue;
				}
				ArrayList<Balloon> temp = new ArrayList<Balloon>();
				for (java.util.Map.Entry<Balloon, Float> b : balloons
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
				if (mIsHost) {
					if (mCallback != null && mIsPlayerJoined) {
						if (human1 != null && human2 != null) {
							try {
								mCallback.onSyncOtherPlayers(
										(Balloon[]) balloons.keySet().toArray(
												new Balloon[0]), new Player[] {
												human1, human2 }, getWidth(),
										getHeight());
							} catch (Exception e) {
								Log.e("onSyncOtherPlayers", e.toString());
							}
						}
					}
					try {
						canvas = holder.lockCanvas();
						drawRole(canvas);
						holder.unlockCanvasAndPost(canvas);
					} catch (Exception e) {
						if (canvas != null) {
							holder.unlockCanvasAndPost(canvas);
						}
					}
				} else if (mSingelPlay) {
					try {
						canvas = holder.lockCanvas();
						drawRole(canvas);
						holder.unlockCanvasAndPost(canvas);
					} catch (Exception e) {
						if (canvas != null) {
							holder.unlockCanvasAndPost(canvas);
						}
					}
				}
				try {
					Thread.sleep(MainActivity.refreshSpeed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	private void drawRole(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		for (java.util.Map.Entry<Balloon, Float> b : balloons.entrySet()) {
			canvas.drawBitmap(ballBitman,
					b.getKey().getX() - ballBitman.getWidth() / 2, b.getKey()
							.getY() - ballBitman.getHeight(), paint);
		}
		if (human1 != null) {
			canvas.drawBitmap(p1Bitmap,
					human1.getX() - p1Bitmap.getWidth() / 2, human1.getY()
							- p1Bitmap.getHeight() / 2, paint);
		}

		if (human2 != null) {
			canvas.drawBitmap(p2Bitmap,
					human2.getX() - p2Bitmap.getWidth() / 2, human2.getY()
							- p2Bitmap.getHeight() / 2, paint);
		}
	}

	private class BalloonThread extends Thread {
		public boolean flag = true;

		@Override
		public void run() {
			super.run();
			while (flag) {
				if (BackgroundView.startPoint.size() != 4) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
				if (!flag) {
					break;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setHumanStatus(int status, int id) {
		switch (id) {
		case 0:
			human1.width = (ballBitman.getWidth() + p1Bitmap.getWidth()) / 2;
			human1.height = (ballBitman.getHeight() + p1Bitmap.getHeight()) / 2;
			break;
		case 1:
			human2.width = (ballBitman.getWidth() + p2Bitmap.getWidth()) / 2;
			human2.height = (ballBitman.getHeight() + p2Bitmap.getHeight()) / 2;
		default:
			break;
		}
	}

	public void resetGame() {
		clearData();
		initGame();
	}

	private void initGame() {
		clearData();
		humans.clear();
		gaming = true;
		human1 = new Player(0, getTopHeight(), getBottomHeight(), getWidth());
		human1.registerCallback(ScoreView.mScoreView);
		human2 = new Player(1, getTopHeight(), getBottomHeight(), getWidth());
		if (mIsHost) {
			human2.registerCallback(ScoreView.mScoreView);
			humans.add(human1);
			humans.add(human2);
			human1.start();
			human2.start();
			balloonThread = new BalloonThread();
			balloonThread.start();
			setHumanStatus(0, 0);
			setHumanStatus(0, 1);
		} else if (mSingelPlay) {
			/**
			 * single player should initialization here,need add one flag to
			 * check it is client or single play
			 * */
			human2 = null;
			humans.add(human1);
			human1.start();
			balloonThread = new BalloonThread();
			balloonThread.start();
			setHumanStatus(0, 0);
		}
	}

	public void clearData() {
		gaming = false;
		if (balloonThread != null) {
			balloonThread.flag = false;
		}
		for (java.util.Map.Entry<Balloon, Float> baEntry : balloons.entrySet()) {
			baEntry.getKey().setExsit(false);
		}
		balloons.clear();
		if (human1 != null)
			human1.setstillAlive(false);
		if (human2 != null)
			human2.setstillAlive(false);
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
		void onSyncOtherPlayers(Balloon[] balloons, Player[] players,
				int screenWidth, int screenHeight);

		void onInputTouchEvent(MotionEvent motionEvent, int screenWidth,
				int screenHeight, boolean touched);

	}

	public void syncGame(ArrayList<BalloonData> balloons2,
			ArrayList<PlayerData> players) {
		int width = getWidth();
		int height = getHeight();
		balloons.clear();
		for (BalloonData balloonData : balloons2) {
			Balloon balloon = new Balloon((balloonData.getX() * width),
					(balloonData.getY() * height));
			balloons.put(balloon, balloon.getX());
		}
		int playerNumber = players.size();
		if (playerNumber == 1) {
			PlayerData playerData = players.get(0);
			human1 = new Player(0);
			human1.setX((playerData.getX() * width));
			human1.setY((playerData.getY() * height));
		} else if (playerNumber == 2) {
			PlayerData playerData = players.get(0);
			human1 = new Player(0);
			human1.setX((playerData.getX() * width));
			human1.setY((playerData.getY() * height));
			playerData = players.get(1);
			human2 = new Player(1);
			human2.setX((playerData.getX() * width));
			human2.setY((playerData.getY() * height));
		}
		try {
			canvas = holder.lockCanvas();
			drawRole(canvas);
			holder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private int getTopHeight() {
		int height = p1Bitmap.getHeight();
		return height / 2;
	}

	private int getBottomHeight() {
		int height = p1Bitmap.getHeight();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bo);
		height = height / 2 + bitmap.getHeight();
		bitmap.recycle();
		return DisplayUtil.getScreenHeight(mContext) - height;
	}

	public void onPlayerTouch(float x, float y, boolean touched) {
		Point point = new Point(x * DisplayUtil.getScreenWidth(mContext), y
				* DisplayUtil.getScreenHeight(mContext));
		if (mIsHost && human2 != null) {
			human2.moveTo(point, touched);
		}

	}
}
