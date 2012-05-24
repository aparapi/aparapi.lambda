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

   final int id;

   final List<Rect> rects = new ArrayList<Rect>();

   final int nb_rects;

   final float threshold;

   final float left_val;

   final float right_val;

   final Point size;

   final int left_node;

   final int right_node;

   final boolean has_left_val;

   final boolean has_right_val;

   final Tree tree;

   public Feature(int _id, Tree _tree, float _threshold, float _left_val, int _left_node, boolean _has_left_val, float _right_val,
         int _right_node, boolean _has_right_val, Point _size) {
      id = _id;
      tree = _tree;
      nb_rects = 0;

      threshold = _threshold;
      left_val = _left_val;
      left_node = _left_node;
      has_left_val = _has_left_val;
      right_val = _right_val;
      right_node = _right_node;
      has_right_val = _has_right_val;
      size = _size;
   }

   public void add(Rect r) {
      rects.add(r);
   }

}
