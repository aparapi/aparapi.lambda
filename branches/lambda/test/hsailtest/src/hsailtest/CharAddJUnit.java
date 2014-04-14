package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

public class CharAddJUnit{

   @Test
   public void test(){
      final int len = JunitHelper.getPreferredArraySize();
      char out[] = new char[len];

      Aparapi.IntTerminal ic = gid -> {
         out[gid] = (char)('A'+gid);
      };

      Device.hsa().forEach(len, ic);
      JunitHelper.dump("hsa", out);
      char[] hsaOut = JunitHelper.copy(out);
      Device.jtp().forEach(len, ic);
      JunitHelper.dump("jtp", out);
      JunitHelper.compare(out, hsaOut);
   }
}
