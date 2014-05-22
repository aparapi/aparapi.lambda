package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localFloatX1;
import static org.junit.Assert.assertTrue;

public class FloatSwapLocalJUnit{

   @Test
   public void test() throws Exception{
      float[] floats = new float[256];
      Device.jtp().forEach(floats.length, id -> floats[id] = id);
      JUnitHelper.dump("before", floats);

      Device.hsa().forEach(floats.length, id -> {
         float[] local = localFloatX1();
         int lid = getWorkItemId();
         local[lid] = floats[id];
         barrier();
         floats[(id+1)%getCurrentWorkGroupSize()] = local[lid];
      });

      JUnitHelper.dump("after", floats);

      assertTrue("HSA equals sequential results", floats[0] == 255f);
   }
}
