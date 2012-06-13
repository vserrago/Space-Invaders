package cps406.oneFourFoxtrot;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AlienController extends MoveableObject
{

	public AlienController(int minScreenX, int maxScreenX)
	{
		_screenBounds = new Bounds();
		_screenBounds.leftBound = minScreenX;
		_screenBounds.rightBound = maxScreenX;
		_alienBounds = new Bounds();
		_alienBounds.leftBound = Integer.MAX_VALUE;
		_alienBounds.rightBound = Integer.MIN_VALUE;
		
	}
	private int _aliensRemaining;
	private int _level;
	private Direction _d;
	private List<AlienRow> _rows;
	private Bounds _alienBounds;
	private Bounds _screenBounds;
	private int _levelSpeed;
	
	private void setDirection()
	{
		if(_d == Direction.RIGHT)
		{
			if(_alienBounds.rightBound + _levelSpeed  >= _screenBounds.rightBound)
				_d = Direction.LEFT;
		}
		if(_d == Direction.LEFT)
		{
			if(_alienBounds.leftBound - _levelSpeed <= _screenBounds.leftBound)
				_d = Direction.RIGHT;
		}
	}
	
	public synchronized void draw(Graphics2D g)
	{
		for(AlienRow ar : _rows)
		{
			ar.draw(g);
		}
	}
	public synchronized void move()
	{
		getAndSetCurrentAlienBounds();
		setDirection();
		for(AlienRow row : _rows)
		{
			row.moveAliens(_d);
		}
	}
	private void getAndSetCurrentAlienBounds()
	{
		_alienBounds.leftBound = Integer.MAX_VALUE;
		_alienBounds.rightBound = Integer.MIN_VALUE;
		for(AlienRow row : _rows)
		{
			Bounds tmp = row.getBounds();
			_alienBounds.leftBound = tmp.leftBound < _alienBounds.leftBound ? tmp.leftBound : _alienBounds.leftBound;
			_alienBounds.rightBound = tmp.rightBound > _alienBounds.rightBound ? tmp.rightBound : _alienBounds.rightBound;
		}
	}
	public synchronized int handleCollisions(List<Projectile> projectiles)
	{
		ArrayList<Projectile> remList = new ArrayList<Projectile>(0);
		
		for(AlienRow row : _rows)
		{
			remList.addAll(row.handleCollisions(projectiles));
		}
		
		int ret = remList.size();
		projectiles.removeAll(remList);
		cleanUpLists();
		_aliensRemaining -= ret;
		return ret;
	}
	
	private void cleanUpLists()
	{
		for (Iterator<AlienRow> ia = _rows.iterator();  ia.hasNext();)
		{
			AlienRow row = ia.next();
			
			if(row.isEmpty())
			{
				ia.remove();
			}
		}
	}
	
	public void setupLevel(int level)
	{
		AlienFactory af = new AlienFactory(_screenBounds.rightBound);
		_d = Direction.RIGHT;
		_level = level;
		af.setupLevel(level);
		_rows = af.getRows(level);
		_aliensRemaining = af.getNumAliens();
		_levelSpeed = af.getLevelSpeed();
		getAndSetCurrentAlienBounds();
	}
	
	public List<Projectile> dropAlienBombsIfReady()
	{
		List<Projectile> projectiles = new ArrayList<Projectile>(1);
		for (AlienRow row : _rows)
		{
			projectiles.addAll(row.dropAlienBombsIfReady());
		}
		return projectiles;
	}

	public int getAliensRemaining()
	{
		return _aliensRemaining;
	}
}
