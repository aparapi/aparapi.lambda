package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FieldIntSquaresJUnit{
   public final int len = JUnitHelper.getPreferredArraySize();
   public int in[] = new int[len];
   public int out[] = new int[len];

   @Test
   public void test(){
      Aparapi.IntTerminal ic = gid -> {
         in[gid] = gid;
         out[gid] = in[gid]*in[gid];
      };
      Device.hsa().forEach(len, ic);
      int[] hsaOut = JUnitHelper.copy(out);
      JUnitHelper.dump("hsa", in, out);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", in, out);
      Device.seq().forEach(len, ic);
      JUnitHelper.dump("seq", in, out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));
   }

}
