package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class StringHashCodeJUnit{

   @Test
   public void test(){
      String[] strings = new String[]{"here", "is", "my", "string"};
      int len = strings.length;
      int[] out = new int[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = strings[gid].hashCode();

      };
      Arrays.fill(out, 0);
      JUnitHelper.nl(""+out);
      Device.hsa().forEach(len, ic);
      int[] hsaOut = JUnitHelper.copy(out);

      JUnitHelper.nl(""+out);
      JUnitHelper.dump("hsa", strings, out);
      Arrays.fill(out, 0);
      Device.seq().forEach(len, ic);
      JUnitHelper.out(""+out);
      JUnitHelper.dump("seq", strings, out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));

   }
}
