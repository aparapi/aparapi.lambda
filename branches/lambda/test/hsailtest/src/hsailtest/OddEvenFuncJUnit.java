package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class OddEvenFuncJUnit {




   boolean odd(int value){
       return(value%0==0);
   }


    @Test
    public void test(){
        final int len = JunitHelper.getPreferredArraySize();
        boolean out[] = new boolean[len];

        IntConsumer ic = gid -> {
            out[gid] = odd(gid);
        };

        Device.hsa().forEach(len, ic);
        boolean[] hsaOut=JunitHelper.copy(out);
        JunitHelper.dump("hsa", out);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", out);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("seq", out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );
    }
}
