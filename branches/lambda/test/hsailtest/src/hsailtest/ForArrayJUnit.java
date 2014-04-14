package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ForArrayJUnit{

   class Indexer{

      int index;

      Indexer(int _index){
         index = _index;
      }
   }

   @Test
   public void test() throws Exception{
      Indexer[] indexers = new Indexer[64];
      int[] indices = new int[64];
      int[] refIndices = new int[64];
      for (int i = 0; i<indexers.length; i++){
         indexers[i] = new Indexer(i);
         refIndices[i] = i;

      }

      Device.hsa().forEach(indexers, indexer -> {
         int value = indexer.index;

         indices[value] = value;
      });
      JunitHelper.dump("ref", refIndices);
      JunitHelper.dump("hsa", indices);
      assertTrue("HSA equals sequential results", JunitHelper.compare(indices, refIndices));
   }
}
