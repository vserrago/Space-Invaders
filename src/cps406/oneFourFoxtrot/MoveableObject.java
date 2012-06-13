package cps406.oneFourFoxtrot;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.lang.Comparable;


public abstract class MoveableObject implements IMovable, IDrawable, Comparable<MoveableObject>
{
	Point _pos;
	Dimension2D _size;
	int _speed;
	
	@Override
	public int compareTo(MoveableObject o)
	{

		 //will compare to based on left to right position ie, leftmost < center < rightmost
		if(this.equals(o))
			return 0;
		if(this._pos.x < o.getPosition().x)
			return -1;
		else return 1;
	}
	
	public abstract void draw(Graphics2D g);

	@Override
	public void setMoveSpeed(int speed)
	{
		_speed = speed;
	}

	@Override
	public int getMoveSpeed()
	{
		return _speed;
	}

	@Override
	public Point getPosition()
	{
		return _pos;
	}
	
	@Override
	public void setPosition(int x, int y)
	{
		_pos = new Point(x, y);
		
	}
	@Override
	public void moveRelative(int x, int y)
	{
		this._pos = new Point(this._pos.x + x, this._pos.y + y);
		
	}
	@Override
	public void moveLeft()
	{
		this.moveRelative(-getMoveSpeed(), 0);
		
	}
	@Override
	public void moveRight()
	{
		this.moveRelative(getMoveSpeed(), 0);
		
	}
	public void moveUp()
	{
		this.moveRelative(0, -getMoveSpeed());
		
	}
	@Override
	public void moveDown()
	{
		this.moveRelative(0, getMoveSpeed());
		
	}
	

	public boolean equals(Object obj) {
		if(obj != null && obj instanceof IMovable)
		{
			IMovable m = (IMovable) obj;
			return m.getPosition().equals(this.getPosition());
		}
		return false;
	}

}
