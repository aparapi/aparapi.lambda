package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AparapiObjectFilterJUnit{
   @Test
   public void test(){

      String[] strings = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};

      String[] substrings = Aparapi.range(strings).filter(s -> s.length() == 3);

      JUnitHelper.nl(substrings.length);

      assertTrue("substrings.length==4", substrings.length == 4);

   }

}
