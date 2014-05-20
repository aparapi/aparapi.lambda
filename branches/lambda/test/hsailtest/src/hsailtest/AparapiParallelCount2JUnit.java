package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiParallelCount2JUnit{

   @Test
   public void test(){
      char[][] stringChars = new char[256][];
      for (int i = 0; i<256; i++){
         stringChars[i] = (""+i).toCharArray();
      }

      int count1 = Aparapi.range(0, 256).parallel().count(i -> stringChars[i][0] == '4');
      int count2 = Aparapi.range(0, 256).count(i -> stringChars[i][0] == '4');
      JunitHelper.nl("count1="+count1);
      JunitHelper.nl("count2="+count2);
      assertTrue("count1==count2", count1 == count2);

   }

}
