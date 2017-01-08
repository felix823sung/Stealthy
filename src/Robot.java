import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The Robot Class Contains methods for moving the Robot, getting miscellaneous
 * information about the robot, a tracking method, a method to check where the
 * Robot can see the player, a method to draw the Robot, and a timer to allow
 * the robot to move on its own
 * @author Felix Sung and Jonathan Sun
 * @version January 12, 2015
 */

public class Robot extends JPanel implements ActionListener
{
	// Establishing variables that will be used in the methods
	private int currentRow;
	private int currentColumn;
	private final int IMAGE_WIDTH = 64;
	private Image[] ROBOT_IMAGES = new Image[4];
	private Timer timer;
	private int time;
	private int xLocation;
	private int yLocation;
	private int[][] mapGrid;
	private int direction;
	private final int MAX_HEALTH = 100;
	private int health;
	private boolean active;
	private boolean aggravated;
	private Image healthImage;

	/**
	 * Constructs a new Robot object
	 * @param row the row at which the Robot is created
	 * @param column the column at which the Robot is created
	 * @param mapSection which section of the map the Robot is created in
	 * @param direction with direction the robot is facing (1=WEST, 2=EAST,
	 *            3=NORTH, 4=SOUTH)
	 */
	public Robot(int row, int column, int[][] mapSection, int direction)
	{
		// Sets up some information about the Robot, such as health, whether is
		// it active etc.
		this.currentRow = row - 1;
		this.currentColumn = column - 1;
		this.mapGrid = mapSection;
		this.direction = direction;
		this.health = MAX_HEALTH;
		this.active = true;
		this.aggravated = false;

		// Loads in images for the Robot, one image for each direction
		ROBOT_IMAGES[0] = new ImageIcon("Robot[0].png").getImage();
		ROBOT_IMAGES[1] = new ImageIcon("Robot[1].png").getImage();
		ROBOT_IMAGES[2] = new ImageIcon("Robot[2].png").getImage();
		ROBOT_IMAGES[3] = new ImageIcon("Robot[3].png").getImage();

		healthImage = new ImageIcon("HealthUnit.png").getImage();
		// Start a timer at a random number between 300 and 800 milliseconds
		int randNo = (int) (Math.random() * 500 + 300);
		timer = new Timer(randNo, this);
		timer.setInitialDelay(randNo);
		timer.start();
	}

	/**
	 * Checks to see if the Robot is currently active
	 * @return true if the Robot is active, false if the Robot is not active
	 */
	public boolean isActivated()
	{
		if (active)
		{
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Stops the timer on the Robot
	 */
	public void removeRobot()
	{
		timer.stop();
	}

	/**
	 * Draws the robot, using different images depending on which direction it
	 * is facing
	 * @param g The Graphics Context
	 */
	public void drawRobot(Graphics g)
	{
		// Draws the robot while it is active
		if (active)
		{
			// Depending on which direction the robot is facing, draws different
			// images
			if (direction == 1)
				g.drawImage(ROBOT_IMAGES[0],
						(currentColumn) * IMAGE_WIDTH,
						(currentRow) * IMAGE_WIDTH, null);
			else if (direction == 2)
				g.drawImage(ROBOT_IMAGES[1],
						(currentColumn) * IMAGE_WIDTH,
						(currentRow) * IMAGE_WIDTH, null);
			else if (direction == 3)
				g.drawImage(ROBOT_IMAGES[2],
						(currentColumn) * IMAGE_WIDTH,
						(currentRow) * IMAGE_WIDTH, null);
			else
				g.drawImage(ROBOT_IMAGES[3],
						(currentColumn) * IMAGE_WIDTH,
						(currentRow) * IMAGE_WIDTH, null);
			for (int healthUnit = 0; healthUnit < (health * 20.0)
					/ MAX_HEALTH; healthUnit++)
			{
				g.drawImage(healthImage, currentColumn * IMAGE_WIDTH
						+ healthUnit * 3, currentRow * IMAGE_WIDTH, this);
			}
		}
	}

	/**
	 * A Timer method to delay certain lines of code
	 * @param timeToDelay the time to delay in milliseconds
	 */
	public void delay(int timeToDelay)
	{
		try
		{
			Thread.sleep(timeToDelay);
		}
		catch (InterruptedException exception)
		{
		}
	}

	/**
	 * Finds out the current row of the Robot object
	 * @return the current row of the Robot
	 */
	public int getRow()
	{
		return currentRow;
	}

	/**
	 * Finds out the current column of the Robot object
	 * @return the current column of the Robot
	 */
	public int getColumn()
	{
		return currentColumn;
	}

	/**
	 * Damages the Robot for 45 health
	 */
	public void takeDamage()
	{
		// If the current health of the Robot is more than 45, 45 health is
		// subtracted, while health is below 45, the current health is set to 0
		if (health > 45)
			health -= 45;
		else
		{
			health = 0;
			active = false;
		}
	}

	/**
	 * Determines whether the Robot can damage the player
	 * @param playerRow the row the player is in
	 * @param playerColumn the column the player is in
	 * @return true if the Robot can attack the player, false if it cannot
	 */
	public boolean canDamage(int playerRow, int playerColumn)
	{
		// If the Robot and the player share the same row and column, the robot
		// can damage the player
		if (currentRow == playerRow && currentColumn == playerColumn)
			return true;
		else if (direction == 1 && currentRow == playerRow
				&& currentColumn == playerColumn + 1)
			return true;
		else if (direction == 2 && currentRow == playerRow
				&& currentColumn == playerColumn - 1)
			return true;
		else if (direction == 3 && currentRow == playerRow + 1
				&& currentColumn == playerColumn)
			return true;
		else if (direction == 4 && currentRow == playerRow - 1
				&& currentColumn == playerColumn)
			return true;
		return false;
	}

	/**
	 * Checks to see if the player is within range of the Robot's line of sight,
	 * which extends 2 blocks from the Robot in the 4 cardinal directions
	 * @param playerRow the row the player is in
	 * @param playerColumn the column the player is in
	 * @param isStealthed whether the player is currently stealth
	 * @return true if the player is within range, false if they are not
	 */
	public boolean inRange(int playerRow, int playerColumn, boolean isStealthed)
	{
		// The Robot can only detect the player while stealth is not on
		if (!isStealthed)
		{
			// Checks if the player is directly below the Robot and within 2
			// blocks range
			if (playerRow > currentRow && playerRow - currentRow <= 2
					&& playerColumn == currentColumn)
			{

				// Checks if there is a wall in front of the Robot downwards
				if (mapGrid[currentRow + 1 + 1][currentColumn + 1] > 3)
				{
					return false;
				}

				// Sets whether the Robot is aggravated to true
				aggravated = true;
				return true;
			}
			// Checks if the player is directly above the Robot and within 2
			// blocks range
			else if (playerRow < currentRow && currentRow - playerRow <= 2
					&& playerColumn == currentColumn)
			{

				// Checks if there is a wall in front of the Robot upwards
				if (mapGrid[currentRow + 1 - 1][currentColumn + 1] > 3)
				{
					return false;
				}

				// Sets whether the Robot is aggravated to true
				aggravated = true;
				return true;
			}
			// Checks if the player is directly to the right of the Robot and
			// within 2 blocks range
			else if (playerColumn > currentColumn
					&& playerColumn - currentColumn <= 2
					&& playerRow == currentRow)
			{

				// Checks if there is a wall in front of the Robot to the right
				if (mapGrid[currentRow + 1][currentColumn + 1 + 1] > 3)
				{
					return false;
				}

				// Sets whether the Robot is aggravated to true
				aggravated = true;
				return true;
			}
			// Checks if the player is directly to the left of the Robot and
			// within 2 blocks range
			else if (playerColumn < currentColumn
					&& currentColumn - playerColumn <= 2
					&& playerRow == currentRow)
			{

				// Checks if there is a wall in front of the Robot to the left
				if (mapGrid[currentRow + 1][currentColumn + 1 - 1] > 3)
				{
					return false;
				}

				// Sets whether the Robot is aggravated to true
				aggravated = true;
				return true;
			}
			// If none of the above conditions are met, sets whether the Robot
			// is aggravated to false
			aggravated = false;
			return false;
		}
		return false;
	}

	/**
	 * Moves the Robot one block to the left
	 */
	public void walkLeft()
	{
		delay(50);
		for (double column = currentColumn; column > currentColumn - 1; column -= 0.15)
		{
			xLocation = (int) (column * IMAGE_WIDTH);
			yLocation = (currentRow) * IMAGE_WIDTH;

			paintImmediately(xLocation, yLocation, IMAGE_WIDTH, IMAGE_WIDTH);

			delay(1);
		}
	}

	/**
	 * Moves the Robot one block to the right
	 */
	public void walkRight()
	{

		delay(50);
		for (double column = currentColumn; column < currentColumn + 1; column += 0.15)
		{
			xLocation = (int) (column * IMAGE_WIDTH);
			yLocation = (currentRow) * IMAGE_WIDTH;

			paintImmediately(xLocation, yLocation, IMAGE_WIDTH, IMAGE_WIDTH);

			delay(1);
		}
	}

	/**
	 * Moves the Robot up block upwards
	 */
	public void walkUp()
	{

		delay(50);
		for (double row = currentRow; row > currentRow - 1; row -= 0.15)
		{
			yLocation = (int) (row * IMAGE_WIDTH);
			xLocation = (currentColumn) * IMAGE_WIDTH;

			paintImmediately(xLocation, yLocation, IMAGE_WIDTH, IMAGE_WIDTH);

			delay(1);
		}

	}

	/**
	 * Moves the Robot up block downwards
	 */
	public void walkDown()
	{
		delay(50);

		for (double row = currentRow; row < currentRow + 1; row += 0.15)
		{
			yLocation = (int) (row * IMAGE_WIDTH);
			xLocation = (currentColumn) * IMAGE_WIDTH;

			paintImmediately(xLocation, yLocation, IMAGE_WIDTH, IMAGE_WIDTH);

			delay(1);
		}
	}

	/**
	 * Called each time a timer event is generated
	 * @param event the Timer event
	 */
	public void actionPerformed(ActionEvent event)
	{
		// Adds a random number between 1 and 10 to the time variable
		int randNum = (int) (Math.random() * 10 + 1);
		time += randNum;
		// Limits the number of times random movement is generated, random
		// movements only occur when the Robot is not aggravated
		if (time % 10 > 3 && aggravated == false)
		{
			// Generates a number between 1 and 4 inclusive to signify the 4
			// directions (1=LEFT, 2=RIGHT, 3=UP,4=DOWN)
			int rand = (int) (Math.random() * 4 + 1);
			// Moves the Robot in one of the four directions depending on the
			// random integer
			if (rand == 1 && currentColumn != 15)
			{
				direction = 2;
				if (mapGrid[currentRow + 1][currentColumn + 2] < 3)
				{
					this.walkRight();
					currentColumn++;
				}
			}
			else if (rand == 2 && currentColumn != 0)
			{
				direction = 1;
				if (mapGrid[currentRow + 1][currentColumn] < 3)
				{
					this.walkLeft();
					currentColumn--;
				}
			}
			else if (rand == 3 && currentRow != 0)
			{
				direction = 3;
				if (mapGrid[currentRow][currentColumn + 1] < 3)
				{
					this.walkUp();
					currentRow--;
				}
			}
			else if (rand == 4 && currentRow != 11)
			{
				direction = 4;
				if (mapGrid[currentRow + 2][currentColumn + 1] < 3)
				{
					this.walkDown();
					currentRow++;
				}
			}
		}
	}

	/**
	 * A method that allows the Robot to track the player's location
	 * @param playerRow the row that the player is in
	 * @param playerColumn the column that the player is in
	 */
	public void goToPlayerLocation(int playerRow, int playerColumn)
	{
		// Sets up three cases, if the player is above the Robot, if the player
		// is below the Robot, and if the player is in the same row as the Robot
		if (playerRow < currentRow) // If the Robot is above the player
		{

			// Ensures the Robot does not track the player off the top of the
			// map section
			if (currentRow != 0)
			{
				// The Robot attempts to move upwards if possible
				direction = 3;
				if (mapGrid[currentRow][currentColumn + 1] < 3)
				{
					this.walkUp();
					currentRow--;
				}
			}
		}
		else if (playerRow > currentRow) // If the player is below the Robot
		{

			// Ensures the Robot does not track the player off the bottom of the
			// map section
			if (currentRow != 11)
			{
				// The Robot attempts to moves downwards if possible
				direction = 4;
				if (mapGrid[currentRow + 2][currentColumn + 1] < 3)
				{
					this.walkDown();
					currentRow++;
				}

			}

		}
		else
		// The player is in the same row as the Robot
		{
			// If the player is on the right of the Robot, also ensures the
			// Robot does not track the player off the right of the map
			if (playerColumn > currentColumn && currentColumn != 15)
			{
				// The Robot attempts to move to the right if possible
				direction = 2;
				if (mapGrid[currentRow + 1][currentColumn + 2] < 3)
				{
					this.walkRight();
					currentColumn++;
				}
			}
			// If the player is to the left of the Robot, also ensures the Robot
			// does not track the player off the left of the map
			else if (playerColumn < currentColumn && currentColumn != 0)
			{
				// The Robot attempts to move to the left is possible
				direction = 1;
				if (mapGrid[currentRow + 1][currentColumn] < 3)
				{
					this.walkLeft();
					currentColumn--;
				}
			}
		}
	}

}
