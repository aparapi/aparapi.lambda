package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class OopArray2DLambda {

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
      final int len = 4; // need this to be final, why is len not effectively final?
      P[][] matrix = new P[len][len];
       for(int x = 0; x < len; x++){
           for(int y = 0; y < len; y++){
               matrix[x][y] = new P(0,0);
           }
       }

      IntConsumer ic  =  gid -> {
         for (int i=0; i< len; i++){
             matrix[gid][i].x=gid;
             matrix[gid][i].y=gid;
         }
      } ;

      Device.hsa().forEach(len, ic);
      // System.out.print("p=(" + p.x + "," + p.y + ")\n");
      System.out.print("hsa ->");
      for(int x = 0; x < len; x++){
          System.out.print("[");
          for(int y = 0; y < len; y++){
              if (y!=0){
                  System.out.print(", ");
              }
             System.out.print("("+matrix[x][y].x+", "+matrix[x][y].y+")");
          }
          System.out.print("]");
      }
      System.out.println();

      Device.jtp().forEach(len, ic);
      System.out.print("jtp ->");
       for(int x = 0; x < len; x++){
           System.out.print("[");
           for(int y = 0; y < len; y++){
               if (y!=0){
                   System.out.print(", ");
               }
               System.out.print("("+matrix[x][y].x+", "+matrix[x][y].y+")");
           }
           System.out.print("]");
       }
      System.out.println();
      Device.seq().forEach(len, ic);
      System.out.print("seq ->");
       for(int x = 0; x < len; x++){
           System.out.print("[");
           for(int y = 0; y < len; y++){
               if (y!=0){
                   System.out.print(", ");
               }
               System.out.print("("+matrix[x][y].x+", "+matrix[x][y].y+")");
           }
           System.out.print("]");
       }
      System.out.println();
   }
}
