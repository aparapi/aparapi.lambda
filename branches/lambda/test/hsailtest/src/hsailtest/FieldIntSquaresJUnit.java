package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import org.junit.Test;



import static org.junit.Assert.assertTrue;


public class FieldIntSquaresJUnit {
    public final int len = JunitHelper.getPreferredArraySize();
    public int in[] = new int[len];
    public int out[] = new int[len];



    @Test
    public void test(){
        Aparapi.IntTerminal ic = gid -> {
            in[gid] = gid;
            out[gid] = in[gid] * in[gid];
        };
        Device.hsa().forEach(len, ic);
        int[] hsaOut = JunitHelper.copy(out);
        JunitHelper.dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("seq", in, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );
    }


}
