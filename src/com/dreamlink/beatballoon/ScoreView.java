package com.dreamlink.beatballoon;

import com.dreamlink.role.Player.HumanLife;
import com.dreamlink.util.DisplayUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class ScoreView extends SurfaceView implements SurfaceHolder.Callback,
		HumanLife {
	public static ScoreView mScoreView;
	private int p1Score;
	private int p2Score;
	private int p1Life = 3;
	private int p2Life = 3;
	private SurfaceHolder holder;
	private Context mContext;
	private boolean over_flag = false;
	private int width, height;
	private RectF finishRectF, retryRectF;
	private boolean localWin = false;
	private String mGameOverReplay;
	private String mGameOverQuit;
	private Paint paint;
	private boolean mIsHost = true;
	private boolean mSingelPlay = true;

	public void setmSingelPlay(boolean mSingelPlay) {
		this.mSingelPlay = mSingelPlay;
	}

	public void setmIsHost(boolean mIsHost) {
		this.mIsHost = mIsHost;
	}

	public ScoreView(Context context) {
		super(context);
		init(context);
	}

	public ScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mScoreView = this;
		mContext = context;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		width = DisplayUtil.getScreenWidth(mContext);
		height = DisplayUtil.getScreenHeight(mContext);
		holder = this.getHolder();
		holder.addCallback(this);
		setZOrderOnTop(true);
		holder.setFormat(PixelFormat.TRANSLUCENT);
		this.setFocusable(false);

		finishRectF = new RectF(3 * width / 8, 13 * height / 32, 5 * width / 8,
				17 * height / 32);
		retryRectF = new RectF(3 * width / 8, 18 * height / 32, 5 * width / 8,
				22 * height / 32);
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (over_flag) {
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();
					if (finishRectF.contains(x, y)) {
						// Quit game button clicked.
						if (mGameOverListener != null) {
							mGameOverListener.onGameOverQuit();
						}
					} else if (retryRectF.contains(x, y)) {
						// Player again button clicked.
						if (mGameOverListener != null) {
							mGameOverListener.onGameOverPlayAgain();
						}
						reset();
					}
				}
				return false;
			}
		});

		mGameOverReplay = mContext.getString(R.string.game_over_replay);
		mGameOverQuit = mContext.getString(R.string.game_over_quit);

		p1Life = MainActivity.LIFE_NUMBER;
		p2Life = MainActivity.LIFE_NUMBER;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawView();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	private void drawView() {
		try {
			Canvas canvas = holder.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			drawScore(p1Score, p2Score, p1Life, p2Life, canvas);
			if (over_flag) {
				drawFinish(canvas);
				drawRetry(canvas);
				if (localWin) {
					drawOverText(canvas, "You Win");
				} else {
					drawOverText(canvas, "You Lose");
				}
			}
			holder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * draw score
	 * 
	 * @param p1
	 *            the person 1 score
	 * @param p2
	 *            the person 2 score
	 * @param p1l
	 *            person 1 life
	 * @param p2l
	 *            person 2 life
	 * */
	private void drawScore(int p1, int p2, int p1l, int p2l, Canvas canvas) {
		paint.setTextSize(height / 16);
		paint.setColor(Color.GREEN);
		canvas.drawText("X" + p1Life + "--" + "P1:" + p1, width / 6,
				height / 16, paint);
		/*--------------------------*/
		if (!mSingelPlay) {
			paint.setColor(Color.RED);
			canvas.drawText("X" + p2Life + "--" + "P2:" + p2, 5 * width / 6,
					height / 16, paint);
		}
	}

	@Override
	public void lifeDec(int id) {
		if (id == 0)
			p1Life--;
		else
			p2Life--;
		detectOver();

		if (mOnSoreChangedListener != null) {
			mOnSoreChangedListener.onLifeChanged(p1Life, p2Life);
		}
		drawView();
	}

	@Override
	public void scoreAdd(int id) {
		if (id == 0)
			p1Score += 100;
		else
			p2Score += 100;

		if (mOnSoreChangedListener != null) {
			mOnSoreChangedListener.onScoreChanged(p1Score, p2Score);
		}
		drawView();
	}

	private void drawRetry(Canvas canvas) {
		paint.setColor(Color.WHITE);
		canvas.drawRect(retryRectF, paint);
		paint.setColor(Color.BLACK);
		paint.setTextSize(height / 16);
		canvas.drawText(mGameOverReplay, width / 2, 21 * height / 32, paint);
	}

	private void drawFinish(Canvas canvas) {
		paint.setColor(Color.WHITE);
		canvas.drawRect(finishRectF, paint);
		paint.setColor(Color.BLACK);
		paint.setTextSize(height / 16);
		canvas.drawText(mGameOverQuit, width / 2, 16 * height / 32, paint);
	}

	private void drawOverText(Canvas canvas, String string) {
		paint.setColor(Color.WHITE);
		paint.setTextSize(3 * height / 16);
		canvas.drawText(string, width / 2, 3 * height / 8, paint);
	}

	public void reset() {
		p1Score = p2Score = 0;
		p1Life = p2Life = 3;
		over_flag = false;
		drawView();
	}

	public void setLife(int lifeOfPlayer1, int lifeOfPlayer2) {
		p1Life = lifeOfPlayer1;
		p2Life = lifeOfPlayer2;
		detectOver();
		drawView();
	}

	public void setScore(int scoreOfPlayer1, int scoreOfPlayer2) {
		p1Score = scoreOfPlayer1;
		p2Score = scoreOfPlayer2;
		drawView();
	}

	private OnSoreChangedListener mOnSoreChangedListener;

	public void setOnSoreChangedListener(OnSoreChangedListener listener) {
		mOnSoreChangedListener = listener;
	}

	public interface OnSoreChangedListener {

		/**
		 * Life is changed.
		 * 
		 * @param lifeOfPlayer1
		 * @param lifeOfPlayer2
		 */
		void onLifeChanged(int lifeOfPlayer1, int lifeOfPlayer2);

		/**
		 * Score is changed.
		 * 
		 * @param scoreOfPlayer1
		 * @param scoreOfPlayer2
		 */
		void onScoreChanged(int scoreOfPlayer1, int scoreOfPlayer2);
	}

	private OnGameOverListener mGameOverListener;

	public void setOnGameOverListener(OnGameOverListener listener) {
		mGameOverListener = listener;
	}

	public interface OnGameOverListener {

		/**
		 * Finish and quit the game.
		 */
		void onGameOverQuit();

		/**
		 * Reset and play the game again.
		 */
		void onGameOverPlayAgain();
	}

	private void detectOver() {
		if (p1Life < 0 || p2Life < 0) {
			over_flag = true;
			GameView.mGameView.clearData();
			if (p1Life < 0) {
				if (mIsHost)
					localWin = false;
				else
					localWin = true;
			} else {
				if (!mIsHost)
					localWin = false;
				else
					localWin = true;
			}
		}
	}
}
