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


public class StringReductionJUnit {

    String[] getLines() throws Exception{
        List<String> lines = new ArrayList<String>();
        for (File file: new File[]{
                new File("../../samples/dickens/data/dickens/ATailOfTwoCities.txt"),
                new File("../../samples/dickens/data/dickens/AChristmasCarol.txt"),
                new File("../../samples/dickens/data/dickens/GreatExpectations.txt") ,
                new File("../../samples/dickens/data/dickens/OliverTwist.txt"),
                new File("../../samples/dickens/data/dickens/HardTimes.txt"),
                new File("../../samples/dickens/data/dickens/TheLampLighter.txt"),
                new File("../../samples/dickens/data/dickens/LittleDorrit.txt") ,
                new File("../../samples/dickens/data/dickens/TheOldCuriosityShop.txt"),

                new File("../../samples/dickens/data/dickens/DavidCopperfield.txt"),
                new File("../../samples/dickens/data/dickens/DombeyAndSon.txt") ,
                new File("../../samples/dickens/data/dickens/NicolasNickleby.txt"),
               
        }) {

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
            br.close();
        }
        while (lines.size()%256 != 0){
            lines.add("");
        }
        return(lines.toArray(new String[0]));
    }







   @Test public void test() throws Exception{
       String[] lines = getLines();
       System.out.println("lines "+lines.length);
       assertTrue("file is read", lines.length>0);
       long start;
       int hsaAverage=0;
       for (int loop = 0; loop<10; loop++) {
           start = System.currentTimeMillis();

           int[] partials = new int[lines.length / 256 + 1];
           Device.hsa().forEach(lines.length, id -> {
               int[] local = localInt(256);
               int lid = getWorkItemId();
               local[lid] = lines[id].length();
               barrier();
               for (int i = 2; i <= getCurrentWorkGroupSize(); i *= 2) {
                   if (lid % i == 0) {
                       local[lid] = local[lid + i / 2] + local[lid];
                   }
                   barrier();
               }
               partials[getWorkGroupId()] = local[0]; // race here is ok

           });
           int sum = 0;
           for (int i : partials) {
               sum += i;
           }
           hsaAverage = sum/lines.length;
           System.out.println("hsa "+loop+" duration " + (System.currentTimeMillis()-start));

       }
       int sequentialAverage=0;
       for (int loop = 0; loop<10; loop++) {

           start = System.currentTimeMillis();
           int sum = 0;
           for (String line : lines) {
               sum += line.length();
           }
           sequentialAverage = sum / lines.length;
           System.out.println("seq "+loop+" duration " + (System.currentTimeMillis() - start));
       }
        assertTrue("HSA equals sequential results", hsaAverage==sequentialAverage );
    }
}
