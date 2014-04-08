package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.amd.aparapi.HSA.*;
import static org.junit.Assert.assertTrue;


public class StringFindJUnit {



   @Test public void test() throws Exception{
       String[] lines = new String[]{"one", "two", "three", "four"};
       boolean[] flags = new boolean[]{false};

           Device.hsa().forEach(lines, line -> {
               if (line.length()==4){
                   flags[0]=true;
               }
           });

       assertTrue("HSA equals sequential results", flags[0]);
    }
}
