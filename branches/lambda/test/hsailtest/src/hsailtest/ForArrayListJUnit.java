package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class ForArrayListJUnit{

   class Indexer{

      int index;

      Indexer(int _index){
         index = _index;
      }
   }

   @Test
   public void test() throws Exception{
      ArrayList<Indexer> indexers = new ArrayList<Indexer>();
      int[] indices = new int[64];
      int[] refIndices = new int[64];
      for (int i = 0; i<indices.length; i++){
         indexers.add(new Indexer(i));
         refIndices[i] = i;

      }

      Device.hsa().forEach(indexers, indexer -> {
         int value = indexer.index;

         indices[value] = value;
      });
      JUnitHelper.dump("ref", refIndices);
      JUnitHelper.dump("hsa", indices);
      assertTrue("HSA equals sequential results", JUnitHelper.compare(indices, refIndices));
   }
}
