package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;
import org.junit.Ignore;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localObjectX1;
import static org.junit.Assert.assertTrue;

public class StringToIntJUnit{

   @Test
   @Ignore("ignored until we can handle exception construction")
   public void test() throws Exception{
      String[] strings = new String[256];
      int[] ints = new int[strings.length];
      Device.jtp().forEach(strings.length, id -> strings[id] = Integer.toString(id));
      JunitHelper.dump("before", strings);

      Device.hsa().forEach(strings.length, id -> {
         String[] local = (String[])localObjectX1();
         int lid = getWorkItemId();
         local[lid] = strings[id];
         barrier();
         ints[(id+1)%getCurrentWorkGroupSize()] = Integer.valueOf(local[lid]);
      });

      JunitHelper.dump("after", ints);

      assertTrue("HSA equals sequential results", ints[0] == 255);
   }
}
