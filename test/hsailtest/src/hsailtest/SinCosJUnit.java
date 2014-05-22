package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SinCosJUnit{

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      double sin[] = new double[len];
      double cos[] = new double[len];
      Aparapi.IntTerminal ic = gid -> {
         sin[gid] = Math.sin(gid);
         cos[gid] = Math.cos(gid);
      };
      Device.hsa().forEach(len, ic);

      JUnitHelper.dump("hsa", sin, cos);
      double[] hsaCos = JUnitHelper.copy(cos);
      double[] hsaSin = JUnitHelper.copy(sin);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", sin, cos);

      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaSin, sin));
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaCos, cos));
   }
}
