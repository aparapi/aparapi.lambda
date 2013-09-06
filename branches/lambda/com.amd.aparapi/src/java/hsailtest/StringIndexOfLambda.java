package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringIndexOfLambda {


    static void dump(String type, String[] _strings, boolean[] results, int[] indices) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+(results[i]?"*":"?")+"="+indices[i]);
        }
        System.out.println();
    }




    public static void main(String[] args) throws AparapiException {
        String[] strings = new String[]{"cat","mat","dog"};
        int len = strings.length;
        String text = "the cat sat on the mat";
        boolean[] results = new boolean[len];
        int[] indices = new int[len];



        IntConsumer ic = gid -> {
            int index = text.indexOf(strings[gid], 0);
            indices[gid] = index;
            //results[gid] = (index>=0);



        };
        Arrays.fill(results, false);
        Arrays.fill(indices, -1);
        Device.hsa().forEach(len, ic);
        dump("hsa", strings, results, indices);
        Arrays.fill(results, false);
        Arrays.fill(indices, -1);
        Device.seq().forEach(len, ic);
        dump("seq", strings, results, indices);

    }
}
