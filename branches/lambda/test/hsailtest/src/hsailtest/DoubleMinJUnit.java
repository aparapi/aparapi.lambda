package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class DoubleMinJUnit {



   @Test public void test() {
        final int len = JunitHelper.getPreferredArraySize();
        double in[] = new double[len];
        double out[] = new double[len];
        IntConsumer ic = gid -> {
            in[gid] = gid;
            out[gid] = Math.min(4.0, in[gid]);
        };
        Device.hsa().forEach(len, ic);
        double[] hsaOut = JunitHelper.copy(out);
        JunitHelper.dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("seq", in, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );

    }
}
