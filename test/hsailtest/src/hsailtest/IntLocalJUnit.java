package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.HSA;
import org.junit.Test;

import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class IntLocalJUnit {



   @Test public void test() {
        final int len = JunitHelper.getPreferredArraySize();
        int in[] = new int[len];
       Device.jtp().forEach(len, id->in[id]=id);
        int out[] = new int[len];
        IntConsumer ic = gid -> {
            int[] local = HSA.localInt(64);
            int lid = HSA.getWorkItemId();
            local[lid]=in[gid];
            HSA.barrier();
            local[lid] = local[lid]+1;
            HSA.barrier();
            out[gid] = local[lid];
        };
        Device.hsa().dump(ic);
        Device.hsa().forEach(len, ic);
        int[] hsaOut = JunitHelper.copy(out);
        JunitHelper.dump("hsa", in, out);
        Device.jtp().forEach(len, gid->out[gid]=in[gid]+1);
        JunitHelper.dump("jtp", in, out);

        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );

    }
}
