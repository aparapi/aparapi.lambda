package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getWorkGroupId;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localIntX1;
import static org.junit.Assert.assertTrue;

public class SumReduction1MJUnit{

   int add(int lhs, int rhs){
      return (lhs+rhs);
   }

   int sum(int[] arr){
      int len = arr.length;
      int pack = 8; // number of simultaneous groups
      int groupSize = 256;
      int packWidth = pack*256;
      int itemsPerPack = len/packWidth; // 32 for 65536 256 for 1 M
      int[] packResults = new int[pack];
      Device.hsa().forEach(packWidth, id -> {
         int[] local = localIntX1();
         int lid = getWorkItemId();
         local[lid] = arr[id];
         barrier();
         for (int i = 1; i<itemsPerPack; i++){
            local[lid] += arr[i*packWidth+id];
            barrier();
         }

         for (int i = 2; i<=groupSize; i *= 2){
            if (lid%i == 0){
               local[lid] = local[lid+i/2]+local[lid];
            }
            barrier();
         }
         packResults[getWorkGroupId()] = local[0]; // race here is ok

      });
      int sum = 0;
      for (int i : packResults){
         sum += i;
      }
      return (sum);
   }

   @Test
   public void test(){
      final int len = 1<<23;
      int[] in = new int[len];
      long start = 0L;
      Device.jtp().forEach(len, id -> in[id] = (Math.random()>.5)?1:0);
      start = System.currentTimeMillis();
      int hsaSum = sum(in);
      JUnitHelper.nl("hsa = "+(System.currentTimeMillis()-start));
      start = System.currentTimeMillis();
      hsaSum = sum(in);
      JUnitHelper.nl("hsa = "+(System.currentTimeMillis()-start));

      int sum = 0;

      start = System.currentTimeMillis();
      for (int i = 0; i<len; i++){
         sum += in[i];
      }
      JUnitHelper.nl("seq = "+(System.currentTimeMillis()-start));

      assertTrue("HSA equals JTP results", sum == hsaSum);

   }
}
