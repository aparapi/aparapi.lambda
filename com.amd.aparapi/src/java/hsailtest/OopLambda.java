package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import java.util.function.IntConsumer;


public class OopLambda{
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
   }



   public static void main(String[] args) throws AparapiException{
      int len = 10;
      P[] points = new P[len];

      for(int i = 0; i < len; i++){
        points[i]=new P(0,0);
      }

      IntConsumer ic  =  gid -> {
         P p = points[gid];
         // p.next = null;
         p.x = 0;
         p.y = 0;
         p.l = 0L;
      } ;

      Device.hsa().forEach(len, ic);

      System.out.print("hsa ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
      System.out.println();

      Device.jtp().forEach(len, ic);
      System.out.print("jtp ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
      System.out.println();
      Device.seq().forEach(len, ic);
      System.out.print("seq ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
      System.out.println();
   }
}
