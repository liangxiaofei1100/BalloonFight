package com.dreamlink.beatballoon;

import java.util.ArrayList;
import java.util.List;

import com.dreamlink.role.Balloon;
import com.dreamlink.role.Human.HumanLife;
import com.dreamlink.util.DisplayUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.AttributeSet;

public class BackgroundView extends SurfaceView implements
		SurfaceHolder.Callback, HumanLife {
	public static List<Point> startPoint;
	private Context mContext;
	private SurfaceHolder holder;
	public static int height, width;
	private int p1Score, p2Score, p1Life = 3, p2Life = 3;
	public static BackgroundView backgroundView;

	public class Point {
		int x;
		int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public BackgroundView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		if (backgroundView == null) {
			backgroundView = this;
		}
		mContext = context;
		holder = this.getHolder();
		setFocusable(false);
		holder.addCallback(this);
		height = DisplayUtil.getScreenHeight(context);
		width = DisplayUtil.getScreenWidth(context);
		startPoint = new ArrayList<BackgroundView.Point>();
	}

	public BackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}

	private void drawBottom(Context context, SurfaceHolder holder) {
		if (context == null || holder == null) {
			return;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);
		RectF rectF = new RectF(0, 0, width, height);
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		Paint paint = new Paint();
		canvas.drawBitmap(bitmap, null, rectF, paint);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bo);
		rectF = new RectF(0, height - bitmap.getHeight(), width, height);
		canvas.drawBitmap(bitmap, null, rectF, paint);
		height = height - bitmap.getHeight();
		bitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.zhuzi1);
		rectF = new RectF(width / 6, height - bitmap.getHeight(), width / 6
				+ bitmap.getWidth(), height);
		canvas.drawBitmap(bitmap, null, rectF, paint);
		startPoint.add(new Point(width / 6, height - bitmap.getHeight()));
		bitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.zhuzi2);
		rectF = new RectF(2 * width / 6, height - bitmap.getHeight(), 2 * width
				/ 6 + bitmap.getWidth(), height);
		canvas.drawBitmap(bitmap, null, rectF, paint);
		startPoint.add(new Point(2 * width / 6, height - bitmap.getHeight()));
		bitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.zhuzi4);
		rectF = new RectF(4 * width / 6, height - bitmap.getHeight(), 4 * width
				/ 6 + bitmap.getWidth(), height);
		canvas.drawBitmap(bitmap, null, rectF, paint);
		startPoint.add(new Point(4 * width / 6, height - bitmap.getHeight()));
		bitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.zhuzi3);
		rectF = new RectF(5 * width / 6, height - bitmap.getHeight(), 5 * width
				/ 6 + bitmap.getWidth(), height);
		canvas.drawBitmap(bitmap, null, rectF, paint);
		startPoint.add(new Point(5 * width / 6, height - bitmap.getHeight()));
		rectF = new RectF(0, 0, 200, 50);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		drawBottom(mContext, this.getHolder());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lifeDec(int id) {
		// TODO Auto-generated method stub
		if (id == 0) {
			p1Life -= 1;
			;
		} else {
			p2Life -= 1;
		}
		drawScore(p1Score, p2Score, p1Life, p2Life);
	}

	@Override
	public void scoreAdd(int id) {
		// TODO Auto-generated method stub
		if (id == 0) {
			p1Score += 100;
		} else {
			p2Score += 100;
		}
		drawScore(p1Score, p2Score, p1Life, p2Life);
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
	public void drawScore(int p1, int p2, int p1l, int p2l) {

		Canvas canvas = holder.lockCanvas(new Rect(0, 0, DisplayUtil
				.getScreenWidth(mContext) / 3, 50));
		Paint mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
//		mTextPaint.setFilterBitmap(true);
		mTextPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(mTextPaint);
		mTextPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		mTextPaint.setTextSize(20);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setColor(Color.GREEN);
		canvas.drawText("P1:" + p1, DisplayUtil.getScreenWidth(mContext) / 6,
				25, mTextPaint);
		holder.unlockCanvasAndPost(canvas);
		/*--------------------------*/
		canvas = holder.lockCanvas(new Rect(DisplayUtil
				.getScreenWidth(mContext) / 3, 0, 2 * DisplayUtil
				.getScreenWidth(mContext) / 3, 50));
		mTextPaint.setColor(Color.BLUE);
		canvas.drawText("P1:" + p1l + "/P2:" + p2l,
				DisplayUtil.getScreenWidth(mContext) / 2, 25, mTextPaint);
		holder.unlockCanvasAndPost(canvas);
		/*--------------------------*/
		canvas = holder.lockCanvas(new Rect(2 * DisplayUtil
				.getScreenWidth(mContext) / 3, 0, DisplayUtil
				.getScreenWidth(mContext), 50));
		mTextPaint.setColor(Color.RED);
		canvas.drawText("P2:" + p2,
				5 * DisplayUtil.getScreenWidth(mContext) / 6, 25, mTextPaint);
		holder.unlockCanvasAndPost(canvas);

	}

}
