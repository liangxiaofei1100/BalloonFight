package com.dreamlink.beatballoon;

import com.dreamlink.role.Human;
import com.dreamlink.role.Human.HumanLife;
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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class ScoreView extends SurfaceView implements SurfaceHolder.Callback,
		HumanLife {
	public static ScoreView mScoreView;
	private int p1Score, p2Score, p1Life = MainActivity.Life_Number,
			p2Life = MainActivity.Life_Number;
	private SurfaceHolder holder;
	private Context mContext;
	private boolean over_flag = false;
	private int width, height;
	private RectF finishRectF, retryRectF;
	private boolean localWin = false;

	public ScoreView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		if (mScoreView == null)
			mScoreView = this;
		mContext = context;
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
				// TODO Auto-generated method stub
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
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		drawView();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	private void drawView() {
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		holder.unlockCanvasAndPost(canvas);
		drawScore(p1Score, p2Score, p1Life, p2Life);
		if (over_flag)
			drawOverView();

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
	private void drawScore(int p1, int p2, int p1l, int p2l) {

		Canvas canvas = holder.lockCanvas(new Rect(0, 0, width / 3, 50));
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		Paint mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(20);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setColor(Color.GREEN);
		canvas.drawText("P1:" + p1, width / 6, 25, mTextPaint);
		holder.unlockCanvasAndPost(canvas);
		/*--------------------------*/
		canvas = holder.lockCanvas(new Rect(width / 3, 0, 2 * width / 3, 50));
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		mTextPaint.setColor(Color.BLUE);
		canvas.drawText("P1:" + p1l + "/P2:" + p2l, width / 2, 25, mTextPaint);
		holder.unlockCanvasAndPost(canvas);
		/*--------------------------*/
		canvas = holder.lockCanvas(new Rect(2 * width / 3, 0, width, 50));
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		mTextPaint.setColor(Color.RED);
		canvas.drawText("P2:" + p2, 5 * width / 6, 25, mTextPaint);
		holder.unlockCanvasAndPost(canvas);

	}

	@Override
	public void lifeDec(int id) {
		// TODO Auto-generated method stub
		if (id == 0)
			p1Life--;
		else
			p2Life--;
		if (p1Life < 0 || p2Life < 0) {
			over_flag = true;
			for (Human human : GameView.humans) {
				human.setstillAlive(false);
			}
			GameView.mGameView.clearData();
		}
		drawView();
	}

	@Override
	public void scoreAdd(int id) {
		// TODO Auto-generated method stub
		if (id == 0)
			p1Score += 100;
		else
			p2Score += 100;
		drawView();
	}

	private void drawOverView() {
		Canvas canvas = holder.lockCanvas(new Rect(width / 8, height / 8,
				7 * width / 8, 7 * height / 8));
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(80);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawRect(finishRectF, paint);
		canvas.drawRect(retryRectF, paint);
		if (localWin)
			canvas.drawText("You   Win ", width / 2, 3 * height / 8, paint);
		else
			canvas.drawText("You   Lose", width / 2, 3 * height / 8, paint);
		paint.setTextSize(40);
		paint.setColor(Color.BLACK);
		canvas.drawText("½á       Êø", width / 2, 16 * height / 32, paint);
		canvas.drawText("ÖØ       À´", width / 2, 21 * height / 32, paint);
		holder.unlockCanvasAndPost(canvas);
	}

	private void reset() {
		p1Score = p2Score = 0;
		p1Life = p2Life = 3;
	}
}
