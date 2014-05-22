package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.AparapiJNI;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.amd.aparapi.HSA.barrier;
import static com.amd.aparapi.HSA.getCurrentWorkGroupSize;
import static com.amd.aparapi.HSA.getWorkGroupId;
import static com.amd.aparapi.HSA.getWorkItemId;
import static com.amd.aparapi.HSA.localIntX1;
import static org.junit.Assert.assertTrue;

public class GetClassNamesJUnit{


   @Test
   public void test() {
      AparapiJNI.getAparapiJNI().dumpLoadedClassNames();

      assertTrue("file is read", true);

   }
}
