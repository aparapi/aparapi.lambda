package hsailtest;

import com.amd.aparapi.*;
import com.amd.aparapi.HSAILMethod;


public class VectorMultiplyAddLambda {


    public void test() throws ClassParseException {
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
        System.out.print("hsa ");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
        for (int i = 0; i < in.length; i++) {
            in[i] = i;
            out[i] = 0;
        }
        Device.jtp().forEach(in.length, id->{
            out[id] = in[id] * m + a;
        });
        System.out.print("jtp ");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        (new VectorMultiplyAddLambda()).test();

    }
}
