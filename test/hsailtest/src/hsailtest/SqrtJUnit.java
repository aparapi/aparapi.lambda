package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class SqrtJUnit{

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      double in[] = new double[len];
      double out[] = new double[len];
      Aparapi.IntTerminal ic = gid -> {
         in[gid] = gid;
         out[gid] = Math.sqrt(in[gid]);
      };
      Device.hsa().forEach(len, ic);
      double[] hsaOut = Arrays.copyOf(out, out.length);
      JUnitHelper.dump("hsa", in, out);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", in, out);
      Device.seq().forEach(len, ic);
      JUnitHelper.dump("seq", in, out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));
   }
}
