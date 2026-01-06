package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The YutaBot is a bot that has tracking while shooting, collision with any
 * objects. It is able to dodge bullets and more
 * Sometimes the bot overheats
 * It trash talks when it kills someone.
 *
 * @author Hamza Faruqui
 * @version 3.0 (September 15th, 2023)
 */
public class YutaBot extends Bot {

	/**
	 * Next message to send, or null if nothing to send.
	 */
	private String nextMessage = null;
	/**
	 * An array of trash talk messages.
	 */
	private String[] killMessages = { "Woohoo!!!", "In your face!", "Pwned", "Take that.", "Gotcha!", "Too easy.", "Hahahahahahahahahaha :-)" };
	/**
	 * Bot image
	 */
	Image current, up, down, right, left;
	/**
	 * My name (set when getName() first called)
	 */
	private String name = null;
	/**
	 * Counter for timing moves in different directions
	 */
	private int moveCount = 0;
	/**
	 * Next move to make
	 */
	private int move = BattleBotArena.UP;
	/**
	 * Counter to pause before sending a victory message
	 */
	private int msgCounter = 0;
	/**
	 * Used to decide if this bot should overheat or not
	 */
	private int targetNum = (int) (Math.random() * BattleBotArena.NUM_BOTS);
	/**
	 * The amount to sleep to simulate overheating because of excessive CPU
	 * usage.
	 */
	private int sleep = (int) (Math.random() * 5 + 1);
	/**
	 * Set to True if we are trying to overheat
	 */
	private boolean overheat = false;
	/**
	 * x and y variables used in code for collision and movement
	 */
	private double x, y;
	/**
	 * Count variable used to make the bullets of the bot more spread out and to
	 * control how often the bot collides, moves, dodges and shoots
	 */
	private int count = 50;

	/**
	 * Return image names to load
	 */

	public String[] imageNames() {
		String[] paths = { "yuta.png", "yuta.png", "yuta.png", "yuta.png" };
		return paths;
	}

	/**
	 * Store the images loaded by the arena
	 */
	public void loadedImages(Image[] images) {
		if (images != null) {
			if (images.length > 0)
				up = images[0];
			if (images.length > 1)
				down = images[1];
			if (images.length > 2)
				right = images[2];
			if (images.length > 3)
				left = images[3];
			current = up;
		}
	}

	/**
	 *
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		// variables
		count--; // decreasing count to decide whether it is time to move
		double centerX = BattleBotArena.RIGHT_EDGE / 2; // this is 400 pixels after dividing in half
		int verticalHalf; // Declaring my verticalHalf variable here
		double BotX = me.getX(); // This variable is used to get the X value of my bot from the Bot class
		double BotY = me.getY(); // This variable is used to get the Y value of my bot from the Bot class
		double bottomOfScreen = BattleBotArena.BOTTOM_EDGE; // this is 600 pixels(bottom of the screen)
		double prevX = x; // Setting the prevX variable to the YutaBot's x value
		double prevY = y; // Setting the prevY variable to the YutaBot's y value

		// used to determine quadrants(two halves of the screen)
		if (BotX <= centerX) {
			verticalHalf = 1;
		} else {
			verticalHalf = 2;
		}
		// default overheating code
		if (overheat) {
			try {
				Thread.sleep(sleep);
			} catch (Exception e) {
			}
		}

		/**
		 * This segment of the code is used for the collision of the YutaBot with other
		 * bots
		 * If the botX and botY are equal to the x and y of the other bots or the same
		 * as the previous x and y the collision code happens
		 * After the movement occurs, if the bot collides it will move in a different
		 * direction, prevX and prevY are set to x and y once more
		 * After the code, it resets x and y to me.getX() and me.getY() to ensure the
		 * code is functioning
		 */
		if ((BotX == x && BotY == y) || (BotX == prevX && BotY == prevY)) {

			if (move == BattleBotArena.UP) {
				move = BattleBotArena.LEFT;
			} else if (move == BattleBotArena.LEFT) {
				move = BattleBotArena.DOWN;
			} else if (move == BattleBotArena.DOWN) {
				move = BattleBotArena.RIGHT;
			} else if (move == BattleBotArena.RIGHT) {
				move = BattleBotArena.UP;
				count = 15 + (int) (Math.random() * 30); // set count to a number after colliding
			}

			prevX = x;
			prevY = y;
		}

		x = me.getX();
		y = me.getY();

		/**
		 * This segment of the code is the movmement for the YutaBot if it spawns on the
		 * left half of the screen
		 * If the bot spawns on this side of the screen, depending on where the bot is
		 * headed, it will go in a certain direction after colliding with an edge
		 * IF the bot hits the bottom of the screen it will move up, if it hits the top
		 * it will move down and if it hits the left it will bounce off to the right
		 */
		if (verticalHalf == 1) {

			if (BotY >= bottomOfScreen - 28) {
				move = BattleBotArena.UP;
			} else if (BotY <= 10) {
				move = BattleBotArena.DOWN;
			}
			if (BotX <= BattleBotArena.LEFT_EDGE + 10) {
				move = BattleBotArena.RIGHT;
				count = 15 + (int) (Math.random() * 30); // set count to a number after the movement code on the left side of the screen is done
			}
		}

		/**
		 * This segment of the code is the movement for the YutaBot if it spawns on the
		 * right half of the screen
		 * If the bot spawns on this side of the screen, depending on where the bot is
		 * headed, it will go in a certain direction after colliding with an edge
		 * If the bot hits the bottom of the screen it will move up, if it hits the top
		 * if will move down and if it hits the right it will bounce off to the left
		 */
		if (verticalHalf == 2) {
			if (BotY <= 10) {
				move = BattleBotArena.DOWN;
			} else if (BotY > bottomOfScreen - 30) {
				move = BattleBotArena.UP;
			}
			if (BotX >= BattleBotArena.RIGHT_EDGE - 30) {
				move = BattleBotArena.LEFT;
				count = 15 + (int) (Math.random() * 30); // set count to a number after movement code on the right side of the screen is done
			}
		}

		/**
		 * This code is used for the Yutabot to dodge the closest bullet that is near
		 * it, else if is used to ensure the movement code takes priority
		 * If the length of the bullets on screen is more than 0(used to stop errors)
		 * the code will execute
		 * Used object oriented programming here to create a custom bulletHelper object
		 * from the BotHelper class, the findClosest bullet method specifically
		 * If the closestBullet is there, make two variables by subtracting the bullet x
		 * and the bullet y from my bot's x and bot's y
		 * If the absolute value of betaX is greater than betaY, it will move left or
		 * right depending on the value of betaX
		 * Else, if the absolute value of betaY is greater than betaY, it will move up
		 * or down depending on the value of the betaY
		 */
		if (bullets.length > 0 && count == 4 + (int) (Math.random() * 10)) { // dodge bullets depending on counts value and bullets on screen
			BotHelper bulletHelper = new BotHelper();
			Bullet closestBullet = bulletHelper.findClosest(me, bullets);

			if (closestBullet != null) {
				double betaX = closestBullet.getX() - me.getX();
				double betaY = closestBullet.getY() - me.getY();

				if (Math.abs(betaX) > Math.abs(betaY)) {
					if (betaX > 0) {
						move = BattleBotArena.LEFT;
					} else {
						move = BattleBotArena.RIGHT;
					}
				} else {
					if (betaY > 0) {
						move = BattleBotArena.DOWN;
					} else {
						move = BattleBotArena.UP;
					}
				}
			}
		}

		/**
		 * This code is used to shoot bullets depending on the location of the other
		 * BattleBots, the code works similar to the dodging bullelts code
		 * If shotOk is true and the amount of livebots on the sceeen is greater than 0,
		 * a botHelper object is created from the botHelper class
		 * By using the second findClosest method in the botHelper for other bots, if
		 * the nearest bot is not null, deltaX and deltaY are created
		 * If the absolute value of deltaX is greater than deltaY, a rnadomChance
		 * variable is created to make the firing more random and chaotic
		 * Depending on the values of deltaX and randomChance, the bot will fire left or
		 * right, the similar wll occur for firing up and down
		 */
		if (count % 6 == 0 && shotOK && liveBots.length > 0) { // count is used here to ensure the bullets are more spread out when the bot is firing
			BotHelper botHelper = new BotHelper();
			BotInfo nearestBot = botHelper.findClosest(me, liveBots);

			if (nearestBot != null) {
				double deltaX = nearestBot.getX() - BotX;
				double deltaY = nearestBot.getY() - BotY;

				if (Math.abs(deltaX) > Math.abs(deltaY)) {
					double randomChance = Math.random();
					if (deltaX > 0 && randomChance < 0.5) {
						return BattleBotArena.FIRERIGHT;
					} else if (deltaX <= 0 && randomChance < 0.5) {
						return BattleBotArena.FIRELEFT;
					}
				} else {
					double randomChance = Math.random();

					if (deltaY > 0 && randomChance < 0.5) {
						return BattleBotArena.FIREDOWN;
					} else if (deltaY <= 0 && randomChance < 0.5) {
						return BattleBotArena.FIREUP;
					}
				}
			}
		}
		return move; // returns move
	}

	/**
	 * Decide whether we are overheating this round or not
	 */
	public void newRound() {
		if (botNumber >= targetNum - 3 && botNumber <= targetNum + 3)
			overheat = true;
	}

	/**
	 * Send the message and then blank out the message string
	 */
	public String outgoingMessage() {
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Construct and return my name
	 */
	public String getName() {
		if (name == null)
			name = "Yuta";
		return name;
	}

	/**
	 * Team "Sorcerers"
	 */
	public String getTeamName() {
		return "Sorcerers";
	}

	/**
	 * Draws the bot at x, y
	 * 
	 * @param g The Graphics object to draw on
	 * @param x Left coord
	 * @param y Top coord
	 */
	public void draw(Graphics g, int x, int y) {
		if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
		else {
			g.setColor(Color.lightGray);
			g.fillOval(x, y, Bot.RADIUS * 2, Bot.RADIUS * 2);
		}
	}

	/**
	 * If the message is announcing a kill for me, schedule a trash talk message.
	 *
	 * @param botNum ID of sender
	 * @param msg    Text of incoming message
	 */
	public void incomingMessage(int botNum, String msg) {
		if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by " + getName() + ".*")) {
			int msgNum = (int) (Math.random() * killMessages.length);
			nextMessage = killMessages[msgNum];
			msgCounter = (int) (Math.random() * 30 + 30);
		}
	}
}
