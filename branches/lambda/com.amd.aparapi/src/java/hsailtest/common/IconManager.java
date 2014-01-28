package hsailtest.common;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: user1
 * Date: 10/31/13
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class IconManager {
 //   static File iconDir = new File("c:/users/user1/apu2013/data/icons");


    public static final ImageIcon chipIcon = createImageIcon("c:/users/user1/apu2013/data/icons/chip.png", "chip");
    public static final ImageIcon startIcon = createImageIcon("c:/users/user1/apu2013/data/icons/start.png", "start");


    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    static ImageIcon createImageIcon(String path, String description) {
        URL imgURL = IconManager.class.getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL, description);

            int status = icon.getImageLoadStatus();
            return (icon);
        } else {
            // maybe it is just a file name
            ImageIcon icon = new ImageIcon(path, description);
            if (icon != null){
                return (icon);
            }
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    static ImageIcon resize(ImageIcon image, int width, int height){
        Image img = image.getImage();
        Image newimg = img.getScaledInstance(width, height,  Image.SCALE_SMOOTH);
        return(new ImageIcon(newimg));
    }
}
