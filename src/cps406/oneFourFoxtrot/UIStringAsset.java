package cps406.oneFourFoxtrot;

import java.awt.Graphics2D;
import java.awt.Point;

public class UIStringAsset implements IDrawable
{
	String _label;
	String _message;
	String _separator;
	Point _p;
	
	public UIStringAsset(String label, String message, String separator, int x, int y)
	{
		_label = label;
		_message = message;
		_separator = separator;
		_p = new Point(x, y);
	}
	
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawString(_label + _separator + _message, _p.x, _p.y);
		
	}
	
	public String getLabel()
	{
		return _label;
		
	}
	public void setLabel(String s)
	{
		_label = s;
	}
	public String getMessage()
	{
		return _message;
		
	}
	public void setMessage(String s)
	{
		_message = s;
	}
	
	public void setPos(Point p)
	{
		_p = p;
	}
	public Point getPos()
	{
		return _p;
	}
}
