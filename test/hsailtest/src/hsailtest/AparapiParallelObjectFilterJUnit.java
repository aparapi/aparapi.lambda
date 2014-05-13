package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiParallelObjectFilterJUnit{
   @Test
   public void test(){

      String[] strings = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};

      String[] substrings = Aparapi.range(strings).parallel().filter(s -> s.length() == 3);

      System.out.println(substrings);

      assertTrue("substrings.length==4", substrings.length == 4);

   }

}
