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

      //  final private int[] stageIds;
      final private int stage_ids;

      // final private int tree_ids;

      final private int[] tree_startEnd;

      final private int[] stage_startEnd;

      final private float[] stage_thresh;

      final private int FEATURE_FLOATS;

      final private int FEATURE_INTS;

      final private int RECT_FLOATS;

      final private int RECT_INTS;

      final private int STAGE_FLOATS;

      final private int STAGE_INTS;

      final private int TREE_INTS;

      final private int[] feature_r1r2r3LnRn;

      final private int[] rect_x1y1x2y2;

      final private float[] rect_w;

      final private float[] feature_LvRvThres;

      final int cascadeWidth;

      final int cascadeHeight;

      public DetectorKernel(HaarCascade _haarCascade) {
         //  stageIds=_haarCascade.stageIds;
         stage_ids = _haarCascade.stage_ids;
         stage_startEnd = _haarCascade.stage_startEnd;
         stage_thresh = _haarCascade.stage_thresh;
         FEATURE_FLOATS = HaarCascade.FEATURE_FLOATS;
         FEATURE_INTS = HaarCascade.FEATURE_INTS;
         RECT_FLOATS = HaarCascade.RECT_FLOATS;
         RECT_INTS = HaarCascade.RECT_INTS;
         STAGE_FLOATS = HaarCascade.STAGE_FLOATS;
         STAGE_INTS = HaarCascade.STAGE_INTS;
         TREE_INTS = HaarCascade.TREE_INTS;
         //  tree_ids = _haarCascade.tree_ids;
         tree_startEnd = _haarCascade.tree_startEnd;
         feature_r1r2r3LnRn = _haarCascade.feature_r1r2r3LnRn;
         feature_LvRvThres = _haarCascade.feature_LvRvThres;
         rect_w = _haarCascade.rect_w;
         rect_x1y1x2y2 = _haarCascade.rect_x1y1x2y2;
         cascadeWidth = _haarCascade.cascadeWidth;
         cascadeHeight = _haarCascade.cascadeHeight;
      }

      boolean pass(int stageId, int[] grayImage, int[] squares, int width, int height, int i, int j, float scale) {

         float sum = 0;
         for (int treeId = stage_startEnd[stageId * STAGE_INTS + 0]; treeId <= stage_startEnd[stageId * STAGE_INTS + 1]; treeId++) {

            //  System.out.println("stage id " + stageId + "  tree id" + treeId);
            int featureId = tree_startEnd[treeId * TREE_INTS + 0];
            float thresh = 0f;
            boolean done = false;
            while (!done) {
               //  System.out.println("feature id "+featureId);

               int w = (int) (scale * cascadeWidth);
               int h = (int) (scale * cascadeHeight);
               float inv_area = 1f / (w * h);
               //System.out.println("w2 : "+w2);
               int total_x = grayImage[i + w + (j + h) * width] + grayImage[i + (j) * width] - grayImage[i + (j + h) * width]
                     - grayImage[i + w + (j) * width];
               int total_x2 = squares[i + w + (j + h) * width] + squares[i + (j) * width] - squares[i + (j + h) * width]
                     - squares[i + w + (j) * width];
               float moy = total_x * inv_area;
               float vnorm = total_x2 * inv_area - moy * moy;
               vnorm = (vnorm > 1) ? sqrt(vnorm) : 1;
               // System.out.println(vnorm);
               int rect_sum = 0;
               for (int r = 0; r < 3; r++) {
                  int rectId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + r];
                  if (rectId != -1) {
                     // System.out.println("rect " + r + " id " + rectId);
                     int x1 = rect_x1y1x2y2[rectId * RECT_INTS + 0];
                     int y1 = rect_x1y1x2y2[rectId * RECT_INTS + 1];
                     int x2 = rect_x1y1x2y2[rectId * RECT_INTS + 2];
                     int y2 = rect_x1y1x2y2[rectId * RECT_INTS + 3];
                     float weight = rect_w[rectId * RECT_FLOATS + 0];
                     int rx1 = i + (int) (scale * x1);
                     int rx2 = i + (int) (scale * (x1 + y1));
                     int ry1 = j + (int) (scale * x2);
                     int ry2 = j + (int) (scale * (x2 + y2));
                     //System.out.println((rx2-rx1)*(ry2-ry1)+" "+r.weight);
                     rect_sum += (int) ((grayImage[rx2 + (ry2) * width] - grayImage[rx1 + (ry2) * width]
                           - grayImage[rx2 + (ry1) * width] + grayImage[rx1 + (ry1) * width]) * weight);
                  }
               }
               // System.out.println(rect_sum);
               float rect_sum2 = rect_sum * inv_area;

               // System.out.println(rect_sum2+" "+ Feature.LvRvThres[featureId * Feature.FLOATS + 2]*vnorm);  

               if (rect_sum2 < feature_LvRvThres[featureId * FEATURE_FLOATS + 2] * vnorm) {

                  int leftNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 3];
                  if (leftNodeId == -1) {
                     //  System.out.println("left-val");
                     thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 0];
                     done = true;
                  } else {
                     // System.out.println("left");
                     featureId = leftNodeId;
                  }
               } else {
                  int rightNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 4];
                  if (rightNodeId == -1) {
                     // System.out.println("right-val");
                     thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 1];
                     done = true;
                  } else {
                     //  System.out.println("right");
                     featureId = rightNodeId;
                  }
               }
            }

            sum += thresh;
         }
         //System.out.println(sum+" "+threshold);

         return sum > stage_thresh[stageId * STAGE_FLOATS + 0];
      }

      public void getFeature(int[] grayImage, int[] squares, int width2, int height2, int i_final, int j_final, float scale_final,
            int size_final) {
         boolean pass = true;
         // Rectangle rectangle = null;
         for (int stageId = 0; pass == true && stageId < stage_ids; stageId++) {

            if (!pass(stageId, grayImage, squares, width2, height2, i_final, j_final, scale_final)) {
               pass = false;

            }
         }
         if (pass) {
            int value = atomicAdd(found, 0, 1);
            // System.out.println("value ="+value);
            rects[value * 4 + 0] = i_final;
            rects[value * 4 + 1] = j_final;
            rects[value * 4 + 2] = size_final;
            rects[value * 4 + 3] = size_final;
         }

      }

      @Override public void run() {
         int i = getGlobalId(0) * scaledFeatureStep;
         int j = getGlobalId(1) * scaledFeatureStep;
         //  System.out.println("i="+i+"  j="+j);
         getFeature(weightedGrayImage, weightedGrayImageSquared, width, height, i, j, scale, scaledFeatureWidth);

      }

      public void set(int _width, int _height, float _scale, int _scaledFeatureWidth, int _scaledFeatureStep,
            int[] _weightedGrayImage, int[] _weightedGreyImageSquared, List<Rectangle> _features) {
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

      kernel = new DetectorKernel(haarCascade);
      kernel.setExplicit(true);
      kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
   }

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {
      final List<Rectangle> features = new ArrayList<Rectangle>();
      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int scaledFeatureStep = (int) (scale * haarCascade.cascadeWidth * increment);
         final int scaledFeatureWidth = (int) (scale * haarCascade.cascadeWidth);

         Range range = Range.create2D((width - scaledFeatureWidth) / scaledFeatureStep, (height - scaledFeatureWidth)
               / scaledFeatureStep);
         System.out.println(range);
         kernel.found[0] = 0;
         kernel.set(width, height, scale, scaledFeatureWidth, scaledFeatureStep, weightedGrayImage, weightedGrayImageSquared,
               features);
         kernel.execute(range);
         System.out.println(kernel.getExecutionMode() + " " + kernel.getConversionTime());
         System.out.println(kernel.getExecutionMode() + " " + kernel.getExecutionTime());
         kernel.get(kernel.found);
         kernel.get(kernel.rects);
         for (int i = 0; i < kernel.found[0]; i++) {
            features.add(new Rectangle(kernel.rects[i * 4 + 0], kernel.rects[i * 4 + 1], kernel.rects[i * 4 + 2],
                  kernel.rects[i * 4 + 3]));
         }
      }

      return (features);
   }

}
