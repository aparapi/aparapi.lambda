package com.amd.aparapi.sample.jjmpeg;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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
   private static final ConvolutionFilter EDGE = new ConvolutionFilter(0f, -1f, 0f, -1f, 4.1f, -1f, 0f, -1f, 0f, 1f);

   private static final ConvolutionFilter NONE = new ConvolutionFilter(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f);

   private static final ConvolutionFilter BLUR = new ConvolutionFilter(.11f, .11f, .11f, .11f, .11f, .11f, .11f, .11f, .11f, 1f);

   private static final ConvolutionFilter EMBOSS = new ConvolutionFilter(-4f, -2f, 0f, -2f, 0f, 2f, 0f, 2f, 4f, 2f);

   public static class ConvolutionKernel extends Kernel{

      private final float[] filter = new float[9];
      
      private float weight =1f;

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
         put(filter).put(inputData).put(outputData);
        // this.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
         //  setExplicit(true); // This gives us a performance boost
      }
      
      int byteToInt(byte b){     
         if (b < 0) {
            return(255+(int)b);
         }else{
            return((int)b);
         }
      }
      byte intToByte(int i){
         if (i > 127){
            return ((byte) (i-256));
         }else{
            return ( (byte) i);
         }
      }

      public void processPixel(int x, int y, int w, int h) {
         float accum = 0;
         for (int count = 0; count < 9; count++) {
            int dx = 3 * ((count % 3) - 1); // 0,1,2 -> -3,0,3
            int dy = (count / 3) - 1; // 0,1,2 -> -1,0,1
            int rgb = byteToInt((inputData[((y + dy) * w) + (x + dx)]));        
            accum += (rgb & 0xff) * filter[count];
         }
         int value =  (int)max(0, min(accum, 255));
         outputData[y * w + x] = intToByte((int)(value*weight));
      }

      public void run() {
         int x = getGlobalId(0);
         int y = getGlobalId(1);
         int w = getGlobalSize(0);
         int h = getGlobalSize(1);
         if (x > 3 && x < (w - 3) && y > 1 && y < (h - 1)) {
            processPixel(x, y, w, h);
         }
      }

      public void apply(ConvolutionFilter _filter, BufferedImage _image) {

         byte[] imageBytes = ((DataBufferByte) _image.getRaster().getDataBuffer()).getData();
        // System.out.println("image = " + imageBytes.length + " " + (width * height * 3));
         System.arraycopy(imageBytes, 0, inputData, 0, imageBytes.length);
         System.arraycopy(_filter.weights, 0, filter, 0, _filter.weights.length);
         weight = _filter.weight;
         long start = System.currentTimeMillis();
         if (false) {

            for (int x = 3; x < width * 3 - 3; x++) {
               for (int y = 1; y < height-1; y++) {
                  processPixel(x, y, width * 3, height);
               }
            }
         } else {
            put(filter).put(inputData).put(outputData);
            execute(range);
            get(outputData);
         }
         System.arraycopy(outputData, 0, imageBytes, 0, imageBytes.length);
         System.out.println("elapsed  =" + (System.currentTimeMillis() - start));
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
                              kernel.apply(EMBOSS, image);

                              // System.out.println(kernel.getExecutionTime());
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
