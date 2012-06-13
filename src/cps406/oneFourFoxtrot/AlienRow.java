package cps406.oneFourFoxtrot;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AlienRow extends MoveableObject
{
	private Alien leftAlien;
	private Alien rightAlien;
	private List<Alien> _aliens;
	
	public Bounds getBounds()
	{
		Bounds b = new Bounds();
		b.leftBound = leftAlien._pos.x;
		b.rightBound = rightAlien._pos.x + Alien.ALIEN_SIZE.width;
		return b;
	}
	public AlienRow()
	{
		_aliens = Collections.synchronizedList(new ArrayList<Alien>());
	}
	public void addAlien(Alien a)
	{
		_aliens.add(a);
		computeBoundsAliens();
	}
	@Override
	public void draw(Graphics2D g)
	{
		for(Alien a : _aliens)
		{
			a.draw(g);
		}
		
	}
	public void moveAliens(Direction d)
	{
		for(Alien a : _aliens)
		{
			if(d == Direction.LEFT)
				a.moveLeft();
			else if(d == Direction.RIGHT)
				a.moveRight();
		}
	}
	
	private void computeBoundsAliens()
	{
		if(_aliens.size() != 0)
		{
			Collections.sort(_aliens);
			leftAlien = _aliens.get(0);
			rightAlien = _aliens.get(_aliens.size()-1);
		}
		
	}
	//returns collided projectiles.
	public ArrayList<Projectile> handleCollisions(List<Projectile> projectiles)
	{
		
		ArrayList<Projectile> collided = new ArrayList<Projectile>();
		boolean sort = false;
		
	
		
		for(Projectile p : projectiles )
		{
			if(p.getDirection() == Direction.UP)
			{
				boolean collision = false;
				for (Iterator<Alien> ia = _aliens.iterator(); !collision && ia.hasNext();)
				{
					Alien a = ia.next();
					
					if(a.contains(p.getCollisionPoint()))
					{
						sort = (a.equals(leftAlien) || a.equals(rightAlien)) ? true : false;
						ia.remove();
						
						SpaceInvaders.playAudioFileOnce("Explosion1.wav");
						collided.add(p);
						collision = true;
					}
				}
			}
		}
		
		if(sort)
		{
			computeBoundsAliens();
		}
		return collided;
	}
	
	public boolean isEmpty()
	{
		return _aliens.size() == 0;
	}
	public  List<Projectile> dropAlienBombsIfReady()
	{
		List<Projectile> projectiles = new ArrayList<Projectile>();
		for (Alien a : _aliens)
		{
			if (a.isReadyToDropBomb())
			{
				SpaceInvaders.playAudioFileOnce("Fire2.wav");
				Point p = new Point(a.getPosition().x + Alien.ALIEN_SIZE.width/2,
						a.getPosition().y + Alien.ALIEN_SIZE.height);
				projectiles.add(new Projectile(SpaceInvaders.ALIEN_PROJ_SPEED, 
						p, Direction.DOWN));
			}
		}
		return projectiles;
	}
}
