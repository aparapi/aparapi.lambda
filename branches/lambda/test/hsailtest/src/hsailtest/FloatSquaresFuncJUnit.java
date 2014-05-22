package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class FloatSquaresFuncJUnit{

   static void dump(String type, float[] in, float[] out){

      JUnitHelper.out(type+" ->");
      for (int i = 0; i<in.length; i++){
         JUnitHelper.out("("+in[i]+","+out[i]+"),");
      }
      JUnitHelper.nl();
   }

   static float mul(float lhs, float rhs){
      return (lhs*rhs);
   }

   static float square(float v){
      return (mul(v, v));
   }

   @Test
   public void test(){
      final int len = JUnitHelper.getPreferredArraySize();
      float in[] = new float[len];
      float out[] = new float[len];
      for (int i = 0; i<len; i++){
         out[i] = 0;
         in[i] = i;
      }
      Aparapi.IntTerminal ic = gid -> {
         out[gid] = square(in[gid]);
      };
      Device.hsa().forEach(len, ic);
      JUnitHelper.dump("hsa", in, out);
      float[] hsaOut = Arrays.copyOf(out, out.length);
      Device.jtp().forEach(len, ic);
      JUnitHelper.dump("jtp", in, out);
      assertTrue("HSA equals JTP results", JUnitHelper.compare(hsaOut, out));
   }

}
