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

   int x1, x2, y1, y2;

   float weight;

   public Rect(int x1, int x2, int y1, int y2, float weight) {

      this.x1 = x1;
      this.x2 = x2;
      this.y1 = y1;
      this.y2 = y2;
      this.weight = weight;

   }

   public static Rect fromString(String text) {

      String[] tab = text.split(" ");
      int x1 = Integer.parseInt(tab[0]);
      int x2 = Integer.parseInt(tab[1]);
      int y1 = Integer.parseInt(tab[2]);
      int y2 = Integer.parseInt(tab[3]);
      float f = Float.parseFloat(tab[4]);

      return new Rect(x1, x2, y1, y2, f);

   }

}
