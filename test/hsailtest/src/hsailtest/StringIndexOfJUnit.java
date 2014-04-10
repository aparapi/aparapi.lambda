package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;


import static org.junit.Assert.assertTrue;


public class StringIndexOfJUnit {






    @Test
    public void test(){
        String[] strings = new String[]{"cat","mat","dog"};

        int len = strings.length;
        String text = "the cat sat on the mat";

        int[] out = new int[len];

        Aparapi.IntTerminal ic = gid -> {
            out[gid] = text.indexOf(strings[gid]);
        };

        Arrays.fill(out, -1);
        Device.hsa().forEach(len, ic);
        int[] hsaOut = JunitHelper.copy(out);
        JunitHelper.dump("hsa", strings,  out);

        Arrays.fill(out, -1);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("jtp", strings, out);

        Arrays.fill(out, -1);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("seq", strings, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out) );

    }
}
