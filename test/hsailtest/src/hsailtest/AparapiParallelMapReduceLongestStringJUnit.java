package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class AparapiParallelMapReduceLongestStringJUnit {


    @Test
    public void test()  {
        assertTrue("hsa version implemented", false);
       // String str = Aparapi.range(new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven","eight", "nine", "ten", "eleven"}).parallel().map(s->{return(s.length());}).select((k,l) -> {return( k>l);});

      //  System.out.println("longest =" +str);


       // assertTrue("str==\"eleven\"", str.equals("eleven"));

    }

}
