package hsailtest;

import com.amd.aparapi.AparapiException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.IntConsumer;

import static com.amd.aparapi.Device.*;


public class TextTools {



    static String getLowercaseText(File _file) throws IOException {
       StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line.toLowerCase());
        }
        return(sb.toString());
    }

    enum State { WS, TEXT, SINGLE, DOUBLE};

    static String[] getSentences(File _file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        for (String line=br.readLine(); line != null; line=br.readLine()){
            sb.append(" ").append(line);
        }
        String asString = sb.toString();
        List<String> sentences = new ArrayList<String>();
        Stack<State> stateStack = new Stack<State>();
        stateStack.push(State.WS);
        int firstNonWs = 0;
        for (int index = 0; index<asString.length(); index++){
           char ch = asString.charAt(index);
           switch (stateStack.peek()){
               case WS:
                   if (Character.isWhitespace(ch)){

                   } else if (ch == '\''){
                       stateStack.pop();
                       stateStack.push(State.SINGLE);
                   }
           }
        }
        return(sentences.toArray(new String[0]));
    }

    static char[] getLowercaseTextChars(File _file) throws IOException {
        return(getLowercaseText(_file).toCharArray());
    }

    static String[] buildLowerCaseDictionary(File _file) throws IOException {
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
        while(list.size()%64!=0){
            list.add("xxxxx");
        }

        return(list.toArray(new String[0]));
    }

    static char[][] buildLowerCaseDictionaryChars(File _file) throws IOException {
        String[] lowerCaseDictionary=buildLowerCaseDictionary(_file);
        char[][] chars = new char[lowerCaseDictionary.length][];
        for (int i=0; i<lowerCaseDictionary.length; i++){
            chars[i]=lowerCaseDictionary[i].toCharArray();
        }

        return(chars);
    }


}
