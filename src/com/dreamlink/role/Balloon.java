package com.dreamlink.role;

import java.util.Random;

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
			try {
				Thread.sleep(MainActivity.refreshSped);
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

}
