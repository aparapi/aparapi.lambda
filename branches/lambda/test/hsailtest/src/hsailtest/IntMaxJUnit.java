package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class IntMaxJUnit {



   @Test public void test() {
        final int len = JunitHelper.getPreferredArraySize();
        int in[] = new int[len];
        int out[] = new int[len];
        IntConsumer ic = gid -> {
            in[gid] = gid;
            out[gid] = Math.max(4, in[gid]);
        };
        Device.hsa().forEach(len, ic);
        int[] hsaOut = Arrays.copyOf(out, out.length);
        JunitHelper.dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("seq", in, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );

    }
}
