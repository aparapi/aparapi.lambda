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


public class GetDickens {
    public class Book {
        String title;
        char[] text;
        URL url;

        Book(String _title, URL _url) {
            title = _title;
            url = _url;
        }

        void get() {
            System.out.print("Reading " + title);
            try {
                text = TextTools.getString(url).toCharArray();
            } catch (IOException e) {
                text = new char[0];
                System.out.print("failed! ");
            }
            System.out.println();
        }

        void checkNames(Device device, char[][] names, boolean[] found) {

            int textLen = text.length;
            device.forEach(names.length, gid -> {
                if (found[gid]) {

                    boolean result = false;
                    char[] nameChars = names[gid];
                    int nameCharLen = nameChars.length;

                    for (int i = 0; !result && i <= textLen - nameCharLen - 1; i++) {
                        if ((i == 0 || text[i - 1] == ' ') && text[i + nameCharLen] == ' ') {
                            result = true; // optimistic!
                            for (int offset = 0; result && offset < nameCharLen; offset++) {
                                result = nameChars[offset] == text[i + offset];
                            }
                        }

                    }

                if (!result){
                    found[gid] = false;

                }
                }
            });

        }
    }
   void dump(String s, char[][] names, boolean[] found){

   }

    public static void main(String[] args) throws IOException {
        new GetDickens().go();
    }

    public void test(String type, Device dev, Book[] library, char[][]names){
        boolean[] found = new boolean[names.length];
        Arrays.fill(found, true);

        long start = System.currentTimeMillis();
        for (Book book : library) {
            book.checkNames(dev, names, found);
        }
        long end = System.currentTimeMillis();


        System.out.print(type+" -> "+(end-start));
        boolean first = true;
        for (int i = 0; i < names.length; i++) {
            if (found[i]) {
                if (!first) {
                    System.out.print(", ");
                } else {
                    first = false;
                }

                for (char c : names[i]) {
                    System.out.print(c);
                }
            }

        }
        System.out.println();

    }

    public void go() throws IOException {
        Book[] library = new Book[]{
                new Book("A Tail Of Two Cities", new URL("http://www.gutenberg.org/cache/epub/1023/pg1023.txt")),
                new Book("A Christmas Carol", new URL("http://www.gutenberg.org/files/24022/24022-0.txt")),
               // new Book("Great expectations", new URL("http://www.gutenberg.org/cache/epub/1400/pg1400.txt")),
               // new Book("David Copperfield", new URL("http://www.gutenberg.org/cache/epub/766/pg766.txt")),
               // new Book("Nicolas Nickleby", new URL("http://www.gutenberg.org/cache/epub/967/pg967.txt")),
               // new Book("Oliver Twist", new URL("http://www.gutenberg.org/cache/epub/730/pg730.txt"))
        };
        Device.seq().forEach(library.length, i -> library[i].get());


        char[][] names = TextTools.buildLowerCaseDictionaryChars(new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\names.txt"));

        test("hsa", Device.hsa(), library, names);
        test("hsa", Device.hsa(), library, names);
        test("jtp", Device.jtp(), library, names);

    }


}
