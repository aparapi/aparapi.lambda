package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;


import static org.junit.Assert.assertTrue;


public class StringLenJUnit {


 @Test
 public void test(){
        String[] strings = new String[]{"here","is", "my", "string"};
        int len = strings.length;
        int[] out = new int[len];

     Aparapi.IntTerminal ic = gid -> {
            out[gid]  = strings[gid].length();


        };
        Arrays.fill(out, 0);
        System.out.println(out);
        Device.hsa().forEach(len, ic);
        int[] hsaOut = JunitHelper.copy(out);

        System.out.println(out);
     JunitHelper.dump("hsa", strings, out);
        Arrays.fill(out, 0);
        Device.seq().forEach(len, ic);
        System.out.println(out);
     JunitHelper.dump("seq", strings, out);
     assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );

    }
}
