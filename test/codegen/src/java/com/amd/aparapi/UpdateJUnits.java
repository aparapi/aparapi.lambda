/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
   774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
   you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
   Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
   Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

 */
package com.amd.aparapi;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpdateJUnits{

   public static class Editor{
      final static SimpleAttributeSet boldItalic = new SimpleAttributeSet();

      static{
         boldItalic.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
        // boldItalic.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
         boldItalic.addAttribute(StyleConstants.CharacterConstants.FontSize, 10);
         boldItalic.addAttribute(StyleConstants.CharacterConstants.FontFamily, "Courier");
        // boldItalic.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.YELLOW);
         //boldItalic.addAttribute(StyleConstants.CharacterConstants.Background, Color.BLACK);
      }

      StyledDocument document = new DefaultStyledDocument();
      JTextPane textPane = new JTextPane(document);

      JScrollPane scrollPane = new JScrollPane(textPane);

      Editor(int _width, int _height, boolean _editable){
       //  Font font = new Font("Serif", Font.ITALIC, 20);
       //  textPane.setFont(font);
         textPane.setBackground(Color.BLACK);
         textPane.setForeground(Color.YELLOW);
         textPane.setEditable(_editable);
         scrollPane.setPreferredSize(new Dimension(_width, _height));
      }

      void setText(String _string){
         try{
            document.remove(0, document.getLength());
            document.insertString(0, _string, boldItalic);


         }catch(BadLocationException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
      }

      JComponent getComponent(){
         return (scrollPane);
      }

      public void setTextLines(List<String> _lines){
         StringBuilder sb = new StringBuilder();
         for(String line : _lines){
            sb.append(line).append("\n");
         }
         setText(sb.toString());
      }
   }

   public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException{
      File rootDir = new File(System.getProperty("root", "/Users/garyfrost/aparapi/aparapi/branches/lambda/test/codegen"));

      final String rootPackageName = CreateJUnitTests.class.getPackage().getName();
      final String testPackageName = rootPackageName + ".test";
      final File sourceDir = new File(rootDir, "src/java");
      File testDir = new File(sourceDir, testPackageName.replace(".", "/"));

      List<String> classNames = new ArrayList<String>();

      for(File sourceFile : testDir.listFiles(new FilenameFilter(){

         @Override public boolean accept(File dir, String name){
            return (name.endsWith(".java"));
         }
      })){
         String fileName = sourceFile.getName();
         String className = fileName.substring(0, fileName.length() - ".java".length());
         classNames.add(className);
      }

      final List<Source> sources = new ArrayList<Source>();
      for(String className : classNames){
         Class clazz = Class.forName(testPackageName + "." + className);
         Source source = new Source(clazz, sourceDir);
         sources.add(source);

         try{
            ClassModel classModel = ClassModel.getClassModel(clazz);
            try{
               Entrypoint e = classModel.getKernelEntrypoint();
               source.addActualOutput(OpenCLKernelWriter.writeToString(e));


            }catch(AparapiException e){
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
         }catch(ClassParseException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
      }

      Collections.sort(sources, new Comparator<Source>(){

         @Override public int compare(Source o1, Source o2){
            return (o1.toString().compareTo(o2.toString()));
         }
      });

      JFrame frame = new JFrame("");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      ListModel listModel = new AbstractListModel<Source>(){
         @Override public int getSize(){
            return sources.size();
         }

         @Override public Source getElementAt(int index){
            return sources.get(index);
         }
      };


      JList list = new JList(listModel);
      //list.setCellRenderer(renderer);
      final Editor javaEditor = new Editor(300, 300, false);
      final Editor initialOpenCLEditor = new Editor(600, 600, true);
      final Editor mid = new Editor(25, 600, false);
      final Editor finalOpenCLEditor = new Editor(600, 600, true);
      JPanel left = new JPanel();
      left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
      left.add(javaEditor.getComponent());
      left.add(new JScrollPane(list));
      panel.add(left);
      panel.add(initialOpenCLEditor.getComponent());
      panel.add(mid.getComponent());
      panel.add(finalOpenCLEditor.getComponent());

      frame.add(panel, BorderLayout.CENTER);

      list.addListSelectionListener(new ListSelectionListener(){
         @Override public void valueChanged(ListSelectionEvent e){
            Source source = (Source) ((JList) e.getSource()).getSelectedValue();
            javaEditor.setText(source.getJava().toString());
            Source.Section lhs=null;
            Source.Section rhs=null;

            if(source.getOpenCLSectionCount() > 0){

               lhs =  source.getOpenCL().get(0);
               initialOpenCLEditor.setText(lhs.toString());
            }else{
               initialOpenCLEditor.setText(" NO OpenCL!\n");
            }
            if(source.getActualOutput() != null){
               rhs = source.getActualOutput();
               finalOpenCLEditor.setText(rhs.toString());
            }else{
               finalOpenCLEditor.setText(" NO Generated OpenCL!\n");
            }
            if (lhs != null && rhs != null){
               Diff.DiffResult result = Diff.diff(lhs.getTrimmedLineArr(), rhs.getTrimmedLineArr());
               List<String> col = new ArrayList<String>();
               for (Diff.DiffResult.Block block:result){
                  switch (block.type) {
                     case SAME:
                        for (int i = block.lhsFrom; i <= block.lhsTo; i++) {
                           col.add("==");
                        }
                        break;
                     case LEFT:
                        for (int i = block.lhsFrom; i <= block.lhsTo; i++) {
                           col.add("  <");
                        }
                        break;
                     case RIGHT:
                        for (int i = block.rhsFrom; i <= block.rhsTo; i++) {
                           col.add("  >");
                        }
                        break;
                  }

               }
               mid.setTextLines(col);
            }
         }
      });


      frame.setVisible(true);
      frame.pack();


   }
}
