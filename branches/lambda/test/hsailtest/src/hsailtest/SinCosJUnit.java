package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;


import static org.junit.Assert.assertTrue;


public class SinCosJUnit {



    @Test
    public void test(){
        final int len = JunitHelper.getPreferredArraySize();
        double sin[] = new double[len];
        double cos[] = new double[len];
        Aparapi.IntTerminal ic = gid -> {
            sin[gid] = Math.sin(gid);
            cos[gid] = Math.cos(gid);
        };
        Device.hsa().forEach(len, ic);

        JunitHelper.dump("hsa", sin, cos);
        double[] hsaCos=JunitHelper.copy(cos);
        double[] hsaSin=JunitHelper.copy(sin);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", sin, cos);

        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaSin,sin) );
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaCos,cos) );
    }
}
