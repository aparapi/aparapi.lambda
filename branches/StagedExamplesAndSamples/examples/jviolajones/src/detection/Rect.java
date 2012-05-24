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

public class Rect{
   final int id; // we use this to access from global parallel arrays

   final int x1, x2, y1, y2;

   final float weight;

   public Rect(int _id, int _x1, int _x2, int _y1, int _y2, float _weight) {
      id = _id;
      x1 = _x1;
      x2 = _x2;
      y1 = _y1;
      y2 = _y2;
      weight = _weight;
   }
}
