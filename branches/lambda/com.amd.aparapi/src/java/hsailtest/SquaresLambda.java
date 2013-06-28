package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;


public class SquaresLambda{

   public static void main(String[] args) throws AparapiException{
      final int len = 10;
      int in[] = new int[len];
      int out[] = new int[len];

      Device.hsa().forEach(len, gid -> {
         in[gid] = gid;
         out[gid] = in[gid] * in[gid];
      });

      System.out.print("hsa ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + in[i] + "," + out[i] + "),");
      }
      System.out.println();

      Device.jtp().forEach(len, gid -> {
         in[gid] = gid;
         out[gid] = in[gid] * in[gid];
      });
      System.out.print("jtp ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + in[i] + "," + out[i] + "),");
      }
      System.out.println();
      Device.seq().forEach(len, gid -> {
         in[gid] = gid;
         out[gid] = in[gid] * in[gid];
      });
      System.out.print("seq ->");
      for(int i = 0; i < len; i++){
         System.out.print("(" + in[i] + "," + out[i] + "),");
      }
      System.out.println();
   }
}
