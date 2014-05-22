package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OddEvenFuncJUnit{

   boolean odd(int value){
      return (value%2 == 0);
   }

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      boolean out[] = new boolean[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = odd(gid);
      };

      Device.hsa().forEach(len, ic);
      boolean[] hsaOut = JUnitHelper.copy(out);
      JUnitHelper.dump("hsa", out);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", out);
      Device.seq().forEach(len, ic);
      JUnitHelper.dump("seq", out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));
   }
}
