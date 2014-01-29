package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class MaxLambda {

    static void dump(String type, int[] in, int[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            if(i>0){
               System.out.print(",");
            }
            System.out.print("(" + in[i] + "," + out[i] + ")");
        }
        System.out.println();
    }

    public static void main(String[] args) throws AparapiException {
        final int len = Runtime.getRuntime().availableProcessors()*3;
        int in[] = new int[len];
        int out[] = new int[len];
        IntConsumer ic = gid -> {
            in[gid] = gid;
            out[gid] = Math.max(4, in[gid]);
        };
        Device.hsa().forEach(len, ic);
        dump("hsa", in, out);
        Device.jtp().forEach(len, ic);
        dump("jtp", in, out);
        Device.seq().forEach(len, ic);
        dump("seq", in, out);
    }
}
