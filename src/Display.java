/**
 * [Display.java]
 * A display to display BouncingBalls and the quadtrees
 * @author Dora Su
 * October 27 2018
 */

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Display extends JFrame {
    //constants
    private final int WINDOW_WIDTH; //width of frame
    private final int WINDOW_HEIGHT; //height of frame
    private final int MAX_OBJECTS; //maximum objects for quadtree (threshold before subdivision)
    private final int MAX_SUBDIVISION; //maximum levels the quadtree can be split into

    private ArrayList<BouncingBall> balls; //arraylist of all balls (added by keypress)
    private QuadTree<BouncingBall> qt; //quadtree
    private BouncePanel panel; //panel to draw the balls
    private MyKeyListener keyListener; //keylistener to allow for adding on keypress
    private Rectangle bound; //boundaries of the frame/panel/root quadtree

    /**
     *
     * @param WINDOW_WIDTH width of window
     * @param WINDOW_HEIGHT height of window
     * @param MAX_OBJECTS max objects the quadtree should contain before subdividing
     * @param MAX_SUBDIVISION max levels of subdivision
     */
    public Display(int WINDOW_WIDTH, int WINDOW_HEIGHT, int MAX_OBJECTS,int MAX_SUBDIVISION) {
        //initialize variables
        this.WINDOW_WIDTH = WINDOW_WIDTH;
        this.WINDOW_HEIGHT = WINDOW_HEIGHT;
        this.MAX_OBJECTS = MAX_OBJECTS;
        this.MAX_SUBDIVISION = MAX_SUBDIVISION;
        balls = new ArrayList<>();
        bound = new Rectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT); //boundaries are 0,0 to the screen width and height
        qt = new QuadTree<>(1, bound,MAX_OBJECTS, MAX_SUBDIVISION);
        panel = new BouncePanel();
        keyListener = new MyKeyListener();

        //add components + modify appearance of the frame
        this.add(panel); //adds the panel
        this.addKeyListener(keyListener); //adds the keylistener
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); //makes sure the program is aborted when the frame is closed
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); //set size of frame to sc
        this.setBackground(new Color(30, 30, 30)); //set background colour
        this.setUndecorated(true); //removes the bar at the top to prevent miscalculations
        this.setLocationRelativeTo(null); //makes it appear in the centre of the screen
        this.setVisible(true); //makes it visible
    }

    //----------------------------- INNER CLASSES ------------------------------

    /**
     * private class for the panel to draw balls on, extends JPanel
     */
    private class BouncePanel extends JPanel {
        /**
         * constructor that sets the size of the panel and makes the graphics draw efficiently
         */
        BouncePanel() {
            this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            setDoubleBuffered(true); //more efficient graphics
        }

        /**
         * draws the balls, checks for collisions, draws the quadtrees
         *
         * @param g Graphics object to draw with
         */
        public void paintComponent(Graphics g) {
            //first clear the tree so updated balls can be added
            qt.clear();

            //insert all the balls into the quadtree
            for (BouncingBall b : balls) {
                qt.insert(b);
            }

            //check for collisions
            qt.collisionCheck();

            //draw the quadtree first so balls don't have a line going through them
            qt.draw(g);

            //draw the balls
            for (BouncingBall b : balls) {
                b.draw(g);
            }

            //update and move the positions of the balls
            for (BouncingBall b : balls) {
                b.move();
            }
        }
    }

    /**
     * private class for the keylistener to check for key presses
     */
    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        /**
         * check for key presses
         * use presses instead of typed to make adding 10000 balls simpler
         */
        public void keyPressed(KeyEvent e) {
            //add a new ball when space is pressed
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                balls.add(new BouncingBall(WINDOW_WIDTH, WINDOW_HEIGHT));
            }
            //exit if esc is pressed
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

}
