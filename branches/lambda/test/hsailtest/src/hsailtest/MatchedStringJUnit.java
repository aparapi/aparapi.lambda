package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static junit.framework.Assert.fail;

public class MatchedStringJUnit{

   static class MatchableString{
      String value;
      boolean matched;

      MatchableString(String _value){
         value = _value;
      }

      void containsCheck(String text){

         matched = text.contains(value);
      }

   }

   static void dump(String type, MatchableString[] _strings){
      System.out.print(type+" ->");
      for (int i = 0; i<_strings.length; i++){
         if (i != 0){
            System.out.print(", ");
         }
         System.out.print(_strings[i].value+((_strings[i].matched)?"*":"?"));
      }
      System.out.println();
   }

   @Test
   public void test(){
      String[] dictionary = new String[]{"cat", "mat", "dog", "car", "ant", "pet", "floor", "man", "table", "boy", "girl", "fork"};
      MatchableString[] hsaMatchableStrings = new MatchableString[dictionary.length];
      MatchableString[] matchableStrings = new MatchableString[dictionary.length];

      for (int i = 0; i<dictionary.length; i++){
         hsaMatchableStrings[i] = new MatchableString(dictionary[i]);
         matchableStrings[i] = new MatchableString(dictionary[i]);
      }

      int len = dictionary.length;
      String text = "the cat sat on the mat";

      Device.hsa().forEach(len, gid -> {
         hsaMatchableStrings[gid].containsCheck(text);
      });
      dump("hsa", hsaMatchableStrings);
      Device.jtp().forEach(len, gid -> {
         matchableStrings[gid].containsCheck(text);
      });
      dump("jtp", hsaMatchableStrings);
      for (int i = 0; i<dictionary.length; i++){
         if (matchableStrings[i].matched != hsaMatchableStrings[i].matched){
            fail("at index "+i);
         }
      }

   }
}
