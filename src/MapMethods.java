import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Final project methods, includes the constructor game, as well as move methods
 * for the player, methods for setting up the map section, timer methods, and
 * methods to deal with user input
 * @author Jonathan Sun and Felix Sung
 * @version January 12, 2015
 */
public class MapMethods extends JPanel implements ActionListener
{
	// Establishing variables that will be used throughout the program

	// Program constants (Declared at the top, can be used by any method)
	static final Font HEALTH_ENERGY_FONT = new Font("Arial", Font.PLAIN, 12);
	static final Font INVENTORY_FONT = new Font("Arial", Font.PLAIN, 20);
	static final Font STORE_FONT = new Font("Arial", Font.PLAIN, 40);
	private final int IMAGE_WIDTH = 64;
	private final String[][] MAP_NO = new String[3][13];
	private final String[][] ITEM_MAP_NO = new String[MAP_NO.length][MAP_NO[0].length];

	// Program images, including arrays of images
	private Image[][] messageImages = new Image[MAP_NO.length][MAP_NO[0].length];
	private Image[][] mapImages = new Image[3][8];
	private Image[] itemImages = new Image[6];
	private Image[][][] playerImages = new Image[2][4][3];
	private Image healthImage;
	private Image energyImage;
	private Image barOutlineImage;
	private Image mapOutlineImage;
	private Image playerIconImage;
	private Image roomIconImage;
	private Image storeImage;
	private Image menuImage;
	private Image settingsImage;
	private Image helpImage;
	private Image gameOverImage;
	private Image[] itemIconImages = new Image[6];
	private Image[][] attackImages = new Image[4][2];
	private Image flashImage;
	private Image offImage;
	private Image onImage;
	private Image endMessageImage;

	// Program audio clips, as arrays
	private AudioClip[][] messages;
	private AudioClip[] randomMessages;
	// Program background music
	private AudioClip backgroundMusic;

	// Program variables (Declared at the top, can be used by any method)
	private int time = 0;
	private int musicTime = 0;
	private int currentAttackFrame;
	private int currentEMPRadius;
	private int currentMapRow = 0;
	private int currentMapColumn = 0;
	private int currentPlayerDirection = 1;
	private int[][] mapGrid;
	private int[][] itemGrid;
	private int[][][][][] savedMap = new int[2][3][12][14][18];
	private int currentRow = 1;
	private int currentColumn = 1;
	private int xLocation;
	private int yLocation;
	private int maxHealth = 100;
	private int maxEnergy = 200;
	private int currentHealth = maxHealth;
	private int currentEnergy = maxEnergy;
	private int[][] inventory = new int[2][5];
	private int stealthStatus = 0;
	private int mapStatus = 0;
	private int moveStatus;
	private int noOfBolts = 0;
	private int endSceneFrame;

	// Program boolean variables
	private boolean[][] mapIsExplored = new boolean[MAP_NO.length][MAP_NO[0].length];
	private boolean[][] actionIsUsed = new boolean[MAP_NO.length][MAP_NO[0].length];
	private boolean isWalking;
	private boolean stealthIsOn;
	private boolean isAttacking;
	private boolean empActive;
	private boolean isTeleporting;
	private boolean miniMapOn;
	private boolean messageIsBeingDisplayed;
	private boolean gamePaused = true;
	private boolean storeIsOpen;
	private boolean animateEnding;
	private boolean gameOver;
	private boolean menuIsOpen = true;
	private boolean settingsIsOpen;
	private boolean helpIsOpen;
	private boolean musicIsOn = true;
	private boolean soundIsOn = true;
	private boolean endMessageIsDisplayed;
	// Program Timer variable
	private Timer timer;

	// Program Robot array
	private Robot[] robotList = new Robot[15];

	/**
	 * Resets the game, reseting any variables that may have changed over the
	 * course of the game. Called if the user clicks on the "New" option in the
	 * menu panel
	 */
	public void newGame()
	{
		// Resets any variables that may have changed from the start of the game
		time = 0;
		currentAttackFrame = 0;
		currentEMPRadius = 0;
		currentMapRow = 0;
		currentMapColumn = 0;
		currentPlayerDirection = 1;
		currentRow = 1;
		currentColumn = 1;
		maxHealth = 100;
		maxEnergy = 300;
		currentHealth = maxHealth;
		currentEnergy = maxEnergy;
		stealthStatus = 0;
		mapStatus = 0;
		moveStatus = 1;
		noOfBolts = 0;
		isWalking = false;
		stealthIsOn = false;
		isAttacking = false;
		empActive = false;
		isTeleporting = false;
		miniMapOn = false;
		messageIsBeingDisplayed = false;
		gamePaused = false;
		storeIsOpen = false;
		animateEnding = false;
		gameOver = false;
		endMessageIsDisplayed = false;

		// Resets the arrays that may have changed over the course of the game,
		// such as mapIsExplored and the inventory
		for (int row = 0; row < MAP_NO.length; row++)
		{
			for (int column = 0; column < MAP_NO[1].length; column++)
			{
				mapIsExplored[row][column] = false;
				actionIsUsed[row][column] = false;
			}
		}
		for (int row = 0; row < 2; row++)
		{
			for (int column = 0; column < 5; column++)
			{
				inventory[row][column] = 0;
			}
		}
		// Removes any robots from the robotList array
		for (int index = 0; index < 15; index++)
		{
			if (robotList[index] != null)
			{
				robotList[index] = null;
			}
		}
		// Sets up the map grid at the very beginning and repaints the display
		setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
				ITEM_MAP_NO[currentMapRow][currentMapColumn]);
		repaint();
	}

	/**
	 * Constructs a section of the map
	 */
	public MapMethods()
	{
		// Image files for the different directions that the player will face.
		// There are two sets of images, when the player is not in stealth and
		// when the player is in stealth. For each direction, there are two
		// additional images to animate walking
		// Most images were taken from Pokemon, and redrawn and recoloured
		playerImages[0][0][0] = new ImageIcon("Player[0][0][0].png").getImage();
		playerImages[0][0][1] = new ImageIcon("Player[0][0][1].png").getImage();
		playerImages[0][0][2] = new ImageIcon("Player[0][0][2].png").getImage();
		playerImages[0][1][0] = new ImageIcon("Player[0][1][0].png").getImage();
		playerImages[0][1][1] = new ImageIcon("Player[0][1][1].png").getImage();
		playerImages[0][1][2] = new ImageIcon("Player[0][1][2].png").getImage();
		playerImages[0][2][0] = new ImageIcon("Player[0][2][0].png").getImage();
		playerImages[0][2][1] = new ImageIcon("Player[0][2][1].png").getImage();
		playerImages[0][2][2] = new ImageIcon("Player[0][2][2].png").getImage();
		playerImages[0][3][0] = new ImageIcon("Player[0][3][0].png").getImage();
		playerImages[0][3][1] = new ImageIcon("Player[0][3][1].png").getImage();
		playerImages[0][3][2] = new ImageIcon("Player[0][3][2].png").getImage();
		playerImages[1][0][0] = new ImageIcon("Player[1][0][0].png")
				.getImage();
		playerImages[1][0][1] = new ImageIcon("Player[1][0][1].png")
				.getImage();
		playerImages[1][0][2] = new ImageIcon("Player[1][0][2].png")
				.getImage();
		playerImages[1][1][0] = new ImageIcon("Player[1][1][0].png")
				.getImage();
		playerImages[1][1][1] = new ImageIcon("Player[1][1][1].png")
				.getImage();
		playerImages[1][1][2] = new ImageIcon("Player[1][1][2].png")
				.getImage();
		playerImages[1][2][0] = new ImageIcon("Player[1][2][0].png")
				.getImage();
		playerImages[1][2][1] = new ImageIcon("Player[1][2][1].png")
				.getImage();
		playerImages[1][2][2] = new ImageIcon("Player[1][2][2].png")
				.getImage();
		playerImages[1][3][0] = new ImageIcon("Player[1][3][0].png")
				.getImage();
		playerImages[1][3][1] = new ImageIcon("Player[1][3][1].png")
				.getImage();
		playerImages[1][3][2] = new ImageIcon("Player[1][3][2].png")
				.getImage();

		// Images for the dismantling animation of the player
		attackImages[0][0] = new ImageIcon("AttackImage[0][0].png").getImage();
		attackImages[0][1] = new ImageIcon("AttackImage[0][1].png").getImage();
		attackImages[1][0] = new ImageIcon("AttackImage[1][0].png").getImage();
		attackImages[1][1] = new ImageIcon("AttackImage[1][1].png").getImage();
		attackImages[2][0] = new ImageIcon("AttackImage[2][0].png").getImage();
		attackImages[2][1] = new ImageIcon("AttackImage[2][1].png").getImage();
		attackImages[3][0] = new ImageIcon("AttackImage[3][0].png").getImage();
		attackImages[3][1] = new ImageIcon("AttackImage[3][1].png").getImage();

		// Images for the map
		mapImages[0][0] = new ImageIcon("GroundTutorial.png").getImage();
		mapImages[0][1] = mapImages[0][0];
		mapImages[0][2] = new ImageIcon("ButtonTutorial.png")
				.getImage();
		mapImages[0][3] = new ImageIcon("LoweredSpikesTutorial.png")
				.getImage();
		mapImages[0][4] = new ImageIcon("SpikesTutorial.png").getImage();
		mapImages[0][5] = new ImageIcon("WallTutorial.png").getImage();
		mapImages[0][6] = new ImageIcon("ImpassibleWallTutorial.png")
				.getImage();
		mapImages[0][7] = mapImages[0][0];

		mapImages[1][0] = new ImageIcon("Ground.png").getImage();
		mapImages[1][1] = mapImages[1][0];
		mapImages[1][2] = new ImageIcon("Button.png").getImage();
		mapImages[1][3] = new ImageIcon("LoweredSpikes.png").getImage();
		mapImages[1][4] = new ImageIcon("Spikes.png").getImage();
		mapImages[1][5] = new ImageIcon("Wall.png").getImage();
		mapImages[1][6] = new ImageIcon("ImpassibleWall.png")
				.getImage();
		mapImages[1][7] = mapImages[1][0];

		mapImages[2][0] = new ImageIcon("GroundEnd.png").getImage();
		mapImages[2][5] = new ImageIcon("WallEnd.png").getImage();

		// Image files for all of the different items that the player can pick
		// up
		itemImages[1] = new ImageIcon("Bolt.png").getImage();
		itemImages[2] = new ImageIcon("Key.png").getImage();
		itemImages[3] = new ImageIcon("HealthPack.png").getImage();
		itemImages[4] = new ImageIcon("EnergyPack.png").getImage();
		itemImages[5] = new ImageIcon("Block.png").getImage();

		// Icons for all the holdable items
		itemIconImages[1] = new ImageIcon("BoltIcon.png").getImage();
		itemIconImages[2] = new ImageIcon("KeyIcon.png").getImage();
		itemIconImages[3] = new ImageIcon("HealthPackIcon.png").getImage();
		itemIconImages[4] = new ImageIcon("EnergyPackIcon.png").getImage();
		itemIconImages[5] = new ImageIcon("BlockIcon.png").getImage();

		// Images for all of the messages that will be displayed
		messageImages[0][0] = new ImageIcon("TextBubble[0][0].png").getImage();
		messageImages[0][1] = new ImageIcon("TextBubble[0][1].png").getImage();
		messageImages[0][2] = new ImageIcon("TextBubble[0][2].png").getImage();
		messageImages[0][3] = new ImageIcon("TextBubble[0][3].png").getImage();
		messageImages[0][4] = new ImageIcon("TextBubble[0][4].png").getImage();
		messageImages[0][5] = new ImageIcon("TextBubble[0][5].png").getImage();
		messageImages[0][6] = new ImageIcon("TextBubble[0][6].png").getImage();

		// Image for the teleport animation
		flashImage = new ImageIcon("Flash.png").getImage();

		// Images for the user interface and menus
		healthImage = new ImageIcon("HealthUnit.png").getImage();
		energyImage = new ImageIcon("EnergyUnit.png").getImage();
		barOutlineImage = new ImageIcon("BarOutline.png").getImage();
		mapOutlineImage = new ImageIcon("MapOutline.png").getImage();
		playerIconImage = new ImageIcon("PlayerIcon.png").getImage();
		roomIconImage = new ImageIcon("RoomIcon.png").getImage();
		storeImage = new ImageIcon("StoreImage.png").getImage();
		menuImage = new ImageIcon("MenuImage.png").getImage();
		settingsImage = new ImageIcon("MenuSettings.png").getImage();
		helpImage = new ImageIcon("MenuHelp.png").getImage();
		gameOverImage = new ImageIcon("GameOverImage.png").getImage();
		offImage = new ImageIcon("Off.png").getImage();
		onImage = new ImageIcon("On.png").getImage();
		endMessageImage = new ImageIcon("EndMessage.png").getImage();

		// Sound files for all the in-game messages
		messages = new AudioClip[1][8];
		messages[0][0] = Applet
				.newAudioClip(getCompleteURL("message[0][0].wav"));
		messages[0][1] = Applet
				.newAudioClip(getCompleteURL("message[0][1].wav"));
		messages[0][2] = Applet
				.newAudioClip(getCompleteURL("message[0][2].wav"));
		messages[0][3] = Applet
				.newAudioClip(getCompleteURL("message[0][3].wav"));
		messages[0][4] = Applet
				.newAudioClip(getCompleteURL("message[0][4].wav"));
		messages[0][5] = Applet
				.newAudioClip(getCompleteURL("message[0][5].wav"));
		messages[0][6] = Applet
				.newAudioClip(getCompleteURL("message[0][6].wav"));
		messages[0][7] = Applet
				.newAudioClip(getCompleteURL("message[0][7].wav"));
		randomMessages = new AudioClip[5];
		randomMessages[0] = Applet
				.newAudioClip(getCompleteURL("randomMessage[0].wav"));
		randomMessages[1] = Applet
				.newAudioClip(getCompleteURL("randomMessage[1].wav"));
		randomMessages[2] = Applet
				.newAudioClip(getCompleteURL("randomMessage[2].wav"));
		randomMessages[3] = Applet
				.newAudioClip(getCompleteURL("randomMessage[3].wav"));
		randomMessages[4] = Applet
				.newAudioClip(getCompleteURL("randomMessage[4].wav"));

		// Creating the String arrays for the names of the text files, to be
		// read in as the map and items
		for (int row = 0; row < 3; row++)
		{
			for (int column = 0; column < 13; column++)
			{
				MAP_NO[row][column] = "map[" + row + "][" + column + "].txt";
				ITEM_MAP_NO[row][column] = "itemMap[" + row + "][" + column
						+ "].txt";
			}
		}

		// Loads up the first map
		setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
				ITEM_MAP_NO[currentMapRow][currentMapColumn]);

		// Makes the game window 1024 by 768 pixels in size
		Dimension size = new Dimension(1024, 768);

		this.setPreferredSize(size);
		this.setFocusable(true);
		this.addKeyListener(new KeyHandler());
		this.addMouseListener(new MouseHandler());
		this.requestFocusInWindow();

		// Sets up a timer that will trigger events every 10 milliseconds
		timer = new Timer(10, this);
		timer.setInitialDelay(100);
		timer.start();
	}

	/**
	 * Gets the URL of a file used to play audio messages
	 * @param fileName The name of the file to get the URL of
	 * @return The URL of the file
	 */
	public URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Paints the map section using the map images, specified by the numbers
	 * read in from the text file
	 * @param g The Graphics Context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Separates the tutorial area images from the rest of the images
		if (currentMapColumn == 12)
		{
			mapStatus = 2;
		}
		else if (currentMapColumn <= 5)
		{
			mapStatus = 0;
		}
		else
		{
			mapStatus = 1;
		}

		// Determines the stealth status based on the boolean stealthIsOn
		if (stealthIsOn)
		{
			stealthStatus = 1;
		}
		else
		{
			stealthStatus = 0;
		}

		// Draws the proper images as specified by the numbers in the mapGrid
		// and itemGrid array
		for (int row = 0; row < mapGrid.length; row++)
		{
			for (int column = 0; column < mapGrid[0].length; column++)
			{
				int mapImageNo = mapGrid[row][column];
				int itemImageNo = itemGrid[row][column];
				g.drawImage(mapImages[mapStatus][mapImageNo],
						(column - 1) * IMAGE_WIDTH,
						(row - 1) * IMAGE_WIDTH, this);
				if (itemImageNo != 0)
				{
					g.drawImage(itemImages[itemImageNo],
							(column - 1) * IMAGE_WIDTH,
							(row - 1) * IMAGE_WIDTH, this);
				}
			}
		}
		// Draws the end message if the player has won
		if (endMessageIsDisplayed)
		{
			g.drawImage(endMessageImage, 0, 0, this);
		}

		// Draws different images based on the player's current action
		if (isWalking)
		{
			g.drawImage(
					playerImages[stealthStatus][currentPlayerDirection][moveStatus],
					xLocation, yLocation, this);
		}
		else if (isTeleporting)
		{
			g.drawImage(flashImage,
					currentColumn * IMAGE_WIDTH,
					currentRow * IMAGE_WIDTH, this);
		}
		else if (isAttacking)
		{
			g.drawImage(
					attackImages[currentPlayerDirection][currentAttackFrame],
					currentColumn * IMAGE_WIDTH,
					currentRow * IMAGE_WIDTH, this);
		}
		else if (animateEnding)
		{
			g.drawImage(
					playerImages[0][endSceneFrame][0],
					xLocation, yLocation,
					this);
		}
		else
		{
			g.drawImage(playerImages[stealthStatus][currentPlayerDirection][0],
					currentColumn * IMAGE_WIDTH,
					currentRow * IMAGE_WIDTH, this);
		}

		// Runs through the array of robots in order to draw any robots that are
		// present in the map section
		int index = 0;
		while (index < 15)
		{
			if (robotList[index] != null)
				robotList[index].drawRobot(g);
			index++;
		}

		// Draws the EMP ability
		if (empActive)
		{
			Color empColor = new Color(72, 150, 237, 50);
			g.setColor(empColor);
			g.fillOval(currentColumn * IMAGE_WIDTH - currentEMPRadius + 32,
					currentRow * IMAGE_WIDTH - currentEMPRadius + 32,
					currentEMPRadius * 2, currentEMPRadius * 2);

		}
		// Draws the mini Map if it is enabled
		if (miniMapOn)
		{
			g.drawImage(mapOutlineImage, 0, 0, this);
			for (int roomRow = 0; roomRow < mapIsExplored.length; roomRow++)
			{
				for (int roomColumn = 0; roomColumn < mapIsExplored[0].length - 1; roomColumn++)
					if (mapIsExplored[roomRow][roomColumn])
					{
						g.drawImage(roomIconImage, roomColumn * 30 + 5,
								roomRow * 30 + 673, this);
						if (roomRow == currentMapRow
								&& roomColumn == currentMapColumn)
						{
							g.drawImage(playerIconImage, roomColumn * 30 + 5,
									roomRow * 30 + 673, this);
						}
					}
			}
		}

		// Draws the health and energy bar in the top left of the display
		for (int healthUnit = 0; healthUnit < (currentHealth * 100.0)
				/ maxHealth; healthUnit++)
		{
			g.drawImage(healthImage, healthUnit * 3 + 13, 7, this);
		}
		for (int energyUnit = 0; energyUnit < (currentEnergy * 100.0)
				/ maxEnergy; energyUnit++)
		{
			g.drawImage(energyImage, energyUnit * 3 + 13, 33, this);
		}

		// Draws the outline for the health and energy bar
		g.drawImage(barOutlineImage, 0, 0, this);

		// Draws the inventory bar
		g.setColor(Color.BLACK);
		for (int image = 0; image < 5; image++)
		{
			if (inventory[0][image] != 0)
			{
				g.drawImage(itemIconImages[inventory[0][image]],
						image * 43 + 407, 723, this);
				g.drawString(inventory[1][image] + "", image * 43 + 408, 734);
			}
		}

		// Draws numbers on the health and energy bar to indicate current health
		// and energy
		g.setFont(HEALTH_ENERGY_FONT);
		g.setColor(Color.WHITE);
		g.drawString(currentHealth + "/" + maxHealth, 180, 30);
		g.drawString(currentEnergy + "/" + maxEnergy, 261, 30);

		// Draws the image for the store if it is open
		if (storeIsOpen)
		{
			g.setFont(STORE_FONT);
			g.setColor(Color.BLACK);
			g.drawImage(storeImage, 0, 0, this);
			g.drawString("You Have " + noOfBolts + " bolts", 550, 160);
		}
		// Draws the message that is to be displayed
		if (messageIsBeingDisplayed)
		{
			g.setColor(Color.BLACK);
			g.drawImage(messageImages[currentMapRow][currentMapColumn], 0, 0,
					this);
		}

		// If the player's life is zero, a "Game Over" image is drawn
		if (gameOver)
		{
			g.drawImage(gameOverImage, 0, 0, this);
		}
		// If the menu is open, draws the menu
		if (menuIsOpen)
		{
			g.drawImage(menuImage, 0, 0, this);
		}
		// If help is open, draws the help screen
		if (helpIsOpen)
		{
			g.drawImage(helpImage, 0, 0, this);
		}
		// If the settings are open, draws the settings screen
		if (settingsIsOpen)
		{
			g.drawImage(settingsImage, 0, 0, this);
			if (!musicIsOn)
			{
				g.drawImage(offImage, 535, 313, this);
			}
			else
			{
				g.drawImage(onImage, 535, 313, this);
			}
			if (!soundIsOn)
			{
				g.drawImage(offImage, 98, 313, this);
			}
			else
			{
				g.drawImage(onImage, 98, 313, this);
			}
		}

	}

	/**
	 * Sets up a section of the map by reading in the map and item text files if
	 * the section has not yet been explored, or if the section has been
	 * explored, reads in the map section from other layers of the savedMap
	 * array. Robots are created regardless in order to save their last location
	 * @param mapFile the name of the text file that contains the map data for
	 *            the section of the map
	 * @param itemMapFile the name of the text file that contains the item data
	 *            for the section of the map
	 */
	public void setUpMapGrid(String mapFile, String itemMapFile)
	{
		// Reads in from a text file if the section of the map has not been
		// explored before
		if (!mapIsExplored[currentMapRow][currentMapColumn])
		{
			try
			{
				// Creates dimensions of the mapGrid array
				mapGrid = new int[14][18];

				// Reads in the text file with the map data
				BufferedReader mazeFile = new BufferedReader(new FileReader(
						mapFile));

				// Assigns the values in the text file to the mapGrid array,
				// leaving a border of 0s
				for (int row = 1; row < mapGrid.length - 1; row++)
				{
					String rowStr = mazeFile.readLine();
					for (int column = 1; column < mapGrid[0].length - 1; column++)
					{
						mapGrid[row][column] = rowStr.charAt(column - 1) - '0';
					}
				}
				mazeFile.close();
			}
			// Catches if the maze file does not exist
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, mapFile +
						" is not a valid maze file",
						"Message - Invalid Maze File",
						JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			}

			try
			{
				// Creates the dimension of the itemGird array
				itemGrid = new int[14][18];

				// Reads in the text file with the item data for the map section
				BufferedReader mazeFile = new BufferedReader(new FileReader(
						itemMapFile));
				for (int row = 1; row < itemGrid.length - 1; row++)
				{
					String rowStr = mazeFile.readLine();
					for (int column = 1; column < itemGrid[0].length - 1; column++)
					{
						itemGrid[row][column] = rowStr.charAt(column - 1) - '0';
					}
				}
				mazeFile.close();
			}
			// Catches if the item map file does not exist
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, itemMapFile +
						" is not a valid maze file",
						"Message - Invalid Maze File",
						JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			}
			mapIsExplored[currentMapRow][currentMapColumn] = true;
		}
		// If the section of the map is explored already, the map and item data
		// is read in from the 1st and 2nd layers of the savedMap array
		// respectively
		else
		{
			mapGrid = savedMap[0][currentMapRow][currentMapColumn];
			itemGrid = savedMap[1][currentMapRow][currentMapColumn];
		}
		// Runs through the mapGrid array, if there is an occurrence of the
		// number 7, a robot is created at the location, and the number is
		// reassigned to 0
		int index = 0;
		for (int row = 1; row < mapGrid.length - 1; row++)
		{
			for (int column = 1; column < mapGrid[0].length - 1; column++)
			{
				if (mapGrid[row][column] == 7)
				{
					robotList[index] = new Robot(
							row, column, mapGrid, 4);
					mapGrid[row][column] = 0;
					index++;
				}
			}
		}
	}

	/**
	 * A method that decreases the player's health when damaged by the Robot
	 */
	public void takeRobotDamage()
	{
		if (currentHealth > 10)
		{
			currentHealth -= 10;
		}
		else
			currentHealth = 0;
	}

	/**
	 * Saves the map, item, and Robot data of the map the player is leaving
	 */
	public void saveMap()
	{
		// Assigns a 7 to every location of a Robot in the mapGrid
		for (int index = 0; index < 15; index++)
		{
			if (robotList[index] != null)
			{
				mapGrid[robotList[index].getRow() + 1][robotList[index]
						.getColumn() + 1] = 7;
				robotList[index].removeRobot();
				robotList[index] = null;
			}
		}
		

		// Assigns the current mapGrid and itemGrid to the first and second
		// layers of the savedMap array respectively
		savedMap[0][currentMapRow][currentMapColumn] = mapGrid;
		savedMap[1][currentMapRow][currentMapColumn] = itemGrid;
	}

	/**
	 * A method to delay certain lines of code
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
	 * Called each time a timer event is generated
	 * @param e the Timer Event
	 */
	public void actionPerformed(ActionEvent e)
	{
		// The section of the method that affects the player only runs if the
		// game is not paused
		if (!gamePaused)
		{
			// Increases the time variable by 1 each time event (10
			// milliseconds)
			time++;
			// Every second the player gains 1 energy
			if (time % 100 == 0 && currentEnergy < maxEnergy)
			{
				currentEnergy++;
			}
			// Every 0.3 seconds, if the player is standing on raised spikes,
			// the
			// player takes 10 damage
			if (time % 30 == 0)
			{
				checkForSpikeDamage(10);
			}
			// Every 0.05 seconds, if the player is in stealth, 1 energy is
			// consumed
			if (time % 5 == 0 && stealthIsOn)
			{
				currentEnergy--;
				if (currentEnergy <= 0)
				{
					stealthIsOn = false;
				}
			}
			// Every 0.5 seconds, if there is a robot that can damage the
			// player,
			// the player takes damage
			if (time % 50 == 0)
			{
				for (int index = 0; index < robotList.length; index++)
				{
					if (robotList[index] != null
							&& robotList[index]
									.canDamage(currentRow, currentColumn))
					{
						takeRobotDamage();
					}
				}
			}
			if (currentHealth == 0)
			{
				gameOver = true;
				gamePaused = true;
			}
			// Repaints the map section
			repaint();
		}
	}

	/**
	 * Animates the player's movement
	 * @param dependancy Determines what variable to use to decide how the
	 *            player takes animated steps
	 */
	public void drawWalk(int dependancy)
	{
		if (dependancy % 2 == 0 || dependancy % 3 == 0 || dependancy % 5 == 0)
		{
			moveStatus = 1;
		}
		else
		{
			moveStatus = 2;
		}

		paintImmediately(xLocation, yLocation, IMAGE_WIDTH, IMAGE_WIDTH);

		delay(10);
	}

	/**
	 * Moves the player one tile to the left, animates the process so that the
	 * player does not seem to teleport
	 */
	public void walkLeft()
	{
		isWalking = true;
		for (double column = currentColumn; column > currentColumn - 1; column -= 0.15)
		{
			xLocation = (int) (column * IMAGE_WIDTH);
			yLocation = (currentRow) * IMAGE_WIDTH;

			drawWalk(xLocation);
		}
		isWalking = false;
	}

	/**
	 * Moves the player one tile over to the right, animates the process so that
	 * the player does not seem to teleport
	 */
	public void walkRight()
	{
		isWalking = true;
		for (double column = currentColumn; column < currentColumn + 1; column += 0.15)
		{
			xLocation = (int) (column * IMAGE_WIDTH);
			yLocation = (currentRow) * IMAGE_WIDTH;

			drawWalk(xLocation);
		}
		isWalking = false;
	}

	/**
	 * Moves the player one tile upwards, animates the process so that the
	 * player does not seem to teleport
	 */
	public void walkUp()
	{
		isWalking = true;
		for (double row = currentRow; row > currentRow - 1; row -= 0.15)
		{
			yLocation = (int) (row * IMAGE_WIDTH);
			xLocation = (currentColumn) * IMAGE_WIDTH;

			drawWalk(yLocation);
		}
		isWalking = false;
	}

	/**
	 * Moves the player one tile downwards, animates the process so that the
	 * player does not seem to teleport
	 */
	public void walkDown()
	{
		isWalking = true;
		for (double row = currentRow; row < currentRow + 1; row += 0.15)
		{
			yLocation = (int) (row * IMAGE_WIDTH);
			xLocation = (currentColumn) * IMAGE_WIDTH;

			drawWalk(yLocation);
		}
		isWalking = false;
	}

	/**
	 * Verifies whether a robot is at a specified location on the map grid
	 * @param row the row to be checked
	 * @param column the column to be checked
	 * @return the index of the robot that is found at the location, if there is
	 *         more than one robot, the index of the first found is returned. If
	 *         no robots are found at the specified location, -1 is returned.
	 */
	public int isRobotHere(int row, int column)
	{
		// Runs through the array of the robots, searching for a match to the
		// given row and column
		for (int index = 0; index < robotList.length; index++)
		{
			if (robotList[index] != null && robotList[index].getRow() == row
					&& robotList[index].getColumn() == column)
			{
				return index;
			}
		}
		return -1;
	}

	/**
	 * Animates the teleport ability
	 * @param delay
	 */
	public void animateTeleport(int delay)
	{
		isTeleporting = true;
		paintImmediately(currentColumn * IMAGE_WIDTH, currentRow
				* IMAGE_WIDTH, IMAGE_WIDTH, IMAGE_WIDTH);
		delay(delay);
		isTeleporting = false;
	}

	/**
	 * Uses or drops the items in the player's inventory
	 * @param inventorySlot
	 */
	public void dropItem(int inventorySlot)
	{
		// Drops an item if there is an item to drop
		if (inventory[1][inventorySlot] >= 1)
		{
			// Subtracts 1 from the number of bolts
			if (inventory[0][inventorySlot] == 1)
			{
				noOfBolts--;
			}
			// Heals the player if they use a health pack
			else if (inventory[0][inventorySlot] == 3
					&& currentHealth < maxHealth)
			{
				if (currentHealth < maxHealth - 50)
				{
					currentHealth += 50;
				}
				else
				{
					currentHealth = maxHealth;
				}
				inventory[1][inventorySlot]--;

			}
			// Restores energy to the player if they use an energy pack
			else if (inventory[0][inventorySlot] == 4
					&& currentEnergy < maxEnergy)
			{
				if (currentEnergy < maxEnergy - 50)
				{
					currentEnergy += 50;
				}
				else
				{
					currentEnergy = maxEnergy;
				}
				inventory[1][inventorySlot]--;
			}
			// Drops the item
			else if (itemGrid[currentRow + 1][currentColumn + 1] == 0
					&& inventory[0][inventorySlot] != 3
					&& inventory[0][inventorySlot] != 4)
			{
				// Subtracts the item from the inventory
				itemGrid[currentRow + 1][currentColumn + 1] = inventory[0][inventorySlot];
				inventory[1][inventorySlot]--;

				// Retracts spikes on the map if the player places a block on a
				// pressure plate
				if (inventory[0][inventorySlot] == 5
						&& mapGrid[currentRow + 1][currentColumn + 1] == 2)
				{
					for (int row = 1; row < mapGrid.length - 1; row++)
					{
						for (int column = 1; column < mapGrid[0].length; column++)
						{
							if (mapGrid[row][column] == 4)
							{
								mapGrid[row][column] = 3;
							}
						}
					}
				}
			}
			// Sets the inventory slot to empty if there is no more of that item
			// in the appropriate slot
			if (inventory[1][inventorySlot] == 0)
			{
				inventory[0][inventorySlot] = 0;
			}
			repaint();
		}
	}

	/**
	 * Allows the player to pick up items, which will appear and organize
	 * themselves in the player's inventory
	 */
	public void pickUpItem()
	{
		// Find out what item is underneath the player
		int itemToAdd = itemGrid[currentRow + 1][currentColumn + 1];
		// Ends the game if the player picks up the key

		if (itemToAdd == 1)
		{
			noOfBolts++;
		}
		else if (itemToAdd == 2)
		{
			// Animates the end of the game
			animateEnding = true;
			for (int spin = 0; spin < 10; spin++)
			{
				for (int frame = 0; frame < 3; frame++)
				{
					endSceneFrame = frame;
					paintImmediately(currentColumn * IMAGE_WIDTH,
							currentRow
									* IMAGE_WIDTH, IMAGE_WIDTH, IMAGE_WIDTH);
					delay(28);
				}
			}
			animateEnding = false;

			saveMap();
			currentMapColumn = 12;
			currentMapRow = 0;
			mapStatus = 2;
			setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
					ITEM_MAP_NO[currentMapRow][currentMapColumn]);
			endMessageIsDisplayed = true;
			return;
		}
		// Searches for the inventory slot to add the item to. If there are no
		// matches to the item, an empty slot is found. If there is a match, the
		// slot of the match is found
		int inventorySpotToAdd = 0;
		while (inventory[0][inventorySpotToAdd] != 0
				&& inventory[0][inventorySpotToAdd] != itemToAdd)
		{
			inventorySpotToAdd++;
		}
		// Adds the item to the specified slot, and adds one to the quantity of
		// the item
		inventory[0][inventorySpotToAdd] = itemToAdd;
		inventory[1][inventorySpotToAdd]++;
		// If a block is removed from the top of a button, any lowered spikes
		// are raised
		if (itemGrid[currentRow + 1][currentColumn + 1] == 5
				&& mapGrid[currentRow + 1][currentColumn + 1] == 2)
		{
			for (int row = 1; row < mapGrid.length - 1; row++)
			{
				for (int column = 1; column < mapGrid[0].length; column++)
				{
					if (mapGrid[row][column] == 3)
					{
						mapGrid[row][column] = 4;
					}
				}
			}
		}
		// Reassigns 0 at the location of the item on the itemGrid
		itemGrid[currentRow + 1][currentColumn + 1] = 0;
	}

	/**
	 * Gets the index of the bolts in the player's inventory
	 * @return the index of bolts in the player's inventory
	 */
	public int getIndexOfBolts()
	{
		for (int index = 0; index < 5; index++)
		{
			if (inventory[0][index] == 1)
			{
				return index;
			}
		}
		return -1;
	}

	/**
	 * Checks if the player is currently on top of any spikes, if true, the
	 * player takes damage
	 * @param damage the amount of damage to take
	 */
	public void checkForSpikeDamage(int damage)
	{
		if (mapGrid[currentRow + 1][currentColumn + 1] == 4)
		{
			if (currentHealth >= damage)
			{
				currentHealth -= damage;
			}
			else
			{
				currentHealth = 0;
			}
		}
	}

	/**
	 * Checks if there are robots on the same block as the player, or on block
	 * in front of the player in the 4 directions. If there are robots present,
	 * damages the robot
	 */
	public void attack()
	{
		// First checks if there is a robot in the same tile as the player
		if (isRobotHere(currentRow, currentColumn) >= 0)
		{
			robotList[isRobotHere(currentRow, currentColumn)].takeDamage();
			// If the robot has run out of health, the robot is removed from the
			// robot array and disappears from the screen
			if (!robotList[isRobotHere(currentRow, currentColumn)]
					.isActivated())
			{
				robotList[isRobotHere(currentRow, currentColumn)] = null;
			}
		}
		// Checks if there are robots in front of the player
		else
		{
			// Checks to the left of the player, follows a similar process to
			// damage the robot and remove it if necessary
			if (currentPlayerDirection == 0
					&& isRobotHere(currentRow, currentColumn - 1) >= 0)
			{
				robotList[isRobotHere(currentRow, currentColumn - 1)]
						.takeDamage();
				if (!robotList[isRobotHere(currentRow, currentColumn - 1)]
						.isActivated())
				{
					robotList[isRobotHere(currentRow, currentColumn - 1)] = null;
				}
			}
			// Checks to the right of the player, follows the same process for
			// the robot
			else if (currentPlayerDirection == 1
					&& isRobotHere(currentRow, currentColumn + 1) >= 0)
			{
				robotList[isRobotHere(currentRow, currentColumn + 1)]
						.takeDamage();
				if (!robotList[isRobotHere(currentRow, currentColumn + 1)]
						.isActivated())
				{
					robotList[isRobotHere(currentRow, currentColumn + 1)] = null;
				}
			}
			// Checks upwards from the player, follows the same process for the
			// robot
			else if (currentPlayerDirection == 2
					&& isRobotHere(currentRow - 1, currentColumn) >= 0)
			{
				robotList[isRobotHere(currentRow - 1, currentColumn)]
						.takeDamage();
				if (!robotList[isRobotHere(currentRow - 1, currentColumn)]
						.isActivated())
				{
					robotList[isRobotHere(currentRow - 1, currentColumn)] = null;
				}
			}
			// Checks downwards from the player, follows a similar process for
			// the robot
			else if (currentPlayerDirection == 3
					&& isRobotHere(currentRow + 1, currentColumn) >= 0)
			{
				robotList[isRobotHere(currentRow + 1, currentColumn)]
						.takeDamage();
				if (!robotList[isRobotHere(currentRow + 1, currentColumn)]
						.isActivated())
				{
					robotList[isRobotHere(currentRow + 1, currentColumn)] = null;
				}
			}
		}

	}

	/**
	 * A method to deal with user input from the keyboard
	 * @author Felix Sung and Jonathan Sun
	 * @version January 16, 2015
	 */
	private class KeyHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent event)
		{
			// Closes the message dialogue box if the player presses N
			if (event.getKeyCode() == KeyEvent.VK_N)
			{
				if (messageIsBeingDisplayed)
				{
					gamePaused = false;
					messageIsBeingDisplayed = false;
					paintImmediately(0, 0, 1024, 768);
				}
			}
			// Game only accepts button presses if the game is not paused
			if (!gamePaused)
			{
				// If the player presses 'Q', the player is attacking, and the
				// attacking animation is called. The attack method is also
				// called
				// to check if any robots can be damaged
				if (event.getKeyCode() == KeyEvent.VK_Q && !stealthIsOn)
				{
					isAttacking = true;
					for (int frame = 0; frame < 2; frame++)
					{
						currentAttackFrame = frame;
						paintImmediately(currentColumn * IMAGE_WIDTH,
								currentRow
										* IMAGE_WIDTH, IMAGE_WIDTH, IMAGE_WIDTH);
						delay(75);
					}
					attack();
					delay(50);

					isAttacking = false;
				}
				// If the player presses 'W', the player enters stealth mode
				else if (event.getKeyCode() == KeyEvent.VK_W)
				{
					if (stealthIsOn)
					{
						stealthIsOn = false;
					}
					else
					{
						stealthIsOn = true;
					}
				}
				// If the player presses 'E', and has more than 25 energy, the
				// player can teleport over one block, with some restrictions
				else if (event.getKeyCode() == KeyEvent.VK_E
						&& currentEnergy >= 25)
				{
					// Takes into account the four different directions that the
					// player can face
					if (currentPlayerDirection == 0
							&& currentColumn > 1
							&& mapGrid[currentRow + 1][currentColumn - 2 + 1] < 5
							&& mapGrid[currentRow + 1][currentColumn - 1 + 1] != 6)
					{
						animateTeleport(50);
						currentColumn -= 2;
						animateTeleport(100);
						currentEnergy -= 25;
						checkForSpikeDamage(20);
					}
					else if (currentPlayerDirection == 1
							&& currentColumn < mapGrid[0].length - 4
							&& mapGrid[currentRow + 1][currentColumn + 2 + 1] < 5
							&& mapGrid[currentRow + 1][currentColumn + 1 + 1] != 6)
					{
						animateTeleport(50);
						currentColumn += 2;
						animateTeleport(100);
						currentEnergy -= 25;
						checkForSpikeDamage(20);
					}
					else if (currentPlayerDirection == 2
							&& currentRow > 1
							&& mapGrid[currentRow - 2 + 1][currentColumn + 1] < 5
							&& mapGrid[currentRow - 1 + 1][currentColumn + 1] != 6)
					{
						animateTeleport(50);
						currentRow -= 2;
						animateTeleport(100);
						currentEnergy -= 25;
						checkForSpikeDamage(20);
					}
					else if (currentPlayerDirection == 3
							&& currentRow < mapGrid.length - 4
							&& mapGrid[currentRow + 2 + 1][currentColumn + 1] < 5
							&& mapGrid[currentRow + 1 + 1][currentColumn + 1] != 6)
					{
						animateTeleport(50);
						currentRow += 2;
						animateTeleport(100);
						currentEnergy -= 25;
						checkForSpikeDamage(20);
					}
				}
				// If the player presses 'R', the EMP ability is used, which
				// uses
				// 100 energy and disables all robots in the current map section
				else if (event.getKeyCode() == KeyEvent.VK_R
						&& currentEnergy >= 100)
				{
					currentEnergy -= 100;
					empActive = true;
					for (int radius = 1; radius < 1150; radius += 3)
					{
						currentEMPRadius = radius;
						paintImmediately(0, 0, 1024, 768);
						delay(1);
					}
					// Removes all robots present in the current map section
					for (int index = 0; index < robotList.length; index++)
					{
						robotList[index] = null;
					}
					delay(50);
					empActive = false;

				}
				// If the player presses Left Arrow, moves the player one space
				// to
				// the left with restrictions
				else if (event.getKeyCode() == KeyEvent.VK_LEFT)
				{
					currentPlayerDirection = 0;
					if (mapGrid[currentRow + 1][currentColumn - 1 + 1] < 5)
					{
						walkLeft();
						currentColumn--;
						checkForSpikeDamage(20);
					}
					// Checks if the player moves within range of any robots
					for (int index = 0; index < robotList.length; index++)
					{
						// If the player is within range, the robot begins to
						// track
						// the player
						if (robotList[index] != null
								&& robotList[index].inRange(currentRow,
										currentColumn, stealthIsOn))
						{

							robotList[index].goToPlayerLocation(currentRow,
									currentColumn);
						}
					}
				}
				// Follows a similar move process when Right Arrow is pressed
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					currentPlayerDirection = 1;
					if (mapGrid[currentRow + 1][currentColumn + 1 + 1] < 5)
					{
						walkRight();
						currentColumn++;
						checkForSpikeDamage(20);
					}
					for (int index = 0; index < robotList.length; index++)
					{
						if (robotList[index] != null
								&& robotList[index].inRange(currentRow,
										currentColumn, stealthIsOn))
						{
							robotList[index].goToPlayerLocation(currentRow,
									currentColumn);
						}
					}
				}
				// Follows a similar process when the Up Arrow is pressed
				else if (event.getKeyCode() == KeyEvent.VK_UP)
				{
					currentPlayerDirection = 2;
					if (mapGrid[currentRow - 1 + 1][currentColumn + 1] < 5)
					{
						walkUp();
						currentRow--;
						checkForSpikeDamage(20);
					}
					for (int index = 0; index < robotList.length; index++)
					{
						if (robotList[index] != null
								&& robotList[index].inRange(currentRow,
										currentColumn, stealthIsOn))
						{
							robotList[index].goToPlayerLocation(currentRow,
									currentColumn);
						}
					}
				}
				// Follows a similar process when the Down Arrow is pressed
				else if (event.getKeyCode() == KeyEvent.VK_DOWN)
				{
					currentPlayerDirection = 3;
					if (mapGrid[currentRow + 1 + 1][currentColumn + 1] < 5)
					{
						walkDown();
						currentRow++;
						checkForSpikeDamage(20);
					}
					for (int index = 0; index < robotList.length; index++)
					{
						if (robotList[index] != null
								&& robotList[index].inRange(currentRow,
										currentColumn, stealthIsOn))
						{
							robotList[index].goToPlayerLocation(currentRow,
									currentColumn);
						}
					}
				}
				// Plays the necessary audio files for the tutorial, as well as
				// random messages when the player is within the actual level
				if (mapGrid[currentRow + 1][currentColumn + 1] == 1
						&& actionIsUsed[currentMapRow][currentMapColumn] == false)
				{
					if (currentMapColumn <= 7 && currentMapRow == 0)
					{
						if (currentMapColumn == 5)
						{
							for (int row = 2; row < 12; row++)
							{
								mapGrid[row][15] = 5;
							}
							mapGrid[6][1] = 5;
							mapGrid[7][1] = 5;
						}
						if (soundIsOn)
						{
							messages[currentMapRow][currentMapColumn].play();
						}
						gamePaused = true;
						messageIsBeingDisplayed = true;
						paintImmediately(0, 0, 1024, 768);
					}
					else if (soundIsOn)
					{
						randomMessages[(int) (Math.random() * 5)].play();
					}
					actionIsUsed[currentMapRow][currentMapColumn] = true;
				}
				// If there is an item underneath the player, the player picks
				// it
				// up, unless it is a movable block. Movable blocks can be
				// picked up
				// by pressing 'SPACE'
				if (itemGrid[currentRow + 1][currentColumn + 1] >= 1)
				{
					if (itemGrid[currentRow + 1][currentColumn + 1] != 5
							|| event.getKeyCode() == KeyEvent.VK_SPACE)
					{
						pickUpItem();
					}
				}
				// If the player presses '1', the item in the first slot of the
				// inventory is used or dropped
				if (event.getKeyCode() == KeyEvent.VK_1)
				{
					dropItem(0);
				}
				// Follows a similar process for the second inventory slot
				else if (event.getKeyCode() == KeyEvent.VK_2)
				{
					dropItem(1);
				}
				// Follows a similar process for the third inventory slot
				else if (event.getKeyCode() == KeyEvent.VK_3)
				{
					dropItem(2);
				}
				// Follows a similar process for the fourth inventory slot
				else if (event.getKeyCode() == KeyEvent.VK_4)
				{
					dropItem(3);
				}
				// Follows a similar process for the fifth inventory slot
				else if (event.getKeyCode() == KeyEvent.VK_5)
				{
					dropItem(4);
				}
				// Toggles if the mini-Map is to be displayed when the player
				// presses M
				else if (event.getKeyCode() == KeyEvent.VK_M)
				{
					if (miniMapOn)
					{
						miniMapOn = false;
					}
					else
					{
						miniMapOn = true;
					}
				}
				// If the player has reached the far right of the map section,
				// saves
				// the
				// current map, item, and robot data, and loads in the map,
				// item,
				// and robot data for next corresponding section of the map
				if (currentColumn == 16)
				{
					saveMap();
					currentMapColumn++;
					currentColumn = 0;
					setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
							ITEM_MAP_NO[currentMapRow][currentMapColumn]);
				}
				// Follows a similar process when the player has reached the far
				// left of the map section
				else if (currentColumn < 0)
				{
					saveMap();
					currentMapColumn--;
					currentColumn = 15;
					setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
							ITEM_MAP_NO[currentMapRow][currentMapColumn]);
				}
				// Follows a similar process when the player has reached the top
				// of
				// the map section
				else if (currentRow < 0)
				{
					saveMap();
					currentMapRow--;
					currentRow = 11;
					setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
							ITEM_MAP_NO[currentMapRow][currentMapColumn]);
				}
				// Follows a similar process when the player has reached the
				// bottum
				// of the map section
				else if (currentRow == 12)
				{
					saveMap();
					currentMapRow++;
					currentRow = 0;
					setUpMapGrid(MAP_NO[currentMapRow][currentMapColumn],
							ITEM_MAP_NO[currentMapRow][currentMapColumn]);
				}
			}
			if (event.getKeyCode() == KeyEvent.VK_SHIFT)
			{
				if (storeIsOpen)
				{
					storeIsOpen = false;
					gamePaused = false;
				}
				else
				{
					storeIsOpen = true;
					gamePaused = true;
				}
			}
			// Repaints the map section
			repaint();

		}
	}
	/**
	 * Responds to mouse events
	 * @author Felix Sung and Jonathan Sun
	 * @version January 21, 2015
	 */
	private class MouseHandler extends MouseAdapter
	{
		/**
		 * Responds to a mousePressed event
		 * @param event Information about the mouse pressed event.
		 */
		public void mousePressed(MouseEvent event)
		{
			// Convert mouse-pressed location to a point
			Point pressedPoint = event.getPoint();

			// Functions for if the store is open
			if (storeIsOpen)
			{

				// If the user clicks on the increase health button, increases
				// player's maximum health by 10
				// Maximum health is capped at 300
				if (pressedPoint.x > 154 && pressedPoint.x < 318
						&& pressedPoint.y > 250
						&& pressedPoint.y < 414 && noOfBolts >= 10
						&& maxHealth < 300)
				{
					maxHealth += 10;
					currentHealth += 10;
					noOfBolts -= 10;
					inventory[1][getIndexOfBolts()] -= 10;

				}
				// If the user clicks on the increase energy button, increases
				// player's maximum energy by 25
				// Maximum energy is capped at 300
				else if (pressedPoint.x > 154 && pressedPoint.x < 318
						&& pressedPoint.y > 478
						&& pressedPoint.y < 642 && noOfBolts >= 10
						&& maxEnergy < 300)
				{
					maxEnergy += 25;
					currentEnergy += 25;
					noOfBolts -= 10;
					inventory[1][getIndexOfBolts()] -= 10;
				}
			}
			// Menu buttons become active when the player is on the menu screen
			if (menuIsOpen && !settingsIsOpen && !helpIsOpen)
			{
				// Button to start the game
				if (pressedPoint.x > 433 && pressedPoint.x < 590
						&& pressedPoint.y > 355
						&& pressedPoint.y < 415)
				{
					gamePaused = false;
					menuIsOpen = false;
				}
				// Button to open the settings
				else if (pressedPoint.x > 595 && pressedPoint.x < 751
						&& pressedPoint.y > 482
						&& pressedPoint.y < 543)
				{
					settingsIsOpen = true;
				}
				// Button to open the help menu
				else if (pressedPoint.x > 268 && pressedPoint.x < 415
						&& pressedPoint.y > 482
						&& pressedPoint.y < 543)
				{
					helpIsOpen = true;
				}
			}
			// Help buttons become active when the player is on the help screen
			if (helpIsOpen)
			{
				// Buttons to close the help
				if (pressedPoint.x > 473 && pressedPoint.x < 552
						&& pressedPoint.y > 12
						&& pressedPoint.y < 71)
				{
					helpIsOpen = false;
				}
			}
			// Settings buttons become active when the player is on the settings
			// screen
			if (settingsIsOpen)
			{
				// Button to close the settings
				if (pressedPoint.x > 473 && pressedPoint.x < 552
						&& pressedPoint.y > 197
						&& pressedPoint.y < 270)
				{
					settingsIsOpen = false;
				}
				// Button to toggle the sound
				else if (pressedPoint.x > 98 && pressedPoint.x < 500
						&& pressedPoint.y > 312
						&& pressedPoint.y < 424)
				{
					if (soundIsOn)
					{
						soundIsOn = false;
					}
					else
					{
						soundIsOn = true;
					}
				}
				// Button to toggle the music
				else if (pressedPoint.x > 535 && pressedPoint.x < 938
						&& pressedPoint.y > 312
						&& pressedPoint.y < 424)
				{
					if (musicIsOn)
					{
						musicIsOn = false;
						backgroundMusic.stop();
					}
					else
					{
						musicIsOn = true;
						backgroundMusic.play();
						musicTime = 0;
					}
				}
			}
			repaint();
		}
	}
}