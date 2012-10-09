package com.amd.aparapi.samples.mandel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.amd.aparapi.Aparapi;

public class Mandel{

   static final int maxIterations = 64;


   static void displayMandel(int width, int height, float offsetx, float offsety, float scale, int rgb[], int[] pallette){
      Aparapi.forEach(width, height, (xin,yin)->{
         /** Translate the gid into an x an y value. */
         float x = (((xin * scale) - ((scale / 2) * width)) / width) + offsetx;

         float y = (((yin * scale) - ((scale / 2) * height)) / height) + offsety;

         int count = 0;

         float zx = x;
         float zy = y;
         float new_zx = 0f;

         // Iterate until the algorithm converges or until maxIterations are reached.
         while (count < maxIterations && zx * zx + zy * zy < 8) {
            new_zx = zx * zx - zy * zy + x;
            zy = 2 * zx * zy + y;
            zx = new_zx;
            count++;
         }

         // Pull the value out of the palette for this iteration count.
         rgb[xin+(yin*height)] = pallette[count];
      });
   }

   /** User selected zoom-in point on the Mandelbrot view. */
   public static volatile Point to = null;

   @SuppressWarnings("serial") public static void main(String[] _args) {

      JFrame frame = new JFrame("MandelBrot");

      /** Width of Mandelbrot view. */
      final int width = 768;

      /** Height of Mandelbrot view. */
      final int height = 768;



      /** Image for Mandelbrot view. */
      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      // Draw Mandelbrot image
      JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, width, height, this);
         }
      };

      // Set the size of JComponent which displays Mandelbrot image
      viewer.setPreferredSize(new Dimension(width, height));

      final Object doorBell = new Object();

      // Mouse listener which reads the user clicked zoom-in point on the Mandelbrot view 
      viewer.addMouseListener(new MouseAdapter(){
         @Override public void mouseClicked(MouseEvent e) {
            to = e.getPoint();
            synchronized (doorBell) {
               doorBell.notify();
            }
         }
      });

      // Swing housework to create the frame
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      // Extract the underlying RGB buffer from the image.
      // Pass this to the kernel so it operates directly on the RGB buffer of the image
      final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
      final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

      // create pallette 
      // Initialize palette values
      final int[] pallette = new int[maxIterations+1];

         for (int i = 0; i < maxIterations; i++) {
            float h = i / (float) maxIterations;
            float b = 1.0f - h * h;
            pallette[i] = Color.HSBtoRGB(h, 1f, b);
         }

      // Create a Kernel passing the size, RGB buffer and the palette.
      
      displayMandel(width, height,  -1f, 0f, 3f, rgb, pallette);
      System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
      viewer.repaint();

      // Window listener to dispose Kernel resources on user exit.
      frame.addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent _windowEvent) {
            System.exit(0);
         }
      });

      // Wait until the user selects a zoom-in point on the Mandelbrot view.
      while (true) {

         // Wait for the user to click somewhere
         while (to == null) {
            synchronized (doorBell) {
               try {
                  doorBell.wait();
               } catch (InterruptedException ie) {
                  ie.getStackTrace();
               }
            }
         }

        
         float x = -1f;
         float y = 0f;
         float defaultScale=3f;
         float scale = 3f;
         float tox = (float) (to.x - width / 2) / width * scale;
         float toy = (float) (to.y - height / 2) / height * scale;

         // This is how many frames we will display as we zoom in and out.
         int frames = 128;
         long startMillis = System.currentTimeMillis();
         for (int sign = -1; sign < 2; sign += 2) {
            for (int i = 0; i < frames - 4; i++) {
               scale = scale + sign * defaultScale / frames;
               x = x - sign * (tox / frames);
               y = y - sign * (toy / frames);

               // Set the scale and offset, execute the kernel and force a repaint of the viewer.
               displayMandel(width, height,  x, y, scale, rgb, pallette);
               System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
               viewer.repaint();
            }
         }

         long elapsedMillis = System.currentTimeMillis() - startMillis;
         System.out.println("FPS = " + frames * 1000 / elapsedMillis);
         // Reset zoom-in point.
         to = null;
      }

   }

}
