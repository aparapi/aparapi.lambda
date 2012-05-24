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
      this.id = Detector.feature_ids++;
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
      Detector.feature_instances.add(this);

   }

   public void add(Rect r) {
      rects.add(r);
   }

   public static void flatten() {
      Detector.feature_r1r2r3LnRn = new int[Detector.feature_ids * Detector.FEATURE_INTS];
      Detector.feature_LvRvThres = new float[Detector.feature_ids * Detector.FEATURE_FLOATS];
      for (int i = 0; i < Detector.feature_ids; i++) {
         Feature f = Detector.feature_instances.get(i);
         Detector.feature_LvRvThres[i * Detector.FEATURE_FLOATS + 0] = f.left_val;
         Detector.feature_LvRvThres[i * Detector.FEATURE_FLOATS + 1] = f.right_val;
         Detector.feature_LvRvThres[i * Detector.FEATURE_FLOATS + 2] = f.threshold;
         Detector.feature_r1r2r3LnRn[i * Detector.FEATURE_INTS + 0] = (f.rects.size() > 0) ? f.rects.get(0).id : -1;
         Detector.feature_r1r2r3LnRn[i * Detector.FEATURE_INTS + 1] = (f.rects.size() > 1) ? f.rects.get(1).id : -1;
         Detector.feature_r1r2r3LnRn[i * Detector.FEATURE_INTS + 2] = (f.rects.size() > 2) ? f.rects.get(2).id : -1;
         Detector.feature_r1r2r3LnRn[i * Detector.FEATURE_INTS + 3] = (f.has_left_val) ? -1 : f.tree.features.get(f.left_node).id;
         Detector.feature_r1r2r3LnRn[i * Detector.FEATURE_INTS + 4] = (f.has_right_val) ? -1 : f.tree.features.get(f.right_node).id;
      }
   }
}
