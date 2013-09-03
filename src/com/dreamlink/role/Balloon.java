package com.dreamlink.role;

import android.util.Log;

import com.dreamlink.beatballoon.GameView;
import com.dreamlink.beatballoon.MainActivity;

public class Balloon extends Thread {
	private int bigX, lessX;
	private int x, y;
	private int speed;
	public boolean exsit = true;
	final int step = 5;

	public Balloon(int x, int y) {
		this.x = x;
		this.y = y;
		bigX = x + 60;
		lessX = x - 60;
		speed = (int) (Math.random() * 10);
		if (speed == 0) {
			speed = 1;
		} else if (speed >= 8) {
			speed = 8;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (exsit) {
			detectHuman();
			y -= speed;
			if (y < 0) {
				exsit = false;
				break;
			}
			if (Math.random() > 0.5) {
				if (x + step < bigX) {
					x += step;
				}
			} else {
				if (x - step > lessX) {
					x -= step;
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
