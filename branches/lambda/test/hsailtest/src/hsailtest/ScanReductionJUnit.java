package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkGroupId;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localIntX1;
import static org.junit.Assert.assertTrue;

public class ScanReductionJUnit{

   int add(int lhs, int rhs){
      return (lhs+rhs);
   }

   int scan(int[] arr){
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
               int halfStep = step/2;
               local[lid] += local[lid-halfStep];
            }
            barrier();
         }
         if ((lid+1) == getCurrentWorkGroupSize()){// last in group

            partials[getWorkGroupId()] = local[lid]; // race here is ok
            local[lid] = 0;
         }
         barrier();
         for (int step = lsize; step>1; step /= 2){
            if (((lid+1)%step) == 0){
               int halfStep = step/2;
               int prev = local[lid-halfStep];
               local[lid-halfStep] = local[lid];
               local[lid] += prev;
            }
            barrier();
         }
         arr[id] = local[lid];
         barrier();

      });
      int sum = 0;
      for (int i : partials){
         sum += i;
      }
      return (sum);
   }
    /*

    __kernel void prefixKernel( __local int* scratch, __global int* data){
        int gid = get_global_id(0);
        int lid = get_local_id(0);
        int lsize = get_local_size(0);

        scratch[lid]= data[gid]; // copy into local scratch for the reduction
        barrier(CLK_LOCAL_MEM_FENCE); // make sure all of scratch is populated

        for (int step=2; step <=lsize; step<<=1){
            if (((lid+1)%step) == 0){
                scratch[lid]+=scratch[lid-(step>>1)];
            }
            barrier(CLK_LOCAL_MEM_FENCE);
        }

        if ((lid+1) == get_local_size(0)){
            scratch[lid]=0;
        }

        barrier(CLK_LOCAL_MEM_FENCE);
        for (int step=lsize; step >1 ; step>>=1){
            if (((lid+1)%step) == 0){
                int prev = scratch[lid-(step>>1)];
                scratch[lid-(step>>1)]=scratch[lid];
                scratch[lid]+= prev;
            }
            barrier(CLK_LOCAL_MEM_FENCE);
        }
        data[gid] = scratch[lid];
    }
*/

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
//                          \          |   This last pass can be ommitted!
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
//                                     |
//                             overwrite with 0
//                                     |
//                                     V
//  4    6    3   11    6    7    2    0
//                  \                 /|
//                   \               / |
//                    \             /  |
//                     \           /   |
//                      \         /    |
//                       \       /     |
//                        \     /      |
//                         \   /       |
//                          \ /        |
//                           /         |
//                          / \        |
//                         /   \       |
//                        /     \      |
//                       /       \     |
//                      /         \    |
//                     /           \   |
//                    /             \  |
//                   /               \ |
//                  /                 \|
//                 V                   +
//  4    6    3    0    6    7    2    11
//         \      /|          \      / |
//          \    / |           \    /  |
//           \  /  |            \  /   |
//            \/   |             \/    |
//            /\   |             /\    |
//           /  \  |            /  \   |
//          /    \ |           /    \  |
//         /      \|          /      \ |
//        V        +         V         +
//  4     0   3    6    6   11    2    18
//    \  /|    \  /|     \  /|     \  /|
//     \/ |     \/ |      \/ |      \/ |
//     /\ |     /\ |      /\ |      /\ |
//    /  \|    /  \|     /  \|     /  \|
//   V    +   V    +     V   +    V    +
//  0     4   6    9    11  17    18   20

   @Test
   public void test(){
      final int len = 256;
      int[] in = new int[len];
      Device.jtp().forEach(len, id -> in[id] = (Math.random()>.5)?1:0);
      int[] inCopy = JunitHelper.copy(in);
      int hsaSum = scan(in);
      JunitHelper.dump("orig", inCopy);
      JunitHelper.dump(" hsa", in);
      int[] out = new int[len];

      for (int i = 1; i<len; i++){
         out[i] = out[i-1]+inCopy[i-1];
      }
      int sum = out[len-1]+inCopy[len-1];

      JunitHelper.dump(" seq", out);

      assertTrue("HSA equals JTP results", sum == hsaSum);

   }
}
