package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertTrue;

public class AparapiParallelStringSelectJUnit{

   @Ignore("Awaiting implementation of T ParallelArray.select(ObjectReducer<T>)")
   @Test
   public void test(){
      String[] strings = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};
      String str = Aparapi.range(strings).parallel().select((k, l) -> k.length()>l.length()?k:l);

      JunitHelper.nl("longest ="+str);

      assertTrue("str==\"eleven\"", str.equals("eleven"));

   }

}
