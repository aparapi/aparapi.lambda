package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class FieldIntSquaresLambda {
    static final int len = 10;
    static int in[] = new int[len];
    static int out[] = new int[len];

    static void dump(String type, int[] in, int[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    public static void main(String[] args) throws AparapiException {

        IntConsumer ic = gid -> {
            in[gid] = gid;
            out[gid] = in[gid] * in[gid];
        };
        Device.hsa().forEach(len, ic);
        dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        dump("seq", in, out);
    }
}
