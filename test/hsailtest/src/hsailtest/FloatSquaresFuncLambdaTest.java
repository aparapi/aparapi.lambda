package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class FloatSquaresFuncLambdaTest {

    static void dump(String type, float[] in, float[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    static float mul(float lhs, float rhs){
        return(lhs*rhs);
    }

    static float square(float v){
        return(mul(v,v));
    }

   @Test public void test(){
        final int len = JunitHelper.getPreferredArraySize();
        float in[] = new float[len];
        float out[] = new float[len];
        for (int i=0; i<len; i++){
            out[i]=0;
            in[i]=i;
        }
        IntConsumer ic = gid -> {
            out[gid] = square(in[gid]);
        };
        Device.hsa().forEach(len, ic);
        JunitHelper.dump("hsa", in, out);
        float[] hsaOut= Arrays.copyOf(out, out.length);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", in, out);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,out, 0.001f) );
       }

}
