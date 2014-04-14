package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TernaryOddEvenJUnit{

   @Test
   public void test(){
      final int len = JunitHelper.getPreferredArraySize();
      boolean out[] = new boolean[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = (gid%2 == 0)?true:false;
      };

      Device.hsa().forEach(len, ic);
      boolean[] hsaOut = JunitHelper.copy(out);
      JunitHelper.dump("hsa", out);
      Device.jtp().forEach(len, ic);
      JunitHelper.dump("jtp", out);
      Device.seq().forEach(len, ic);
      JunitHelper.dump("seq", out);
      assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut, out));
   }
}
