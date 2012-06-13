package cps406.oneFourFoxtrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SpaceInvaders extends JFrame implements LineListener
{
	//Constants - Game
	public static final int CANVAS_WIDTH = 400;
	public static final int CANVAS_HEIGHT = 600;
	public static final int DEFENDER_HEIGHT = CANVAS_HEIGHT -Defender.HEIGHT -20;
	
	private static final Color GAME_BACKGROUND = Color.BLACK;

	private static final long UPDATE_PERIOD = 10000000;
	
	private static final String PAUSEDSTRING = "PAUSED";
	
	//Constants - Assets
	private static final int DEFENDER_PROJ_SPEED = 8;
	static final int ALIEN_PROJ_SPEED = 4;
	private static final int DEFENDER_SPEED = 2;
	private static final String RESOURCES_FOLDER = "resources/";
	private static final String SPRITE_FOLDER = RESOURCES_FOLDER + "Sprites/";
	
	//Game Assets
	boolean _shootNext;
	int _level = 1;
	
	AlienController _alienController;
	GameState _state;
	//List<Alien> _alienList;
	List<Projectile> _projectiles;
	Defender _defender;
	UIStringAsset _score;
	GameCanvas _canvas;
    ScoreTracker _tracker;

	List<UIStringAsset> _uiAssets;

	
	//Audio Assets
	private static int _musicState = 1;	// starting state; used to track what part of the song is playing
	private static Clip _gameMusicClip = null;
	private static Clip _menuMusicClip = null;
	
	//Player Control assets
	boolean _useMouseControls;
	public enum KeyPress {LEFT, RIGHT, F1, F2}
	Vector<KeyPress> _keyList;
	
	//Image Assets
	private static BufferedImage _backgroundImg = null;
	private static final int BACKGROUND_INITIAL_Y_POS = 1500;		// initial value determines how far from the earth we are starting
	private static int _backgroundImgScrollYPosCounter = BACKGROUND_INITIAL_Y_POS;
	private static boolean _backgroundScrollDown = true;
	
	public static Sprite _alienSprite;
	public static Sprite _defenderSprite;
	
	//Other
	private Point _frameLocation = null;

	
	public SpaceInvaders()
	{
		super();
		configureUI();
		loadResources();
		initGame();
		start();
	}
	
	private void configureUI()
	{
		_canvas = new GameCanvas();

		_canvas.setPreferredSize(new Dimension(SpaceInvaders.CANVAS_WIDTH,
				SpaceInvaders.CANVAS_HEIGHT));
		_canvas.setVisible(true);
		_canvas.setSize(new Dimension(SpaceInvaders.CANVAS_WIDTH,
				SpaceInvaders.CANVAS_HEIGHT));
		_canvas.setFocusable(true);
		
		this.setContentPane(_canvas);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setTitle("CYBERSPACE INVADERS");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = screenSize.width/2 - CANVAS_WIDTH/2;
		int y = screenSize.height/2 - CANVAS_HEIGHT/2;
		this.setLocation(x,y);
	
		this.setResizable(false);
		
		_frameLocation = new Point(0,0);
		this.addComponentListener(new SpaceInvadersComponentListener());
		
	}

	private void loadResources()
	{
		// load things like images, sounds, etc here
		// for now just images
		try 
		{
			_backgroundImg = ImageIO.read(SpaceInvaders.class.getResource(RESOURCES_FOLDER + "BackgroundImg1.png"));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Get sprites from files
		BufferedImage alienimg = Sprite.getSpriteFromFile(SPRITE_FOLDER + "AlienSprite.png");
		_alienSprite = new Sprite(Alien.ALIEN_SIZE.width,Alien.ALIEN_SIZE.height, 
				Alien.FRAMECOUNT, alienimg);
		
		BufferedImage defenderimg = Sprite.getSpriteFromFile(SPRITE_FOLDER + "ShipSprite.png");
		_defenderSprite = new Sprite(Defender.WIDTH, Defender.HEIGHT,
				Defender.FRAMECOUNT, defenderimg);
	}
	
	private void initGame()
	{
		//_alienList = Collections.synchronizedList(new ArrayList<Alien>());
		//AlienFactory.createAliens(_alienList, _level);
		_projectiles = Collections
				.synchronizedList(new ArrayList<Projectile>());
		_alienController = new AlienController(0, CANVAS_WIDTH);
		
		_keyList = new Vector<KeyPress>();
		_defender = new Defender(DEFENDER_SPEED, CANVAS_WIDTH / 2, DEFENDER_HEIGHT, _defenderSprite);
		_shootNext = false;
		_score = new UIStringAsset("Score", "0", ": ", 1, CANVAS_HEIGHT - 5);
		_level = 1;
		_alienController.setupLevel(_level);
		setGameState(GameState.INITIALIZED);
		_useMouseControls = false;
        _tracker = new ScoreTracker();
        
	}

	public void start()
	{
		this.pack();
		if (_state == GameState.INITIALIZED)
		{
			Thread gameThread = new Thread()
			{
				@Override
				public void run()
				{
					gameLoop();
				}
			};
			this.setVisible(true);
			gameThread.start();
		}

	}

	public GameState getGameState()
	{
		return _state;
	}

	private void setGameState(GameState s)
	{
		if(s == GameState.GAMEOVER)
		{
			_tracker.resetScore();
			playIntroExitMusic();
		}
		else if (s == GameState.INITIALIZED)
		{
			playIntroExitMusic();
		}
		if(s == GameState.PLAYING)
		{
			_alienController = new AlienController(0, CANVAS_WIDTH);
			_alienController.setupLevel(_level);
			
			// if we are coming from the menu screen state into playing, then cue the music
			// otherwise we are coming from a level-transition state, so don't start it again
			if (_state == GameState.INITIALIZED || _state == GameState.GAMEOVER)
			{
				playGameMusic(this);
			}
		}
		_state = s;
	}
	
	private void clearAssets()
	{
		_alienController = new AlienController(0, CANVAS_WIDTH);
		_projectiles.clear();
	}
	
	private synchronized void shoot()
	{
		// defender shoot
		playAudioFileOnce("Fire1.wav");
		Point p = new Point(_defender.getPosition().x + Defender.WIDTH/2 - 2,
				_defender.getPosition().y);
		_projectiles.add(new Projectile(DEFENDER_PROJ_SPEED, p, Direction.UP));
	}

	private synchronized void drawComponents(Graphics2D g)
	{
		if(_state == GameState.PLAYING || _state == GameState.PAUSED)
		{
			_alienController.draw(g);
			for (Projectile p : _projectiles)
			{
				p.draw(g);
			}
	
			_defender.draw(g);
			g.setColor(Color.green);	//Score should be green
			_score.draw(g);
			if(_state == GameState.PAUSED)
			{
				g.setFont( new Font("Arial", Font.PLAIN, 30));
				g.drawString(PAUSEDSTRING, CANVAS_WIDTH/2 - 70, 
						CANVAS_HEIGHT/2);
				g.setFont( new Font("Arial", Font.PLAIN, 12));
			}
		}
		else if (_state == GameState.INITIALIZED || _state == GameState.GAMEOVER)
		{
			//draw menu.
			g.setColor(Color.green);
			int menuTextLeftPos = 15;
			g.setFont( new Font("Arial", Font.PLAIN, 30));
			g.drawString("CYBERSPACE INVADERS", menuTextLeftPos, 50);
			g.setFont( new Font("Arial", Font.PLAIN, 12));
            g.drawString("Your Score: " + _tracker.prevScore, menuTextLeftPos, 80);	
			g.drawString("High Score: " + _tracker.highScore, menuTextLeftPos, 100);			
			g.drawString("Press f1 to start a new game", menuTextLeftPos, 130);	
		}
	}
	
	private synchronized void dropAlienBombsIfReady()
	{
		_projectiles.addAll(_alienController.dropAlienBombsIfReady());
	}

	private synchronized void moveAssets()
	{
		_alienController.move();
		
		for (Iterator<Projectile> i = _projectiles.iterator(); i.hasNext();)
		{
			Projectile p = i.next();
			if (p.getDirection().equals(Direction.DOWN))
			{
				// if we would move it past the edge to the right
				if (p.getPosition().y <= CANVAS_HEIGHT)
				{
					p.moveDown();

				} else
				{
					i.remove();
				}
			} else
			// direction is UP
			{

				if (p.getPosition().y >= 0)
				{
					p.moveUp();
				} else
				{
					i.remove();
				}

			}

		}
		int mouseX = 0;
		int movex = 0;
		if(_useMouseControls)
		{
			mouseX = MouseInfo.getPointerInfo().getLocation().x - _frameLocation.x;
			movex = Math.abs(mouseX - _defender.getPosition().x ) > _defender.getMoveSpeed()   
					?  _defender.getMoveSpeed() * Integer.signum(mouseX - _defender.getPosition().x  ) 
							: mouseX - _defender.getPosition().x ;
		}
		else//use keyBoard controls
		{
			if(!_keyList.isEmpty())
			{
				KeyPress fe = _keyList.firstElement();
				if(fe.equals(KeyPress.RIGHT))
					movex = _defender.getMoveSpeed();
				else if(fe.equals(KeyPress.LEFT))
					movex = -_defender.getMoveSpeed();
			}
		}
		//If between 0 and the right side
		if(movex + _defender.getPosition().x + Defender.WIDTH <= CANVAS_WIDTH 
				&& movex + _defender.getPosition().x > 0)
			_defender.moveRelative(movex, 0);
	}

	private void gameLoop()
	{

		long beginTime, timeTaken, timeLeft;
		while (true)
		{
			beginTime = System.nanoTime();
			
			if (_state == GameState.INITIALIZED || _state == GameState.GAMEOVER)
			{
				//do menu screen
				
			}
			else if (_state == GameState.PLAYING)
			{
				if(_alienController.getAliensRemaining() == 0)
				{
					// transition to next state
					_backgroundScrollDown = false;
					setGameState(GameState.SCROLL_UP_BETWEEN_LEVELS);
				}
				
				if (getShoot())
				{
					shoot();
					setShoot(false);
				}

				_tracker.update();
				dropAlienBombsIfReady();
				moveAssets();
				detectCollisions();
				updateScore();
			}
			else if (_state == GameState.READY_FOR_NEXT_LEVEL)
			{
				_level += 1;
				_alienController.setupLevel(_level);
				// transition to next state
				_backgroundScrollDown = true;
				setGameState(GameState.PLAYING);
			}
			
			repaint();
			// _canvas.repaint();

			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000;
			
			if (timeLeft < 10)
				timeLeft = 10;

			try
			{
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex)
			{
			}
		}
	}

	private void updateScore()
	{
		_score.setMessage(_tracker.getScore() + "");
	}

	private void togglePause()
	{
		if(_state == GameState.PLAYING)
		{
			_state = GameState.PAUSED;
		}
		else if(_state == GameState.PAUSED)
		{
			_state = GameState.PLAYING;
		}
	}
	
	private synchronized void detectCollisions()
	{
		_tracker.updateKill(_alienController.handleCollisions(_projectiles));
		for (Iterator<Projectile> i = _projectiles.iterator(); i.hasNext();)
		{
			Projectile p = i.next();
			if (p.getDirection().equals(Direction.DOWN))
			{
				if (p.getPosition().y <= (DEFENDER_HEIGHT - 5) && _defender.contains(p.getPosition()))
				{
					// game over
					setGameState( GameState.GAMEOVER);
					playAudioFileOnce("Explosion1.wav");
					_level = 1;
				}
			}
			
		}
	}

	private synchronized boolean getShoot()
	{
		return _shootNext;
	}

	private synchronized void setShoot(boolean b)
	{
		_shootNext = b;
	}

	public static void main(String[] args)
	{
		// Use the event dispatch thread to build the UI for thread-safety.
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new SpaceInvaders();
			}
		});
	}

	public class GameCanvas extends JPanel implements MouseListener, KeyListener, MouseMotionListener
	{

		public GameCanvas()
		{
			requestFocus();
			addMouseListener(this);
			addKeyListener(this);
			addMouseMotionListener(this);
		}

		@Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			paintGameBackground(g2d);
			drawComponents(g2d);
		}
		
		private void paintGameBackground(Graphics2D g)
		{
			int scrollSpeedScaleRate = 15;
			int scrollAmountScaled = _backgroundImgScrollYPosCounter / scrollSpeedScaleRate;		
			
			if (_state == GameState.INITIALIZED)
			{
				g.drawImage(_backgroundImg, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT,  GAME_BACKGROUND, null);
			}
			else if (_state == GameState.PAUSED)
			{
				g.drawImage(_backgroundImg, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, 0, scrollAmountScaled, CANVAS_WIDTH, CANVAS_HEIGHT + scrollAmountScaled,  GAME_BACKGROUND, null);
			}
			else
			{
				g.drawImage(_backgroundImg, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, 0, scrollAmountScaled, CANVAS_WIDTH, CANVAS_HEIGHT + scrollAmountScaled,  GAME_BACKGROUND, null);
								
				if (_backgroundScrollDown)
				{
					if (scrollAmountScaled < 350)
					{
						_backgroundImgScrollYPosCounter++;	
					}
				}
				else
				{
					_backgroundImgScrollYPosCounter -= 20;		// scroll up more quickly between levels
					if (scrollAmountScaled < 120)
					{
						setGameState(GameState.READY_FOR_NEXT_LEVEL);
					}
				}		
			}
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			setShoot(true);
		}

		@Override
		public void mouseEntered(MouseEvent e){}

		@Override
		public void mouseExited(MouseEvent e){}

		@Override
		public void mousePressed(MouseEvent e){}

		@Override
		public void mouseReleased(MouseEvent e){}

		@Override
		public void keyPressed(KeyEvent e) 
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_RIGHT && !_keyList.contains(KeyPress.RIGHT))
			{
				_keyList.add(KeyPress.RIGHT);
				_useMouseControls = false;
			}
			else if(key == KeyEvent.VK_LEFT && !_keyList.contains(KeyPress.LEFT))
			{
				_keyList.add(KeyPress.LEFT);
				_useMouseControls = false;
			}
			else if((_state == GameState.INITIALIZED || _state == GameState.GAMEOVER) && key == KeyEvent.VK_F1)
			{
                _projectiles.clear();
				setGameState(GameState.PLAYING);
                _tracker.start();
				_backgroundImgScrollYPosCounter = BACKGROUND_INITIAL_Y_POS;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) 
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_RIGHT)
			{
				_keyList.remove(KeyPress.RIGHT);
			}
			else if(key == KeyEvent.VK_LEFT)
			{
				_keyList.remove(KeyPress.LEFT);
			}
			else if(key == KeyEvent.VK_SPACE)
				setShoot(true);
		}

		@Override
		public void keyTyped(KeyEvent e) 
		{
			char key = e.getKeyChar();
			if (key == 'p')
				togglePause();
		}

		@Override
		public void mouseDragged(MouseEvent e) 
		{
			_useMouseControls = true;
		}

		@Override
		public void mouseMoved(MouseEvent e) 
		{
			_useMouseControls = true;
		}
	}

	public static synchronized void playAudioFileOnce(final String fName) 
	{
		 Thread audioThread = new Thread()
		 {
			 @Override
			 public void run() 
			 {
				 try 
				 {
					 Clip clip = AudioSystem.getClip();
					 InputStream in = SpaceInvaders.class.getResourceAsStream(RESOURCES_FOLDER + fName);
					 AudioInputStream inputStream = AudioSystem.getAudioInputStream(in);
					 clip.open(inputStream);
					 clip.start();  
				 }
				 catch (Exception e) 
				 {
					 System.err.println(e.getMessage());
				 }
			 }
		 };
		 audioThread.start();
	}
	
	public static synchronized void playGameMusic(final SpaceInvaders caller) 
	{
		 Thread audioThread = new Thread()
		 {
			 @Override
			 public void run() 
			 {
				 try 
				 {
					 if (_menuMusicClip != null)
					 {
						 _menuMusicClip.stop();
					 }
					 _gameMusicClip = AudioSystem.getClip();
					 String songPart;
					 int loopCount = 3;		// 3 for shorter verses turn-around, 7 for longer
					 if (_musicState == 0 || _musicState == 1)
					 {
						 songPart = RESOURCES_FOLDER + "Song1a.wav";
					 }
					 else if (_musicState == 2)
					 {
						 songPart = RESOURCES_FOLDER + "Song1b.wav";						 
					 }
					 else
					 {
						 songPart = RESOURCES_FOLDER + "Song1c.wav";
					 }
					 InputStream inSong = SpaceInvaders.class.getResourceAsStream(songPart);
					 AudioInputStream inStreamSong1a = AudioSystem.getAudioInputStream(inSong);
					 _gameMusicClip.addLineListener(caller);
					 _gameMusicClip.open(inStreamSong1a);
					 _gameMusicClip.loop(loopCount);
					 _gameMusicClip.start();
				 }
				 catch (Exception e) 
				 {
					 System.err.println(e.getMessage());
				 }
			 }
		 };
		 audioThread.start();
	}
	
	public static synchronized void playIntroExitMusic() 
	{
		 Thread audioThread = new Thread()
		 {
			 @Override
			 public void run() 
			 {
				 try 
				 {
					 if (_gameMusicClip != null)
					 {
						 _gameMusicClip.stop();
					 }
					 _menuMusicClip = AudioSystem.getClip();
					 InputStream inSong = SpaceInvaders.class.getResourceAsStream(RESOURCES_FOLDER + "MenuMusic.wav");
					 AudioInputStream inStream = AudioSystem.getAudioInputStream(inSong);
					 _menuMusicClip.open(inStream);
					 _menuMusicClip.start();
				 }
				 catch (Exception e) 
				 {
					 System.err.println(e.getMessage());
				 }
			 }
		 };
		 audioThread.start();
	}
	
	public class SpaceInvadersComponentListener implements ComponentListener
	{
		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void componentMoved(ComponentEvent e) 
		{
			_frameLocation = e.getComponent().getLocationOnScreen();
		}

		@Override
		public void componentResized(ComponentEvent e) {}

		@Override
		public void componentShown(ComponentEvent e) {}
	}

	@Override
	public void update(LineEvent arg0) {
		
		/* this is from the LineListener (audio) interface.
		 * This method gets called when there is some event from a 
		 * LineListener.  Used to track when a section of the song
		 * is over.
		 */
		if (arg0.getType().toString().equals("Stop"))
		{
			if (_state == GameState.GAMEOVER)
			{
				_musicState = 1;	// reset the song, but don't play again
			}
			else
			{
				if (_musicState == 3)
				{
					_musicState = 0;
				}
				else
				{
					_musicState++;
				}
				playGameMusic(this);
			}
		}
	}

	public GameState get_state()
	{
		return _state;
	}
}