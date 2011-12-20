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

package com.amd.aparapi.sample.convolution;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * An example Aparapi application which demonstrates image manipulation via convolution filter
 * 
 * Converted to use int buffer and some performance tweaks by Gary Frost
 * http://processing.org/learning/pixels/
 * 
 * @author Gary Frost
 */
public class Main{

   public static class ConvolutionKernel extends Kernel{
     // http://docs.gimp.org/en/plug-in-convmatrix.html
    
      private final float[] filterBlur = new float[] {
            1f,
            1f,
            1f,
            1f,
            1f,
            1f,
            1f,
            1f,
            1f,
            0f
      }; // NW,N,NE, W,0,E,SW,S,SE,adjust
      private final float[] filterEdge = new float[] {
            -1f,
            -1f,
            -1f,
            -1f,
            9f,
            -1f,
            -1f,
            -1f,
            -1f,
            127f
      }; // NW,N,NE, W,0,E,SW,S,SE 
      
      private final float[] filterEdge1 = new float[] {
            0f,
            0f,
            0f,
            -1f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f
      }; // NW,N,NE, W,0,E,SW,S,SE 
      
      private final float[] filterEdge2 = new float[] {
            0f,
            1f,
            0f,
            1f,
            -4f,
            1f,
            0f,
            1f,
            0f,
            0f
      }; // NW,N,NE, W,0,E,SW,S,SE 
      
      private final float[] filterEmboss = new float[] {
            -2f,
            -1f,
            0f,
            -1f,
            1f,
            1f,
            1f,
            1f,
            2f,
            0f
      }; // NW,N,NE, W,0,E,SW,S,SE 
      
      private final float[] filter = filterEmboss;
      private final int[] imageData;

      private final int width;

      private final int height;

      private int fromBase;

      private int toBase;

      public ConvolutionKernel(int _width, int _height, BufferedImage _image) {
         imageData = ((DataBufferInt) _image.getRaster().getDataBuffer()).getData();
         width = _width;
         height = _height;
         fromBase = height * width;
         toBase = 0;
         setExplicit(true); // This gives us a performance boost
         put(imageData); // Because we are using explicit buffer management we must put the imageData array

      }

      public void run() {

         int x = getGlobalX();
         int y = getGlobalY();
         int w = getGlobalWidth();
         int h = getGlobalHeight();
         if (x > 1 && x < (w - 1) && y > 1 && y < (h - 1)) {

            int result = 0;
            // We handle each color separately using rgbshift as an 8 bit mask for red, green, blue
            for (int rgbShift = 0; rgbShift < 24; rgbShift+=8) { // 0,8,16
               int channelAccum = 0;
               float accum=0;
               int count = 0;
               for (int dx = -1; dx < 2; dx++) { // west to east
                  for (int dy = -1; dy < 2; dy++) { // north to south
                     int rgb = (imageData[fromBase + ((y + dy) * w) + (x + dx)]);            
                     int channelValue = ((rgb>>rgbShift) & 0xff);
                      accum+=filter[count];
                     channelAccum += channelValue * filter[count++];
                 
                  }
               }
              
               channelAccum/=accum;
               channelAccum+=filter[count++];
               channelAccum = max(0,min(channelAccum, 0xff));
               result |= (channelAccum << rgbShift);
            }
            imageData[toBase + y * w + x] = result;
         }
      }

      public void nextGeneration() {
         int swap = fromBase;
         fromBase = toBase;
         toBase = swap;

         execute(Range.create2D(width, height));
         System.out.println(this.getAccumulatedExecutionTime() - this.getConversionTime());
      }

   }

   public static void main(String[] _args) throws IOException {

      JFrame frame = new JFrame("Convolution");
      final int width = Integer.getInteger("width", 1024 + 512);

      final int height = Integer.getInteger("height", 768);

      BufferedImage testCard = ImageIO.read(new File("testcard.jpg"));

      // Buffer is twice the size as the screen.  We will alternate between mutating data from top to bottom
      // and bottom to top in alternate generation passses. The LifeKernel will track which pass is which
      final BufferedImage image = new BufferedImage(width, height * 2, BufferedImage.TYPE_INT_RGB);
      image.getGraphics().drawImage(testCard, 0, 0, null);
      final ConvolutionKernel lifeKernel = new ConvolutionKernel(width, height, image);

      // Create a component for viewing the offsecreen image
      @SuppressWarnings("serial") JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {
            if (lifeKernel.isExplicit()) {
               lifeKernel.get(lifeKernel.imageData); // We only pull the imageData when we intend to use it.
            }
            // We copy one half of the offscreen buffer to the viewer, we copy the half that we just mutated.
            if (lifeKernel.toBase == 0) {
               g.drawImage(image, 0, 0, width, height, 0, 0, width, height, this);
            } else {
               g.drawImage(image, 0, 0, width, height, 0, height, width, 2 * height, this);
            }
         }
      };

      // Set the default size and add to the frames content pane
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);

      // Swing housekeeping
      frame.pack();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      lifeKernel.nextGeneration(); // Work is performed here
      viewer.repaint(); // Request a repaint of the viewer (causes paintComponent(Graphics) to be called later not synchronous

      long start = System.nanoTime();
      lifeKernel.nextGeneration(); // Work is performed here
      System.out.println((System.nanoTime() - start) / 1000000);
      viewer.repaint(); // Request a repaint of the viewer (causes paintComponent(Graphics) to be called later not synchronous
    
      
   }
}
