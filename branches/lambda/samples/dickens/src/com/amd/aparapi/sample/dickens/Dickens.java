/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amd.aparapi.sample.dickens;


import com.amd.aparapi.Device;
import com.amd.aparapi.sample.common.AparapiModeToggleButton;
import com.amd.aparapi.sample.common.IconManager;
import com.amd.aparapi.sample.common.ModeToggleButton;
import com.amd.aparapi.sample.common.Watch;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * JVM Args
 * -server
 * -showversion
 * -XX:-UseCompressedOops
 * -XX:-BootstrapGraal
 * -XX:+ShowMessageBoxOnError
 * -G:+InlineEverything
 * -G:-RemoveNeverExecutedCode
 * -Dsun.boot.library.path=C:\Users\gary\okra\dist\bin;C:\Users\gary\okra\hsa\bin\x86_64
 * -Xms1g
 * -Xmx1g
 * -Dcom.amd.sumatra.offload.immediate=true
 * <p/>
 * Environment args
 * set ENABLE64=1
 * set GPU_BLIT_ENGINE_TYPE=2
 * set HSA_RUNTIME=1
 * set PATH=%PATH%;c:\Users\gary\okra\hsa\bin\x86_64
 * <p/>
 * set JAVA_HOME=C:\Users\gary\graal-win-release-131022\jdk1.8.0-internal\product
 *
 * @author user1
 */
public class Dickens {

    public  static  class Book implements Comparable<Book> {

        private String title;
        private char[] text;
        private boolean selected = true;
        private boolean done = false;
        private boolean started = false;

        public char[] getText() {
            return (text);
        }

        public void start() {
            started = true;
        }

        public void done() {
            done = true;
        }

        void clear() {
            done = false;
            started = false;
        }

        String getTitle() {
            return (title);
        }

        boolean isSelected() {
            return (selected);
        }

        void setSelected(boolean _selected) {
            selected = _selected;
        }

        boolean isDone() {
            return (done);
        }

        boolean isStarted() {
            return (started);
        }

        Book(String _title, String _text) {
            title = _title;
            text = _text.toCharArray();
        }

        Book(String _title, File _file, boolean _selected) {
            title = _title;
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(" ").append(line);
                }
                br.close();
                text = sb.toString().toCharArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            selected = _selected;
        }

        @Override
        public int compareTo(Book other) {
            return (other.title.compareTo(title));
        }


        void checkNames(Name[] names) {
            started = true;
            //  System.out.println("started "+title);

            IntStream.range(0, names.length).parallel().forEach(gid -> { // process name array in parallel
                Name name = names[gid];
                char[] nameChars = name.getName();
                int nameLen = nameChars.length;
                int textLen = text.length;
                for (int i = 0; i < textLen - nameLen; i++) {
                    int offset = 0;
                    // search i + offset to i + nameLen and bump offset if chars match
                    while (offset < nameLen && (nameChars[offset] == text[i + offset])) {
                        offset++;
                    }
                    if (offset == nameLen) {
                        name.incrementCount();
                    }
                }

            });

            done = true;

        }
    }

    static public class Name implements Comparable<Name> {
        private char[] name;
        private int count;

        void clear() {
            count = 0;
        }

        void incrementCount() {
            count++;
        }

        int getCount() {
            return (count);
        }


        Name(char[] _chars) {
            name = _chars;
            count = 0;
        }

        @Override
        public int compareTo(Name other) {
            return (other.count - count);
        }

        String getNameAsString() {
            return (new String(name));
        }

        char[] getName() {
            return (name);
        }
    }


    public static class UI extends JFrame {
        NameTable nameTable;
        BookList bookList;
        Watch watch = new Watch();
        Book[] library;
        Name[] names;
        AparapiModeToggleButton hsaToggleButton;

        UI(Book[] _library, Name[] _names) {
            super("Dickens");

            library = _library;
            names = _names;
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                nameTable = new NameTable(names);
                bookList = new BookList(library);

                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel panel = new JPanel(new BorderLayout());
                JPanel east = new JPanel();
                JPanel center = new JPanel(new BorderLayout());
                JPanel west = new JPanel(new BorderLayout());
                JPanel south = new JPanel();
                JButton startButton = new JButton(IconManager.startIcon);
                startButton.addActionListener(ae -> go());
                JPanel clockAndGo = new JPanel();
                clockAndGo.add(startButton);
                clockAndGo.add(watch.getContainer());

                west.add(BorderLayout.NORTH, bookList.container);
                west.add(BorderLayout.CENTER, clockAndGo);
                JPanel buttonPad = new JPanel();
                hsaToggleButton = new AparapiModeToggleButton(150, ModeToggleButton.Mode.MultiCore);
                buttonPad.add(hsaToggleButton);

                west.add(BorderLayout.SOUTH, buttonPad);

                center.add(BorderLayout.NORTH, nameTable.container);
                panel.add(BorderLayout.EAST, east);
                panel.add(BorderLayout.CENTER, center);
                panel.add(BorderLayout.WEST, west);
                panel.add(BorderLayout.SOUTH, south);
                getContentPane().add(panel);
                pack();
                setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        volatile boolean clockRunning = false;


        public void go() {
            clockRunning = true;
            SwingWorker sw1 = new SwingWorker<Void, Void>() {

                @Override
                public Void doInBackground() {
                    try {
                        while (clockRunning) {
                            Thread.sleep(500);
                            nameTable.update();
                            bookList.update();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return (null);
                }
            };
            sw1.execute();

            Device device = hsaToggleButton.getDevice();
            SwingWorker sw = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    Arrays.stream(library).forEach(Book::clear);
                    Arrays.stream(names).forEach(Name::clear);
                    watch.start(); // start the timer
                    Arrays.stream(library)
                            .filter(Book::isSelected)
                            .forEach(book -> { // this outer loop is processed sequentially
                                book.start();

                                    char[] text = book.getText();
                                    int textLen = text.length;
                                    Name[] localNames = names;
                                    device.forEach(names.length, id -> {
                                        Name name = localNames[id];
                                        char[] nameChars = name.getName();
                                        int nameLen = nameChars.length;
                                        for (int i = 0; i < textLen - nameLen; i++) {
                                            int offset = 0;
                                             for (offset = 0; offset < nameLen && (nameChars[offset] == text[i + offset]); offset++)
                                                ;
                                             if (offset == nameLen) {
                                                name.incrementCount();
                                             }
                                        }
                                    });
                                book.done();
                            });
                    clockRunning = false;
                    watch.stop();  // stop the timer
                    return (null);
                }
            };

            sw.execute();

        }
    }

    public static class NameTable {
        Name[] names;
        Name[] topSortedNames;

        JPanel container;

        class MyTableModel extends AbstractTableModel {


            private String[] columnNames = new String[]{
                    "Name",
                    "Count", ""};

            private int[] columnWidths = new int[]{
                    200,
                    30};

            public int getColumnCount() {
                return columnNames.length;
            }

            public int getRowCount() {
                return topSortedNames == null ? 0 : topSortedNames.length;
            }

            public String getColumnName(int col) {
                return columnNames[col];
            }

            public Object getValueAt(int row, int col) {
                if (topSortedNames == null) {
                    return (null);
                }
                return col == 0 ? topSortedNames[row].getNameAsString() : topSortedNames[row].getCount();
            }

            /*
             * JTable uses this method to determine the default renderer/
             * editor for each cell.  If we didn't implement this method,
             * then the last column would contain text ("true"/"false"),
             * rather than a check box.
             */
            public Class getColumnClass(int c) {
                Class rendererClass =

                        getValueAt(0, c).getClass();

                return (rendererClass);
            }


        }

        // int highestCount = 0;

        int maxNames = 30;

        void update() {
            Name[] sortedNames = Arrays.copyOf(names, names.length);
            Arrays.sort(sortedNames);
            Name[] newTopSortedNames = Arrays.copyOf(sortedNames, maxNames);

            topSortedNames = newTopSortedNames;
            tableModel.fireTableDataChanged();
        }

        int maxBarWidth = 400;
        float range = ((float) maxBarWidth) / 800; // max is actually around 703

        class HistoRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (column != 2) {
                    return (super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
                } else {
                    JPanel p = new JPanel() {
                        @Override
                        public void paint(Graphics g) {
                            super.paint(g);
                            g.setColor(Color.RED);
                            float w = ((Integer) value).intValue() * range;
                            g.fillRect(0, 2, (int) w, 8);
                        }
                    };

                    return (p);
                }
            }

            HistoRenderer() {
                setHorizontalAlignment(JLabel.RIGHT);
            }
        }

        AbstractTableModel tableModel = new MyTableModel();
        JTable table = new JTable(tableModel);

        NameTable(Name[] _names) {
            names = _names;

            container = new JPanel();
            table.setRowHeight(20);
            table.setFont(new Font("Arial", Font.BOLD, 18));
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(1).setPreferredWidth(80);
            table.getColumnModel().getColumn(2).setPreferredWidth(maxBarWidth);
            table.setDefaultRenderer(Integer.class, new HistoRenderer());

            table.setPreferredScrollableViewportSize(new Dimension(600, 600));

            table.setFillsViewportHeight(true);

            //Create the scroll pane and add the table to it.
            JScrollPane scrollPane = new JScrollPane(table);

            //Add the scroll pane to this panel.
            container.add(scrollPane);
        }
    }

    public static class BookList {
        Book[] books;
        JPanel container;
        private String[] columnNames = new String[]{
                "Title",
                "Selected"};

        private int[] columnWidths = new int[]{
                160,
                40};


        class MyTableModel extends AbstractTableModel {
            @Override
            public boolean isCellEditable(int row, int col) {
                //Note that the data/cell address is constant,
                //no matter where the cell appears onscreen.
                boolean result = (col == 1);
                return (result);
            }


            @Override
            public void setValueAt(Object value, int row, int col) {
                books[row].setSelected(((Boolean) value).booleanValue());
                fireTableCellUpdated(row, col);
            }


            public int getColumnCount() {
                return columnNames.length;
            }

            public int getRowCount() {
                return books == null ? 0 : books.length;
            }

            public String getColumnName(int col) {
                return columnNames[col];
            }

            public Object getValueAt(int row, int col) {
                Object returnValue = null;
                switch (col) {
                    case 0:
                        returnValue = books[row].getTitle();
                        break;
                    case 1:
                        returnValue = books[row].isSelected();
                        break;
                    // case 2:  returnValue =  books[row].isDone(); break;
                }
                return returnValue;
            }

            /*
             * JTable uses this method to determine the default renderer/
             * editor for each cell.  If we didn't implement this method,
             * then the last column would contain text ("true"/"false"),
             * rather than a check box.
             */
            public Class getColumnClass(int c) {


                return (getValueAt(0, c).getClass());
            }


        }

        synchronized void update() {
            tableModel.fireTableDataChanged();
        }

        Color started = new Color(0, 240, 100);
        Color done = new Color(0, 200, 230);
        Color unselected = new Color(255, 255, 255);

        class DoneRenderer extends DefaultTableCellRenderer {
            TableCellRenderer delegate;

            DoneRenderer(TableCellRenderer _delegate) {
                delegate = _delegate;
                setHorizontalAlignment(JLabel.RIGHT);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (books[row].isDone()) {
                    c.setBackground(done);
                } else if (books[row].isStarted()) {
                    c.setBackground(started);
                } else {
                    c.setBackground(unselected);
                }

                return (c);

            }


        }

        AbstractTableModel tableModel = new MyTableModel();
        JTable table = new JTable(tableModel);

        BookList(Book[] _books) {
            books = _books;
            container = new JPanel();
            table.setRowHeight(20);
            table.setFont(new Font("Arial", Font.BOLD, 18));
            table.getColumnModel().getColumn(0).setPreferredWidth(columnWidths[0]);
            table.getColumnModel().getColumn(1).setPreferredWidth(columnWidths[1]);
            table.setPreferredScrollableViewportSize(new Dimension(300, 300));

            table.setDefaultRenderer(Boolean.class, new DoneRenderer(table.getDefaultRenderer(Boolean.class)));
            table.setDefaultRenderer(String.class, new DoneRenderer(table.getDefaultRenderer(String.class)));
            table.setFillsViewportHeight(true);
            JScrollPane scrollPane = new JScrollPane(table);
            container.add(scrollPane);
        }
    }


    public static void main(String[] args) {
        new Dickens().go();
    }

    UI ui;

    public void go() {
        File dataDir = new File("data");
        File booksDir = new File(dataDir, "dickens");
        Book[] library = new Book[]{
                new Book("A Tail Of Two Cities", new File(booksDir, "ATailOfTwoCities.txt"), true),
                new Book("A Christmas Carol", new File(booksDir, "AChristmasCarol.txt"), true),
                new Book("Great Expectations", new File(booksDir, "GreatExpectations.txt"), true),
                new Book("David Copperfield", new File(booksDir, "DavidCopperfield.txt"), true),
                new Book("Nicolas Nickleby", new File(booksDir, "NicolasNickleby.txt"), false),
                new Book("Oliver Twist", new File(booksDir, "OliverTwist.txt"), false),
                new Book("The Pickwick Papers", new File(booksDir, "ThePickwickPapers.txt"), false),
                new Book("Little Dorrit", new File(booksDir, "LittleDorrit.txt"), false),
                new Book("Dombey And Son", new File(booksDir, "DombeyAndSon.txt"), false),
                new Book("The Old Curiosity Shop", new File(booksDir, "TheOldCuriosityShop.txt"), false),
                new Book("Hard Times", new File(booksDir, "HardTimes.txt"), false),
                //   new Book("The Lamplighter", new File(booksDir, "TheLampLighter.txt"), false)
        };

        List<Name> list = new ArrayList<Name>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dataDir, "names.txt"))));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                list.add(new Name((" " + line.trim() + " ").toCharArray()));
            }
            int padNames = Integer.getInteger("padNames", 64);
            System.out.println("padName=" + padNames);
            // We pad the name list to a group boundary.
            while (padNames > 0 && (list.size() % padNames) != 0) {
                list.add(new Name("xxxxx".toCharArray()));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Name[] names = list.toArray(new Name[0]);
        Arrays.stream(names).forEach(Name::clear);

        new Book("empty", " this text ").checkNames(names);   // We run once so we can extract initial HSA generation from timing.

        if (Boolean.getBoolean("NoUI")) {
            Arrays.stream(library).forEach(Book::clear);
            Arrays.stream(names).forEach(Name::clear);
            long gpuStart = System.currentTimeMillis();
            System.out.println("Starting on GPU");
            for (Book book : library) {
                if (book.isSelected()) {
                    System.out.println("   GPU processing " + book.getTitle());
                    book.checkNames(names);
                }
            }
            long gpuEnd = System.currentTimeMillis();
            System.out.println("GPU = " + (gpuEnd - gpuStart));
            Arrays.stream(library).forEach(Book::clear);
            Arrays.stream(names).forEach(Name::clear);
            long cpuStart = System.currentTimeMillis();
            System.out.println("Starting on CPU");
            for (Book book : library) {
                if (book.isSelected()) {
                    System.out.println("   CPU processing " + book.getTitle());
                    book.checkNames(names);
                }
            }
            long cpuEnd = System.currentTimeMillis();
            System.out.println("CPU = " + (cpuEnd - cpuStart));
            System.out.println("ratio = " + (((float) (cpuEnd - cpuStart)) / (gpuEnd - gpuStart)));
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ui = new UI(library, names);
                }
            });
        }
    }


}

