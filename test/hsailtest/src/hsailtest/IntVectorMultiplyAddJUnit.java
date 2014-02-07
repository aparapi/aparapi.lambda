package hsailtest;

import com.amd.aparapi.*;
import com.amd.aparapi.HSAILMethod;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class IntVectorMultiplyAddJUnit {


    @Test
    public void test()  {
        int[] in = new int[Runtime.getRuntime().availableProcessors()*3];
        int[] out = new int[in.length];
        int m = 2;
        int a = 100;
        for (int i = 0; i < in.length; i++) {
            in[i] = i;
            out[i] = 0;
        }

        Device.hsa().forEach(in.length, id->{
            out[id] = in[id] * m + a;
        });
        JunitHelper.dump("hsa", in, out);
        int[] hsaOut = JunitHelper.copy(out);
        Arrays.fill(out,  0);


        Device.jtp().forEach(in.length, id->{
            out[id] = in[id] * m + a;
        });
        JunitHelper.dump("jtp", in, out);
        assertTrue("HSA and JTP output match", JunitHelper.compare(out, hsaOut) );
    }


}
