package com.dreamlink.role;

import com.dreamlink.beatballoon.GameView;
import com.dreamlink.beatballoon.MainActivity;
import com.dreamlink.util.Log;

public class Player extends Thread {
	private int id;
	private HumanLife humanLife;
	private float x, y;
	private boolean stillAlive = true;
	private boolean moving = false;
	private Point vectorPoint;
	public int height, width;
	public boolean hasBalloon = true;
	private int bottomHeight, topHeight;
	private int maxX;
	private float DownSpeed, UpSpeed, XSpeed;
	private boolean mResetFlag = false;
	private float speedVector;
	private float speedX, speedY;
	private boolean stillAdd = true;

	public void registerCallback(HumanLife humanLife) {
		this.humanLife = humanLife;
	}

	public Player(int id, int topHeight, int bottomHeight, int maxX) {
		this.id = id;
		this.bottomHeight = bottomHeight;
		this.topHeight = topHeight;
		UpSpeed = bottomHeight / (2000 / MainActivity.refreshSpeed);
		DownSpeed = bottomHeight / (3000 / MainActivity.refreshSpeed);
		this.maxX = maxX;
		XSpeed = maxX / (4000 / MainActivity.refreshSpeed);
		speedX = XSpeed;
		speedY = 0;
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
			// if (moving) {
			// if (x != vectorPoint.x || y != vectorPoint.y) {
			// doMove();
			// if (speedVector == 0) {
			//
			// speedVector = (vectorPoint.y - y) / (vectorPoint.x - x);
			// }
			//
			// } else {
			// moving = false;
			// downSpeed = DownSpeed;
			// y += downSpeed;
			// }
			// } else {
			// if (downSpeed <= 0) {
			// downSpeed = 0;
			// } else {
			// downSpeed += 0.5f;
			// }
			// y += downSpeed;
			// }
			speedOp(moving);
			x += speedX;
			y += speedY;
			if (moving) {
				if (Math.abs(vectorPoint.x - x) <= Math.abs(speedX)) {
					x = vectorPoint.x;
				}
				if (Math.abs(vectorPoint.y - y) <= Math.abs(speedY)) {
					y = vectorPoint.y;
				}
				if (vectorPoint.x == x && vectorPoint.y == y) {
					stillAdd = false;
				}
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

	public void moveTo(Point point, boolean moveing) {
		vectorPoint = point;
		this.moving = moveing;
		if (vectorPoint != null && moveing) {
			if (vectorPoint.x != x) {
				speedVector = (vectorPoint.y - y) / (vectorPoint.x - x);
			} else {
				speedVector = -Float.MAX_VALUE;
			}
			stillAdd = true;
		}
	}

	/**
	 * private void doMove() { if (!hasBalloon) { y += downSpeed; return; } if
	 * (vectorPoint.x != x && vectorPoint.y == y) { if (xSpeed <= 0) { xSpeed =
	 * 0; } else { xSpeed += 1; } } else if (vectorPoint.x == x && vectorPoint.y
	 * != y) { xSpeed = 0; } else { float xDec = Math.abs(vectorPoint.x - x);
	 * float yDec = Math.abs(vectorPoint.y - y); if (xDec / yDec >= 3) { if
	 * (xSpeed <= 0) { xSpeed = 0; } else { xSpeed += 1; } upSpeed = downSpeed =
	 * xSpeed * yDec / xDec; } else { upSpeed = UpSpeed; downSpeed = DownSpeed;
	 * 
	 * if (vectorPoint.y > y) { xSpeed = downSpeed * xDec / yDec; } else {
	 * xSpeed = upSpeed * xDec / yDec; } } } if (vectorPoint.x - x >= 0) { if
	 * (vectorPoint.x - x >= xSpeed) x += xSpeed; else x = vectorPoint.x; } else
	 * { if (x - xSpeed > vectorPoint.x) x -= xSpeed; else x = vectorPoint.x; }
	 * 
	 * if (vectorPoint.y - y >= 0) { if (y + upSpeed >= vectorPoint.y) y =
	 * vectorPoint.y; else y += upSpeed; } else { if (y - upSpeed <=
	 * vectorPoint.y) y = vectorPoint.y; else y -= upSpeed; } }
	 */
	private void stillAlive() {
		if (y >= bottomHeight) {
			y = bottomHeight;
			if (moving) {
				if (vectorPoint.x - x > width / 2) {
					x += width / 2;
				} else if (x - vectorPoint.x > width / 2) {
					x -= width / 2;
				}
			}
			speedX = 0;
			speedY = 0;
		}
		if (y <= topHeight / 2) {
			y = topHeight;
			if (moving) {
				if (vectorPoint.x - x > width / 2) {
					x += width / 2;
				} else if (x - vectorPoint.x > width / 2) {
					x -= width / 2;
				}
			}
			speedX = -speedX;
		}
		if (x < width / 2) {
			x = width;
			if (y < bottomHeight - topHeight / 2)
				y += topHeight / 2;
			else
				y = bottomHeight;
			speedX = -speedX;
		}
		if (x > maxX - width / 2) {
			x = maxX - width;
			if (y < bottomHeight - topHeight / 2)
				y += topHeight / 2;
			else
				y = bottomHeight;
			speedX = -speedX;
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

	private void speedOp(boolean moveing) {
		if (moveing) {
			if (!stillAdd) {
				return;
			}
			if (vectorPoint.x > x) {
				speedX += 0.5;
			} else if (vectorPoint.x < x) {
				speedX -= 0.5;
			}
			if (vectorPoint.y > y) {
				speedY += 0.5;
			} else {
				speedY -= 1;
			}
			if (y <= topHeight) {
				speedY = -speedY;
			}
			if (speedVector != 0 && vectorPoint.y != y) {
				speedX = speedY / speedVector;
				if (Math.abs(speedVector) < 0.3) {
					if (speedX >= XSpeed) {
						speedX = XSpeed;
						speedY = speedX * speedVector;
					} else if (speedX <= -XSpeed) {
						speedX = -XSpeed;
						speedY = speedX * speedVector;
					}
				}
			}
			if (vectorPoint.y == y) {
				speedY = 0;
			}
			if (vectorPoint.x == x) {
				speedX = 0;
			}
		} else {
			speedX -= 0.01 * speedX;
			if (y >= bottomHeight) {
				speedY = 0;
			} else {
				speedY += 0.5;
			}
			if (Math.abs(speedX) == 0) {
				speedX = 0;
			}
		}
		if (speedY <= -UpSpeed) {
			speedY = -UpSpeed;
		} else if (speedY >= DownSpeed) {
			speedY = DownSpeed;
		}
		if (speedX >= XSpeed) {
			speedX = XSpeed;
		} else if (speedX <= -XSpeed) {
			speedX = -XSpeed;
		}
	}
}
