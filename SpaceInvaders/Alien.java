import java.awt.*;
import java.awt.image.*;

/*
* Creates aliens
*/
public class Alien {
    private int x, y;
    private Rectangle alienBox;

    public int row;
    public int column;
    public int pointValue; // point value of the alien

    public static int speed = 1;
    public static int dir = -1;
    private static int aliensDestroyed = 0; // number of aliens that are destroyed

	public static final int WIDTH = 50, HEIGHT = 24;

    private BufferedImage pic;

    // Constructor:
    public Alien(int xx, int yy, String picName, int pValue, int rrow, int ccolumn) {
        x = xx;
        y = yy;
        row = rrow;
        column = ccolumn;

        pic = SpaceInvadersPanel.loadBuffImg(picName);
        alienBox = new Rectangle(x, y, WIDTH, HEIGHT);

        pointValue = pValue;
    }

    public Alien() {
        row = 0;
    }

    // resets the aliens
    public static void reset() {
        aliensDestroyed = 0;                   // resets the number of aliens that were destroyed
        speed = 1;                             // resets the speed
        dir = -1;                              // resets the direction
    }

    // increases the speed of the aliens based on the Level:
    public static void increaseSpeed(int level) {
        speed += 2*(level-1);
    }

    // changes the direction of the aliens:
    public static void changeDirection() {
        dir *= -1;
    }

    public void shoot() {
        SpaceInvadersPanel.alienLasers.add(new Laser(x + WIDTH/2, y + HEIGHT, false));
    }

    // method for if an alien is hit by a laser
	public void hitByLaser() {
		for(Laser laser : SpaceInvadersPanel.lasers) {
			if(getRect().intersects(laser.getRect())) {                                                // if a lazer hits the the alien
                aliensDestroyed += 1;
                PlaySound.playSoundEffect(PlaySound.alienExplosion);

                if(aliensDestroyed >= 55) {                                                            // if all of the aliens are destroyed
                    if(SpaceInvadersPanel.getLevel() == 1) SpaceInvadersPanel.screen = "levelTwo";     // moves on to the next level if the player is at Level 2
                    else SpaceInvadersPanel.screen = "win";                                            // if the player is at Level 2, the player has won the game
                }

                if(aliensDestroyed % 5 == 0 && aliensDestroyed != 0) {                                 // the aliens speed up by 1 for every 5 aliens that are destroyed
                    speed += 1;
                }
                SpaceInvadersPanel.removeLaser(laser);                                                 // adds the laser to the list of the player's lasers that hit the alien
                SpaceInvadersPanel.removeAlien(this);
                SpaceInvadersPanel.addToScore(pointValue);
			}
        }
	}

    public void move(boolean onEdge) {

        // erases base shelters if alien touches them:
        for(BaseShelter baseshelter : SpaceInvadersPanel.baseshelters) {
            if(getRect().intersects(baseshelter.getRect())) {
                SpaceInvadersPanel.removeBaseShelter(baseshelter);
            }
        }

        if(y + HEIGHT >= SpaceInvadersPanel.lasercannon.getY()) {                                      // if an alien gets to lasercannon level
            SpaceInvadersPanel.screen = "gameover";                                                    // the player got INVADED (a.k.a. they lost the game)
        }

        // moves the aliens left and right:
        x += speed*dir;                                                                                
        alienBox.translate(speed*dir, 0);

        // bumps the aliens down if they are on the edge:
        if(onEdge) {
            y += 5;
            alienBox.translate(0, 5);
        }
    }

    // Getter methods:
    public Rectangle getRect() {
        return alienBox;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int xx) {
        x = xx;
    }
    public void setY(int yy) {
        y = yy;
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }

    // draws the alien:
    public void draw(Graphics g){
    	g.drawImage(pic, x, y, null);
    }
}