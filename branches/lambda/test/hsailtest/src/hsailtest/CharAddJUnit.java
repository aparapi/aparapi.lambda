package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

public class CharAddJUnit{

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      char out[] = new char[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = (char)('A'+gid);
      };

      Device.hsa().forEach(len, ic);
      JUnitHelper.dump("hsa", out);
      char[] hsaOut = JUnitHelper.copy(out);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", out);
      JUnitHelper.compare(out, hsaOut);
   }
}
