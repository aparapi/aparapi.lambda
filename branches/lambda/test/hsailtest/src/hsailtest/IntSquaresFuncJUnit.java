package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by user1 on 1/29/14.
 */
public class IntSquaresFuncJUnit{

   static int mul(int lhs, int rhs){

      return (lhs*rhs);
   }

   static int square(int v){
      return (mul(v, v));
   }

   @Test
   public void testMain() throws Exception{
      final int len = JUnitHelper.getPreferredArraySize();
      int in[] = new int[len];
      int out[] = new int[len];
      for (int i = 0; i<len; i++){
         out[i] = 0;
         in[i] = i;
      }
      Aparapi.IntTerminal ic = gid -> {
         out[gid] = square(in[gid]);
      };
      Device.hsa().forEach(len, ic);
      JUnitHelper.dump("hsa", in, out);
      int[] hsaOut = JUnitHelper.copy(out);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", in, out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));
   }

}
