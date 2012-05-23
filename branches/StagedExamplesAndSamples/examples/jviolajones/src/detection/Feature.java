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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Feature{
   static int ids;

   static List<Feature> instances = new ArrayList<Feature>();

   static final int INTS = 5;

   static final int FLOATS = 3;

   static int[] r1r2r3LnRn;

   static float[] LvRvThres;

   int id;

   List<Rect> rects;

   int nb_rects;

   float threshold;

   float left_val;

   float right_val;

   Point size;

   int left_node;

   int right_node;

   boolean has_left_val;

   boolean has_right_val;

   Tree tree;

   public Feature(Tree tree, float threshold, float left_val, int left_node, boolean has_left_val, float right_val, int right_node,
         boolean has_right_val, Point size) {
      this.id = ids++;
      nb_rects = 0;
      rects = new ArrayList<Rect>();
      this.tree = tree;
      this.threshold = threshold;
      this.left_val = left_val;
      this.left_node = left_node;
      this.has_left_val = has_left_val;
      this.right_val = right_val;
      this.right_node = right_node;
      this.has_right_val = has_right_val;
      this.size = size;
      this.instances.add(this);

   }

   public int getLeftOrRight(int[] grayImage, int[] squares, int width, int height, int i, int j, float scale) {

      int w = (int) (scale * size.x);
      int h = (int) (scale * size.y);
      double inv_area = 1. / (w * h);
      //System.out.println("w2 : "+w2);
      int total_x = grayImage[i + w + (j + h) * width] + grayImage[i + (j) * width] - grayImage[i + (j + h) * width]
            - grayImage[i + w + (j) * width];
      int total_x2 = squares[i + w + (j + h) * width] + squares[i + (j) * width] - squares[i + (j + h) * width]
            - squares[i + w + (j) * width];
      double moy = total_x * inv_area;
      double vnorm = total_x2 * inv_area - moy * moy;
      vnorm = (vnorm > 1) ? Math.sqrt(vnorm) : 1;

      int rect_sum = 0;
      for (Rect r : rects) {
         int rx1 = i + (int) (scale * r.x1);
         int rx2 = i + (int) (scale * (r.x1 + r.y1));
         int ry1 = j + (int) (scale * r.x2);
         int ry2 = j + (int) (scale * (r.x2 + r.y2));
         //System.out.println((rx2-rx1)*(ry2-ry1)+" "+r.weight);
         rect_sum += (int) ((grayImage[rx2 + (ry2) * width] - grayImage[rx1 + (ry2) * width] - grayImage[rx2 + (ry1) * width] + grayImage[rx1
               + (ry1) * width]) * r.weight);
      }
      //System.out.println(rect_sum);
      double rect_sum2 = rect_sum * inv_area;

      //System.out.println(rect_sum2+" "+threshold*vnorm);	

      return (rect_sum2 < threshold * vnorm) ? Tree.LEFT : Tree.RIGHT;

   }

   public void add(Rect r) {

      rects.add(r);

   }

   public static void flatten() {
      r1r2r3LnRn = new int[ids * INTS];
      LvRvThres = new float[ids * FLOATS];
      for (int i = 0; i < ids; i++) {
         Feature f = instances.get(i);
         LvRvThres[i * FLOATS + 0] = f.left_val;
         LvRvThres[i * FLOATS + 1] = f.right_val;
         LvRvThres[i * FLOATS + 2] = f.threshold;
         r1r2r3LnRn[i * INTS + 0] = (f.rects.size() > 0) ? f.rects.get(0).id : -1;
         r1r2r3LnRn[i * INTS + 1] = (f.rects.size() > 1) ? f.rects.get(1).id : -1;
         r1r2r3LnRn[i * INTS + 2] = (f.rects.size() > 2) ? f.rects.get(2).id : -1;
         r1r2r3LnRn[i * INTS + 3] = (f.has_left_val) ? -1 : f.tree.features.get(f.left_node).id;
         r1r2r3LnRn[i * INTS + 4] = (f.has_right_val) ? -1 : f.tree.features.get(f.right_node).id;
      }
   }
}
