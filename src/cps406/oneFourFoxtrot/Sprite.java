package cps406.oneFourFoxtrot;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite
{
	private int _frameWidth;
	private int _frameHeight;
	private int _numFrames;
	private int _frameCounter;			//which frame we are on (from 1 to n)
	private BufferedImage _spriteSheet;
	
	public Sprite(int frameWidth, int frameHeight, int numFrames, 
			BufferedImage img)
	{
		_frameWidth = frameWidth;
		_frameHeight = frameHeight;
		_numFrames = numFrames;
		_frameCounter = 0;
		_spriteSheet = img;
	}
	public Sprite(Dimension d, int numFrames, BufferedImage img)
	{
		this(d.width,d.height,numFrames,img);
	}
	
	public void setBounds(int frameWidth, int frameHeight)
	{
		_frameWidth = frameWidth;
		_frameHeight = frameHeight;
	}
	
	public static BufferedImage getSpriteFromFile(String filepath)
	{
		BufferedImage _buff = null;
		
		//load the image file
		try 
		{
			_buff = ImageIO.read(Sprite.class.getResource(filepath));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return _buff;
	}

	private void iterateFrame()
	{
		if(_numFrames != 1)		//Not necessary with only 1 frame
		{
			_frameCounter++;
			if(_numFrames == _frameCounter)
				_frameCounter = 0;
		}
	}
	
	public void drawSprite(int x, int y, Graphics2D g)
	{
		int sx = _frameWidth*_frameCounter;		//eg 40 x 5 for the 5th frame
		int sy = 0;
		g.drawImage(_spriteSheet, x, y, x+_frameWidth, y+_frameHeight,
				sx, sy, sx+_frameWidth, sy+_frameHeight, null);
		iterateFrame();
	}
}