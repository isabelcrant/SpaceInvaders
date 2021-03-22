import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/*
Isabel Crant
SpaceInvaders
Fixed shooter game where the player tries to shoot all of the aliens before they get to the bottom of the screen.
- Base Shelters temporarily protect the player from the alien's lasers
- Mystery Ships give you a random amount of points
*/
public class SpaceInvaders extends JFrame {
    SpaceInvadersPanel game;

    public SpaceInvaders() {
        super("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        game = new SpaceInvadersPanel();
        add(game);
        pack();
        setVisible(true);
        //setResizable(false);
    }

    public static void main(String[] args) {
        SpaceInvaders frame = new SpaceInvaders();
    }
}

class SpaceInvadersPanel extends JPanel implements MouseListener, ActionListener, KeyListener {
    static LaserCannon lasercannon;
    static ArrayList<LaserCannon> minilasercannons;

    static ArrayList<Laser> lasers;
    static ArrayList<Laser> removedLasers;                                        // player's lasers that need to be removed

    static ArrayList<Laser> alienLasers;
    static ArrayList<Laser> removedAlienLasers;                                   // alien lasers that need to be removed

    static ArrayList<BaseShelter> baseshelters;
    static ArrayList<BaseShelter> removedBaseShelters;                            // !!!!!!!!!!

    static ArrayList<Alien> aliens;
    static ArrayList<Alien> removedAliens;                                        // aliens that need to be removed

    public static int score;                                                      // final score
    public static int level;                                                      // the level you are on (in this game there are 2 levels)

    private static boolean[] keys;
    private static Random rand = new Random();

    public static boolean laserCannonHit;                                         // says if the laser cannon is hit by a laser

    private static MysteryShip mysteryship;
    private static boolean canShowMysteryShip;                                    // says if the mystery ship pops up

    // screens
    public static String screen = "start";
    private Image levelTwoScreen;
    private Image startScreen;
    private Image gameOverScreen;
    private Image winScreen;

    Timer myTimer;

    public static final int WIDTH = 1000, HEIGHT = 750;                           // width and height of panel

    // BufferedImage loader - used to load BufferedImages:
    public static BufferedImage loadBuffImg(String n) {
        try {
            return ImageIO.read(new File("Images/" + n));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    public SpaceInvadersPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addMouseListener(this);
		addKeyListener(this);

        startScreen = new ImageIcon("Images/startscreen.png").getImage();
        gameOverScreen = new ImageIcon("Images/gameover.png").getImage();
        winScreen = new ImageIcon("Images/winscreen.png").getImage();
        levelTwoScreen = new ImageIcon("Images/level2.png").getImage();

        score = 0;                                                                 // score always starts off at 0           
        level = 1;                                                                 // player always starts off at Level 1
        initialize(level);

        myTimer = new Timer(100, this);

        setFocusable(true);
		requestFocus();
    }

    // initializes objects and variables based on the Level
    // Level 1 starts with normally coloured aliens that start slower and higher up on the screen
    // Level 2 starts with multicoloured aliens that start faster and lower on the screen
    public static void initialize(int level) {
        laserCannonHit = false;                                                    // laser cannon cannot be hit at the start of the Level

        keys = new boolean[KeyEvent.KEY_LAST+1];

        lasers = new ArrayList<Laser>();
        removedLasers = new ArrayList<Laser>();

        alienLasers = new ArrayList<Laser>();
        removedAlienLasers = new ArrayList<Laser>();

        // adds 4 equally spaced Base Shelters:
        baseshelters = new ArrayList<BaseShelter>();
        for(int i=1; i <= 4; i++) {
            baseshelters.add(new BaseShelter(i*WIDTH/4 - BaseShelter.WIDTH - 90, HEIGHT - BaseShelter.HEIGHT - 100));
        }
        removedBaseShelters = new ArrayList<BaseShelter>();

        aliens = new ArrayList<Alien>();
        removedAliens = new ArrayList<Alien>();

        mysteryship = new MysteryShip(1, 1);                                       // the mystery ship always starts off at the top left corner
        canShowMysteryShip = false;                                                // the mystery ship cannot appear at the start of the Level

        generateAliens(level);                                                     // generates aliens based on the Level

        int[] k = {KeyEvent.VK_A, KeyEvent.VK_D};                                  // important keys for the lasercannon - D moves it right, A moves it left

        lasercannon = new LaserCannon(k);

        minilasercannons = new ArrayList<LaserCannon>();                           // mini laser cannons that show up of the bottom left corner of the screen, showing how many laser cannons you have left
        for(int i=0; i<3; i++) {
            minilasercannons.add(new LaserCannon(20 + i*35, HEIGHT - 20));
        }
    }

    // generates aliens based on the Level:
    public static void generateAliens(int level) {
        // adds aliens:
        String[] picNames;
        if(level == 1) {                                                           // the aliens are normally coloured in Level 1
            picNames = new String[] {"largealien.png", "mediumalien.png", "mediumalien.png", "smallalien.png", "smallalien.png"};
        } else {                                                                   // the aliens are multicoloured in Level 2
            picNames = new String[] {"rainbowlargealien.png", "rainbowmediumalien.png", "rainbowmediumalien.png", "rainbowsmallalien.png", "rainbowsmallalien.png"};
        }

        int[] pointValues = {30, 20, 20, 10, 10};                                  // the point values of the aliens. Large aliens are worth 30 points, medium aliens are worth 20 points, and small aliens are worth 10 points

        // Adds 5 rows of 11 equally spaced aliens:
        for(int i=1; i<=5; i++) {
            for(int j=2; j<=12; j++) {
                aliens.add(new Alien(j*WIDTH/13 - Alien.WIDTH - 40, Alien.HEIGHT + 100 + 50*(i-1+(level-1)), picNames[i-1], pointValues[i-1], i, j-1));
            }
        }

    }

    // Main Game Loop
    @Override
	public void actionPerformed(ActionEvent e) {
        if(screen == "game") move();
        resetObjects();
        generateMysteryShip();
        if(laserCannonHit) {
            repaint();
            pause();
            laserCannonHit = false;
        }
		repaint();
	}

    // moves the objects
    public void move() {
        boolean onEdge = false;                                                           // flag for if an alien is on the edge

        lasercannon.move(keys);
        lasercannon.hitByLaser();

        // checks if a baseshelter was hit by a laser:
        for(BaseShelter baseshelter : baseshelters) {
            baseshelter.hitByLaser(lasers);
            baseshelter.hitByLaser(alienLasers);
        }

        for(Laser laser : lasers) {
            laser.move();
        }
        for(Laser laser : alienLasers) {
            laser.move();
        }

        if(canShowMysteryShip) {                                                          // if the mystery ship can appear
            mysteryship.move();
        }

        for(Alien alien : aliens) {
            if(!onEdge && (alien.getX() <= 0 || alien.getX() + Alien.WIDTH >= WIDTH)) {   // if an alien is on the edge of the screen
                onEdge = true;
                Alien.changeDirection();                                                  // changes the direction of the aliens
                break;
            }
        }
        for(Alien alien : aliens) {
            alien.move(onEdge);                                                           // moves the aliens
            alien.hitByLaser();                                                           // tests if the aliens were hit by lasers
        }
        onEdge = false;
        aliensShoot();                                                                    // picks a random alien to shoot a laser
    }

    // method for when an alien hits the edge
    public static void hitEdge() {
        Alien.dir *= -1;                                                                  // changes the direction of the aliens
        for(Alien alien: aliens) {
            alien.setY(alien.getY()+5);                                                   // bumps each alien down a line
        }
    }

    public static int getLevel() {
        return level;
    }

    public static void removeLaser(Laser laser) {
        removedLasers.add(laser);
    }
    public static void removeAlienLaser(Laser laser) {
        removedAlienLasers.add(laser);
    }
    public static void removeAlien(Alien alien) {
        removedAliens.add(alien);
    }
    public static void resetLasers() {
        lasers.removeAll(removedLasers);
        removedLasers.clear();
    }
    public static void resetAlienLasers() {
        alienLasers.removeAll(removedAlienLasers);
        removedAlienLasers.clear();
    }
    public static void resetAliens() {
        for(Alien alien: removedAliens) {
            aliens.remove(alien);
        }
        removedAliens.clear();
    }
    public static void removeBaseShelter(BaseShelter baseshelter) {
        removedBaseShelters.add(baseshelter);
    }
    public static void resetBaseShelters() {
        baseshelters.removeAll(removedBaseShelters);
        removedBaseShelters.clear();
    }

    // resets the objects: the player's lasers, the alien's lasers, the aliens, and the baseshelters
    public static void resetObjects() {
        resetLasers();
        resetAlienLasers();
        resetAliens();
        resetBaseShelters();
    }

    public static void addToScore(int scoreChange) {
        score += scoreChange;
    }

    // gets a random alien to shoot a laser
    public static void aliensShoot() {

        // tempPossibleAliens stores the aliens that are allowed to shoot (a.k.a. the bottommost aliens)
        // the indices of tempPossibleAliens represents the columns of aliens
        // if the row number of the alien is 0, that means there is no alien in that column
        Alien[] tempPossibleAliens = new Alien[11];
        for(int i=0; i<11; i++) {
            tempPossibleAliens[i] = new Alien();
        }

        // finds the aliens that are allowed to shoot (a.k.a. the bottommost aliens)
        for(Alien alien : aliens) {
            if(alien.getRow() > tempPossibleAliens[alien.getColumn() - 1].getRow()) {
                tempPossibleAliens[alien.getColumn() - 1] = alien;
            }
        }

        ArrayList<Alien> possibleAliens = new ArrayList<>();

        // removes the columns with no aliens:
        for(Alien alien : tempPossibleAliens) {
            if(alien.getRow() != 0) {
                possibleAliens.add(alien);
            }
        }

        int randomAlien = rand.nextInt(possibleAliens.size());                              // chooses a random alien from the list of possible aliens
        
        if(alienLasers.size() < 1) {
            possibleAliens.get(randomAlien).shoot();                                        // if there ISN'T an alien laser on the screen, the random alien shoots a laser
            PlaySound.playSoundEffect(PlaySound.laser);
        }
    }

    // pauses the game for 2 seconds:
    public static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // generates a mystery ship randomly:
    public static void generateMysteryShip() {
        int random = (int)(Math.random()*1000);
		if (random == 700) {
            canShowMysteryShip = true;                                                      // chooses a random time for the mystery ship to appear
        }
    }

    // removes the mystery ship from the screen:
    public static void removeMysteryShip(MysteryShip mysteryShip) {
        int mysteryShipX;

        // makes the next mystery ship appear at the opposite corner of the screen
        if(mysteryship.getDir() == 1) {
            mysteryShipX = WIDTH - 1;
        }
        else {
            mysteryShipX = 1;
        }

        // makes the next mystery ship move in the opposite direction:
        int mysteryShipDir = -1*mysteryShip.getDir();

        canShowMysteryShip = false;                                                        // removes the mystery ship from the screen

        mysteryship = new MysteryShip(mysteryShipX, mysteryShipDir);                       // sets up the next mystery ship
    }

    @Override
    public void paint(Graphics g){ 	
        if(screen == "start") {
            g.drawImage(startScreen, 0, 0, null);
        }
        if(screen == "game") {                                                             // only paints objects when the screen is "game"
            g.setColor(Color.black);  
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // draws the player's lasers:
            for(Laser laser : lasers) {
                laser.draw(g);
            }
            // draws the alien's lasers:
            for(Laser laser : alienLasers) {
                laser.draw(g);
            }

            if(canShowMysteryShip) {
                mysteryship.draw(g);                                                       // draws mystery ship if it is ALLOWED to appear
            }

            // draws baseshelters:
            for(BaseShelter baseshelter : baseshelters) {
                baseshelter.draw(g);
            }

            lasercannon.draw(g);                                                           // draws laser cannon

            // draws the mini laser cannons:
            for(LaserCannon minilasercannon : minilasercannons) {
                minilasercannon.draw(g);
            }

            // draws the rows of aliens:
            for(Alien alien : aliens) {
                alien.draw(g);
            }

            g.setColor(Color.white);
            g.drawString("" + score, 100, 50);                                             // draws the score
        }
        if(screen == "gameover") {
            g.drawImage(gameOverScreen, 0, 0, null);
        }
        if(screen == "levelTwo") {
            g.drawImage(levelTwoScreen, 0, 0, null);
        }
        if(screen == "win") {
            g.drawImage(winScreen, 0, 0, null);
        }
    }
    
    @Override
	public void	mousePressed(MouseEvent e) {}
	public void	mouseClicked(MouseEvent e) {}
	public void	mouseEntered(MouseEvent e) {}
	public void	mouseExited(MouseEvent e) {}
	public void	mouseReleased(MouseEvent e) {}
	
	public void	keyPressed(KeyEvent e) {
        if (screen != "game" && e.getKeyCode() == KeyEvent.VK_ENTER) {            
            if(screen != "levelTwo") {
                level = 1;                                                                                  // resets the game to Level 1 if the player is not at Level 2
                score = 0;                                                                                  // resets the score
            } else {                                                                       
                level++;
            }
            if(screen == "start") {
                myTimer.start();
            }
            Alien.reset();                                                                                  // resets the aliens back to normal
            Alien.increaseSpeed(level);                                                                     // increases the speed of the aliens based on the Level (LEvel 2 is faster than Level 1)
            initialize(level);                                                                              // initializes the objects based on the Level
            screen = "game";
        }

        // shoots a laser if the space key is pressed and there are no more of the player's lasers on the screen:
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !keys[KeyEvent.VK_SPACE] && lasers.size() < 1) {
            lasercannon.shoot();
            PlaySound.playSoundEffect(PlaySound.laser);
        }

		keys[e.getKeyCode()] = true;
	}

	public void	keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
	}
		
	public void	keyTyped(KeyEvent e) {}
}