import java.awt.*;
import javax.swing.*;
import java.util.Random;

/*
* Creates mystery ships
*/
public class MysteryShip {
    private int x, y;
    private static final int WIDTH = 80, HEIGHT = 40;                    // width and height of the mystery ship
    private final int SPEED = 14;                                        // speed of the mystery ship
    private int dir;
    private Image pic;
    private int pointValue;                                              // the point value of the mystery ship

    private static Random rand = new Random();

    private Rectangle mysteryShipBox;

    public MysteryShip(int xx, int ddir) {
        x = xx;
        y = 20;

        dir = ddir;

        pic = new ImageIcon("Images/mysteryship.png").getImage();

        pointValue = rand.nextInt(60) + 170;                             // sets the point value as a random number between 170 and 230

        mysteryShipBox = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void move() {
        x += SPEED*dir;
        mysteryShipBox.translate(SPEED*dir, 0);
        PlaySound.playSoundEffect(PlaySound.mysteryShip);

        if(x <= 0 || x + WIDTH >= SpaceInvadersPanel.WIDTH) {            // if the mystery ship hit the edge of the screen
            SpaceInvadersPanel.removeMysteryShip(this);                  // removes the mystery ship from the screen
        }
    }

    public void hitByLaser() {
		for(Laser laser : SpaceInvadersPanel.lasers) {
			if(getRect().intersects(laser.getRect())) {                  // if a laser hits the mystery ship
                SpaceInvadersPanel.removeLaser(laser);                   // removes the player's laser that hit the mystery ship
				SpaceInvadersPanel.removeMysteryShip(this);              // removes the mystery ship

				PlaySound.playSoundEffect(PlaySound.alienExplosion);
                SpaceInvadersPanel.score += pointValue;                  // adds the point value of the mystery ship to the total score
			}
        }
	}

    // Getter and setter methods:
    public int getX() {
        return x;
    }
    public void setX(int xx) {
        x = xx;
    }
    public void setDir(int ddir) {
        dir = ddir;
    }
    public int getDir() {
        return dir;
    }
    public Rectangle getRect() {
        return mysteryShipBox;
    }

    // draws the mystery ship:
    public void draw(Graphics g) {
		g.drawImage(pic, x, y, null);
    }
}