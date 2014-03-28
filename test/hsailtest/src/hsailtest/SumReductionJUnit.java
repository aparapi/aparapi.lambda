package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.*;
import static org.junit.Assert.assertTrue;


public class SumReductionJUnit {



   @Test public void test() {
        final int len = 256;



       int[] in = new int[len];
       int[] out = new int[len];
       Device.jtp().forEach(len, id->in[id]=id);
       int[] partials=new int[len/256+1];
       Device.hsa().forEach(len, id -> {
           int[] local = localInt(256);
           int lid = getWorkItemId();
           local[lid] = in[id];
           barrier();
           for (int i = 2; i <= getCurrentWorkGroupSize(); i *= 2) {
               if (lid % i == 0) {
                   local[lid] = local[lid + i / 2]+ local[lid];
               }
               barrier();
           }
           out[id]=local[lid];
           barrier();
           partials[getWorkGroupId()] = local[0]; // race here is ok

       });

        int[] partialsOut = JunitHelper.copy(partials);
        JunitHelper.dump("hsa",  partials);
       // JunitHelper.dump("hsa-out",  out);
        partials[0]=0;
        for (int i:in){
            partials[0]+=i;
        }
        JunitHelper.dump("loop", partials);

        assertTrue("HSA equals JTP results", JunitHelper.compare(partialsOut,partials) );

    }
}
