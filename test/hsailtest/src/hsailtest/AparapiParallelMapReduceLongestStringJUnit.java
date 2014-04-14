package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiParallelMapReduceLongestStringJUnit{
   @Test
   public void test(){

      String[] strings = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};
      String str = Aparapi.range(strings).parallel().map(s -> {
         return (s.length());
      }).select((k, l) -> {
         return (k>l);
      });
      String[] substrings = Aparapi.range(strings).parallel().filter(s -> {
         return (s.length() == 1);
      });

      System.out.println("longest ="+str);

      assertTrue("str==\"eleven\"", str.equals("eleven"));

   }

}
