package hsailtest;

import com.amd.aparapi.Device;
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

public class WordReductionJUnit{

   String[] getLines() throws Exception{
      List<String> lines = new ArrayList<String>();
      for (File file : new File[]{
            new File("/usr/share/dict/words")

      }){

         BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
         for (String line = br.readLine(); line != null; line = br.readLine()){
            lines.add(line);
         }
         br.close();
      }
      while (lines.size()%256 != 0){
         lines.add("");
      }
      return (lines.toArray(new String[0]));
   }

   @Test
   public void test() throws Exception{
      String[] lines = getLines();
      assertTrue("file is read", lines.length>0);
      long start;
      int hsaAverage = 0;
      for (int loop = 0; loop<10; loop++){
         start = System.currentTimeMillis();

         int[] partials = new int[lines.length/256+1];
         Device.hsa().forEach(lines.length, id -> {
            int[] local = localIntX1();
            int lid = getWorkItemId();
            int vowels = 0;
            String line = lines[id];
            for (int i = 0; i<line.length(); i++){
               char ch = line.charAt(i);
               if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o'|ch == 'u'){
                  vowels++;
               }
            }
            local[lid] = vowels;
            barrier();
            for (int i = 2; i<=getCurrentWorkGroupSize(); i *= 2){
               if (lid%i == 0){
                  local[lid] = local[lid+i/2]+local[lid];
               }
               barrier();
            }
            partials[getWorkGroupId()] = local[0]; // race here is ok

         });
         int sum = 0;
         for (int i : partials){
            sum += i;
         }
         hsaAverage = sum/lines.length;
         JunitHelper.nl("hsa "+loop+" duration "+(System.currentTimeMillis()-start));

      }
      int sequentialAverage = 0;
      for (int loop = 0; loop<10; loop++){

         start = System.currentTimeMillis();
         int sum = 0;
         for (String line : lines){
            for (int i = 0; i<line.length(); i++){
               char ch = line.charAt(i);
               if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o'|ch == 'u'){
                  sum++;
               }
            }

         }
         sequentialAverage = sum/lines.length;
         JunitHelper.nl("seq "+loop+" duration "+(System.currentTimeMillis()-start));
      }
      assertTrue("HSA equals sequential results", hsaAverage == sequentialAverage);
   }
}
