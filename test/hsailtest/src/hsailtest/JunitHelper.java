package hsailtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static junit.framework.Assert.fail;

/**
 * Created by user1 on 1/30/14.
 */
public class JunitHelper{
   static void dump(String type, String[] _strings, int[] results){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<_strings.length; i++){
         if (i != 0){
            s.print(", ");
         }
         s.print(_strings[i]+"="+results[i]);
      }
      s.println();
      }
   }

   static void dump(String type, String[] _strings){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<_strings.length; i++){
         if (i != 0){
            s.print(", ");
         }
         s.print(_strings[i]);
      }
      s.println();
      }
   }

   public static void dump(String type, int[][] array){
      if (verbose){

      s.print(type+" ->");
      for (int x = 0; x<array.length; x++){
         s.print("[");
         for (int y = 0; y<array[0].length; y++){
            if (y != 0){
               s.print(", ");
            }
            s.print(array[x][y]);
         }
         s.print("]");
      }
      s.println();
      }
   }

   static void dump(String type, char[] results){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<results.length; i++){
         if (i != 0){
            s.print(", ");
         }
         s.print(results[i]);
      }
      s.println();
      }
   }

   static void dump(String type, String[] _strings, boolean[] results){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<_strings.length; i++){
         if (i != 0){
            s.print(", ");
         }
         s.print(_strings[i]+(results[i]?"*":"?"));
      }
      s.println();
      }
   }

   static void dump(String type, boolean[] in){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<in.length; i++){
         s.print("("+in[i]+"),");
      }
      s.println();
      }
   }

   static void dump(String type, int[] out){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<out.length; i++){
         s.print(out[i]+",");
      }
      s.println();
      }
   }

   static void dump(String type, int[] out, String format, int max){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<max; i++){
         s.printf(format+",", out[i]);
      }
      s.println();
      }
   }

   static void dump(String type, int[] in, int[] out){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<in.length; i++){
         s.print("("+in[i]+","+out[i]+"),");
      }
      s.println();
      }
   }

   static void dump(String type, double[] in, double[] out){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<in.length; i++){
         s.print("("+in[i]+","+out[i]+"),");
      }
      s.println();
      }
   }

   static void dump(String type, float[] in, float[] out){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<in.length; i++){
         s.print("("+in[i]+","+out[i]+"),");
      }
      s.println();
      }
   }

   static void dump(String type, float[] in){
      if (verbose){
      s.print(type+" ->");
      for (int i = 0; i<in.length; i++){
         s.print(in[i]+",");
      }
      s.println();
   }
   }

   static int getPreferredArraySize(){
      return (Runtime.getRuntime().availableProcessors()*8);
   }

   static boolean compare(int[] in, int[] out){
      for (int i = 0; i<in.length; i++){
         if (in[i] != out[i]){

            return (false);
         }
      }
      return (true);
   }

   static boolean compare(int[] in, int[] out, int _tolerance){
      for (int i = 0; i<in.length; i++){
         if (in[i] != out[i]){
            if (Math.abs(in[i]-out[i])>_tolerance){
               s.println("failed "+i+" "+in[i]+" vs "+out[i]);
               return (false);
            }
         }
      }
      return (true);
   }

   static boolean compare(int[][] in, int[][] out){

      for (int x = 0; x<in.length; x++){
         for (int y = 0; y<in.length; y++){
            if (in[x][y] != out[x][y]){
               return (false);
            }
         }
      }
      return true;

   }

   static boolean compare(boolean[] in, boolean[] out){
      for (int i = 0; i<in.length; i++){
         if (in[i] != out[i]){
            return (false);
         }
      }
      return (true);
   }

   static boolean compare(char[] in, char[] out){
      for (int i = 0; i<in.length; i++){
         if (in[i] != out[i]){
            return (false);
         }
      }
      return (true);
   }

   static boolean compare(double[] in, double[] out){
      for (int i = 0; i<in.length; i++){
         if (!withinTolerance(in[i], out[i])){
            return (false);
         }
      }
      return (true);
   }

   static float floatTolerance = 0.01f;
   static float doubleTolerance = 0.00001f;

   static boolean compare(float[] in, float[] out){
      for (int i = 0; i<in.length; i++){
         if (!withinTolerance(in[i], out[i])){
            return (false);
         }
      }
      return (true);
   }

   static boolean withinTolerance(int lhs, int rhs, int tolerance){
      return (Math.abs(lhs-rhs)<tolerance);
   }

   static boolean withinTolerance(float lhs, float rhs){
      return (Math.abs(lhs-rhs)<floatTolerance);
   }

   static boolean withinTolerance(double lhs, double rhs){
      return (Math.abs(lhs-rhs)<doubleTolerance);
   }

   static int[] copy(int[] in){
      return (Arrays.copyOf(in, in.length));
   }

   static boolean[] copy(boolean[] in){
      return (Arrays.copyOf(in, in.length));
   }

   static float[] copy(float[] in){
      return (Arrays.copyOf(in, in.length));
   }

   static double[] copy(double[] in){
      return (Arrays.copyOf(in, in.length));
   }

   static char[] copy(char[] in){
      return (Arrays.copyOf(in, in.length));
   }

   static int[][] copy(int[][] in){
      int[][] copy = new int[in.length][in[0].length];
      for (int x = 0; x<in.length; x++){
         for (int y = 0; y<in.length; y++){
            copy[x][y] = in[x][y];
         }
      }
      return copy;

   }

   interface LineProcessor{
      String line(String line);
   }

   static void process(File _inFile, File _outFile, LineProcessor _lineProcessor) throws IOException{
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(_outFile)));
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_inFile)));
      for (String line = br.readLine(); line != null; line = br.readLine()){
         bw.append(_lineProcessor.line(line)).append("\n");

      }
      br.close();
      bw.close();
   }

   static void process(File _inFile, LineProcessor _lineProcessor) throws IOException{
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_inFile)));
      for (String line = br.readLine(); line != null; line = br.readLine()){
         _lineProcessor.line(line);

      }
      br.close();
   }

   static String getText(InputStream _is){
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(_is));
      try{
         for (String line = br.readLine(); line != null; line = br.readLine()){
            sb.append(" ").append(line);
         }
         br.close();
         _is.close();
         return (sb.toString());
      }catch (IOException ioe){
         fail(ioe.getMessage());
         return (null);
      }
   }

   static String getLowercaseText(InputStream _is){

      return (getText(_is).toLowerCase());
   }

   static String getLowercaseText(File _file){
      try{
         return (getLowercaseText(new FileInputStream(_file)));
      }catch (IOException ioe){
         return (null);
      }
   }

   enum State{WS, TEXT, SINGLE, DOUBLE}

   ;

   static String[] getSentences(File _file) throws IOException{
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
      for (String line = br.readLine(); line != null; line = br.readLine()){
         sb.append(" ").append(line);
      }
      br.close();
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

               }else if (ch == '\''){
                  stateStack.pop();
                  stateStack.push(State.SINGLE);
               }
         }
      }
      return (sentences.toArray(new String[0]));
   }

   static char[] getLowercaseTextChars(File _file){
      return (getLowercaseText(_file).toCharArray());
   }

   static char[] getLowercaseTextChars(InputStream _is) throws IOException{
      return (getLowercaseText(_is).toCharArray());
   }

   static char[] getLowercaseTextCharsOnly(File _file) throws IOException{
      char[] chars = getLowercaseText(_file).toCharArray();
      for (int i = 0; i<chars.length; i++){
         if (!Character.isAlphabetic(chars[i])){
            chars[i] = ' ';
         }
      }
      return (chars);
   }

   static char[] getLowercaseTextCharsOnly(InputStream _is) throws IOException{
      char[] chars = getLowercaseText(_is).toCharArray();
      for (int i = 0; i<chars.length; i++){
         if (!Character.isAlphabetic(chars[i])){
            chars[i] = ' ';
         }
      }
      return (chars);
   }

   static String getString(InputStream inputStream) throws IOException{
      String text = getLowercaseText(inputStream);
      inputStream.close();
      return (text);

   }

   static String getString(URL url) throws IOException{
      return (getString(url.openConnection().getInputStream()));

   }

   static String getLowerCaseString(File _file) throws IOException{
      InputStream is = new FileInputStream(_file);

      return (getLowercaseText(is));

   }

   static String getString(File _file) throws IOException{
      InputStream is = new FileInputStream(_file);

      return (getText(is));

   }

   static String[] buildDictionary(File _file) throws IOException{
      List<String> list = new ArrayList<String>();
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
      for (String line = br.readLine(); line != null; line = br.readLine()){
         if (!line.trim().startsWith("//")){
            list.add(line.trim());
         }else{
            s.println("Comment -> "+line);
         }
      }
      while ((list.size()%256) != 0){
         list.add("xxxxx");
      }

      return (list.toArray(new String[0]));
   }

   static String[] buildLowerCaseDictionary(File _file){
      List<String> list = new ArrayList<String>();
      StringBuilder sb = new StringBuilder();
      try{
         BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
         for (String line = br.readLine(); line != null; line = br.readLine()){
            if (!line.trim().startsWith("//")){
               list.add(line.trim().toLowerCase());
            }else{
               s.println("Comment -> "+line);
            }
         }
         while ((list.size()%64) != 0){
            list.add("xxxxx");
         }

         return (list.toArray(new String[0]));
      }catch (IOException ioe){
         fail(ioe.getMessage());
         return (null);
      }
   }

   static char[][] buildLowerCaseDictionaryChars(File _file){
      String[] lowerCaseDictionary = buildLowerCaseDictionary(_file);
      char[][] chars = new char[lowerCaseDictionary.length][];
      for (int i = 0; i<lowerCaseDictionary.length; i++){
         chars[i] = lowerCaseDictionary[i].toCharArray();
      }

      return (chars);
   }

   static char[][] buildWhiteSpacePaddedDictionaryChars(File _file) throws IOException{
      String[] dictionary = buildDictionary(_file);
      char[][] chars = new char[dictionary.length][];
      for (int i = 0; i<dictionary.length; i++){
         chars[i] = (" "+dictionary[i]+" ").toCharArray();
      }

      return (chars);
   }

   static boolean verbose=Boolean.getBoolean("verbose");
   static PrintStream s = System.out;

   static void nl(String _txt){

      if (verbose){
         s.println(_txt);
      }
   }
   static void nl(){

     nl("");
   }
   static void nl(int _i){

      nl(""+_i);
   }

   static void out(String _txt){

      if (verbose){
         s.print(_txt);
      }
   }

   static void outf(String _format, Object ... args){

      if (verbose){
         s.printf(_format, args);
      }
   }

}
