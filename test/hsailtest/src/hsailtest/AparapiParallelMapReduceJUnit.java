package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class AparapiParallelMapReduceJUnit {


    @Test
    public void testMin()  {
        assertTrue("hsa version implemented", false);
        int min = Aparapi.range(0,12).parallel().map(i->i).reduce((l,r)-> l<r?l:r);


        System.out.println("min "+min);
        assertTrue("min==0", min==0 );

    }
    @Test
    public void testMax()  {
        assertTrue("hsa version implemented", false);
        int max = Aparapi.range(0,12).parallel().map(i->i*2).reduce((l,r)-> l>r?l:r);



        System.out.println("max "+max);
        assertTrue("max==0", max==22 );

    }
    @Test
    public void testSum()  {
        assertTrue("hsa version implemented", false);
        int sum = Aparapi.range(0,12).parallel().map(i->i).reduce((l,r)-> l+r);


        System.out.println("sum "+sum);
        assertTrue("sum==0", sum==66 );

    }

}