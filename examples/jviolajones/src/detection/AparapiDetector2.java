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

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class AparapiDetector2 extends Detector{

   class DetectorKernel extends Kernel{

      private int width;

      private int[] weightedGrayImage;

      private int[] weightedGrayImageSquared;

      static final private int MAX_FOUND = 200;

      static final private int RECT_FOUND_INTS = 3;

      private int[] found_rects = new int[MAX_FOUND * RECT_FOUND_INTS];

      private int[] found = new int[1];

      final private int stage_ids;

      final private int[] tree_startEnd;

      final private int[] stage_startEnd;

      final private float[] stage_thresh;

      static final private int FEATURE_FLOATS = HaarCascade.FEATURE_FLOATS;

      static final private int FEATURE_INTS = HaarCascade.FEATURE_INTS;

      static final private int RECT_FLOATS = HaarCascade.RECT_FLOATS;

      static final private int RECT_INTS = HaarCascade.RECT_INTS;

      static final private int STAGE_FLOATS = HaarCascade.STAGE_FLOATS;

      static final private int STAGE_INTS = HaarCascade.STAGE_INTS;

      static final private int TREE_INTS = HaarCascade.TREE_INTS;

      static final private int SCALE_INTS = ScaleInfo.SCALE_INTS;

      final private int[] feature_r1r2r3LnRn;

      final private int[] rect_x1y1x2y2;

      final private float[] rect_w;

      final private float[] feature_LvRvThres;

      private int scaleIds;

      final int cascadeWidth;

      final int cascadeHeight;

      private short[] scale_ValueWidthIJ;

      static final private int rectcache_size = 8192;

      final private int rect_x1y1x2y2_$local$[] = new int[rectcache_size];

      public DetectorKernel(HaarCascade _haarCascade) {

         stage_ids = _haarCascade.stage_ids;
         stage_startEnd = _haarCascade.stage_startEnd;
         stage_thresh = _haarCascade.stage_thresh;
         tree_startEnd = _haarCascade.tree_startEnd;
         feature_r1r2r3LnRn = _haarCascade.feature_r1r2r3LnRn;
         feature_LvRvThres = _haarCascade.feature_LvRvThres;
         rect_w = _haarCascade.rect_w;
         rect_x1y1x2y2 = _haarCascade.rect_x1y1x2y2;
         cascadeWidth = _haarCascade.cascadeWidth;
         cascadeHeight = _haarCascade.cascadeHeight;
         System.out.println("RECT_INTS=" + RECT_INTS);
         System.out.println("rect arr size=" + rect_x1y1x2y2.length);
         System.out.println("rect arr bytes=" + rect_x1y1x2y2.length * 8);

      }

      @Override public void run() {
         int scaleId = getGlobalId(0);
         if (scaleId < scaleIds) {
            int localSize = getLocalSize(0);
            int localId = getLocalId(0);
            int chunk = rectcache_size / localSize;
            for (int i = localId * chunk; i < (localId + 1) * chunk; i++) {
               rect_x1y1x2y2_$local$[i] = rect_x1y1x2y2[i];
            }
            this.localBarrier();
            short i = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 2];

            short j = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 3];
            short scaledFeatureWidth = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 1];
            short scale = (short) scale_ValueWidthIJ[scaleId * SCALE_INTS + 0];
            short w = (short) (scale * cascadeWidth);
            short h = (short) (scale * cascadeHeight);
            float inv_area = 1f / (w * h);
            boolean pass = true;
            for (short stageId = 0; pass == true && stageId < stage_ids; stageId++) {
               float sum = 0;
               for (int treeId = stage_startEnd[stageId * STAGE_INTS + 0]; treeId <= stage_startEnd[stageId * STAGE_INTS + 1]; treeId++) {
                  int featureId = tree_startEnd[treeId * TREE_INTS + 0];
                  float thresh = 0f;

                  for (boolean done = false; !done;) {

                     int total_x = weightedGrayImage[i + w + (j + h) * width] + weightedGrayImage[i + (j) * width]
                           - weightedGrayImage[i + (j + h) * width] - weightedGrayImage[i + w + (j) * width];
                     int total_x2 = weightedGrayImageSquared[i + w + (j + h) * width] + weightedGrayImageSquared[i + (j) * width]
                           - weightedGrayImageSquared[i + (j + h) * width] - weightedGrayImageSquared[i + w + (j) * width];
                     float moy = total_x * inv_area;
                     float vnorm = total_x2 * inv_area - moy * moy;
                     vnorm = (vnorm > 1) ? sqrt(vnorm) : 1;
                     int rect_sum = 0;

                     for (int r = 0; r < 3; r++) {
                        int rectId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + r];
                        if (rectId != -1) {
                           int x1 = 0;
                           int y1 = 0;
                           int x2 = 0;
                           int y2 = 0;
                           if (rectId < rectcache_size / 4) {
                              x1 = rect_x1y1x2y2_$local$[rectId * RECT_INTS + 0];
                              y1 = rect_x1y1x2y2_$local$[rectId * RECT_INTS + 1];
                              x2 = rect_x1y1x2y2_$local$[rectId * RECT_INTS + 2];
                              y2 = rect_x1y1x2y2_$local$[rectId * RECT_INTS + 3];
                           } else {
                              x1 = rect_x1y1x2y2[rectId * RECT_INTS + 0];
                              y1 = rect_x1y1x2y2[rectId * RECT_INTS + 1];
                              x2 = rect_x1y1x2y2[rectId * RECT_INTS + 2];
                              y2 = rect_x1y1x2y2[rectId * RECT_INTS + 3];
                           }
                           float weight = rect_w[rectId * RECT_FLOATS + 0];
                           int rx1 = i + (int) (scale * x1);
                           int rx2 = i + (int) (scale * (x1 + y1));
                           int ry1 = j + (int) (scale * x2);
                           int ry2 = j + (int) (scale * (x2 + y2));
                           rect_sum += (int) ((weightedGrayImage[rx2 + (ry2) * width] - weightedGrayImage[rx1 + (ry2) * width]
                                 - weightedGrayImage[rx2 + (ry1) * width] + weightedGrayImage[rx1 + (ry1) * width]) * weight);
                        }
                     }

                     float rect_sum2 = rect_sum * inv_area;

                     if (rect_sum2 < feature_LvRvThres[featureId * FEATURE_FLOATS + 2] * vnorm) {
                        int leftNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 3];
                        if (leftNodeId == -1) {
                           thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 0];
                           done = true;
                        } else {
                           featureId = leftNodeId;
                        }
                     } else {
                        int rightNodeId = feature_r1r2r3LnRn[featureId * FEATURE_INTS + 4];
                        if (rightNodeId == -1) {
                           thresh = feature_LvRvThres[featureId * FEATURE_FLOATS + 1];
                           done = true;
                        } else {
                           featureId = rightNodeId;
                        }
                     }
                  }

                  sum += thresh;
               }

               pass = sum > stage_thresh[stageId * STAGE_FLOATS + 0];
            }
            if (pass) {
               int value = atomicAdd(found, 0, 1);
               found_rects[value * RECT_FOUND_INTS + 0] = i;
               found_rects[value * RECT_FOUND_INTS + 1] = j;
               found_rects[value * RECT_FOUND_INTS + 2] = scaledFeatureWidth;

            }
         }

      }

   }

   DetectorKernel kernel;

   private Device device;

   public AparapiDetector2(HaarCascade haarCascade, float baseScale, float scaleInc, float increment, boolean doCannyPruning) {
      super(haarCascade, baseScale, scaleInc, increment, doCannyPruning);
      device = Device.firstGPU();
      kernel = new DetectorKernel(haarCascade);
      kernel.setExplicit(true);
      // kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
   }

   ScaleInfo scaleInfo = null;

   Range range = null;

   @Override List<Rectangle> getFeatures(final int width, final int height, float maxScale, final int[] weightedGrayImage,
         final int[] weightedGrayImageSquared, final int[] cannyIntegral) {

      final List<Rectangle> features = new ArrayList<Rectangle>();
      if (scaleInfo == null) {
         scaleInfo = new ScaleInfo(width, height, maxScale);

         int scaleIds = scaleInfo.scaleIds;

         scaleIds = scaleIds + (256 - (scaleIds % 256));

         range = device.createRange(scaleIds);
         System.out.println(range);
         kernel.width = width;
         kernel.scaleIds = scaleInfo.scaleIds;
         System.out.println("scaledIds = " + kernel.scaleIds);
         kernel.scale_ValueWidthIJ = scaleInfo.scale_ValueWidthIJ;
      }

      kernel.found[0] = 0;

      kernel.weightedGrayImage = weightedGrayImage;
      kernel.weightedGrayImageSquared = weightedGrayImageSquared;
      kernel.put(kernel.found);
      kernel.put(kernel.weightedGrayImage);
      kernel.put(kernel.weightedGrayImageSquared);
      kernel.execute(range);
      kernel.get(kernel.found);
      kernel.get(kernel.found_rects);
      kernel.get(kernel.weightedGrayImage);
      kernel.get(kernel.weightedGrayImageSquared);
      kernel.get(kernel.weightedGrayImageSquared);
      for (int i = 0; i < kernel.found[0]; i++) {
         features.add(new Rectangle(kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 0], kernel.found_rects[i
               * DetectorKernel.RECT_FOUND_INTS + 1], kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 2],
               kernel.found_rects[i * DetectorKernel.RECT_FOUND_INTS + 2]));
      }
      // List<ProfileInfo> profileInfoList = kernel.getProfileInfo();
      //  for (ProfileInfo profileInfo : profileInfoList) {
      //   System.out.println(profileInfo);
      // }

      return (features);
   }

}
