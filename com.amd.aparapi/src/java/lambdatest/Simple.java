package lambdatest;

import com.amd.aparapi.Aparapi;


public class Simple {
    public static void main(String[] args){
        int[] in = new int[1024];
        int[] out = new int[1024];
        for(int i=0; i<1024; i++){
            in[0]=i;
            out[0]=0;
        }
        Aparapi.forEach(100, (i) ->{out[i]=in[i]*2;});
    }
}
