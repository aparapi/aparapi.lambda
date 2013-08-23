package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;


public class StringLambdaHisto {


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

    static char[][] buildDictionary(String... words){
        char[][] dict = new char[words.length][];
        for (int i=0; i<words.length; i++){
            dict[i]=words[i].toLowerCase().toCharArray();
        }
        return(dict);
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
        String[] strings = buildDictionary(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = strings.length;
        String text = getText(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\alice.txt"));
        int[] counts = new int[len];
        IntConsumer ic = gid -> {
            String chars = strings[gid];
            int count = 0;
            int textLen = text.length();
            int charsLen = chars.length();
            for (int i=0; i<=textLen-charsLen; i++){
                if (i==0 || (i>0 && (text.charAt(i-1)<'a' || text.charAt(i-1)>'z'))){
                    boolean result = true; // optimistic!
                    for (int offset=0; result && offset<charsLen; offset++){
                       result = chars.charAt(offset) == text.charAt(i+offset);
                    }
                    if (result && !(i+charsLen<textLen && (text.charAt(i+charsLen)>='a' && text.charAt(i+charsLen)<='z'))){
                        count++;
                    }
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
