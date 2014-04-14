package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class AparapiIntVectorMultiplyAddJUnit{

   @Test
   public void test(){
      int[] in = new int[Runtime.getRuntime().availableProcessors()*3];
      int[] out = new int[in.length];
      int m = 2;
      int a = 100;
      for (int i = 0; i<in.length; i++){
         in[i] = i;
         out[i] = 0;
      }

      Aparapi.IntTerminal it = id -> {
         out[id] = in[id]*m+a;
      };

      Aparapi.range(in.length).parallel().forEach(it);
      JunitHelper.dump("hsa", in, out);
      int[] hsaOut = JunitHelper.copy(out);
      Arrays.fill(out, 0);

      Aparapi.range(in.length).forEach(it);
      JunitHelper.dump("jtp", in, out);
      assertTrue("HSA and JTP output match", JunitHelper.compare(out, hsaOut));
   }

}
