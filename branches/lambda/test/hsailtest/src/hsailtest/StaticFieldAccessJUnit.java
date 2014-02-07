package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.UnsafeWrapper;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertTrue;


public class StaticFieldAccessJUnit {
    public static int value=42;


   @Test
   public void test() {
        int len = JunitHelper.getPreferredArraySize();
        int[] out = new int[len];
        IntConsumer ic = gid -> {
            out[gid]=gid+value;
        };
        Device.hsa().forEach(len, ic);
        int[] hsaOut = JunitHelper.copy(out);
       JunitHelper.dump("hsa", out);
        Device.jtp().forEach(len, ic);
       JunitHelper.dump("jtp", out);

       //(new FieldAccess()).test();
       // Field field = StaticFieldAccessJUnit.class.getField("value");
       // long offset = UnsafeWrapper.objectFieldOffset(field);
        //int addressSize = UnsafeWrapper.addressSize();
       // long address = UnsafeWrapper.addressOf(fieldAccess);
      //  int local_value = UnsafeWrapper.getUnsafe().getInt(address + offset);
      //  System.out.printf("     offset = %x\n",offset);
       // System.out.printf("addressSize = %x\n", addressSize);
      //  System.out.printf("    address = %x\n",address);
       // System.out.printf("local_value = %x\n",local_value);
      //  UnsafeWrapper.getUnsafe().putInt(address + offset, 8);
     //   System.out.printf("value = %x\n",fieldAccess.value);
        assertTrue("HSA == JTP", JunitHelper.compare(out, hsaOut));

    }
}
