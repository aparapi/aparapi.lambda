package hsailtest;

import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class FieldAccess {
    public int value=0;

    public void test(){
        IntConsumer ic = gid -> {
            value = gid;
        };
        Device.hsa().forEach(100, ic);
        System.out.println(value);
    }

    public static void main(String[] args) throws Exception {
       FieldAccess fieldAccess = new FieldAccess();
       fieldAccess.test();
    }
}
