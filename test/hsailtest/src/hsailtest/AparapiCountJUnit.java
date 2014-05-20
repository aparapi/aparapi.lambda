package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiCountJUnit{

   @Test
   public void test(){

      int evenCount = Aparapi.range(0, 12).count(i -> i%2 == 0);

      JunitHelper.nl("evenCount="+evenCount);
      assertTrue("evenCount==6", evenCount == 6);

   }

}
