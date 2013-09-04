package com.dreamlink.role;

import com.dreamlink.beatballoon.GameView;
import com.dreamlink.beatballoon.MainActivity;

public class Player extends Thread {
	private int id;
	private HumanLife humanLife;
	private float x, y;
	private boolean stillAlive = true;
	private float upSpeed, downSpeed;
	private float xSpeed;
	private boolean moving = false;
	private Point movoToPoint;
	public int height, width;
	public boolean hasBalloon = true;
	private int bottomHeight, topHeight;
	private int maxX;
	private float DownSpeed, UpSpeed, XSpeed;
	private boolean mResetFlag = false;

	public void registerCallback(HumanLife humanLife) {
		this.humanLife = humanLife;
	}

	public Player(int id, int topHeight, int bottomHeight, int maxX) {
		this.id = id;
		this.bottomHeight = bottomHeight;
		this.topHeight = topHeight;
		UpSpeed = upSpeed = bottomHeight / (3000 / MainActivity.refreshSpeed);
		DownSpeed = downSpeed = bottomHeight
				/ (3000 / MainActivity.refreshSpeed);
		this.maxX = maxX;
		XSpeed = xSpeed = maxX / (2000 / MainActivity.refreshSpeed);
		humanLocate();
	}

	public void lifeDec() {
		if (mResetFlag) {
			return;
		}
		mResetFlag = true;
		humanLife.lifeDec(id);
		newLocate();
	}

	public interface HumanLife {
		public void lifeDec(int id);

		public void scoreAdd(int id);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
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
		super.run();
		while (stillAlive) {
			if (moving) {
				if (x != movoToPoint.x || y != movoToPoint.y) {
					doMove();
				} else {
					moving = false;
					downSpeed = DownSpeed;
					y += downSpeed;
				}
			} else {
				downSpeed = DownSpeed;
				y += downSpeed;
			}
			detectBalloons();
			detectHuman();
			stillAlive();
			try {
				Thread.sleep(MainActivity.refreshSpeed);
			} catch (InterruptedException e) {
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
			xSpeed = XSpeed;
		} else if (movoToPoint.x == x && movoToPoint.y != y) {
			xSpeed = 0;
		} else {
			float xDec = Math.abs(movoToPoint.x - x);
			float yDec = Math.abs(movoToPoint.y - y);
			if (xDec / yDec >= 3) {
				xSpeed = XSpeed;
				upSpeed = downSpeed = xSpeed * yDec / xDec;
			} else {
				upSpeed = UpSpeed;
				downSpeed = DownSpeed;
				if (movoToPoint.y > y) {
					xSpeed = downSpeed * xDec / yDec;
				} else {
					xSpeed = upSpeed * xDec / yDec;
				}
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
			if (moving) {
				if (movoToPoint.x - x > width / 2) {
					x += width / 2;
				} else if (x - movoToPoint.x > width / 2) {
					x -= width / 2;
				}
			}
			moving = false;
		}
		if (y < height / 2) {
			y = height;
			if (moving) {
				if (movoToPoint.x - x > width / 2) {
					x += width / 2;
				} else if (x - movoToPoint.x > width / 2) {
					x -= width / 2;
				}
			}
			moving = false;
		}
		if (x < width / 2) {
			x = width;
			if (y < bottomHeight - height / 2)
				y += height / 2;
			else
				y = bottomHeight;
			moving = false;
		}
		if (x > maxX - width / 2) {
			x = maxX - width;
			if (y < bottomHeight - height / 2)
				y += height / 2;
			else
				y = bottomHeight;
			moving = false;
		}

	}

	public void scoreDetect() {
		humanLife.scoreAdd(id);
	}

	private void detectBalloons() {
		for (java.util.Map.Entry<Balloon, Float> b : GameView.balloons
				.entrySet()) {
			float xDec = Math.abs(x - b.getKey().getX());
			float yDec = b.getKey().getY() - y;
			if (xDec <= width && yDec <= height && yDec >= height / 2) {
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
				float xDec = Math.abs(human.getX() - this.x);
				float yDec = this.y - human.y;
				if (xDec <= width / 2 && Math.abs(yDec) >= height / 2
						&& Math.abs(yDec) < height) {
					if (yDec < 0) {
						human.lifeDec();
					} else {
						this.lifeDec();
					}
				} else if (Math.abs(yDec) <= height / 2 && xDec < width / 2) {
					if (human.getX() > this.x) {
						this.x -= width;
					} else {
						this.x += width;
					}
					moving = false;
				}
			}
		}
	}

	private void humanLocate() {
		if (id == 0) {
			x = 3 * maxX / 4;
			y = bottomHeight;
		} else {
			x = maxX / 4;
			y = bottomHeight;
		}
	}

	private void newLocate() {
		this.x = maxX / 4;
		this.y = topHeight;
		mResetFlag = false;
	}
}
