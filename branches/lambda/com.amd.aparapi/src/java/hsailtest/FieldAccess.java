package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class FieldAccess {
    public int value;

    public void test(){
        IntConsumer ic = gid -> {
            int local =value;
        };
        Device.hsa().forEach(1, ic);
    }

    public static void main(String[] args) throws AparapiException {
        (new FieldAccess()).test();

    }
}
