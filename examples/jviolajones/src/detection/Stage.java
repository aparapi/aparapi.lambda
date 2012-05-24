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
import java.util.LinkedList;
import java.util.List;

public class Stage{
   final static List<Stage> stage_instances = new ArrayList<Stage>();

   final static int STAGE_INTS = 2;

   final static int STAGE_FLOATS = 1;

   static int stage_ids;

   static int stage_startEnd[];

   static float stage_thresh[];

   int id;

   List<Tree> trees;

   float threshold;

   public Stage(float threshold) {
      this.id = stage_ids++;
      this.threshold = threshold;
      trees = new LinkedList<Tree>();
      stage_instances.add(this);
   }

   public static void flatten() {
      stage_startEnd = new int[stage_ids * STAGE_INTS];
      stage_thresh = new float[stage_ids * STAGE_FLOATS];
      for (int i = 0; i < stage_ids; i++) {
         Stage t = stage_instances.get(i);
         stage_startEnd[i * STAGE_INTS + 0] = t.trees.get(0).id;
         stage_startEnd[i * STAGE_INTS + 1] = t.trees.get(t.trees.size() - 1).id;
         stage_thresh[i * STAGE_FLOATS + 0] = t.threshold;
      }
   }

   public void addTree(Tree t) {

      trees.add(t);

   }

   public boolean pass(int[] grayImage, int[] squares, int width, int height, int i, int j, float scale) {

      float sum = 0;
      for (Tree t : trees) {

         //System.out.println("Returned value :"+t.getVal(grayImage, squares,i, j, scale));

         sum += t.getVal(grayImage, squares, width, height, i, j, scale);
      }
      //System.out.println(sum+" "+threshold);

      return sum > threshold;

   }

}
