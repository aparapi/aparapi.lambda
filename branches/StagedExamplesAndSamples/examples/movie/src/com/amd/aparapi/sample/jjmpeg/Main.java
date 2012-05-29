package com.amd.aparapi.sample.jjmpeg;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import au.notzed.jjmpeg.io.JJMediaReader;
import au.notzed.jjmpeg.io.JJMediaReader.JJReaderVideo;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * Code based on Demo of JJVideoScanner class
 *
 * @author notzed
 */
public class Main{
   final static class ConvolutionFilter{
      private float[] weights;

      private float weight;

      ConvolutionFilter(float _nw, float _n, float ne, float _w, float _o, float _e, float _sw, float _s, float _se, float _weight) {
         weights = new float[] {
               _nw,
               _w,
               ne,
               _w,
               _o,
               _e,
               _sw,
               _s,
               _se
         };
         weight = _weight;
      }

   }

   private static final ConvolutionFilter EDGE = new ConvolutionFilter(0f, -10f, 0f, -10f, 40f, -10f, 0f, -10f, 0f, 1f);

   private static final ConvolutionFilter NONE = new ConvolutionFilter(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f);

   private static final ConvolutionFilter BLUR = new ConvolutionFilter(.11f, .11f, .11f, .11f, .11f, .11f, .11f, .11f, .11f, 1f);

   private static final ConvolutionFilter EMBOSS = new ConvolutionFilter(-2f, -1f, 0f, -1f, 0f, 1f, 0f, 1f, 2f, 1f);

   public static class ConvolutionKernel extends Kernel{

      private final float[] filter = new float[9];

      private float weight = 1f;

      private final byte[] inputData;

      private final byte[] outputData;

      private final int width;

      private final int height;

      private Range range;

      public ConvolutionKernel(int _width, int _height) {
         width = _width;
         height = _height;
         range = Range.create2D(width * 3, height);
         inputData = new byte[3 * width * height];
         outputData = new byte[3 * width * height];
         System.out.println(range);
         this.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
         setExplicit(true); // This gives us a performance boost
      }

      public void processPixel(int x, int y, int w, int h) {
         float accum = 0;
         int count = 0;
         for (int dx = -3; dx < 6; dx += 3) {
            for (int dy = -1; dy < 2; dy += 1) {
               int rgb = 0xff & inputData[((y + dy) * w) + (x + dx)];
               accum += rgb * filter[count++];
            }
         }
         outputData[y * w + x] = (byte) (((int) max(0, min(accum * weight, 255))) & 0xff);
      }

      public void run() {
         int x = getGlobalId(0);
         int y = getGlobalId(1);
         int w = getGlobalSize(0);
         int h = getGlobalSize(1);
         if (x > 3 && x < (w - 3) && y > 1 && y < (h - 1)) {
            processPixel(x, y, w, h);
         } else {
            outputData[y * w + x] = inputData[(y * w) + x];
         }
      }

      public void apply(ConvolutionFilter _filter, BufferedImage _image) {

         byte[] imageBytes = ((DataBufferByte) _image.getRaster().getDataBuffer()).getData();
         System.arraycopy(imageBytes, 0, inputData, 0, imageBytes.length);
         System.arraycopy(_filter.weights, 0, filter, 0, _filter.weights.length);
         weight = _filter.weight;

         if (false) {
            for (int x = 0; x < width * 3; x++) {
               for (int y = 0; y < height; y++) {
                  if (x > 3 && x < (width * 3 - 3) && y > 1 && y < (height - 1)) {
                     processPixel(x, y, width * 3, height);
                  }
               }
            }

         } else if (false) {
            int threadCount = Runtime.getRuntime().availableProcessors();

            //  Thread[] threads = new Thread[threadCount];
            final CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);
            for (int thread = 0; thread < threadCount; thread++) {
               final int threadId = thread;
               final int groupHeight = height / threadCount;
               //  System.out.println("groupHeight = "+groupHeight+ "height="+height);
               new Thread(new Runnable(){
                  public void run() {
                     for (int x = 0; x < width * 3; x++) {
                        for (int y = groupHeight * threadId; y < groupHeight * (threadId + 1); y++) {
                           if (x > 3 && x < (width * 3 - 3) && y > 1 && y < (height - 1)) {
                              processPixel(x, y, width * 3, height);
                           }
                        }
                     }
                     try {
                        barrier.await();
                     } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                     } catch (BrokenBarrierException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                     }
                  }
               }).start();

            }
            try {
               barrier.await();
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (BrokenBarrierException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         } else {
            put(filter).put(inputData);
            execute(range);
            get(outputData);
         }
         System.arraycopy(outputData, 0, imageBytes, 0, imageBytes.length);

      }
   }

   public static void main(final String[] args) {
      SwingUtilities.invokeLater(new Runnable(){
         public void run() {
            JFrame frame = new JFrame("Video Frames");
            final JLabel label = new JLabel();
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(label, BorderLayout.CENTER);
            try {
               String name = "c:\\users\\gfrost\\Desktop\\afds\\MV5BMjEyMjMzODc0MV5BMTFeQW1wNF5BbWU3MDE3NzA0Nzc@.mp4";
               name = "C:\\Users\\gfrost\\Downloads\\leo_1080p.mov";
               //   name = "C:\\Users\\gfrost\\Downloads\\HK2207_720p.mp4";
               // name= "C:\\Users\\gfrost\\Downloads\\Froblins.H.264-SD.mov";
               name = "C:\\Users\\gfrost\\Downloads\\leo_1080p.mov";

               final JJMediaReader reader = new JJMediaReader(name);
               final JJReaderVideo vs = reader.openFirstVideoStream();
               final BufferedImage image = vs.createImage();
               final ConvolutionKernel kernel = new ConvolutionKernel(image.getWidth(), image.getHeight());
               label.setIcon(new ImageIcon(image));
               frame.pack();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
               new Thread(new Runnable(){
                  public void run() {
                     try {
                        while (true) {
                           JJMediaReader.JJReaderStream rs = reader.readFrame();
                           if (rs != null) {
                              vs.getOutputFrame(image);
                              long start = System.currentTimeMillis();
                              kernel.apply(EDGE, image);
                              System.out.println("elapsed  =" + (System.currentTimeMillis() - start));

                              //System.out.println(kernel.getExecutionTime());
                              label.repaint();
                           } else {
                              System.out.println("end of file, restart");
                              reader.dispose();
                              System.exit(1);
                           }
                           Thread.sleep(1);
                        }
                     } catch (Exception ex) {
                        ex.printStackTrace();
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                     }
                  }
               }).start();
            } catch (Exception ex) {
               Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });
   }
}
