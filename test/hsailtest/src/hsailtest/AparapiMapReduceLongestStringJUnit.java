package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class AparapiMapReduceLongestStringJUnit {


    @Test
    public void test()  {

        String str = Aparapi.range(new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven","eight", "nine", "ten", "eleven"}).map(s->{return(s.length());}).select((k,l) -> {return( k>l);});

        System.out.println("longest =" +str);


        assertTrue("str==\"eleven\"", str.equals("eleven"));

    }

}
