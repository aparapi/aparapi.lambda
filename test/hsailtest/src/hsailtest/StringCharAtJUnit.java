package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class StringCharAtJUnit{
   @Test
   public void test(){
      String string = "here is my string";
      int len = string.length();
      char[] out = new char[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = string.charAt(gid);
      };
      Arrays.fill(out, '?');
      JunitHelper.nl(""+out);
      Device.hsa().forEach(len, ic);
      char[] hsaOut = JunitHelper.copy(out);
      JunitHelper.nl(""+out);
      JunitHelper.dump("hsa", out);

      Arrays.fill(out, '?');
      JunitHelper.nl(""+out);
      Device.seq().forEach(len, ic);
      JunitHelper.nl(""+out);
      JunitHelper.dump("seq", out);
      assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut, out));
   }
}
