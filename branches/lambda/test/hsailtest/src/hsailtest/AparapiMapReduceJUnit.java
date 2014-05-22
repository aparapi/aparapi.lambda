package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiMapReduceJUnit{

   @Test
   public void testMin(){

      int min = Aparapi.range(0, 12).map(i -> i).reduce((l, r) -> l<r?l:r);

      JUnitHelper.nl("min "+min);
      assertTrue("min==0", min == 0);

   }

   @Test
   public void testMax(){
      int max = Aparapi.range(0, 12).map(i -> i*2).reduce((l, r) -> l>r?l:r);

      JUnitHelper.nl("max "+max);
      assertTrue("max==0", max == 22);

   }

   @Test
   public void testSum(){

      int sum = Aparapi.range(0, 12).map(i -> i).reduce((l, r) -> l+r);

      JUnitHelper.nl("sum "+sum);
      assertTrue("sum==0", sum == 66);

   }

}
