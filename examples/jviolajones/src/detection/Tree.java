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

import java.util.ArrayList;
import java.util.List;

public class Tree{
   final static int LEFT = 0;

   final static int RIGHT = 1;

   static int ids;

   static List<Tree> instances = new ArrayList<Tree>();

   int id;

   static final int INTS = 2;

   static int startEnd[];

   Stage stage;

   List<Feature> features = new ArrayList<Feature>();

   public Tree(Stage stage) {
      this.id = ids++;
      this.stage = stage;
      instances.add(this);
   }

   public void addFeature(Feature f) {
      features.add(f);
   }

   public static void flatten() {
      startEnd = new int[ids * INTS];

      for (int i = 0; i < ids; i++) {
         Tree t = instances.get(i);
         startEnd[i * INTS + 0] = t.features.get(0).id;
         startEnd[i * INTS + 1] = t.features.get(t.features.size() - 1).id;
      }
   }

   public float getVal(int[] grayImage, int[] squares, int width, int height, int i, int j, float scale) {

      // System.out.println("stage id "+stage.id+" tree id"+id);
      Feature cur_node = features.get(0);

      while (true) {
         //  System.out.println("feature id "+cur_node.id);
         int where = cur_node.getLeftOrRight(grayImage, squares, width, height, i, j, scale);
         if (where == LEFT) {
            if (cur_node.has_left_val) {
               //  System.out.println("left-val");

               return cur_node.left_val;
            } else {
               // System.out.println("REDIRECTION !");
               //System.exit(0);
               //  System.out.println("left");
               cur_node = features.get(cur_node.left_node);
            }
         } else {
            if (cur_node.has_right_val) {
               //  System.out.println("right-val");
               //  System.out.println("RIGHT");

               return cur_node.right_val;
            } else {
               //  System.out.println("REDIRECTION !");
               //System.exit(0);
               cur_node = features.get(cur_node.right_node);
               // System.out.println("right");
            }
         }
      }

   }

}
