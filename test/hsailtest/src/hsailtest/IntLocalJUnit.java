package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import com.amd.aparapi.HSA;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IntLocalJUnit{

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      int in[] = new int[len];
      Device.jtp().forEach(len, id -> in[id] = id);
      int out[] = new int[len];
      Aparapi.IntTerminal ic = gid -> {
         int[] local = HSA.localIntX1();
         int lid = HSA.getWorkItemId();
         local[lid] = in[gid];
         HSA.barrier();
         local[lid] = local[lid]+1;
         HSA.barrier();
         out[gid] = local[lid];
      };
     // Device.hsa().dump(ic);
      Device.hsa().forEach(len, ic);
      int[] hsaOut = JUnitHelper.copy(out);
      JUnitHelper.dump("hsa", in, out);
      Device.jtp().forEach(len, gid -> out[gid] = in[gid]+1);
      JUnitHelper.dump("jtp", in, out);

      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));

   }
}
