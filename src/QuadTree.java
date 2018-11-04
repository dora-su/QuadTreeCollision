/**
 * [QuadTree.java]
 * A quad tree data structure that splits into 4 nodes once a certain number of objects has been reached,
 * continues to split until the maximum number of divisions has been reached
 * @author Dora Su
 * October 26 2018
 */

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
import java.util.ArrayList;

public class QuadTree<Object> {
    //max value of objects in one node and max subdivisions, it is constant
    private final int MAX_OBJECTS;
    private final int MAX_SUBDIVISION;

    private ArrayList<Object> objects; //arraylist that stores the balls
    private QuadTree<Object>[] nodes; //array that represents the quadtree's nodes (children)

    private boolean canTakeObjects; //boolean to determine whether a certain quad can contain more balls
    private boolean isSubdivided; //true if the quad is subdivided

    private int level; //level of subdivision
    private int w,h; //width and height of rectangle
    private int x,y; //coordinates of quad's top-left corner

    /**
     * Constructor
     * @param level, level of subdivision (depth)
     * @param bound, rectangle that represents the boundaries of the quadtree
     * @param MAX_OBJECTS maximum amount of objects that can be stored in the quadtree
     * @param MAX_SUBDIVISION maximum amount of levels that the entire root tree can divide into
     */
    public QuadTree(int level, Rectangle bound, int MAX_OBJECTS, int MAX_SUBDIVISION){
        //initialize variables
        this.MAX_OBJECTS = MAX_OBJECTS;
        this.MAX_SUBDIVISION = MAX_SUBDIVISION;
        this.level = level;
        nodes = new QuadTree[4];
        objects = new ArrayList<Object>();

        //calculate the x, y, width, and heights of the rectangle
        w = (int)bound.getWidth();
        h = (int)bound.getHeight();
        x = (int)bound.getX();
        y = (int)bound.getY();

        //a new quadtree can take balls, and is not subdivided
        canTakeObjects = true;
        isSubdivided = false;
    }

    /**
     * a recursive method to clear the quadtree and its nodes + arraylist of balls
     */
    public void clear(){
        objects.clear(); //clears the arraylist of balls
        //clears each of the nodes, recursively
        for(QuadTree node:nodes){
            if(node!=null) {
                node.clear();
                node = null;
            }
        }
        //an empty tree is not subdivided and can take more balls
        isSubdivided = false;
        canTakeObjects =true;
    }

    /**
     * a method to insert a BouncingBall into the quadtree
     * @param b BouncingBall to be inserted
     */
    public void insert(Object b) {
        //if ball is not in the boundaries of the quadtree, don't insert it
        if (!inBounds(b)) {
            return ;
        }

        //if the ball has already been added, do not insert it again
        if (objects.contains(b)) {
            return ;
        }

        //if the quadtree is not full and can take more balls, then add it to the arraylist of balls
        if (objects.size() < MAX_OBJECTS && canTakeObjects) {
            objects.add(b);
            return;
        }

        //if the quadtree is at the level equal to the threshold, just add it to the arraylist since the quadtree cannot subdivide further
        if (level == MAX_SUBDIVISION) {
            objects.add(b);
            return;
        }

        //if the quadtree is full, then subdivide
        if (objects.size() >= MAX_OBJECTS) {
            if(!isSubdivided) {
                subdivide();
            }
        }

        //once it is subdivided, recursively run this method on each of the nodes so it can be added into the right node(s)
        if (isSubdivided) {
            for(QuadTree<Object> node: nodes){
                node.insert(b);
            }
            return ;
        }
    }

    /**
     * a method to subdivide the tree into more quads
     */
    public void subdivide() {
        //rectangles to use when creating new quadtrees, each has dimensions of half the height and half the width
        Rectangle sw = new Rectangle(x,h / 2 + y, w / 2, h / 2); //rectangle to represent the southwest corner
        Rectangle se = new Rectangle(x + w / 2, y + h / 2, w / 2, h / 2); //rectangle to represent the southeast corner
        Rectangle nw = new Rectangle(x, y, w / 2, h / 2); //rectangle to represent the northwest corner
        Rectangle ne = new Rectangle(x + w / 2, y, w / 2, h / 2); //rectangle to represent the northeast corner

        //initialize each of the new nodes
        nodes[0] = new QuadTree<Object>(level + 1, ne, this.MAX_OBJECTS, this.MAX_SUBDIVISION);
        nodes[1] = new QuadTree<Object>(level + 1, nw, this.MAX_OBJECTS, this.MAX_SUBDIVISION);
        nodes[2] = new QuadTree<Object>(level + 1, sw, this.MAX_OBJECTS, this.MAX_SUBDIVISION);
        nodes[3] = new QuadTree<Object>(level + 1, se, this.MAX_OBJECTS, this.MAX_SUBDIVISION);

        //distribute the balls of this current tree to its subnodes
        for (Object o: objects) {
            nodes[0].insert(o);
            nodes[1].insert(o);
            nodes[2].insert(o);
            nodes[3].insert(o);
        }

        //clear this current trees list of ball since they have all been distributed to subnodes
        objects.clear();

        //a subdivided tree cannot take more balls, and it is now subdivided
        canTakeObjects = false;
        isSubdivided = true;
    }

    /**
     * a recursive method to draw the entire quadtree (including all its nodes)
     * @param g Graphics object to draw the
     */
    public void draw(Graphics g){
        //set the colour
        g.setColor(Color.WHITE);

        if(level!=1) { //do not draw if it is the first level (results in weird border)
            //draw the lines
            g.drawLine(x, y, x + w, y);
            g.drawLine(x, y, x, y + h);
        }

        //draw recursively for each of the trees subnodes
        for(QuadTree<Object> node: nodes){
            if(node!=null) {
                node.draw(g);
            }
        }

    }

    /**
     * recursive method to check for collisions
     */
    public void collisionCheck(){
        //if it isn't subdivided, just check its own array of balls since there are no further levels to check
        if(!isSubdivided){
            for(int i =0;i<objects.size();i++){
                for(int j = i+1;j<objects.size();j++){
                    ((BouncingBall)objects.get(i)).collide((BouncingBall)objects.get(j));
                }
            }
        }else{ //otherwise, check for collisions in each of its nodes
            for(QuadTree<Object> node:nodes){
                node.collisionCheck();
            }
        }
    }

    /**
     * private method to check whether the ball is in the boundaries of the quadtree
     * @param b BouncingBall to be checked
     * @return returns true if the BouncingBall b is contained in the boundaries of the quad tree, otherwise returns false
     */
    private boolean inBounds(Object b) {
        //if the coordinates of the centre of the ball are in the boundaries
        if ((((BouncingBall)b).getPosX() >=x)&& (((BouncingBall)b).getPosX() <=x+w) && (((BouncingBall)b).getPosY() >=y)&&(((BouncingBall)b).getPosY()<=y+h)) {
            return true;
        }

        //if the coordinates of the centre of the ball are not in the boundaries, but other parts of the ball are
        if ((((BouncingBall)b).getPosX() + ((BouncingBall)b).getRadius() >=x)&& (((BouncingBall)b).getPosX() - ((BouncingBall)b).getRadius()<=x+w) &&
                (((BouncingBall)b).getPosY() + ((BouncingBall)b).getRadius() >=y) && (((BouncingBall)b).getPosY() - ((BouncingBall)b).getRadius()<=y+h)) {
            return true;
        }

        //return false if the ball is not in the boundaries of the quadtree at all
        return false;
    }
}
