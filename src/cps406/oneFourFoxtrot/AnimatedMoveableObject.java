package cps406.oneFourFoxtrot;

import java.awt.Graphics2D;

public abstract class AnimatedMoveableObject extends MoveableObject
{
	Sprite _sprite;

	public AnimatedMoveableObject(Sprite s) 
	{
		_sprite = s;
	}

	@Override
	public void draw(Graphics2D g) 
	{
		_sprite.drawSprite(_pos.x, _pos.y, g);
	}
}