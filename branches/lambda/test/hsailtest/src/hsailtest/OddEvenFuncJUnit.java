package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;



import static org.junit.Assert.assertTrue;


public class OddEvenFuncJUnit {




   boolean odd(int value){
       return(value%2==0);
   }


    @Test
    public void test(){
        final int len = JunitHelper.getPreferredArraySize();
        boolean out[] = new boolean[len];

        Aparapi.IntTerminal ic = gid -> {
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
