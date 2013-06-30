package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class Array2DLambda {


    public static void main(String[] args) throws AparapiException {
        final int len = 4; // need this to be final, why is len not effectively final?
        int[][] matrix = new int[len][len];

        IntConsumer ic = gid -> {
            for (int i = 0; i < len; i++) {
                matrix[gid][i] = i;
            }
        };

        Device.hsa().forEach(len, ic);
        // System.out.print("p=(" + p.x + "," + p.y + ")\n");
        System.out.print("hsa ->");
        for (int x = 0; x < len; x++) {
            System.out.print("[");
            for (int y = 0; y < len; y++) {
                if (y != 0) {
                    System.out.print(", ");
                }
                System.out.print(matrix[x][y]);
            }
            System.out.print("]");
        }
        System.out.println();

        Device.jtp().forEach(len, ic);
        System.out.print("jtp ->");
        for (int x = 0; x < len; x++) {
            System.out.print("[");
            for (int y = 0; y < len; y++) {
                if (y != 0) {
                    System.out.print(", ");
                }
                System.out.print(matrix[x][y]);
            }
            System.out.print("]");
        }
        System.out.println();
        Device.seq().forEach(len, ic);
        System.out.print("seq ->");
        for (int x = 0; x < len; x++) {
            System.out.print("[");
            for (int y = 0; y < len; y++) {
                if (y != 0) {
                    System.out.print(", ");
                }
                System.out.print(matrix[x][y]);
            }
            System.out.print("]");
        }
        System.out.println();
    }
}
