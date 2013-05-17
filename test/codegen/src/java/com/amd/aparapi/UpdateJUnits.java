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

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;


import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UpdateJUnits{

   public static class Editor{
      final static SimpleAttributeSet boldItalic = new SimpleAttributeSet();

      static{
         boldItalic.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
         boldItalic.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
      }

      StyledDocument document = new DefaultStyledDocument();
      JTextPane textPane = new JTextPane(document);

      JScrollPane scrollPane = new JScrollPane(textPane);

      Editor(){


         textPane.setEditable(false);
         scrollPane.setPreferredSize(new Dimension(600, 800));
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
      File rootDir = new File(System.getProperty("root", "/home/gfrost/aparapi/branches/lambda/test/codegen"));

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
         Source source =  new Source(clazz, sourceDir);
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
            return(o1.toString().compareTo(o2.toString()));
         }
      });

      JFrame frame = new JFrame("");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel panel = new JPanel();
      ListModel listModel = new AbstractListModel<Source>(){
         @Override public int getSize(){
            return sources.size();  //To change body of implemented methods use File | Settings | File Templates.
         }
         @Override public Source getElementAt(int index){
            return sources.get(index);  //To change body of implemented methods use File | Settings | File Templates.
         }
      }  ;



      JList list = new JList(listModel);
      //list.setCellRenderer(renderer);
      final Editor javaEditor = new Editor();
      final Editor initialOpenCLEditor = new Editor();
      final Editor finalOpenCLEditor = new Editor();
      panel.add(new JScrollPane(list));
      panel.add(javaEditor.getComponent());
      panel.add(initialOpenCLEditor.getComponent());
      panel.add(finalOpenCLEditor.getComponent());

      frame.add(panel, BorderLayout.CENTER);




      list.addListSelectionListener(new ListSelectionListener(){
         @Override public void valueChanged(ListSelectionEvent e){
            Source source = (Source)((JList)e.getSource()).getSelectedValue();
            javaEditor.setText(source.getJava().toString());
            if(source.getOpenCLSectionCount() > 0){
               initialOpenCLEditor.setText(source.getOpenCL().toString());
            }else{
               initialOpenCLEditor.setText(" NO OpenCL!\n");
            }
            if (source.getActualOutput()!=null){
               finalOpenCLEditor.setText(source.getActualOutput().toString());
            } else{
               finalOpenCLEditor.setText(" NO Generated OpenCL!\n");
            }
         }
      });


      frame.setVisible(true);
      frame.pack();


   }
}
