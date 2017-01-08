/** The Main JFrame program for a simple Maze Game
 * @author G. Ridout
 * @version last updated December 2014
 */
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Final project : The main program that sets up the menu bar as well as runs
 * the game
 * @author Jonathan Sun and Felix Sung
 * @version January 21, 2015
 */
public class MainGame extends JFrame implements ActionListener
{
	MapMethods mapMethods;
	private JMenuItem exitOption, rulesMenuItem, aboutMenuItem, newOption;

	/**
	 * Runs the game and creates the menu bar as well as reacts to buttons being
	 * pressed
	 */
	public MainGame()
	{
		// Set up the frame and the grid
		super("Stealthy");
		setLocation(0, 0);
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("GameIcon.png"));

		// Set up for the maze area
		mapMethods = new MapMethods();
		add(mapMethods, BorderLayout.CENTER);

		exitOption = new JMenuItem("Exit");
		exitOption.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		exitOption.addActionListener(this);

		newOption = new JMenuItem("New");
		newOption.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newOption.addActionListener(this);

		// Set up the Help Menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		rulesMenuItem = new JMenuItem("Rules...", 'R');
		rulesMenuItem.addActionListener(this);
		helpMenu.add(rulesMenuItem);
		aboutMenuItem = new JMenuItem("About...", 'A');
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);

		JMenu gameMenu = new JMenu("Game");
		gameMenu.add(exitOption);
		gameMenu.addSeparator();
		gameMenu.add(newOption);
		JMenuBar mainMenu = new JMenuBar();
		mainMenu.add(gameMenu);
		mainMenu.add(helpMenu);
		setJMenuBar(mainMenu);
	}

	/**
	 * Reacts to when the menu buttons are clicked
	 */
	public void actionPerformed(ActionEvent event)
	{

		if (event.getSource() == exitOption)
		{
			this.setVisible(false);
			System.exit(0);
		}
		else if (event.getSource() == newOption)
		{
			mapMethods.newGame();
		}
		else if (event.getSource() == rulesMenuItem)
		{
			JOptionPane
					.showMessageDialog(
							this,
							"Move using the arrowkeys "
									+ "\nQ=Attack" + "\nW=Stealth"
									+ "\nE=Teleport" + "\nR=EMP"
									+ "\nM=Toggle Mini-Map"
									+ "\nN=Close Dialogue Box"
									+ "\nShift=Open Workshop"
									+ "\n\n1-5=Drop/Use Item"
									+ "\nSpace=Pick Up Blocks"
									+ "\n\nFind the key to escape. Good luck!",
							"Rules",
							JOptionPane.INFORMATION_MESSAGE);
		}
		else if (event.getSource() == aboutMenuItem) // Selected "About"
		{
			JOptionPane.showMessageDialog(this,
					"By Jonathan Sun & Felix Sung" +
							"\n\u00a9 2015", "About Stealthy",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Sets up the main frame for the Game
	public static void main(String[] args)
	{
		MainGame frame = new MainGame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	} // main method
} // MazeGame class

