package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class CharArrayStateMachineJUnit{

   static void dump(String type, char[][] _strings, int[] results){
      JUnitHelper.out(type+" ->");
      boolean first = true;
      for (int i = 0; i<_strings.length; i++){
         if (results[i]>0){
            if (!first){
               JUnitHelper.out(", ");
            }else{
               first = false;
            }

            for (char c : _strings[i]){
               JUnitHelper.out(""+c);
            }
            JUnitHelper.out("="+results[i]);
         }

      }
      JUnitHelper.nl();
   }

   static final int NON_ALPHA = 0;
   static final int PARTIAL_MATCH = 1;
   static final int FINAL_CHECK = 2;
   static final int ALPHA = 3;

   @Test
   public void test(){
      File dataDir = new File("../../samples/dickens/data");
      if (!dataDir.exists()){
         dataDir = new File("samples/dickens/data");
      }
      assertTrue("Data Dir Exists", dataDir.exists() && dataDir.isDirectory());
      char[][] strings = JUnitHelper.buildLowerCaseDictionaryChars(new File(dataDir, "names.txt"));
      assertNotNull("names dictionary", strings);
      int len = strings.length;

      char[] text = JUnitHelper.getLowercaseTextChars(new File(dataDir, "dickens/OliverTwist.txt"));
      assertNotNull("names dictionary", text);
      int[] counts = new int[len];
      Aparapi.IntTerminal ic = gid -> {

         char[] chars = strings[gid];
         char firstChar = chars[0];
         int count = 0;
         int state = NON_ALPHA;
         int chIndex = 0;
         for (int i = 0; i<text.length; i++){
            char ch = text[i];
            if (state == PARTIAL_MATCH){
               if (chars[chIndex] == ch){
                  chIndex++;
                  if (chIndex == chars.length){
                     state = FINAL_CHECK;
                  }
               }else if (ch<'a' || ch>'z'){
                  state = NON_ALPHA;
               }else{
                  state = ALPHA;
               }
            }else if (state == NON_ALPHA && firstChar == text[i]){
               state = PARTIAL_MATCH;
               chIndex = 1;

            }else if (state == ALPHA && ch<'a' || ch>'z'){
               state = NON_ALPHA;
            }else if (state == FINAL_CHECK){
               if (ch<'a' || ch>'z'){
                  count++;
                  state = NON_ALPHA;
               }else{
                  state = ALPHA;
               }
            }
         }
         if (state == FINAL_CHECK){
            count++;
         }
         counts[gid] = count;

      };

      long start = 0L;

      for (int i = 0; i<2; i++){
         Arrays.fill(counts, 0);
         start = System.currentTimeMillis();
         Device.hsa().forEach(len, ic);
         JUnitHelper.nl();
         dump("hsa"+i+"= "+(System.currentTimeMillis()-start), strings, counts);
      }
      int[] hsaCounts = JUnitHelper.copy(counts);

      Arrays.fill(counts, 0);
      start = System.currentTimeMillis();
      Device.jtp().forEach(len, ic);
      JUnitHelper.nl();
      dump("jtp = "+(System.currentTimeMillis()-start), strings, counts);
      JUnitHelper.compare(hsaCounts, counts);

   }
}
