package hsailtest;

import static org.junit.Assert.*;
import org.junit.Test;
import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;

import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * Created by user1 on 1/29/14.
 */
public class IntSquaresFuncJUnit {



    static int mul(int lhs, int rhs){

        return(lhs*rhs);
    }

    static int square(int v){
        return(mul(v,v));
    }

    @Test public void testMain() throws Exception {
        final int len = JunitHelper.getPreferredArraySize();
        int in[] = new int[len];
        int out[] = new int[len];
        for (int i=0; i<len; i++){
            out[i]=0;
            in[i]=i;
        }
        IntConsumer ic = gid -> {
            out[gid] = square(in[gid]);
        };
        Device.hsa().forEach(len, ic);
        JunitHelper.dump("hsa", in, out);
        int[] hsaOut= JunitHelper.copy(out);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", in, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );
    }

}
