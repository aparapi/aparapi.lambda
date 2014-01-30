package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;

/**
 * Created by user1 on 1/29/14.
 */
public class HypotJUnit {
    @Test
    public void testMain() throws Exception {
        int len = JunitHelper.getPreferredArraySize();
        double in[] = new double[len];
        double out[] = new double[len];
        IntConsumer ic = gid -> {
            in[gid] = gid;
            out[gid] = Math.hypot(in[gid], 4.0);
        };
        Device.hsa().forEach(len, ic);
        double[] hsaOut= Arrays.copyOf(out, out.length);
        JunitHelper.dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", in, out);

        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out, .01) );
    }
}
