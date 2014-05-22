package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by user1 on 1/29/14.
 */
public class HypotJUnit{
   @Test
   public void testMain() throws Exception{
      int len = JUnitHelper.getPreferredArraySize();
      double in[] = new double[len];
      double out[] = new double[len];
      Aparapi.IntTerminal ic = gid -> {
         in[gid] = gid;
         out[gid] = Math.hypot(in[gid], 4.0);
      };
      Device.hsa().forEach(len, ic);
      double[] hsaOut = JUnitHelper.copy(out);
      JUnitHelper.dump("hsa", in, out);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", in, out);

      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));
   }
}
