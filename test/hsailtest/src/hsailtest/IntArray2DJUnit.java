package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class IntArray2DJUnit {





    @Test
    public void test(){
        final int len = JunitHelper.getPreferredArraySize();
        int[][] matrix = new int[len][len];

        IntConsumer ic = gid -> {
            for (int i = 0; i < matrix[0].length; i++) {
                matrix[gid][i] = i;
            }
        };
        Device.hsa().forEach(len, ic);
        int[][] hsaOut = JunitHelper.copy(matrix);
        JunitHelper.dump("hsa", matrix);
        Device.jtp().forEach(len, ic);
        JunitHelper.dump("jtp", matrix);
        Device.seq().forEach(len, ic);
        JunitHelper.dump("seq", matrix);
        assertTrue("HSA equals JTP results", JunitHelper.compare(hsaOut,matrix) );
    }
}
