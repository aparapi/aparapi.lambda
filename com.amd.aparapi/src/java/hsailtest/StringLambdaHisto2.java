package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;


public class StringLambdaHisto2 {


    static void dump(String type, String[] _strings, int[] results) {
        System.out.print(type + " ->");
        boolean first = true;
        for (int i = 0; i < _strings.length; i++) {
            if (results[i]>0){
            if (!first) {
                System.out.print(", ");
            }   else{
                first = false;
            }


                System.out.print(_strings[i]+"=" + results[i]);
            }

        }
        System.out.println();
    }

    static String getText(File _file) throws IOException {
       StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line.toLowerCase());
        }
        return(sb.toString());
    }




    static String[] buildDictionary(File _file) throws IOException {
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            if (!line.trim().startsWith("//")){
               list.add(line.trim().toLowerCase()) ;
            }else{
                System.out.println("Comment -> "+line);
            }
        }
        while(list.size()%64==0){
            list.add("xxxxx");
        }

        return(list.toArray(new String[0]));
    }


    public static void main(String[] args) throws AparapiException, IOException {
        File dir = new File("C:\\Users\\user1\\aparapi\\branches\\lambda");
        String[] strings = buildDictionary(new File(dir, "names.txt"));
        int len = strings.length;
        String text = getText(new File(dir, "moby.txt"));
        int[] counts = new int[len];
        IntConsumer ic = gid -> {
            String word = strings[gid];
            int wordLen = word.length();
            int count = 0;
            int index = 0;
            int textLen = text.length();
            while (index <textLen){
               index = text.indexOf(word, index);
               if (index == -1){
                   break;
               }
               count++;
               index+=wordLen;

            }

            counts[gid] = count;
        };
        Arrays.fill(counts, 0);

        long start = System.currentTimeMillis();
        Device.jtp().forEach(len, ic);
        dump("jtp = " + (System.currentTimeMillis() - start), strings, counts);
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        dump("hsa1= "+(System.currentTimeMillis()-start), strings, counts);
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        System.out.println();
        dump("hsa2= "+(System.currentTimeMillis()-start), strings, counts);
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.seq().forEach(len, ic);
        System.out.println();
        dump("seq= "+(System.currentTimeMillis()-start), strings, counts);
    }
}
