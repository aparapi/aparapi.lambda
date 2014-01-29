package hsailtest;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

/**
 * Created by user1 on 1/27/14.
 */
public class PixelRenderer {
    float fps  =0;
    float frame;
    long first = System.currentTimeMillis();

    JComponent component;
    int width;
    int height;
    BufferedImage offscreen ;
    int[] offscreenPixels;
    PixelRenderer(int _width, int _height){
        width=_width;
        height = _height;
        component= new JComponent(){};
        component.setPreferredSize(new Dimension(width, height));

        offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        offscreenPixels= ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
    }
    public void sync(){

        component.getGraphics().drawImage(offscreen, 0, 0, null);
        long delta = System.currentTimeMillis()-first;
        frame+=1;
        if (delta > 1000){
            fps =(frame*1000)/delta;
            first = System.currentTimeMillis() ;
            frame=0;
        }
    }

    public void clear(){
        Arrays.fill(offscreenPixels, 0);
        print(100, 100, String.format("fps %5.2f ", fps));
    }
    public void print(int x, int y, String _string){
        offscreen.getGraphics().setColor(Color.WHITE);
        offscreen.getGraphics().drawString(_string, x, y);
    }
    public void cross(int x, int y,int rgb){
        set(x-1,y, rgb);
        set(x,y, rgb);
        set(x+1,y, rgb);
        set(x,y-1, rgb);
        set(x,y+1, rgb);
    }

    public void x(int x, int y,int rgb){
        set(x-1,y-1, rgb);
        set(x,y, rgb);
        set(x+1,y+1, rgb);
        set(x+1,y-1, rgb);
        set(x-1,y+1, rgb);
    }

    public void o(int x, int y,int rgb){
        set(x-1,y-1, rgb);
        set(x-1,y, rgb);
        set(x-1,y+1, rgb);
        set(x,y-1, rgb);
        set(x,y+1, rgb);
        set(x+1,y-1, rgb);
        set(x+1,y, rgb);
        set(x+1,y+1, rgb);
    }
    public void set(int x, int y,int rgb){
        if (x>=0 && x<width && y>=0 && y<height){
            offscreenPixels[x+y*width]=rgb;
        }
    }
}
