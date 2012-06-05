package com.amd.aparapi.sample.jjmpeg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.amd.aparapi.examples.jviolajones.Detector;
import com.amd.aparapi.examples.jviolajones.HaarCascade;
import com.amd.aparapi.examples.jviolajones.MultiThreadedDetector;

/**
 * Code based on Demo of JJVideoScanner class
 *
 * @author notzed
 */
public class Faces{

   public static void main(final String[] args) {

      HaarCascade haarCascade = HaarCascade.create("..\\jviolajones\\haarcascade_frontalface_alt2.xml");
      final Detector detector = new MultiThreadedDetector(haarCascade, 1f, 2f, 0.1f, false);

      new JJMPEGPlayer("Faces", "faces.mp4", false){
          @Override protected void process(Graphics2D gc, BufferedImage image) {
            List<Rectangle> rects = detector.getFeatures(image);
            gc.setColor(Color.RED);
            for (Rectangle rect : rects) {
               gc.draw(rect);
            }
         }
      };

   }
}
