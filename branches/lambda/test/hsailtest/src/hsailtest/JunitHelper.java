package hsailtest;

/**
 * Created by user1 on 1/30/14.
 */
public class JunitHelper {

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
    static boolean compare( double[] in, double[] out, double _variance) {
        for (int i=0; i<in.length; i++){
            if (Math.abs(in[i]-out[i])>_variance){
                return(false);
            }
        }
        return(true);
    }
    static boolean compare( float[] in, float[] out, float _variance) {
        for (int i=0; i<in.length; i++){
            if (Math.abs(in[i]-out[i])>_variance){
                return(false);
            }
        }
        return(true);
    }
}
