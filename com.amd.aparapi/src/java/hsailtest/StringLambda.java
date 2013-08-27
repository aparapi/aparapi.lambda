package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringLambda {


    static void dump(String type, String[] _strings, boolean[] results) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+(results[i]?"*":"?"));
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        String[] strings = new String[]{"cat","mat","dog"};
        int len = strings.length;
        String text = "the cat sat on the mat";
        boolean[] results = new boolean[len];




        IntConsumer ic = gid -> {
            boolean v = text.contains(strings[gid]);
           // results[gid]  = v;


        };
        Arrays.fill(results, false);
        System.out.println(results);
        Device.hsa().forEach(len, ic);
        System.out.println(results);
        dump("hsa", strings, results);

    }
}
