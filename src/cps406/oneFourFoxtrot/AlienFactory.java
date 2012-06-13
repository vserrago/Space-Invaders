package cps406.oneFourFoxtrot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlienFactory
{
	final int MAX_ALIENS_PER_ROW;
	final int ALIEN_SPACING_X = 10;
	final int ALIEN_SPACING_Y = 10;
	final int ALIEN_X_OFFSET = 10;
	final int ALIEN_Y_OFFSET = 10;
	final double ALIEN_LEVEL_SPEED_MULTIPLIER = 0.005;
	int _numAliens;
	int _levelSpeed;
	List<AlienRow> _rows;
	public AlienFactory(int screenWidth)
	{
		MAX_ALIENS_PER_ROW =(int) Math.floor( (screenWidth * 0.75) / (Alien.ALIEN_SIZE.width + ALIEN_SPACING_Y));
		_numAliens = 0;
		_levelSpeed = -1;
	}
	
	public List<AlienRow> getRows(int level)
	{
		return _rows;
	}

	public int getNumAliens()
	{
		return _numAliens;
	}
	
	public int getLevelSpeed()
	{
		return _levelSpeed;
	}

	public void setupLevel(int level)
	{
		_levelSpeed = (int) Math.floor((ALIEN_LEVEL_SPEED_MULTIPLIER * level) + 1.0);
		_rows = Collections.synchronizedList(new ArrayList<AlienRow>());
		int i = 0;
		while (i < level)
		{
			AlienRow r = new AlienRow();
			int j = 0;
			while (j < MAX_ALIENS_PER_ROW && i < level)
			{
				r.addAlien(new Alien(_levelSpeed, new Point(j * 10 + j
						* Alien.ALIEN_SIZE.width + ALIEN_X_OFFSET, _rows.size()
						* ALIEN_SPACING_Y + ALIEN_X_OFFSET +  Alien.ALIEN_SIZE.height * _rows.size()),
						SpaceInvaders._alienSprite));
				j++;
				i++;
				
			}
			_numAliens += j;
			_rows.add(r);
		}	
	}
}