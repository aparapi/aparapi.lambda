package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class StringCharAtJUnit {
    @Test
    public void test() {
        String string = "here is my string";
        int len = string.length();
        char[] out = new char[len];

        IntConsumer ic = gid -> {
            out[gid]  = string.charAt(gid);
        };
        Arrays.fill(out, '?');
        System.out.println(out);
        Device.hsa().forEach(len, ic);
        char[] hsaOut = JunitHelper.copy(out);
        System.out.println(out);
        JunitHelper.dump("hsa",  out);

        Arrays.fill(out, '?');
        System.out.println(out);
        Device.seq().forEach(len, ic);
        System.out.println(out);
        JunitHelper.dump("seq",  out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut, out));
    }
}
