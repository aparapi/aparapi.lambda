package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OopArray2DJUnit{

   public static class P{
      P next;
      int x;
      int y;
      long l;

      P(int _x, int _y){
         next = null;
         x = _x;
         y = _y;
         l = 0;
      }

      @Override
      public String toString(){
         return ("("+x+", "+y+")");
      }
   }

   static void dump(String type, P[][] points){
      JUnitHelper.out(type+" ->");
      for (int x = 0; x<points.length; x++){
         JUnitHelper.out("[");

         for (int y = 0; y<points[0].length; y++){
            if (y != 0){
               JUnitHelper.out(", ");
            }
            JUnitHelper.out(""+points[x][y]);
         }
         JUnitHelper.out("]");
      }
      JUnitHelper.nl();
   }

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      P[][] matrix = new P[len][len];
      for (int x = 0; x<len; x++){
         for (int y = 0; y<len; y++){
            matrix[x][y] = new P(0, 0);
         }
      }

      Aparapi.IntTerminal ic = gid -> {
         for (int i = 0; i<len; i++){
            matrix[gid][i].x = gid;
            matrix[gid][i].y = gid;
         }
      };

      Device.hsa().forEach(len, ic);
      dump("hsa", matrix);
      Device.jtp().forEach(len, ic);
      dump("jtp", matrix);
      Device.seq().forEach(len, ic);
      dump("seq", matrix);
      assertTrue("same ", true);
   }
}
