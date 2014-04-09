package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;

/**
 * Created by user1 on 1/29/14.
 */
public class IntSquaresSubRangeJUnit {




    @Test public void testMain() throws Exception {
        final int len = JunitHelper.getPreferredArraySize();
        int in[] = new int[len];
        int out[] = new int[len];
        for (int i=0; i<len; i++){
            out[i]=0;
            in[i]=i;
        }
        IntConsumer ic = gid -> {
            out[gid] = in[gid]*in[gid];
        };
        Device.hsa().forEach(len/2, len, ic);
        JunitHelper.dump("hsa", in, out);
        int[] hsaOut= JunitHelper.copy(out);
        Device.jtp().forEach(len/2, len, ic);
        JunitHelper.dump("jtp", in, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );
    }

}
