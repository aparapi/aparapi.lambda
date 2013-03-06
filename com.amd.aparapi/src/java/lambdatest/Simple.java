package lambdatest;

import com.amd.aparapi.Device;


public class Simple {

    void go(){
        int[] in = new int[1024];
        int[] out = new int[1024];
        for(int i=0; i<1024; i++){
            in[i]=i;
            out[i]=0;
        }
        Device.firstGPU().forEach(100, (i) -> {
            out[i] = in[i] * 2;
        });
        for(int i=0; i<64; i++){
           System.out.println(in[i]+" "+out[i]);
        }
    }
    public static void main(String[] args){
       new Simple().go();

    }
}
