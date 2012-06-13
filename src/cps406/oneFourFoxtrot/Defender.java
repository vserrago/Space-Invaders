package cps406.oneFourFoxtrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

public class Defender extends AnimatedMoveableObject implements IDestructable
{
	public static final int HEIGHT = 30;
	public static final int WIDTH = 30;
	public static final int FRAMECOUNT = 8;
//	private int hitHeight = HEIGHT + 5;	// Seems if the defender is too short, projectiles are passing through before collision is detected
	private int hitHeight = HEIGHT;
	
	public Defender(int speed, int x, int y, Sprite s)
	{
		super(s);
		this._pos = new Point(x, y);
		this._speed = speed;
	}
	
//	@Override
//	public void draw(Graphics2D g)
//	{
//		// TODO Auto-generated method stub
//		g.setColor(Color.green);
//		g.fillOval(this._pos.x, this._pos.y, WIDTH, HEIGHT);
//		
//	}
	
	@Override
	public boolean contains(Point p)
	{
		boolean withinXbounds = _pos.x <= p.x && (_pos.x + WIDTH) >= p.x; 
		boolean withinYbounds = p.y >= _pos.y - hitHeight;
		if(withinXbounds && withinYbounds)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}