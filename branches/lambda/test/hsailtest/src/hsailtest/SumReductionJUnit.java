package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.*;
import static org.junit.Assert.assertTrue;


public class SumReductionJUnit {

   int add(int lhs, int rhs){
      return(lhs+rhs);
   }

   int sum(int[] arr){
       int len = arr.length;
       int[] partials=new int[len/256+1];
       Device.hsa().forEach(len, id -> {
           int[] local = localInt(256);
           int lid = getWorkItemId();
           local[lid] = arr[id];
           barrier();
           for (int i = 2; i <= getCurrentWorkGroupSize(); i *= 2) {
               if (lid % i == 0) {
                   local[lid] =  add(local[lid + i / 2], local[lid]);
               }
               barrier();
           }
           partials[getWorkGroupId()] = local[0]; // race here is ok

       });
       int sum =0;
       for (int i:partials){
           sum+=i;
       }
       return(sum);
   }

   @Test public void test() {
       final int len = 65536;
       int[] in = new int[len];
       Device.jtp().forEach(len, id->in[id]=(Math.random()>.5)?1:0);

       int hsaSum = sum(in);
       int sum = 0;
       for (int i = 0; i < len; i++) {
          sum += in[i];
       }


        assertTrue("HSA equals JTP results", sum==hsaSum );

    }
}
