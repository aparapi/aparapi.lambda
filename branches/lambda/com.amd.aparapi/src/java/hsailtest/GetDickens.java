package hsailtest;

import com.amd.aparapi.Device;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.IntConsumer;
import java.util.Comparator;


public class GetDickens {

    public class Name {
       char[] name;
       int count;
       Name(char[] _chars){
          name = _chars;
          count = 0;
       }
    }
    public class Book {
        String title;
        char[] text;
        URL url;
         File file;

        Book(String _title, URL _url) {
            title = _title;
            url = _url;
        }
       Book(String _title, File _file) {
          title = _title;
          file = _file;
       }

        void get() {
            System.out.print("Reading " + title);
            try {
                if (url != null){
                text = TextTools.getString(url).toCharArray();
                }else if (file != null) {
                   text = TextTools.getString(file).toCharArray();
                }
            } catch (IOException e) {
                text = new char[0];
                System.out.print("failed! ");
            }
            System.out.println();
        }

        void checkNames(Device device, Name[] names) {

            int textLen = text.length;


            device.forEach(names.length, gid -> {

                    boolean result = false;
                    char[] nameChars = names[gid].name;
                    int nameCharLen = nameChars.length;
                    int count = 0;
                    for (int i = 0; i <= textLen - nameCharLen - 1; i++) {
                        if ((i == 0 || text[i - 1] == ' ') && text[i + nameCharLen] == ' ') {
                            result = true; // optimistic!
                            for (int offset = 0; result && offset < nameCharLen; offset++) {
                                result = nameChars[offset] == text[i + offset];
                            }
                            if (result){
                               count++;
                            }
                        }

                    }
                    names[gid].count+=count;


            });

        }
    }
   void dump(String s, char[][] names, boolean[] found){

   }

    public static void main(String[] args) throws IOException {
        new GetDickens().go();
    }

    public void test(String type, Device dev, Book[] library, Name[] names){

        long start = System.currentTimeMillis();
        for (Book book : library) {
            book.checkNames(dev, names);
        }
        long end = System.currentTimeMillis();

       Name[] sortedNames = new Name[names.length];
       for (int i = 0; i<names.length; i++){
          sortedNames[i] = names[i];
       }
       Arrays.sort(sortedNames, new Comparator<Name>(){
          @Override public int compare(Name lhs, Name rhs){
             return (rhs.count-lhs.count);

          }
       });
        System.out.print(type+" -> "+(end-start));
        boolean first = true;
        for (int i = 0; i < sortedNames.length; i++) {
            if (sortedNames[i].count>0) {
                if (!first) {
                    System.out.print(", ");
                } else {
                    first = false;
                }

                for (char c : sortedNames[i].name) {
                    System.out.print(c);
                }
                System.out.print("("+sortedNames[i].count+")");
            }

        }
        System.out.println();

    }

    public void go() throws IOException {
       File lambdaDir = new File("/Users/garyfrost/aparapi/aparapi/branches/lambda");
         File booksDir = new File(lambdaDir, "books/dickens");
        Book[] library = new Book[] {

       new Book("A Tail Of Two Cities", new File(booksDir, "ATailOfTwoCities.txt")),
           //  new Book("A Christmas Carol", new File(booksDir, "AChristmasCarol.txt")),
           //   new Book("Great Expectations", new File(booksDir, "GreatExpectations.txt")),
           //   new Book("David Copperfield", new File(booksDir, "DavidCopperfield.txt")),
            //  new Book("Nicolas Nickleby", new File(booksDir,"NicolasNickleby.txt")),
            //  new Book("Oliver Twist", new File(booksDir,"OliverTwist.txt")),
              //  new Book("A Tail Of Two Cities", new URL("http://www.gutenberg.org/cache/epub/1023/pg1023.txt")),
              //  new Book("A Christmas Carol", new URL("http://www.gutenberg.org/files/24022/24022-0.txt")),
               // new Book("Great expectations", new URL("http://www.gutenberg.org/cache/epub/1400/pg1400.txt")),
               // new Book("David Copperfield", new URL("http://www.gutenberg.org/cache/epub/766/pg766.txt")),
               // new Book("Nicolas Nickleby", new URL("http://www.gutenberg.org/cache/epub/967/pg967.txt")),
               // new Book("Oliver Twist", new URL("http://www.gutenberg.org/cache/epub/730/pg730.txt"))
        };
        Device.seq().forEach(library.length, i -> library[i].get());


        char[][] nameChars = TextTools.buildLowerCaseDictionaryChars(new File(lambdaDir, "names.txt"));
        Name[] names = new Name[nameChars.length];
        Device.seq().forEach(names.length, i -> names[i] = new Name(nameChars[i]));


        //test("hsa", Device.hsa(), library, names);
        //test("hsa", Device.hsa(), library, names);
        test("jtp", Device.jtp(), library, names);
       Device.seq().forEach(names.length, i -> names[i].count = 0);
       test("seq", Device.seq(), library, names);


    }


}
