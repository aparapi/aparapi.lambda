package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;


public class OopLambda{
   public static class P{
      int x;
      int y;

      P(int _x, int _y){
         x = _x;
         y = _y;
      }
   }



   public static void main(String[] args) throws AparapiException{
      int len = 10;
      P[] points = new P[len];

      for(int i = 0; i < len; i++){
        points[i]=new P(0,0);
      }

      Device.hsa().forEach(len, gid -> {
         P p = points[gid];
         p.x = 0;
         //points[gid].x = gid;
        // points[gid].y = 4;
      });

      System.out.print("hsa ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
      System.out.println();

      Device.jtp().forEach(len, gid -> {
         points[gid].x = gid;
         points[gid].y = 4;
      });
      System.out.print("jtp ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
      System.out.println();
      Device.seq().forEach(len, gid -> {
         points[gid].x = gid;
         points[gid].y = 4;
      });
      System.out.print("seq ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
      System.out.println();
   }
}
