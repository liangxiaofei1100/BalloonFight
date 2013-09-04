package com.dreamlink.role;

import java.util.Random;

import android.util.Log;

import com.dreamlink.beatballoon.GameView;
import com.dreamlink.beatballoon.MainActivity;

public class Balloon extends Thread {
	private int maxX, minX;
	private int x, y;
	private int speedY;
	private boolean exsit = true;
	private static final int speedX = 5;
	private static Random random = new Random();
	private int maxY, minY;

	public Balloon(int x, int y) {
		this.x = x;
		this.y = y;
		maxY = y;
		minY = 0;
		maxX = x + 60;
		minX = x - 60;
		speedY = random.nextInt(4) + 2;
		speedY = ((maxY - minY) / (speedY * 1000 / MainActivity.refreshSpeed));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (exsit) {
			detectHuman();
			y -= speedY;
			if (y < 0) {
				exsit = false;
				break;
			}
			if (Math.random() > 0.5) {
				if (x + speedX < maxX) {
					x += speedX;
				}
			} else {
				if (x - speedX > minX) {
					x -= speedX;
				}
			}
			detectHuman();
			try {
				Thread.sleep(MainActivity.refreshSpeed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isExsit() {
		return exsit;
	}

	public void setExsit(boolean exsit) {
		this.exsit = exsit;
	}

	private void detectHuman() {
		for (Player human : GameView.humans) {
			int xDec = Math.abs(human.getX() - x);
			int yDec = y - human.getY();
			if (xDec <= human.width && yDec <= human.height
					&& yDec > human.height / 2) {
				Log.d("ArbiterLiu", "" + yDec);
				this.exsit = false;
			}
		}
	}

	@Override
	public String toString() {
		return "Balloon [x=" + x + ", y=" + y + ", exsit=" + exsit + "]";
	}

}
