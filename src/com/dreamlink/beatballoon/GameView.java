package com.dreamlink.beatballoon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.dreamlink.role.Balloon;
import com.dreamlink.role.Human;
import com.dreamlink.role.Human.HumanLife;
import com.dreamlink.util.DisplayUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.os.DropBoxManager.Entry;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private Context mContext;
	private SurfaceHolder holder;
	private int width, height;
	private boolean draw = true;
	public static boolean gaming = true;
	private Human human1, human2;
	private ConcurrentHashMap<Balloon, Integer> balloons;

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
		mContext = context;
		holder = this.getHolder();
		holder.addCallback(this);
		setZOrderOnTop(true);
		holder.setFormat(PixelFormat.TRANSLUCENT);
		balloons = new ConcurrentHashMap<Balloon, Integer>();
		height = DisplayUtil.getScreenHeight(context);
		width = DisplayUtil.getScreenWidth(context);
		human1 = new Human(0);
		human1.registerCallback(BackgroundView.backgroundView);
		setFocusable(true);
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Point point = new Point();
				point.x = (int) event.getRawX();
				point.y = (int) event.getRawY();
				human1.moveTo(point);
				return false;
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		baThread.start();
		drawBa.start();
		human1.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

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
				drawRole();
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
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.human_define_left);
		if (human1 != null) {
			canvas.drawBitmap(bitmap, human1.getX() - bitmap.getWidth() / 2,
					human1.getY() - bitmap.getHeight() / 2, paint);
		}
		if (human2 != null) {
			canvas.drawBitmap(bitmap, human1.getX() - bitmap.getWidth() / 2,
					human1.getY() - bitmap.getHeight() / 2, paint);
		}
		bitmap.recycle();
		holder.unlockCanvasAndPost(canvas);
	}

	private Thread baThread = new Thread() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (gaming) {
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
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

}
