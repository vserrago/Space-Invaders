package cps406.oneFourFoxtrot;

import java.awt.Point;


public interface IMovable
{

	void setMoveSpeed(int speed);
	int getMoveSpeed();
	void moveRelative(int x, int y);
	void setPosition(int x, int y);
	Point getPosition();
	void moveLeft();
	void moveRight();
	void moveUp();
	void moveDown();
}
