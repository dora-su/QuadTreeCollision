/**
 * [Main.java]
 * A class that tests everything
 * @author Dora Su
 * October 27 2018
 */

import java.awt.Toolkit;

public class Main {
    public static void main(String[] args) {
        //calculate screen width and height
        int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        Display display = new Display(screenWidth,screenHeight,5,8);//creates new display, size of screen of user
        while(true){ //runs forever unless esc is pressed
            try {
                Thread.sleep(15);//wait 15 milliseconds before repainting
            } catch (InterruptedException e) {
                return;
            }
            display.repaint(); //repaint
        }
    }
}
