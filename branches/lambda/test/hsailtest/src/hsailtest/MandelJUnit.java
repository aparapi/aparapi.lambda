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

package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.sample.common.AparapiModeToggleButton;
import com.amd.aparapi.sample.common.FPSCounter;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static org.junit.Assert.fail;

public class MandelJUnit {
    final static int FRAMES_PER_ZOOM = 128;
    final static int MAX_ZOOMS = 1;
    final static int FRAMES_PER_DEVICE=MAX_ZOOMS*FRAMES_PER_ZOOM*2;
    final int width = 768;
    final int height = 768;

    final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    final int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    final int[][][] frameRgb = new int[2][FRAMES_PER_DEVICE][rgb.length];


    final int maxIterations = 64;

    //Palette which maps iteration values to RGB values.
    final int pallette[] = new int[maxIterations + 1];

    // These are members so zoom out continues from where zoom in stopped

    int getMandelCount(float x, float y, int maxIterations) {
        float zx = x;
        float zy = y;
        float new_zx;
        int count = 0;
        while (count < maxIterations && zx * zx + zy * zy < 8) {
            new_zx = zx * zx - zy * zy + x;
            zy = 2 * zx * zy + y;
            zx = new_zx;
            count++;
        }
        return (count);

    }


    void getNextImage(Device device, int[] frameRgb, final float x_offset, final float y_offset, final float scale) {
        int[] data = new int[rgb.length];
        device.forEach(width * height, gid -> {
            float lx = ((((gid % width) * scale) - ((scale / 2) * width)) / width) + x_offset;
            float ly = (((gid / width * scale) - ((scale / 2) * height)) / height) + y_offset;
            int count = getMandelCount(lx, ly, maxIterations);
            rgb[gid] = pallette[count];
            data[gid]=count;
        });
        if (frameRgb != null){
           System.arraycopy(data, 0, frameRgb, 0, data.length);
        }
    }

    final static boolean showUI=Boolean.getBoolean("showUI");
    @Test
    public void test(){
        JComponent viewer = null;
        if (showUI){
        JFrame frame = new JFrame("MandelBrot");
        viewer = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, width, height, this);
            }
        };
        viewer.setPreferredSize(new Dimension(width, height));
        frame.getContentPane().add(viewer, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent _windowEvent) {
                System.exit(0);
            }
        });
        }

        for (int i = 0; i < maxIterations; i++) {
            float h = i / (float) maxIterations;
            float b = 1.0f - h * h;
            pallette[i] = Color.HSBtoRGB(h, 1f, b);
        }

        Device device = Device.hsa();


        getNextImage(device, null, -1f, 0f, 3f);
        if (showUI){
            viewer.repaint();
        }

        Device[] devices = new Device[]{Device.jtp(),Device.hsa()};

        for (int deviceIndex = 0; deviceIndex<devices.length; deviceIndex++){
        int frameCount = 0;
      //  int framesToShowPerZoom = FRAMES_PER_ZOOM;
        long startMs = System.currentTimeMillis();

        for (int zooms = 0; zooms < MAX_ZOOMS; zooms++) {
            float scale = 3f;
            float x = -1f;
            float y = 0f;
            for (int sign : new int[]{-1, 1}) {
                float tox = 1;
                float toy = 1;
                for (int i = 0; i < FRAMES_PER_ZOOM - 4; i++) {
                    scale = scale + sign * 3f / FRAMES_PER_ZOOM;
                    x = x - sign * (tox / FRAMES_PER_ZOOM);
                    y = y - sign * (toy / FRAMES_PER_ZOOM);
                    getNextImage(devices[deviceIndex], frameRgb[deviceIndex][frameCount], x, y, scale);
                    if (showUI){
                        viewer.repaint();
                    }
                    frameCount++;
                }
            }
        }

        long endMs = System.currentTimeMillis();
        long elapsedMs = (endMs - startMs);
        System.out.println((deviceIndex==0?"jtp":"hsa")+" fps=" + (((float) frameCount*1000) / elapsedMs));
        }
        for (int i=0; i< FRAMES_PER_DEVICE; i++){
            if (!JunitHelper.compare(frameRgb[0][i], frameRgb[1][i])){
                fail("failed at index " + i);
            }
        }
    }


}
