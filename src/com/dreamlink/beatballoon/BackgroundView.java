package com.dreamlink.beatballoon;

import java.util.ArrayList;
import java.util.List;

import com.dreamlink.util.DisplayUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BackgroundView extends SurfaceView implements
		SurfaceHolder.Callback {
	public static List<Point> startPoint;
	private Context mContext;
	private SurfaceHolder holder;
	private  int height, width;

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



}
