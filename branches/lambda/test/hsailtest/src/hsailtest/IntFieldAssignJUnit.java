package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IntFieldAssignJUnit{

   int fromId = 0;
   float toId = 0;

   @Test
   public void test(){
      Device.hsa().forEach(30, id -> {
         if (id == 24){
            fromId = id;
            toId = (float)id;
         }
      });

      assertTrue("fromId==24", fromId == 24);
      assertTrue("toId==24", toId == 24);
   }

}
