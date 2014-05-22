package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class StringContainsJUnit{

   @Test
   public void test(){
      String[] strings = new String[]{"cat", "the", "dog", "on"};
      int len = strings.length;
      String string = "the cat sat on the mat";
      boolean[] out = new boolean[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = string.contains(strings[gid]);
      };
      Arrays.fill(out, false);
      Device.hsa().forEach(len, ic);
      boolean[] hsaOut = JUnitHelper.copy(out);
      JUnitHelper.dump("hsa", strings, out);
      Arrays.fill(out, false);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", strings, out);
      Arrays.fill(out, false);
      Device.seq().forEach(len, ic);
      JUnitHelper.dump("seq", strings, out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));

   }
}
