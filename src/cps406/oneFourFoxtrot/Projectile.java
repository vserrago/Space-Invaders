package cps406.oneFourFoxtrot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public  class Projectile extends MoveableObject implements IDestructable
{
	public Projectile(int speed, Point pos, Direction d)
	{
		this.setMoveSpeed(speed);
		this._pos = pos;
		this._d = d;
	}
	
	Direction _d;
	void setDirection(Direction d)
	{
		_d = d;
	}
	Direction getDirection()
	{
		return _d;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if(this._d.equals(Direction.UP)) 
		{
			// from defender
			g.setColor(Color.ORANGE);
			g.drawOval(_pos.x, _pos.y, 2, 3);
			
		}
		else
		{
			// from alien
			g.setColor(Color.green);
			g.drawOval(_pos.x, _pos.y, 2, 4);
		}
		
	}

	@Override
	public boolean contains(Point p)
	{
		if(_pos.x < p.x && _pos.x > p.x && _pos.y < p.y && _pos.y  > p.y)
			return true;
		return false;
	}
	
	//tweak collision point here if nessessary.
	public Point getCollisionPoint()
	{
		return this._pos;
	}
}