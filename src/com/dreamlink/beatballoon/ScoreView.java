package com.dreamlink.beatballoon;

import com.dreamlink.role.Player;
import com.dreamlink.role.Player.HumanLife;
import com.dreamlink.util.DisplayUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class ScoreView extends SurfaceView implements SurfaceHolder.Callback,
		HumanLife {
	public static ScoreView mScoreView;
	private int p1Score;
	private int p2Score;
	private int p1Life;
	private int p2Life;
	private SurfaceHolder holder;
	private Context mContext;
	private boolean over_flag = false;
	private int width, height;
	private RectF finishRectF, retryRectF;
	private boolean localWin = false;
	private String mGameOverReplay;
	private String mGameOverQuit;
	private Paint paint;

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
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				if (3 * width / 8 < x && x < 5 * width / 8) {
					if (13 * height / 32 < y && y < 17 * height / 32) {
						MainActivity.mainActivity.finish();
					} else if (18 * height / 32 < y && y < 22 * height / 32) {
						GameView.mGameView.resetGame();
						reset();
						over_flag = false;
						drawView();
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
				drawOverText(canvas, "You Win");
			}
			holder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			// TODO: handle exception
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
		canvas.drawText("P1:" + p1, width / 6, height / 16, paint);
		/*--------------------------*/
		paint.setColor(Color.BLUE);
		canvas.drawText("P1:" + p1l + "/P2:" + p2l, width / 2, height / 16,
				paint);
		/*--------------------------*/
		paint.setColor(Color.RED);
		canvas.drawText("P2:" + p2, 5 * width / 6, height / 16, paint);

	}

	@Override
	public void lifeDec(int id) {
		if (id == 0)
			p1Life--;
		else
			p2Life--;
		if (p1Life < 0 || p2Life < 0) {
			over_flag = true;
			for (Player human : GameView.humans) {
				human.setstillAlive(false);
			}
			GameView.mGameView.clearData();
		}
		drawView();
	}

	@Override
	public void scoreAdd(int id) {
		Log.e("ArbiterLiu", "---------------" + id);
		if (id == 0)
			p1Score += 100;
		else
			p2Score += 100;
		drawView();
	}

	// private void drawOverView() {
	// Canvas canvas = holder.lockCanvas(new Rect(width / 8, height / 8,
	// 7 * width / 8, 7 * height / 8));
	// canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
	// Paint paint = new Paint();
	// paint.setAntiAlias(true);
	// paint.setColor(Color.WHITE);
	// paint.setTextSize(80);
	// paint.setTextAlign(Paint.Align.CENTER);
	// canvas.drawRect(finishRectF, paint);
	// canvas.drawRect(retryRectF, paint);
	// if (localWin)
	// canvas.drawText("You   Win ", width / 2, 3 * height / 8, paint);
	// else
	// canvas.drawText("You   Lose", width / 2, 3 * height / 8, paint);
	// paint.setTextSize(40);
	// paint.setColor(Color.BLACK);
	// canvas.drawText(mGameOverQuit, width / 2, 16 * height / 32, paint);
	// canvas.drawText(mGameOverReplay, width / 2, 21 * height / 32, paint);
	// holder.unlockCanvasAndPost(canvas);
	// }

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

	private void reset() {
		p1Score = p2Score = 0;
		p1Life = p2Life = 3;
	}
}
