package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;


public class CharArrayLambdaHisto {


    static void dump(String type, char[][] _strings, int[] results) {
        System.out.print(type + " ->");
        boolean first = true;
        for (int i = 0; i < _strings.length; i++) {
            if (results[i]>0){
            if (!first) {
                System.out.print(", ");
            }   else{
                first = false;
            }

            for (char c:_strings[i]){
                System.out.print(c);
            }
                System.out.print("="+results[i]);
            }

        }
        System.out.println();
    }

    static char[] getText(File _file) throws IOException {
       StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line.toLowerCase());
        }
        return(sb.toString().toCharArray());
    }

    static char[][] buildDictionary(String... words){
        char[][] dict = new char[words.length][];
        for (int i=0; i<words.length; i++){
            dict[i]=words[i].toLowerCase().toCharArray();
        }
        return(dict);
    }


    static char[][] buildDictionary(File _file) throws IOException {
        List<char[]> list = new ArrayList<char[]>();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            if (!line.trim().startsWith("//")){
               list.add(line.trim().toLowerCase().toCharArray()) ;
            }else{
                System.out.println("Comment -> "+line);
            }
        }
        while(list.size()%64==0){
            list.add("xxxxx".toCharArray());
        }

        return(list.toArray(new char[0][]));
    }


    public static void main(String[] args) throws AparapiException, IOException {
        char[][] strings = buildDictionary(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = strings.length;
        char[] text = getText(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\alice.txt"));
        int[] counts = new int[len];
        IntConsumer ic = gid -> {
            char[] chars = strings[gid];
            int count = 0;
            for (int i=0; i<=text.length-chars.length; i++){
                boolean result = true; // optimistic!
                for (int offset=0; result && offset<chars.length; offset++){

                    result = chars[offset] == text[i+offset];
                }
                if (result){
                    count++;
                }
            }
            counts[gid] = count;
        };
        Arrays.fill(counts, 0);

        long start = System.currentTimeMillis();
        Device.jtp().forEach(len, ic);
        System.out.println();
        dump("jtp = "+(System.currentTimeMillis()-start), strings, counts);

        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        Device.hsa().forEach(len, ic);
        System.out.println();
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
