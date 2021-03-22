import java.awt.*;

/*
* Creates lasers
*/
public class Laser {
    private int x, y;
    private Rectangle laserBox;
    public boolean goodGuy;                             // if the laser was shot by the player (good guy) or the alien (bad guy)

    public static final int WIDTH = 2, HEIGHT = 25;     // width and height of the laser
    private final int lASERSPEED = 25;                  // speed of the laser

    private Color laserColor = Color.GREEN;
    
    // Constructor:
    public Laser(int xx, int yy, boolean good) {
        goodGuy = good;

        x = xx - WIDTH/2;
    	y = yy - HEIGHT/2;
        laserBox = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // moves the laser
    public void move() {
        if(goodGuy) {                                   // the player (good guy) shoots their lazer up
            y -= lASERSPEED;
            laserBox.translate(0, -lASERSPEED);
        }
        else {                                          // aliens shoot their lazers down
            y += lASERSPEED;
            laserBox.translate(0, lASERSPEED);
        }

        // if the player's laser goes above the screen, this removes the player's laser:
        if(y + HEIGHT <= 0) {
            SpaceInvadersPanel.removeLaser(this);
        }
        // if the alien's laser goes below the screen, this removes the alien's laser:
        else if(y >= SpaceInvadersPanel.HEIGHT) {
            SpaceInvadersPanel.removeAlienLaser(this);
        }
    }

    // Getter methods:
    public int getX() {
        return x + WIDTH/2;
    }
    public int getY() {
        return y;
    }
    public Rectangle getRect() {
        return laserBox;
    }

    // draws laser:
    public void draw(Graphics g) {
        g.setColor(laserColor);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }
}