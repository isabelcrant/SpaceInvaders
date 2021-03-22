import java.awt.*;
import javax.swing.*;

/*
* Creates laser cannons
*/
public class LaserCannon {
    private int x, y;
	private int right, left;                                         // right and left keys
	public int WIDTH, HEIGHT;                                        // width and height of the laser cannon

	private Image pic;

	private Rectangle laserCannonBox;

	private int laserCannonsLeft = 3;                                // the amount of laser cannons left

	// Constructor:
    public LaserCannon(int[] k) {
    	left = k[0];
    	right = k[1];

		pic = new ImageIcon("Images/lasercannon.png").getImage();

		WIDTH = pic.getWidth(null);
		HEIGHT = pic.getHeight(null);

		// laser cannon starts off in the middle of the screen:
		y = SpaceInvadersPanel.HEIGHT-HEIGHT-30;
		x = SpaceInvadersPanel.WIDTH/2;

		laserCannonBox = new Rectangle(x, y, WIDTH, HEIGHT);
    }

	// mini laser cannon Constructor:
	public LaserCannon(int xx, int yy) {
		pic = new ImageIcon("Images/minilasercannon.png").getImage();

		x = xx;
		y = yy;
	}

	// moves Laser Cannon left and right:
    public void move(boolean[] keys){
		if(keys[left] && x >= 20){
			x -= 15;
			laserCannonBox.translate(-15, 0);
		}
		else if(keys[right] && x <= SpaceInvadersPanel.WIDTH-WIDTH-3){
			x += 15;
			laserCannonBox.translate(15, 0);
		}
    }

	// checks if Laser Cannon is hit by a laser
	public void hitByLaser() {
		for(Laser laser : SpaceInvadersPanel.alienLasers) {
			if(getRect().intersects(laser.getRect())) {                // if a lazer intersects with the Laser Cannon
				SpaceInvadersPanel.removeAlienLaser(laser);

				laserCannonsLeft--;

				PlaySound.playSoundEffect(PlaySound.laserCannonExplosion);
				SpaceInvadersPanel.minilasercannons.remove(SpaceInvadersPanel.minilasercannons.get(laserCannonsLeft));
				SpaceInvadersPanel.laserCannonHit = true;

				if(laserCannonsLeft == 0) {                            // if there are no laser cannons left
					SpaceInvadersPanel.screen = "gameover";            // the player ran out of laser cannons :( (a.k.a. game over)
				}
			}
        }
	}

	// gets laser cannon to shoot a laser from its centre:
	public void shoot() {
        SpaceInvadersPanel.lasers.add(new Laser(x + WIDTH/2, y, true));
    }

	// Getter methods:
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Rectangle getRect() {
		return laserCannonBox;
	}

	// draws the laser cannon:
    public void draw(Graphics g){
		g.drawImage(pic, x, y, null);
    }
}