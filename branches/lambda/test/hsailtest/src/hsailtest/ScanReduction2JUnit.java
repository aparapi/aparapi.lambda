package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkGroupId;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localIntX1;
import static org.junit.Assert.assertTrue;

public class ScanReduction2JUnit{

   int add(int lhs, int rhs){
      return (lhs+rhs);
   }

   void scan(int[] arr){
      int len = arr.length;
      int[] partials = new int[len/256+1];
      Device.hsa().forEach(len, id -> {
         int[] local = localIntX1();
         int lid = getWorkItemId();
         int lsize = getCurrentWorkGroupSize();
         local[lid] = arr[id];
         barrier();
         for (int step = 2; step<=lsize; step *= 2){
            if ((lid+1)%step == 0){
               int stride = step/2;
               local[lid] += local[lid-stride];
            }
            barrier();
         }
         if (lid == lsize-1){
            partials[getWorkGroupId()] = local[lid]; // extract the partial
         }

         barrier();
         for (int step = lsize/2; step>1; step /= 2){ // 4, 2, 1
            if (((lid+1)<lsize) && (((lid+1)%step) == 0)){ // lid = {3} {1,3,5}
               int stride = step/2; //2, 1
               local[lid+stride] = local[lid]+local[lid+stride];
            }
            barrier();
         }
         arr[id] = local[lid];
         barrier();

      });
   }

//  4    2    3    2    6    1    2    3
//   \   |     \   |     \   |     \   |
//    \  |      \  |      \  |      \  |
//     \ |       \ |       \ |       \ |
//      \|        \|        \|        \|
//       +         +         +         +
//  4    6    3    5    6    7    2    5
//        \        |          \        |
//         \       |           \       |
//          \      |            \      |
//           \     |             \     |
//            \    |              \    |
//             \   |               \   |
//              \  |                \  |
//               \ |                 \ |
//                \|                  \|
//                 +                   +
//  4    6    3   11    6    7    2   12
//                  \                  |
//                   \                 |
//                    \                |
//                     \               |
//                      \              |
//                       \             |
//                        \            |
//                         \           |
//                          \          |
//                           \         |
//                            \        |
//                             \       |
//                              \      |
//                               \     |
//                                \    |
//                                 \   |
//                                  \  |
//                                   \ |
//                                    \|
//                                     +
//  4    6    3   11    6    7    2   23
//                  \        |
//                   \       |
//                    \      |
//                     \     |
//                      \    |
//                       \   |
//                        \  |
//                         \ |
//                          \|
//                           +
//  4    6    3   11    6   18    2   23
//        \   |     \   |     \   |
//         \  |      \  |      \  |
//          \ |       \ |       \ |
//           \|        \|        \|
//            +         +         +
//  4    6   10   11   17   18   20   23

   @Test
   public void test(){
      final int len = 256;
      int[] in = new int[len];
      Device.jtp().forEach(len, id -> in[id] = (Math.random()>.5)?1:0);
      int[] inCopy = JunitHelper.copy(in);

      scan(in);
      JunitHelper.dump("orig", inCopy);
      JunitHelper.dump(" hsa", in);
      int[] out = new int[len];
      out[0] = inCopy[0];
      for (int i = 1; i<len; i++){
         out[i] = out[i-1]+inCopy[i];
      }

      JunitHelper.dump(" seq", out);

      assertTrue("HSA equals JTP results", JunitHelper.compare(in, out));

   }
}
