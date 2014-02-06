/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hsailtest;


import com.amd.aparapi.Device;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;


class Book implements Comparable<Book> {

        private String title;
        private char[] textChars;


        String getTitle() {
            return (title);
        }





        Book(String _title, File _file) {
            title = _title;
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(" ").append(line);
                }
                br.close();
                textChars = sb.toString().toCharArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int compareTo(Book other) {
            return (other.title.compareTo(title));
        }


        void checkNames(Device _device, Name[] _names) {

            System.out.println("started "+title);
            char[] textChars=this.textChars;

            _device.forEach(_names.length, gid -> { // process name array in parallel
                Name name = _names[gid];
                char[] nameChars = name.getNameChars();
                int nameLen = nameChars.length;
                int textLen = textChars.length;
                int count=name.getCount();
                for (int i = 0; i < textLen - nameLen; i++) {
                    int offset = 0;
                    while (offset < nameLen && (nameChars[offset] == textChars[i + offset])) {
                        offset++;
                    }
                    if (offset == nameLen) {
                       count++;
                    }
                }
                name.setCount(count);
            });


        }
    }

     class Name implements Comparable<Name> {
        char[] nameChars;
        String nameAsString;
        int count;



        void incrementCount(int _delta) {
            count+=_delta;
        }

        int getCount() {
            return (count);
        }
        void setCount(int _count) {
            count = _count;
        }

        Name(String _nameAsString) {
            nameAsString=_nameAsString;
            nameChars = nameAsString.toCharArray();
            count = 0;
        }

        @Override
        public int compareTo(Name other) {
            return (other.count - count);
        }


        String getNameAsString() {
            return (nameAsString);
        }

        char[] getNameChars() {
            return (nameChars);
        }
    }

public class DickensJUnit {



    @Test
    public void test() {
        File dataDir = new File("../../samples/dickens/data"); // we do assume standard layout of samples!
        File booksDir = new File(dataDir, "dickens");
        Book[] library = new Book[]{
                new Book("A Tail Of Two Cities", new File(booksDir, "ATailOfTwoCities.txt")),
                new Book("A Christmas Carol", new File(booksDir, "AChristmasCarol.txt")),
                new Book("Great Expectations", new File(booksDir, "GreatExpectations.txt")),
                new Book("David Copperfield", new File(booksDir, "DavidCopperfield.txt")),
              //  new Book("Nicolas Nickleby", new File(booksDir, "NicolasNickleby.txt")),
              //  new Book("Oliver Twist", new File(booksDir, "OliverTwist.txt")),
              //  new Book("The Pickwick Papers", new File(booksDir, "ThePickwickPapers.txt")),
               // new Book("Little Dorrit", new File(booksDir, "LittleDorrit.txt")),
               // new Book("Dombey And Son", new File(booksDir, "DombeyAndSon.txt")),
               // new Book("The Old Curiosity Shop", new File(booksDir, "TheOldCuriosityShop.txt")),
                //new Book("Hard Times", new File(booksDir, "HardTimes.txt"))
        };

        List<Name> jtpList = new ArrayList<Name>();
        List<Name> hsaList = new ArrayList<Name>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dataDir, "names.txt"))));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                jtpList.add(new Name((" " + line.trim() + " ")));
                hsaList.add(new Name((" " + line.trim() + " ")));
            }
            int padNames = Integer.getInteger("padNames", 64);
            System.out.println("padName=" + padNames);
            // We pad the name list to a group boundary.
            while (padNames > 0 && (jtpList.size() % padNames) != 0) {
                jtpList.add(new Name("xxxxx"));
                hsaList.add(new Name("xxxxx"));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Name[][] nameLists = new Name[][]{jtpList.toArray(new Name[0]),hsaList.toArray(new Name[0])};


        Device[] devices = new Device[]{Device.jtp(),Device.hsa()};

        for (int deviceIndex =0; deviceIndex<devices.length; deviceIndex++){
            Device device = devices[deviceIndex];
            Name[] names = nameLists[deviceIndex];
            long startMs = System.currentTimeMillis();
            System.out.println("Starting on device "+device.getClass().getSimpleName());
            for (Book book : library) {
                book.checkNames(device, names);
            }
            long endMs = System.currentTimeMillis();
            System.out.println("Elapsed = " + (endMs - startMs));
        }

        for (int nameIdx=0; nameIdx<nameLists[0].length; nameIdx++){
            if (!nameLists[0][nameIdx].getNameAsString().equals(nameLists[1][nameIdx].getNameAsString())){
                fail("name mismatch "+nameIdx);
            }
            if (nameLists[0][nameIdx].getCount()!=nameLists[1][nameIdx].getCount()){
                fail("count mismatch "+nameIdx);
            }
        }
    }
}

