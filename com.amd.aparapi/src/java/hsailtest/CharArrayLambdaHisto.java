package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;

import static com.amd.aparapi.Device.hsaForEach;
import static com.amd.aparapi.Device.jtpForEach;
import static com.amd.aparapi.Device.seqForEach;
import static com.amd.aparapi.Device.hybForEach;


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
                System.out.print("=" + results[i]);
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
        while(list.size()%64!=0){
            list.add("xxxxx".toCharArray());
        }

        return(list.toArray(new char[0][]));
    }


    public static void main(String[] args) throws AparapiException, IOException {
        char[][] strings = buildDictionary(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));
        int len = strings.length;
        char[] text = getText(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\moby.txt"));
        int[] counts = new int[len];
        IntConsumer ic = gid -> {
            char[] chars = strings[gid];
            char firstChar=chars[0];
            int count = 0;
            for (int i=0; i<=text.length-chars.length; i++){
                char prevChar =0;
                if (i>0){
                   prevChar = text[i-1];
                }
                if (firstChar==text[i] && (prevChar<'a' || prevChar>'z')){

                    boolean result = true; // optimistic!
                    for (int offset=1; result && offset<chars.length; offset++){
                       result = chars[offset] == text[i+offset];
                    }
                    char endChar=0;
                    if ((i+chars.length)<text.length){
                        endChar=text[i+chars.length];
                    }
                    if (result && (endChar<'a' || endChar>'z')){
                        count++;
                    }
                }
            }
            counts[gid] = count;
        };

        long start=0L;
        boolean seq = false;
        boolean jtp = true;
        boolean hyb = true;
        boolean hsa = true;

        if (hsa){

        for (int i=0; i<4; i++){
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        hsaForEach(len, ic);
        System.out.println();
        dump("hsa"+i+"= "+(System.currentTimeMillis()-start), strings, counts);
        }


        }
        if (hyb){
        for (float gpushare : new float[]{.88f,.89f,.9f,.91f, .92f,.93f, .94f,.95f, .96f,.97f,.98f }) {
            Arrays.fill(counts, 0);
            start = System.currentTimeMillis();
            hybForEach(len, gpushare, ic);
            System.out.println();
            dump("hyb"+gpushare+"= "+(System.currentTimeMillis()-start), strings, counts);
        }
        }

        if (jtp){
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        jtpForEach(len, ic);
        System.out.println();
        dump("jtp = "+(System.currentTimeMillis()-start), strings, counts);
        }
        if (seq){
        Arrays.fill(counts, 0);
        start = System.currentTimeMillis();
        seqForEach(len, ic);
        System.out.println();
        dump("seq= "+(System.currentTimeMillis()-start), strings, counts);
        }
    }
}
