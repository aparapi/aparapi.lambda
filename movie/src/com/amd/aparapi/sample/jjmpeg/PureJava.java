package com.amd.aparapi.sample.jjmpeg;

import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
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
public class PureJava{
   

   
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
               name = "C:\\Users\\gfrost\\Downloads\\HK2207_720p.mp4";
               final JJMediaReader reader = new JJMediaReader(name);
               final JJReaderVideo vs = reader.openFirstVideoStream();
               final BufferedImage image = vs.createImage();
               final BufferedImage imageOut = vs.createImage();
               label.setIcon(new ImageIcon(imageOut));
               frame.pack();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
               java.awt.image.Kernel conv = new  java.awt.image.Kernel(3, 3, new float[]{0f, -10f, 0f, -10f, 40f, -10f, 0f, -10f, 0f});
               
               final ConvolveOp convOp = new ConvolveOp(conv, ConvolveOp.EDGE_NO_OP, null);
               new Thread(new Runnable(){
                  public void run() {
                     try {
                        while (true) {
                           JJMediaReader.JJReaderStream rs = reader.readFrame();
                           if (rs != null) {
                              vs.getOutputFrame(image);
                              long start = System.currentTimeMillis();
                              convOp.filter(image, imageOut);
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
                        Logger.getLogger(PureJava.class.getName()).log(Level.SEVERE, null, ex);
                     }
                  }
               }).start();
            } catch (Exception ex) {
               Logger.getLogger(PureJava.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });
   }
}
