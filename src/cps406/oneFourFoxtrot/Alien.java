package cps406.oneFourFoxtrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;


public class Alien extends AnimatedMoveableObject implements IDestructable
{
	public static final Dimension ALIEN_SIZE = new Dimension(20, 20);
	public static final int FRAMECOUNT = 1;
	
	
	private long _nextBombDrop;
	private final int MAX_MILLISECONDS_BETWEEN_BOMBS = 3000;	// this isn't really the max, the max is this plus the MIN...
	private final int MIN_MILLISECONDS_BETWEEN_BOMBS = 200;
	private Random _rand;

	
	
	public Alien(int speed, Point pos, Sprite s)
	{
		super(s);
		this._rand = new Random();
		this.setMoveSpeed(speed);
		this._pos = pos;
		this._nextBombDrop = getNextBombDropTime();
	}
	
	public boolean isReadyToDropBomb()
	{
		if (System.currentTimeMillis() >= this._nextBombDrop)
		{
			this._nextBombDrop = getNextBombDropTime();
			return true;
		}
		else
		{
			return false;
		}
	}

//	@Override
//	public void draw(Graphics2D g)
//	{
//		g.setColor(Color.green);
//		g.fill3DRect(_pos.x, _pos.y, ALIEN_SIZE.width, ALIEN_SIZE.height, true);
//		
//	}
	@Override
	public boolean contains(Point p)
	{
		if(_pos.x <= p.x && (_pos.x + ALIEN_SIZE.width) >= p.x && _pos.y <= p.y && _pos.y + ALIEN_SIZE.height >= p.y)
			return true;
		return false;
	}

	private long getNextBombDropTime()
	{
		long nextBombInterval = ((long)this._rand.nextInt(MAX_MILLISECONDS_BETWEEN_BOMBS) + MIN_MILLISECONDS_BETWEEN_BOMBS);
		return System.currentTimeMillis() + nextBombInterval;
	}
}