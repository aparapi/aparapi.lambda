package detection;

/**
This project is based on the open source jviolajones project created by Simon
Houllier and is used with his permission. Simon's jviolajones project offers 
a pure Java implementation of the Viola-Jones algorithm.

http://en.wikipedia.org/wiki/Viola%E2%80%93Jones_object_detection_framework

The original Java source code for jviolajones can be found here
http://code.google.com/p/jviolajones/ and is subject to the
gnu lesser public license  http://www.gnu.org/licenses/lgpl.html

Many thanks to Simon for his excellent project and for permission to use it 
as the basis of an Aparapi example.
**/

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class AparapiDetector extends Detector{

   class DetectorKernel extends Kernel{

      @Override public void run() {
         // TODO Auto-generated method stub

      }

      public void set(int width, int height, float maxScale, int[] weightedGrayImage, int[] weightedGrayImageSquared) {
         // TODO Auto-generated method stub

      }

   }

   DetectorKernel kernel;

   AparapiDetector(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);

      kernel = new DetectorKernel();
   }

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {
      
      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int scaledFeatureStep = (int) (scale * haarCascade.width * increment);
         final int scaledFeatureWidth = (int) (scale * haarCascade.width);
         final float scale_f = scale;
         Range range = Range.create2D(width, height);
      }
      
      Range range = Range.create2D(width, height);
      final List<Rectangle> ret = new ArrayList<Rectangle>();
      kernel.set(width, height, maxScale, weightedGrayImage, weightedGrayImageSquared);
      kernel.execute(range);
      return (ret);
   }

}
