package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localObjectX1;
import static org.junit.Assert.assertTrue;

public class StringSwapLocalJUnit{

   @Test
   public void test() throws Exception{
      String[] lines = new String[256];
      Device.jtp().forEach(lines.length, id -> lines[id] = Integer.toString(id));
      JunitHelper.dump("before", lines);

      Device.hsa().forEach(lines.length, id -> {
         String[] local = (String[])localObjectX1();
         int lid = getWorkItemId();
         local[lid] = lines[id];
         barrier();
         lines[(id+1)%getCurrentWorkGroupSize()] = local[lid];
      });

      JunitHelper.dump("after", lines);
      String first = Integer.toString(255);

      assertTrue("HSA equals sequential results", lines[0].equals(first));
   }
}
