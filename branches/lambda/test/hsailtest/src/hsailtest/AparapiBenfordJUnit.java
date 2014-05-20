package hsailtest;

import com.amd.aparapi.Aparapi;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class AparapiBenfordJUnit{

   static class TwitterUser{
      int id;
      int friendCount;

      TwitterUser(int _id, int _friendCount){
         id = _id;
         friendCount = _friendCount;
      }
   }

   static ArrayList<TwitterUser> getTwitterData(){
      ArrayList<TwitterUser> twitterUsers = new ArrayList<TwitterUser>();

      Pattern linePattern = Pattern.compile(" *([0-9]+) *([0-9]+) *");
      try{
         FileInputStream fin = new FileInputStream("data/twitter-data.txt");
         BufferedReader br = new BufferedReader(new InputStreamReader(fin));
         for (String line = br.readLine(); line != null; line = br.readLine()){
            if (!line.startsWith("#")){
               Matcher matcher = linePattern.matcher(line);
               if (matcher.matches()){
                  int id = Integer.valueOf(matcher.group(1));
                  int friendCount = Integer.valueOf(matcher.group(2));
                  twitterUsers.add(new TwitterUser(id, friendCount));
               }else{
                  System.out.println("failed \""+line+"\"");
               }

            }
         }
         while ((twitterUsers.size()%(256*8)) != 0){
            twitterUsers.remove(0);
            // twitterUsers.add(new TwitterUser(0,0));
         }

      }catch (IOException io){
         io.printStackTrace();
      }
      return (twitterUsers);
   }

   @Test
   public void test(){
      ArrayList<TwitterUser> twitterData = getTwitterData();
      // int min = Aparapi.range(0, 12).reduce((l, r) -> l<r?l:r);
      JunitHelper.nl("samples "+twitterData.size());

      assertTrue("sample size > 0 ", twitterData.size()>0);
      int[] histogram = new int[10];
      Aparapi.range(twitterData).forEach(td -> {
         int digit = td.id;

         while (digit>9){
            digit /= 10;
         }
         // System.out.println(digit);
         histogram[digit]++;
      });

      int total = 0;

      for (int i : histogram){
         JunitHelper.out(i+" ");
         total += i;
      }
      JunitHelper.nl();
      int c = 0;
      float sum = 0f;
      for (int i : histogram){
         float normalized = (i*100f)/total;
         sum += normalized;
         JunitHelper.outf("%d %5.2f :", c++, normalized);
         for (int s = 0; s<normalized; s++){
            JunitHelper.out("*");

         }
         JunitHelper.nl();
      }
      JunitHelper.outf("%5.2f\n", sum);

   }

}
