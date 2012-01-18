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

package com.amd.aparapi.sample.prefix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

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

   public static class DimsKernel extends Kernel{

      private final int[] image;

      final int global_pallette[];

      final int width_pallette[];

      final int height_pallette[];

      final int group_pallette[];

      public int[] createPallette(int _range) {
         int pallette[] = new int[_range + 1];
         for (int i = 0; i < _range; i++) {
            float h = i / (float) _range;
            float b = 1.0f - h * h;
            pallette[i] = Color.HSBtoRGB(h, 1f, b);
         }
         return (pallette);
      }

      public DimsKernel(BufferedImage _image, Range _range) {
         System.out.println(_range);
         image = ((DataBufferInt) _image.getRaster().getDataBuffer()).getData();
         int width = _range.getGlobalSize(0);
         int height = _range.getGlobalSize(1);

         global_pallette = createPallette(width * height);
         width_pallette = createPallette(width);
         height_pallette = createPallette(height);
         group_pallette = createPallette(_range.getNumGroups(0) * _range.getNumGroups(1));

      }

      public void run() {

         int x = getGlobalId(0);
         int y = getGlobalId(1);
         int w = getGlobalSize(0);
         int h = getGlobalSize(1);
         image[y * w + x] = width_pallette[getGroupId()];

      }

   }

   public static void main(String[] _args) throws IOException {

      JFrame frame = new JFrame("Dims");

      final int width = 256;

      final int height = 256;

      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final Range range = Range.create2D(width, height, 4, 4);
      final DimsKernel lifeKernel = new DimsKernel(image, range);

      @SuppressWarnings("serial") JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics g) {

            g.drawImage(image, 0, 0, width, height, 0, 0, width, height, this);
         }
      };

      // Set the default size and add to the frames content pane
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);

      // Swing housekeeping
      frame.pack();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      lifeKernel.execute(range);

      viewer.repaint(); // Request a repaint of the viewer (causes paintComponent(Graphics) to be called later not synchronous

   }
}
