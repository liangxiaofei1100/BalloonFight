package com.dreamlink.role;

import com.dreamlink.beatballoon.BackgroundView;
import com.dreamlink.beatballoon.MainActivity;

import android.graphics.Point;

public class Human extends Thread {
	private int id;
	private HumanLife humanLife;
	private int x, y, lifeNum = 3;
	private boolean stillAlive = true;
	private final int upSpeed = 8, downSpeed = 3;
	private int xSpeed = 10;
	private boolean moving = false;
	private Point movoToPoint;

	public void registerCallback(HumanLife humanLife) {
		this.humanLife = humanLife;
	}

	public interface HumanLife {
		public void lifeDec(int id);

		public void scoreAdd(int id);
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

	public int getLifeNum() {
		return lifeNum;
	}

	public void setLifeNum(int lifeNum) {
		this.lifeNum = lifeNum;
	}

	public boolean isstillAlive() {
		return stillAlive;
	}

	public void setstillAlive(boolean alive) {
		this.stillAlive = alive;
	}

	public Human(int num) {
		if (num == 0) {
			x = 500;
			y = 200;
		} else {
			x = 200;
			y = 200;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (stillAlive) {
			if (moving) {
				if (x != movoToPoint.x || y != movoToPoint.y) {
					doMove();
				} else {
					moving = false;
					y += downSpeed;
				}
			} else {
				y += downSpeed;
			}
			stillAlive();
			try {
				Thread.sleep(MainActivity.refreshSped);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void moveTo(Point point) {
		movoToPoint = point;
		moving = true;
		xSpeed = 0;
	}

	private void doMove() {
		if (movoToPoint.x != x && movoToPoint.y == y) {
			xSpeed = 10;
		} else if (movoToPoint.x == x && movoToPoint.y != y) {
			xSpeed = 0;
		} else if (movoToPoint.y > y) {
			xSpeed = downSpeed * (int) Math.abs(movoToPoint.x - x)
					/ (movoToPoint.y - y);
			if (xSpeed == 0)
				xSpeed = 1;
		} else {
			xSpeed = upSpeed * (int) Math.abs(movoToPoint.x - x)
					/ (movoToPoint.y - y);
			if (xSpeed == 0)
				xSpeed = 1;
		}
		if (movoToPoint.x - x >= 0) {
			if (movoToPoint.x - x >= xSpeed)
				x += xSpeed;
			else
				x = movoToPoint.x;
		} else {
			if (x - xSpeed > movoToPoint.x)
				x -= xSpeed;
			else
				x = movoToPoint.x;
		}

		if (movoToPoint.y - y >= 0) {
			if (y + upSpeed >= movoToPoint.y)
				y = movoToPoint.y;
			else
				y += upSpeed;
		} else {
			if (y - upSpeed <= movoToPoint.y)
				y = movoToPoint.y;
			else
				y -= upSpeed;
		}
	}

	private void stillAlive() {
		if (y >= BackgroundView.height) {
			if (humanLife != null) {
				humanLife.lifeDec(id);
			}
			x = 500;
			y = 200;
		}
	}

	public void scoreDetect() {

	}
}
