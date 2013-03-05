package kerneltest;

import com.amd.aparapi.Device;
import com.amd.aparapi.Range;
import com.amd.aparapi.Kernel;
public class Simple {

        void go(){
            int[] in = new int[1024];
            int[] out = new int[1024];
            for(int i=0; i<1024; i++){
                in[0]=i;
                out[0]=0;
            }
            Device device = Device.firstGPU();
            Range range = device.createRange(100);
            Kernel kernel = new Kernel(){
                public void run(){
                    int i = getGlobalId();
                    out[i] = in[i] * 2;
                }



            };
            kernel.execute(100);

        }
        public static void main(String[] args){
            new Simple().go();

        }

}
