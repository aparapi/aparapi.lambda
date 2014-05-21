package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiParallelObjectCountJUnit{
   // char[][] stringChars = new char[256][];
   @Test
   public void test(){
      String[] strings = new String[256];
      for (int i = 0; i<256; i++){
         strings[i] = ""+i;
      }

      int count1 = Aparapi.range(strings).parallel().count(s -> s.length()>2);
      int count2 = Aparapi.range(strings).count(s -> s.length()>2);
      JunitHelper.nl("count1="+count1);
      JunitHelper.nl("count2="+count2);
      assertTrue("count1==count2", count1 == count2);

   }

}
