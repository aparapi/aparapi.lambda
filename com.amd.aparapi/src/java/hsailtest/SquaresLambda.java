package hsailtest;

import com.amd.aparapi.*;
import java.util.function.IntConsumer;


public class SquaresLambda{

   public static void main(String[] args) throws AparapiException{
      int in[] = new int[10];
      int out[] = new int[in.length];

      Device.hsa().forEach(in.length, gid -> {
         in[gid]=gid;
         out[gid] = in[gid] * in[gid];
      });

      for (int i=0; i< in.length; i++){
         System.out.print("("+in[i]+","+out[i]+"),");
      }
   }
}
