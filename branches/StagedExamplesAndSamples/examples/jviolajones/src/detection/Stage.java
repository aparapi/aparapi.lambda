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

public class Stage{
   final int id;

   final List<Tree> trees = new ArrayList<Tree>();

   final float threshold;

   public Stage(int _id, float _threshold) {
      id = _id;
      threshold = _threshold;
   }

   public void addTree(Tree t) {
      trees.add(t);
   }
}
