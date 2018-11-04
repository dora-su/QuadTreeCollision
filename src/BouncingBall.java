/**
 * [BouncingBall.java]
 * Bouncing ball that moves around and bounces off the walls
 * @author Dora Su
 * October 25 2018
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

public class BouncingBall extends Object { //extends Object (required to make it generic)
    //boundaries
    private int maxX;
    private int maxY;

    private double posX, posY; //top left corner coordinate values of the smallest square that can contain the ball (drawing coordinates), doubles to maintain precise values
    private final int SIZE = 20; //diameter of the ball, constant
    private double vx, vy; //x and y components of velocity
    private Color color; //color of the ball
    private static ArrayList<Color> colors = new BallColourPalette().getColors(); //colour palette to choose colours from 

    /**
     * Constructor, creates new ball and generates random values
     */
    public BouncingBall(int maxX, int maxY) {
        //sets boundaries
        this.maxX = maxX;
        this.maxY = maxY;

        //sets variables to random numbers
        posX = new Random().nextInt(maxX);
        posY = new Random().nextInt(maxY);

        vx = new Random().nextInt(11) - 5;
        vy = new Random().nextInt(11) - 5;

        //sets colours to random colour from the ArrayList
        color = colors.get(new Random().nextInt(colors.size()));
    }

    /**
     * A method for a ball to update its position and move
     */
    public void move() {
        if ((posX + SIZE > maxX) || posX < 0) { //crashing into vertical walls
            vx *= -1; //bounce off the wall
        }
        if ((posY + SIZE > maxY) || posY < 0) { //crashing into horizontal walls
            vy *= -1; //bounce off the wall
        }

        //preventing balls from going off the screen, brings them back if they went off the screen
        if (posX > maxX - SIZE) {
            posX = maxX - SIZE;
        } else if (posX < 0) {
            posX = 0;
        }
        if (posY > maxY - SIZE) {
            posY = maxY - SIZE;
        } else if (posY < 0) {
            posY = 0;
        }

        //update the positions
        posX  = vx +posX;
        posY =  vy +posY;
    }

    /**
     * Draws a ball onto the screen
     * @param g Graphics to draw the balls
     */
    public void draw(Graphics g) {
        //set the colour of the ball
        g.setColor(color);
        //draws the ball
        g.fillOval((int)posX, (int)posY, SIZE, SIZE);
    }


    public double getPosX() {
        return posX + getRadius();
    }

    public double getPosY() {
        return posY + getRadius();
    }

    public int getRadius() {
        return SIZE / 2;
    }

    /**
     * Checks for collision between two balls and if they collide, adjust the positions and velocities
     * @param b Ball to check collision with (and collide with)
     */
    public void collide(BouncingBall b) {
        //calculate differences
        double deltaX = b.posX - posX;
        double deltaY = b.posY - posY;

        //calculate the distance
        double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        //balls are colliding if the distance between them is smaller than the distance between their centres
        if (distance <= getRadius() + b.getRadius()) {//Apply TLAP,  use math as a tool
            // calculate the dot product
            double deltaVx = b.vx - vx;
            double deltaVy = b.vy - vy;
            double dotProduct = deltaX * deltaVx + deltaY * deltaVy;

            if (dotProduct < 0) { //if dot product is negative, they are moving towards each other
                if(((vx == 0) && (b.vx == 0)) || ((vy == 0) && (b.vy == 0))) { //if the objects are moving parallel to one another, this is a very special situation!
                    //in this special case that the balls are moving parallel, it requires more calculation than regular cases
                    //by using only this calculation method for this specific situation, it is more efficient

                    double aCollision = Math.atan(deltaY/deltaX);//angle of collision (between centres of the two objects)

                    //auxiliary velocities to help the following math look less intense
                    //for this object
                    double vxp = vx*Math.cos(aCollision) + vy*Math.sin(aCollision); //vx'
                    double vyp =  vy*Math.cos(aCollision); //vy'
                    //for ball b
                    double vxbp = b.vx*Math.cos(aCollision) + b.vy*Math.sin(aCollision); //vxb'
                    double vybp =b.vy*Math.cos(aCollision); //vyb'
                    //combined velocitiy of this ball and ball b (not in components)
                    double s2 = (2*b.SIZE*vxbp)/(2*SIZE);
                    double sb2 = (2*SIZE*vxp)/(2*SIZE);

                    //calculate new velocities with TLAP formulas
                    double vxf = s2*Math.cos(aCollision)-vyp*Math.sin(aCollision);
                    double vbxf = sb2*Math.cos(aCollision)-vybp*Math.sin(aCollision);
                    double vyf = s2*Math.sin(aCollision)+vyp*Math.cos(aCollision);
                    double vbyf = sb2*Math.sin(aCollision)+vybp*Math.cos(aCollision);

                    //set new velocities
                    vx = vxf;
                    vy = vyf;
                    b.vx = vbxf;
                    b.vy = vbyf;
                } else {
                    // if the dotProduct is smaller than zero, then the objects are moving towards each other, prevents objects from sticking together
                    //Apply more TLAP formulas based on perfectly elastic collisions, since size is the same, the velocities just swap
                    //swap + set new velocities
                    double temp = vx;
                    vx = b.vx;
                    b.vx = temp;
                    temp = vy;
                    vy = b.vy;
                    b.vy = temp;
                }
            }
        } else {
            return; //return if they do not collide
        }
    }
}
