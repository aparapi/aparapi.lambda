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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Detector{

   HaarCascade haarCascade;

   Detector(HaarCascade _haarCascade) {
      haarCascade = _haarCascade;
   }

   /** Returns the list of detected objects in an image applying the Viola-Jones algorithm.
    * 
    * The algorithm tests, from sliding windows on the image, of variable size, which regions should be considered as searched objects.
    * Please see Wikipedia for a description of the algorithm.
    * @param file The image file to scan.
    * @param baseScale The initial ratio between the window size and the Haar classifier size (default 2).
    * @param scale_inc The scale increment of the window size, at each step (default 1.25).
    * @param increment The shift of the window at each sub-step, in terms of percentage of the window size.
    * @return the list of rectangles containing searched objects, expressed in pixels.
    */
   public List<Rectangle> getFaces(String file, float baseScale, float scale_inc, float increment, int min_neighbors,
         boolean doCannyPruning) {

      try {
         BufferedImage image = ImageIO.read(new File(file));

         return getFaces(image, baseScale, scale_inc, increment, min_neighbors, doCannyPruning);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;

   }

   public List<Rectangle> getFaces(BufferedImage image, float baseScale, float scale_inc, float increment, int min_neighbors,
         boolean doCannyPruning) {

      final List<Rectangle> ret = new ArrayList<Rectangle>();
      final int width = image.getWidth();
      final int height = image.getHeight();
      final float maxScale = (Math.min((width + 0.f) / haarCascade.size.x, (height + 0.0f) / haarCascade.size.y));
      final int[] imagePixels = new int[width * height];
      final int[] grayImage = new int[width * height];
      final int[] img = new int[width * height];
      final int[] squares = new int[width * height];
      final StopWatch allTimer = new StopWatch("All");
      final StopWatch timer = new StopWatch();
      System.out.println(image);
      timer.start();
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            imagePixels[i + j * width] = image.getRGB(i, j);
         }
      }
      timer.print("imagegrabber");

      timer.start();

      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            int c = imagePixels[i + j * width];
            int red = (c & 0x00ff0000) >> 16;
            int green = (c & 0x0000ff00) >> 8;
            int blue = c & 0x000000ff;
            int value = (30 * red + 59 * green + 11 * blue) / 100;
            img[i + j * width] = value;
         }
      }
      timer.print("greyscaler");
      timer.start();

      for (int i = 0; i < width; i++) {
         int col = 0;
         int col2 = 0;
         for (int j = 0; j < height; j++) {
            int value = img[i + j * width];
            col += value;
            grayImage[i + j * width] = (i > 0 ? grayImage[i - 1 + j * width] : 0) + col;
            col2 += value * value;
            squares[i + j * width] = (i > 0 ? squares[i - 1 + j * width] : 0) + col2;
         }
      }

      timer.print("grey and squares");

      int[] canny = null;
      if (doCannyPruning) {
         timer.start();
         canny = getIntegralCanny(img, width, height);
         timer.print("canny pruning");
      }

      boolean simple = true;
      StopWatch faceDetectTimer = new StopWatch("face detection");
      faceDetectTimer.start();
      if (simple) {

         boolean multiThread = true; // true fastest

         if (multiThread) {
            ExecutorService threadPool = Executors.newFixedThreadPool(16);
            boolean inner = false; // false fastest
            if (inner) {
               for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {

                  //  int loops = 0;
                  //  timer.start();
                  int step = (int) (scale * haarCascade.size.x * increment);
                  int size = (int) (scale * haarCascade.size.x);
                  for (int i = 0; i < width - size; i += step) {
                     for (int j = 0; j < height - size; j += step) {
                        final int i_final = i;
                        final int j_final = j;
                        final float scale_final = scale;
                        final int size_final = size;
                        Runnable r = new Runnable(){
                           public void run() {

                              boolean pass = true;
                              for (int stageId : haarCascade.stageIds) {
                                 if (!pass(stageId, grayImage, squares, width, height, i_final, j_final, scale_final)) {
                                    pass = false;
                                    //  System.out.println("Failed at Stage " + k);
                                    break;
                                 }
                              }
                              if (pass) {
                                 System.out.println("found!");
                                 synchronized (ret) {
                                    ret.add(new Rectangle(i_final, j_final, size_final, size_final));
                                 }
                              }
                           }
                        };
                        threadPool.execute(r);
                     }
                  }
                  //  timer.print("scale " + scale + " " + loops + " ");
               }
            } else {
               for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {

                  //  int loops = 0;
                  //  timer.start();
                  final int step = (int) (scale * haarCascade.size.x * increment);
                  final int size = (int) (scale * haarCascade.size.x);
                  final float scale_final = scale;

                  for (int i = 0; i < width - size; i += step) {
                     final int i_final = i;
                     Runnable r = new Runnable(){
                        public void run() {
                           for (int j = 0; j < height - size; j += step) {

                              int j_final = j;

                              final int size_final = size;

                              boolean pass = true;
                              for (int stageId : haarCascade.stageIds) {
                                 if (!pass(stageId, grayImage, squares, width, height, i_final, j_final, scale_final)) {
                                    pass = false;
                                    //  System.out.println("Failed at Stage " + k);
                                    break;
                                 }
                              }
                              if (pass) {
                                 System.out.println("found!");
                                 synchronized (ret) {
                                    ret.add(new Rectangle(i_final, j_final, size_final, size_final));
                                 }
                              }
                           }

                        }
                     };
                     threadPool.execute(r);
                  }
                  //  timer.print("scale " + scale + " " + loops + " ");

               }
            }
            threadPool.shutdown();
            try {
               threadPool.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         } else {

            for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
               int loops = 0;
               timer.start();
               int step = (int) (scale * haarCascade.size.x * increment);
               int size = (int) (scale * haarCascade.size.x);
               for (int i = 0; i < width - size; i += step) {
                  for (int j = 0; j < height - size; j += step) {

                     boolean pass = true;
                     for (int stageId : haarCascade.stageIds) {
                        if (!pass(stageId, grayImage, squares, width, height, i, j, scale)) {
                           pass = false;
                           //  System.out.println("Failed at Stage " + k);
                           break;
                        }

                     }
                     if (pass) {
                        System.out.println("pass!");
                        ret.add(new Rectangle(i, j, size, size));
                     }
                  }
               }
               timer.print("scale " + scale + " " + loops + " ");
            }
         }

      } else {

         for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
            int loops = 0;
            timer.start();
            int step = (int) (scale * haarCascade.size.x * increment);
            int size = (int) (scale * haarCascade.size.x);
            for (int i = 0; i < width - size; i += step) {
               for (int j = 0; j < height - size; j += step) {
                  if (doCannyPruning) {
                     int edges_density = canny[i + size + (j + size) * width] + canny[i + (j) * width]
                           - canny[i + (j + size) * width] - canny[i + size + (j) * width];
                     int d = edges_density / size / size;
                     if (d < 20 || d > 100)
                        continue;
                  }
                  boolean pass = true;
                  int k = 0;
                  for (int stageId : haarCascade.stageIds) {
                     if (!pass(stageId, grayImage, squares, width, height, i, j, scale)) {
                        pass = false;
                        //  System.out.println("Failed at Stage " + k);
                        break;
                     }
                     k++;
                  }
                  if (pass) {

                     System.out.println("found!");
                     ret.add(new Rectangle(i, j, size, size));
                  }
               }
            }
            timer.print("scale " + scale + " " + loops + " ");
         }
      }
      faceDetectTimer.stop();
      allTimer.stop();
      return merge(ret, min_neighbors);
   }

   private boolean pass(int stageId, int[] grayImage, int[] squares, int width, int height, int i, int j, float scale) {

      float sum = 0;
      for (int treeId = haarCascade.stage_startEnd[stageId * haarCascade.STAGE_INTS + 0]; treeId <= haarCascade.stage_startEnd[stageId
            * haarCascade.STAGE_INTS + 1]; treeId++) {

         //  System.out.println("stage id " + stageId + "  tree id" + treeId);
         int featureId = haarCascade.tree_startEnd[treeId * haarCascade.TREE_INTS + 0];
         float thresh = 0f;
         boolean done = false;
         while (!done) {
            //  System.out.println("feature id "+featureId);

            int w = (int) (scale * haarCascade.size.x);
            int h = (int) (scale * haarCascade.size.y);
            double inv_area = 1. / (w * h);
            //System.out.println("w2 : "+w2);
            int total_x = grayImage[i + w + (j + h) * width] + grayImage[i + (j) * width] - grayImage[i + (j + h) * width]
                  - grayImage[i + w + (j) * width];
            int total_x2 = squares[i + w + (j + h) * width] + squares[i + (j) * width] - squares[i + (j + h) * width]
                  - squares[i + w + (j) * width];
            double moy = total_x * inv_area;
            double vnorm = total_x2 * inv_area - moy * moy;
            vnorm = (vnorm > 1) ? Math.sqrt(vnorm) : 1;
            // System.out.println(vnorm);
            int rect_sum = 0;
            for (int r = 0; r < 3; r++) {
               int rectId = haarCascade.feature_r1r2r3LnRn[featureId * haarCascade.FEATURE_INTS + r];
               if (rectId != -1) {
                  // System.out.println("rect " + r + " id " + rectId);
                  int x1 = haarCascade.rect_x1y1x2y2[rectId * haarCascade.RECT_INTS + 0];
                  int y1 = haarCascade.rect_x1y1x2y2[rectId * haarCascade.RECT_INTS + 1];
                  int x2 = haarCascade.rect_x1y1x2y2[rectId * haarCascade.RECT_INTS + 2];
                  int y2 = haarCascade.rect_x1y1x2y2[rectId * haarCascade.RECT_INTS + 3];
                  float weight = haarCascade.rect_w[rectId * haarCascade.RECT_FLOATS + 0];
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
            double rect_sum2 = rect_sum * inv_area;

            // System.out.println(rect_sum2+" "+ Feature.LvRvThres[featureId * Feature.FLOATS + 2]*vnorm);  

            if (rect_sum2 < haarCascade.feature_LvRvThres[featureId * haarCascade.FEATURE_FLOATS + 2] * vnorm) {

               int leftNodeId = haarCascade.feature_r1r2r3LnRn[featureId * haarCascade.FEATURE_INTS + 3];
               if (leftNodeId == -1) {
                  //  System.out.println("left-val");
                  thresh = haarCascade.feature_LvRvThres[featureId * haarCascade.FEATURE_FLOATS + 0];
                  done = true;
               } else {
                  // System.out.println("left");
                  featureId = leftNodeId;
               }
            } else {
               int rightNodeId = haarCascade.feature_r1r2r3LnRn[featureId * haarCascade.FEATURE_INTS + 4];
               if (rightNodeId == -1) {
                  // System.out.println("right-val");
                  thresh = haarCascade.feature_LvRvThres[featureId * haarCascade.FEATURE_FLOATS + 1];
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

      return sum > HaarCascade.stage_thresh[stageId * HaarCascade.STAGE_FLOATS + 0];
   }

   public int[] getIntegralCanny(int[] grayImage, int width, int height) {

      int[] canny = new int[grayImage.length];
      final StopWatch timer = new StopWatch();
      timer.start();
      for (int i = 2; i < width - 2; i++) {
         for (int j = 2; j < height - 2; j++) {
            int sum = 0;
            sum += 2 * grayImage[i - 2 + (j - 2) * width];
            sum += 4 * grayImage[i - 2 + (j - 1) * width];
            sum += 5 * grayImage[i - 2 + (j + 0) * width];
            sum += 4 * grayImage[i - 2 + (j + 1) * width];
            sum += 2 * grayImage[i - 2 + (j + 2) * width];
            sum += 4 * grayImage[i - 1 + (j - 2) * width];
            sum += 9 * grayImage[i - 1 + (j - 1) * width];
            sum += 12 * grayImage[i - 1 + (j + 0) * width];
            sum += 9 * grayImage[i - 1 + (j + 1) * width];
            sum += 4 * grayImage[i - 1 + (j + 2) * width];
            sum += 5 * grayImage[i + 0 + (j - 2) * width];
            sum += 12 * grayImage[i + 0 + (j - 1) * width];
            sum += 15 * grayImage[i + 0 + (j + 0) * width];
            sum += 12 * grayImage[i + 0 + (j + 1) * width];
            sum += 5 * grayImage[i + 0 + (j + 2) * width];
            sum += 4 * grayImage[i + 1 + (j - 2) * width];
            sum += 9 * grayImage[i + 1 + (j - 1) * width];
            sum += 12 * grayImage[i + 1 + (j + 0) * width];
            sum += 9 * grayImage[i + 1 + (j + 1) * width];
            sum += 4 * grayImage[i + 1 + (j + 2) * width];
            sum += 2 * grayImage[i + 2 + (j - 2) * width];
            sum += 4 * grayImage[i + 2 + (j - 1) * width];
            sum += 5 * grayImage[i + 2 + (j + 0) * width];
            sum += 4 * grayImage[i + 2 + (j + 1) * width];
            sum += 2 * grayImage[i + 2 + (j + 2) * width];

            canny[i + j * width] = sum / 159;
            //System.out.println(canny[i][j]);
         }
      }
      timer.print("canny convolution");
      timer.start();
      int[] grad = new int[grayImage.length];
      for (int i = 1; i < width - 1; i++) {
         for (int j = 1; j < height - 1; j++) {
            int grad_x = -canny[i - 1 + (j - 1) * width] + canny[i + 1 + (j - 1) * width] - 2 * canny[i - 1 + (j) * width] + 2
                  * canny[i + 1 + (j) * width] - canny[i - 1 + (j + 1) * width] + canny[i + 1 + (j + 1) * width];
            int grad_y = canny[i - 1 + (j - 1) * width] + 2 * canny[i + (j - 1) * width] + canny[i + 1 + (j - 1) * width]
                  - canny[i - 1 + (j + 1) * width] - 2 * canny[i + (j + 1) * width] - canny[i + 1 + (j + 1) * width];
            grad[i + j * width] = Math.abs(grad_x) + Math.abs(grad_y);
            //System.out.println(grad[i][j]);
         }
      }
      timer.print("canny convolution 2");
      timer.start();
      //JFrame f = new JFrame();
      //f.setContentPane(new DessinChiffre(grad));
      //f.setVisible(true);
      for (int i = 0; i < width; i++) {
         int col = 0;
         for (int j = 0; j < height; j++) {
            int value = grad[i + j * width];
            canny[i + j * width] = (i > 0 ? canny[i - 1 + j * width] : 0) + col + value;
            col += value;
         }
      }
      timer.print("canny convolution 3");
      return canny;

   }

   public List<java.awt.Rectangle> merge(List<java.awt.Rectangle> rects, int min_neighbors) {

      List<java.awt.Rectangle> retour = new LinkedList<java.awt.Rectangle>();
      int[] ret = new int[rects.size()];
      int nb_classes = 0;
      for (int i = 0; i < rects.size(); i++) {
         boolean found = false;
         for (int j = 0; j < i; j++) {
            if (equals(rects.get(j), rects.get(i))) {
               found = true;
               ret[i] = ret[j];
            }
         }
         if (!found) {
            ret[i] = nb_classes;
            nb_classes++;
         }
      }
      //System.out.println(Arrays.toString(ret));
      int[] neighbors = new int[nb_classes];
      Rectangle[] rect = new Rectangle[nb_classes];
      for (int i = 0; i < nb_classes; i++) {
         neighbors[i] = 0;
         rect[i] = new Rectangle(0, 0, 0, 0);
      }
      for (int i = 0; i < rects.size(); i++) {
         neighbors[ret[i]]++;
         rect[ret[i]].x += rects.get(i).x;
         rect[ret[i]].y += rects.get(i).y;
         rect[ret[i]].height += rects.get(i).height;
         rect[ret[i]].width += rects.get(i).width;
      }
      for (int i = 0; i < nb_classes; i++) {
         int n = neighbors[i];
         if (n >= min_neighbors) {
            Rectangle r = new Rectangle(0, 0, 0, 0);
            r.x = (rect[i].x * 2 + n) / (2 * n);
            r.y = (rect[i].y * 2 + n) / (2 * n);
            r.width = (rect[i].width * 2 + n) / (2 * n);
            r.height = (rect[i].height * 2 + n) / (2 * n);
            retour.add(r);
         }
      }

      return retour;

   }

   public boolean equals(Rectangle r1, Rectangle r2) {

      int distance = (int) (r1.width * 0.2);

      if (r2.x <= r1.x + distance && r2.x >= r1.x - distance && r2.y <= r1.y + distance && r2.y >= r1.y - distance
            && r2.width <= (int) (r1.width * 1.2) && (int) (r2.width * 1.2) >= r1.width) {

         return true;
      }
      if (r1.x >= r2.x && r1.x + r1.width <= r2.x + r2.width && r1.y >= r2.y && r1.y + r1.height <= r2.y + r2.height) {

         return true;
      }

      return false;

   }
}
