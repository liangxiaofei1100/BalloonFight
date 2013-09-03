package com.dreamlink.role;

import com.dreamlink.beatballoon.GameView;
import com.dreamlink.beatballoon.MainActivity;

import android.graphics.Point;

public class Player extends Thread {
	private int id;
	private HumanLife humanLife;
	private int x, y;
	private boolean stillAlive = true;
	private int upSpeed = 6, downSpeed = 8;
	private int xSpeed = 10;
	private boolean moving = false;
	private Point movoToPoint;
	public int height, width;
	public boolean hasBalloon = true;
	private int bottomHeight, topHeight;

	public void registerCallback(HumanLife humanLife) {
		this.humanLife = humanLife;
	}

	public Player(int id, int topHeight, int bottomHeight) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.bottomHeight = bottomHeight;
		this.topHeight = topHeight;
		humanLocate();
	}

	public void lifeDec() {
		humanLife.lifeDec(id);
		newLocate();
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

	public boolean isstillAlive() {
		return stillAlive;
	}

	public void setstillAlive(boolean alive) {
		this.stillAlive = alive;
	}

	public Player(int num) {
		id = num;
		humanLocate();
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
			detectBalloons();
			detectHuman();
			stillAlive();
			try {
				Thread.sleep(MainActivity.refreshSpeed);
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
		if (!hasBalloon) {
			y += downSpeed;
			return;
		}
		if (movoToPoint.x != x && movoToPoint.y == y) {
			xSpeed = 10;
		} else if (movoToPoint.x == x && movoToPoint.y != y) {
			xSpeed = 0;
		} else if (movoToPoint.y > y) {
			int xDec = Math.abs(movoToPoint.x - x);
			int yDec = Math.abs(movoToPoint.y - y);
			if (xDec / yDec > 3) {
				xSpeed = 10;
				xSpeed = 10;
				upSpeed = downSpeed = xSpeed * yDec / xDec;
			} else {
				upSpeed = 6;
				downSpeed = 8;
				xSpeed = upSpeed * xDec / yDec;
			}
		} else {
			int xDec = Math.abs(movoToPoint.x - x);
			int yDec = Math.abs(movoToPoint.y - y);
			if (xDec / yDec > 3) {
				xSpeed = 10;
				upSpeed = downSpeed = xSpeed * yDec / xDec;
			} else {
				upSpeed = 6;
				downSpeed = 8;
				xSpeed = upSpeed * xDec / yDec;
			}
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
		if (y >= bottomHeight) {
			y = bottomHeight;
		}
	}

	public void scoreDetect() {
		humanLife.scoreAdd(id);
	}

	private void detectBalloons() {
		for (java.util.Map.Entry<Balloon, Integer> b : GameView.balloons
				.entrySet()) {
			int xDec = Math.abs(x - b.getKey().getX());
			int yDec = y - b.getKey().getY();
			if (xDec <= width && yDec <= height && yDec >= 0) {
				b.getKey().setExsit(false);
				scoreDetect();
			}
		}
	}

	private void detectHuman() {
		for (Player human : GameView.humans) {
			if (human.id == this.id) {
				continue;
			} else {
				int xDec = Math.abs(human.getX() - this.x);
				int yDec = this.y - human.y;
				if (xDec <= width / 2 && Math.abs(yDec) <= height / 2) {
					if (yDec < 0) {
						human.lifeDec();
					} else {
						this.lifeDec();
					}
				}
			}
		}
	}

	private void humanLocate() {
		if (id == 0) {
			x = 3 * MainActivity.mainActivity.width / 4;
			y = bottomHeight;
		} else {
			x = MainActivity.mainActivity.width / 4;
			y = bottomHeight;
		}
	}

	private void newLocate() {
		this.x = MainActivity.mainActivity.width / 4;
		this.y = topHeight;

	}
}
