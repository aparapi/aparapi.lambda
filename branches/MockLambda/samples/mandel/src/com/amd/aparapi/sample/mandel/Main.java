/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

 */

package com.amd.aparapi.sample.mandel;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;


class MandelbrotCoordinate {
   public static List<MandelbrotCoordinate> allCoordinates;
   public static int rgb[];

   int pos;
   public MandelbrotCoordinate(int p) { pos = p; }
   public int getPos() { return pos; }
}

public class Main{

   /** Width of Mandelbrot view. */
   static final int width = 768;

   /** Height of Mandelbrot view. */
   static final int height = 768;

   /** Image for Mandelbrot view. */
   static final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   static final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

   // Extract the underlying RGB buffer from the image.
   static final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
   static final int[] imageRgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

   static float defaultScale = 3f;
   /** Maximum iterations for Mandelbrot. */
   static final private int maxIterations = 64;

   /** Palette which maps iteration values to RGB values. */
   static final int pallette[] = new int[maxIterations + 1];

   /** User selected zoom-in point on the Mandelbrot view. */
   public static volatile Point to = null;

   // This is how many frames we will display as we zoom in and out.
   static final int frames = 128;

   // These are static so zoom out continues from where zoom in stopped
   static float scale = defaultScale;
   static float x = -1f;
   static float y = 0f;

   // Draw Mandelbrot image
   static JComponent viewer = new JComponent(){
      @Override public void paintComponent(Graphics g) {
         g.drawImage(image, 0, 0, width, height, this);
      }
   };


   enum ZoomDirection {
      ZOOM_IN(-1), ZOOM_OUT(1);

      private int sign;

      private ZoomDirection(int c) { sign = c; }
      public int getSign()         {  return sign; }
   }


   public static int getCount(float x, float y){
      int count =0;
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
      return(count);
   }


   static int[]	getNextImage(float x, float y, float scale) {
      Arrays.parallel(MandelbrotCoordinate.allCoordinates.toArray(new MandelbrotCoordinate[1])).
      forEach(p -> {
         /** Determine which RGB value we are going to process (0..RGB.length). */
         int gid = p.getPos();

         /** Translate the gid into an x an y value. */
         float lx = (((gid % width * scale) - ((scale / 2) * width)) / width) + x;
         float ly = (((gid / width * scale) - ((scale / 2) * height)) / height) + y;

         int count = getCount(lx,ly);

         // Pull the value out of the palette for this iteration count.
         p.rgb[gid] = pallette[count];
      });
      return MandelbrotCoordinate.allCoordinates.get(0).rgb;
   }


   static void doZoom(int sign, float tox, float toy) {
      // Zoom in or out per iteration 
      for (int i = 0; i < frames - 4; i++) {
         scale = scale + sign * defaultScale / frames;
         x = x - sign * (tox / frames);
         y = y - sign * (toy / frames);

         getNextImage(x, y, scale);

         System.arraycopy(rgb, 0, imageRgb, 0, rgb.length);
         viewer.repaint();
      }
   }


   static void zoomInAndOut(Point to, int[] rgb, int[] imageRgb) {
      float tox = (float) (to.x - width / 2) / width * defaultScale;
      float toy = (float) (to.y - height / 2) / height * defaultScale;

      // This cannot be parallel lambda or you will get a headache!!
      Arrays.stream(ZoomDirection.values()).forEach( e -> {
         doZoom(e.getSign(), tox, toy); 
         System.out.println("inner done, sign=" + e.getSign() );          
      } );
   }


   public static void main(String[] _args) {
      JFrame frame = new JFrame("MandelBrot");
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

      //Initialize palette values
      for (int i = 0; i < maxIterations; i++) {
         float h = i / (float) maxIterations;
         float b = 1.0f - h * h;
         pallette[i] = Color.HSBtoRGB(h, 1f, b);
      }

      // Used to find the index in the rgb array when processing 
      // each element in the lambda  
      MandelbrotCoordinate.allCoordinates = new ArrayList<MandelbrotCoordinate>(width*height);
      for(int i=0; i<width*height; i++) {
         MandelbrotCoordinate.allCoordinates.add(new MandelbrotCoordinate(i));
      }
      MandelbrotCoordinate.rgb = rgb;

      getNextImage(x, y, scale);

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

         long startMillis = System.currentTimeMillis();

         zoomInAndOut(to, rgb, imageRgb);

         long elapsedMillis = System.currentTimeMillis() - startMillis;
         System.out.println("FPS = " + frames * 1000 / elapsedMillis);

         // Reset zoom-in point.
         to = null;
      }
   }
}
