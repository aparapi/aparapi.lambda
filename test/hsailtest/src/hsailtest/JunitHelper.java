package hsailtest;

import java.util.Arrays;

/**
 * Created by user1 on 1/30/14.
 */
public class JunitHelper {
    static void dump(String type, String[] _strings, int[] results) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+"="+results[i]);
        }
        System.out.println();
    }
    public static void dump(String type, int[][] array){
        System.out.print(type+" ->");
        for (int x = 0; x < array.length; x++) {
            System.out.print("[");
            for (int y = 0; y < array[0].length; y++) {
                if (y != 0) {
                    System.out.print(", ");
                }
                System.out.print(array[x][y]);
            }
            System.out.print("]");
        }
        System.out.println();
    }

    static void dump(String type,  char[] results) {
        System.out.print(type + " ->");
        for (int i = 0; i < results.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(results[i]);
        }
        System.out.println();
    }
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

    static void dump(String type, boolean[] in) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "),");
        }
        System.out.println();
    }

    static void dump(String type, int[] in, int[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    static void dump(String type, double[] in, double[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    static void dump(String type, float[] in, float[] out) {
        System.out.print(type + " ->");
        for (int i = 0; i < in.length; i++) {
            System.out.print("(" + in[i] + "," + out[i] + "),");
        }
        System.out.println();
    }

    static int getPreferredArraySize(){
        return(Runtime.getRuntime().availableProcessors()*8);
    }

    static boolean compare( int[] in, int[] out) {
        for (int i=0; i<in.length; i++){
            if (in[i]!=out[i]){
                return(false);
            }
        }
        return(true);
    }
    static boolean compare(int[][] in, int[][] out){

        for (int x=0; x<in.length; x++){
            for (int y=0; y<in.length; y++){
                if (in[x][y]!=out[x][y]){
                    return(false);
                }
            }
        }
        return true;

    }
    static boolean compare( boolean[] in, boolean[] out) {
        for (int i=0; i<in.length; i++){
            if (in[i]!=out[i]){
                return(false);
            }
        }
        return(true);
    }
    static boolean compare( char[] in, char[] out) {
        for (int i=0; i<in.length; i++){
            if (in[i]!=out[i]){
                return(false);
            }
        }
        return(true);
    }
    static boolean compare( double[] in, double[] out) {
        for (int i=0; i<in.length; i++){
            if (!withinTolerance(in[i], out[i])){
                return(false);
            }
        }
        return(true);
    }
    static float floatTolerance = 0.0001f;
    static float doubleTolerance = 0.000001f;
    static boolean compare( float[] in, float[] out) {
        for (int i=0; i<in.length; i++){
            if (!withinTolerance(in[i], out[i])){
                return(false);
            }
        }
        return(true);
    }

    static boolean withinTolerance(float lhs, float rhs){
        return(Math.abs(lhs-rhs)<floatTolerance);
    }
    static boolean withinTolerance(double lhs, double rhs){
        return(Math.abs(lhs-rhs)<doubleTolerance);
    }

    static int[] copy(int[] in){
        return(Arrays.copyOf(in, in.length));
    }
    static boolean[] copy(boolean[] in){
        return(Arrays.copyOf(in, in.length));
    }
    static float[] copy(float[] in){
        return(Arrays.copyOf(in, in.length));
    }
    static double[] copy(double[] in){
        return(Arrays.copyOf(in, in.length));
    }
    static char[] copy(char[] in){
        return(Arrays.copyOf(in, in.length));
    }
    static int[][] copy(int[][] in){
        int[][] copy = new int[in.length][in[0].length];
        for (int x=0; x<in.length; x++){
            for (int y=0; y<in.length; y++){
               copy[x][y]=in[x][y];
            }
        }
        return copy;

    }
}
