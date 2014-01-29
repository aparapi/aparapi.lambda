package hsailtest;

import static org.junit.Assert.*;
import org.junit.Test;
import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;

import java.util.function.IntConsumer;

/**
 * Created by user1 on 1/29/14.
 */
public class SquaresFuncLambdaTest {

    static void dump(String type, int[] in, int[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    static int mul(int lhs, int rhs){

        return(lhs*rhs);
    }

    static int square(int v){
        return(mul(v,v));
    }

    @Test public void testMain() throws Exception {
        final int len = 10;
        int in[] = new int[len];
        int out[] = new int[len];
        for (int i=0; i<len; i++){
            out[i]=0;
            in[i]=i;
        }
        IntConsumer ic = gid -> {
            out[gid] = square(in[gid]);
        };
      //  ((HSADevice) Device.hsa()).dump(ic);

        if (true){
            Device.hsa().forEach(len, ic);
            dump("hsa", in, out);
            Device.jtp().forEach(len, ic);
            dump("jtp", in, out);
            Device.seq().forEach(len, ic);
            dump("seq", in, out);
        }
        assertTrue("Range > max work size", true);

    }



}
