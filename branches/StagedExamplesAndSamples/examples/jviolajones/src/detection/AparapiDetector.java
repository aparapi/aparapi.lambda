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

      private int width;

      private int height;

      private int scaledFeatureWidth;

      private int scaledFeatureStep;

      private float scale;

      private int[] weightedGrayImage;

      private int[] weightedGrayImageSquared;

      private int MAXFOUND = 100;

      private int[] rects = new int[MAXFOUND * 4];

      private int[] found = new int[1];
      
      private List<Rectangle> features;

      @Override public void run() {

         int i = getGlobalId(0) * scaledFeatureStep;

         int j = getGlobalId(1) * scaledFeatureStep;
         
         if (i < width - scaledFeatureWidth ){
            if ( j < height - scaledFeatureWidth ){
               
               Rectangle rectangle = haarCascade.getFeature(weightedGrayImage, weightedGrayImageSquared, width, height, i, j,
                     scale, scaledFeatureWidth);
               if (rectangle != null) {

                  features.add(rectangle);

               }
           
            }else{
              // System.out.println("out j "+j);
            }
         }else{
           // System.out.println("out i"+i);
         }

      }

      public void set(int _width, int _height, float _scale, int _scaledFeatureWidth, int _scaledFeatureStep,
            int[] _weightedGrayImage, int[] _weightedGreyImageSquared,List<Rectangle> _features) {
         width = _width;
         height = _height;
         scale = _scale;
         scaledFeatureWidth = _scaledFeatureWidth;
         scaledFeatureStep = _scaledFeatureStep;

         weightedGrayImage = _weightedGrayImage;
         weightedGrayImageSquared = _weightedGreyImageSquared;
         features = _features;

      }

   }

   DetectorKernel kernel;

   AparapiDetector(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);

      kernel = new DetectorKernel();
   }

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {
      final List<Rectangle> features = new ArrayList<Rectangle>();
      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int scaledFeatureStep = (int) (scale * haarCascade.width * increment);
         final int scaledFeatureWidth = (int) (scale * haarCascade.width);

         Range range = Range.create2D(width- scaledFeatureWidth, height -scaledFeatureWidth);
         System.out.println(range);
         kernel.set(width, height, scale, scaledFeatureWidth, scaledFeatureStep, weightedGrayImage, weightedGrayImageSquared, features);
         kernel.execute(range);
      }

     

      return (features);
   }

}
