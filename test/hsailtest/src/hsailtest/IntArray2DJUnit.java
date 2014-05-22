package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IntArray2DJUnit{

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      int[][] matrix = new int[len][len];

      Aparapi.IntTerminal ic = gid -> {
         for (int i = 0; i<matrix[0].length; i++){
            matrix[gid][i] = i;
         }
      };
      Device.hsa().forEach(len, ic);
      int[][] hsaOut = JUnitHelper.copy(matrix);
      JUnitHelper.dump("hsa", matrix);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", matrix);
      Device.seq().forEach(len, ic);
      JUnitHelper.dump("seq", matrix);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, matrix));
   }
}
