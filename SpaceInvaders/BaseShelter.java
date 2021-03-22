import java.awt.image.*;
import java.awt.*;
import java.util.ArrayList;

/*
* Creates base shelters
*/
public class BaseShelter {
    private int x, y;
    private Rectangle baseShelterBox;

    private BufferedImage pic;
    public static final int WIDTH = 85, HEIGHT = 50;                                // width and height of the base shelters

    private final int BLANK = 0x00000000;                                           // colour value for transparent

    // Constructor:
    public BaseShelter(int xx, int yy) {
        x = xx;
        y = yy;

        pic = SpaceInvadersPanel.loadBuffImg("baseshelter.png");
        baseShelterBox = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // checks if Base Shelter is hit by a laser:
    public void hitByLaser(ArrayList<Laser> lasers) {
        ArrayList<Laser> removedLasers = new ArrayList<Laser>();

		for(Laser laser : lasers) {
            int laserX = laser.getX();
			int laserY = laser.getY();
            
			if(getRect().intersects(laser.getRect())) {                             // if a laser intersects with the base shelter
                int holeX = Math.abs(laserX - x);
                int holeY = Math.abs(laserY - y);

                if(holeX > 0 && holeX < WIDTH && holeY > 0 && holeY < HEIGHT) {     // double checks if pixel is in bounds
                    if(pic.getRGB(holeX, holeY) != BLANK) {                         // if the pixel is coloured in
                        
                        // erases a square of pixels around it:
                        for(int hx = holeX - 8; hx < holeX + 8; hx++) {
                            for(int hy = holeY - 10; hy < holeY + 15; hy++) {
                                if(hx > 0 && hx < WIDTH && hy > 0 && hy < HEIGHT) { // makes sure that the pixel is within the bounds
                                    pic.setRGB(hx, hy, BLANK);
                                }
                            }
                        }

                        removedLasers.add(laser);                                   // adds the laser to the list of lasers that hit the BaseShelter
                    }
                }
			}
        }

        lasers.removeAll(removedLasers);                                            // removes the lasers that hit the base shelter
	}

    // Getter methods:
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Rectangle getRect() {
        return baseShelterBox;
    }
    
    // draws the base shelter:
    public void draw(Graphics g){
    	g.drawImage(pic, x, y, null);
    }
}